package io.libralink.platform.agent.api;

import io.libralink.client.payment.protocol.api.balance.GetBalanceRequest;
import io.libralink.client.payment.protocol.api.balance.GetBalanceResponse;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.agent.services.AgentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
public class AccountController {

    @Autowired
    private AgentStatusService agentStatusService;

    @GetMapping("/account/register")
    public String register() throws Exception {
        return "OK";
    }

    @PostMapping(value = "/account/balance", produces = "application/json")
    public Envelope getBalance(@RequestBody Envelope envelope) throws Exception {

        final String address = ((GetBalanceRequest) envelope.getContent().getEntity()).getPub();
        Optional<Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, address);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException();
        }

        GetBalanceResponse response = GetBalanceResponse.builder()
                .addAvailable(BigDecimal.ZERO)
                .addPending(BigDecimal.ZERO)
                .addTotal(BigDecimal.ZERO)
                .addPub(address)
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
