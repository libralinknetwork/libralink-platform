package io.libralink.platform.agent.api.protocol;

import io.libralink.client.payment.protocol.api.account.RegisterKeyRequest;
import io.libralink.client.payment.protocol.api.account.RegisterKeyResponse;
import io.libralink.client.payment.protocol.api.balance.GetBalanceRequest;
import io.libralink.client.payment.protocol.api.balance.GetBalanceResponse;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.client.payment.validator.BaseEntityValidator;
import io.libralink.client.payment.validator.rules.GetBalanceRequestSignedRule;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.agent.services.AgentService;
import io.libralink.platform.wallet.integration.dto.BalanceDTO;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Api(tags = "Agent")
@RestController
public class AgentController {

    private static final Logger LOG = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @PostMapping(value = "/protocol/agent/register", produces = "application/json")
    public Envelope register(@RequestBody Envelope envelope) throws Exception {

        Optional<RegisterKeyRequest> requestOptional = EnvelopeUtils.findEntityByType(envelope, RegisterKeyRequest.class)
                .map(req -> (RegisterKeyRequest) req);

        if (requestOptional.isEmpty()) {
            throw new AgentProtocolException("Invalid Body", 999);
        }

        final RegisterKeyRequest request = requestOptional.get();
        final String address = request.getAddress();

        /* Verify signature */
        Optional<Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, address);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        agentService.registerAgent(address, request.getPubKey(), request.getAlgorithm(), request.getConfirmationId(), request.getHash());

        RegisterKeyResponse response = RegisterKeyResponse.builder()
                .addAddress(address)
                .build();

        return Envelope.builder()
                .addContent(
                    EnvelopeContent.builder()
                        .addEntity(response)
                        .addSigReason(SignatureReason.NONE)
                        .build()
                ).build();
    }

    @PostMapping(value = "/protocol/agent/balance", produces = "application/json")
    public Envelope getBalance(@RequestBody Envelope envelope) throws Exception {

        boolean isValid = BaseEntityValidator.findFirstFailedRule(envelope, GetBalanceRequestSignedRule.class).isEmpty();
        if (!isValid) {
            throw new AgentProtocolException("Invalid Request", 999);
        }

        final String address = EnvelopeUtils.extractEntityAttribute(envelope, GetBalanceRequest.class, GetBalanceRequest::getAddress).get();

        /* Verify all signatures, aka authentication & authorization */
        Optional<Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, address);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        /* Call Service */
        BalanceDTO balanceDTO = agentService.getBalance(address);

        GetBalanceResponse response = GetBalanceResponse.builder()
                .addAvailable(balanceDTO.getAvailable())
                .addPending(balanceDTO.getPending())
                .addAddress(address)
                .build();

        return Envelope.builder()
                .addContent(
                    EnvelopeContent.builder()
                        .addEntity(response)
                        .addSigReason(SignatureReason.NONE)
                        .build()
                ).build();
    }
}
