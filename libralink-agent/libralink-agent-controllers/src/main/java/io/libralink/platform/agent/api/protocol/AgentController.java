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

        final Optional<String> addressOption = EnvelopeUtils.extractEntityAttribute(envelope, RegisterKeyRequest.class, RegisterKeyRequest::getAddress);
        final Optional<String> challengeOption = EnvelopeUtils.extractEntityAttribute(envelope, RegisterKeyRequest.class, RegisterKeyRequest::getChallenge);

        if (addressOption.isEmpty() || challengeOption.isEmpty()) {
            throw new AgentProtocolException();
        }

        /* Verify signature */
        final String address = addressOption.get();
        Optional<Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, address);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException();
        }

        /* TODO: register */

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
            throw new AgentProtocolException();
        }

        final String address = EnvelopeUtils.extractEntityAttribute(envelope, GetBalanceRequest.class, GetBalanceRequest::getAddress).get();

        /* Verify all signatures, aka authentication & authorization */
        Optional<Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, address);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException();
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
