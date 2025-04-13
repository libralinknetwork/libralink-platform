package io.libralink.platform.agent.services;

import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.signature.SignatureHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

@Service
public class ECheckDepositService {

    private final Credentials processorCredentials;

    public ECheckDepositService(@Value("${libralink.processor.key.private}") String processorPrivateKey) {
        processorCredentials = Credentials.create(processorPrivateKey);
    }

    public Envelope deposit(Envelope envelope) throws Exception {

        /* TODO: heavy logic here */

        EnvelopeContent responseEnvelopeContent = EnvelopeContent.builder()
                .addEntity(envelope)
                .build();

        Envelope responseEnvelope = Envelope.builder()
                .addContent(responseEnvelopeContent).build();

        return SignatureHelper.sign(responseEnvelope, processorCredentials, SignatureReason.CONFIRM);
    }
}
