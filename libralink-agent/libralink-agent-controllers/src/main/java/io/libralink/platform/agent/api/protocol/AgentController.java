package io.libralink.platform.agent.api.protocol;

import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.api.GetBalanceResponseBuilder;
import io.libralink.client.payment.proto.builder.api.RegisterKeyResponseBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Api(tags = "Agent")
@RestController
public class AgentController {

    private static final Logger LOG = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @PostMapping(value = "/protocol/agent/register", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public Libralink.Envelope register(@RequestBody String body, @RequestHeader("Accept") String accept) throws Exception {

        Libralink.Envelope envelope = Libralink.Envelope.parseFrom(Base64.getDecoder().decode(body.getBytes()));
        Optional<Libralink.RegisterKeyRequest> requestOptional = EnvelopeUtils.findEntityByType(envelope, Libralink.RegisterKeyRequest.class);

        if (requestOptional.isEmpty()) {
            throw new AgentProtocolException("Invalid Body", 999);
        }

        final Libralink.RegisterKeyRequest request = requestOptional.get();
        final String address = request.getAddress();

        /* Verify signature */
        Optional<Libralink.Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, address);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        agentService.registerAgent(address, request.getPubKey(), request.getAlgorithm(), request.getConfirmationId(), request.getHash());

        Libralink.RegisterKeyResponse response = RegisterKeyResponseBuilder.newBuilder()
                .addAddress(address)
                .build();

        return EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(
                        EnvelopeContentBuilder.newBuilder()
                                .addEntity(Any.pack(response))
                                .addSigReason(Libralink.SignatureReason.NONE)
                                .build()
                ).build();
    }

    @PostMapping(value = "/protocol/agent/balance", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public Libralink.Envelope getBalance(@RequestBody String body, @RequestHeader("Accept") String accept) throws Exception {

        Libralink.Envelope envelope = Libralink.Envelope.parseFrom(Base64.getDecoder().decode(body.getBytes()));
        boolean isValid = BaseEntityValidator.findFirstFailedRule(envelope, GetBalanceRequestSignedRule.class).isEmpty();
        if (!isValid) {
            throw new AgentProtocolException("Invalid Request", 999);
        }

        final String address = EnvelopeUtils.extractEntityAttribute(envelope, Libralink.GetBalanceRequest.class, Libralink.GetBalanceRequest::getAddress).get();

        /* Verify all signatures, aka authentication & authorization */
        Optional<Libralink.Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, address);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        /* Call Service */
        BalanceDTO balanceDTO = agentService.getBalance(address);

        Libralink.GetBalanceResponse response = GetBalanceResponseBuilder.newBuilder()
                .addAvailable(balanceDTO.getAvailable())
                .addPending(balanceDTO.getPending())
                .addAddress(address)
                .build();

        return EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(
                        EnvelopeContentBuilder.newBuilder()
                                .addEntity(Any.pack(response))
                                .addSigReason(Libralink.SignatureReason.NONE)
                                .build()
                ).build();
    }
}
