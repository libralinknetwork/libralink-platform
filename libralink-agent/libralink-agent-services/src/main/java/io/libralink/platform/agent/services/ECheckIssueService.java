package io.libralink.platform.agent.services;

import io.libralink.client.payment.protocol.echeck.ECheck;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.signature.SignatureHelper;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import java.util.Optional;

@Service
public class ECheckIssueService {

    private final Credentials processorCredentials;

    public ECheckIssueService(@Value("${libralink.processor.key.private}") String processorPrivateKey) {
        processorCredentials = Credentials.create(processorPrivateKey);
    }

    public Envelope issue(Envelope envelope) throws Exception {

        /* Get E-Check details */
        Optional<ECheck> eCheckOption = EnvelopeUtils.findEntityByType(envelope, ECheck.class)
                .map(eCheck -> (ECheck) eCheck);
        if (eCheckOption.isEmpty()) {
            /* No E-Check details */
            throw new AgentProtocolException();
        }
        ECheck eCheck  = eCheckOption.get();

        /* TODO: Verify processor(s) */
        /* TODO: Verify E-Check details (expiration, etc) */
        /* TODO: Block Payer's funds */

        EnvelopeContent responseEnvelopeContent = EnvelopeContent.builder()
                .addEntity(envelope)
                .build();

        Envelope responseEnvelope = Envelope.builder()
                .addContent(responseEnvelopeContent).build();

        return SignatureHelper.sign(responseEnvelope, processorCredentials, SignatureReason.CONFIRM);
    }
}
