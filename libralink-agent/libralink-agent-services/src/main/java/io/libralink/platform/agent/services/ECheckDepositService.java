package io.libralink.platform.agent.services;

import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
import io.libralink.client.payment.signature.SignatureHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import java.util.UUID;

@Service
public class ECheckDepositService {

    private final Credentials processorCredentials;

    public ECheckDepositService(@Value("${libralink.processor.key.private}") String processorPrivateKey) {
        processorCredentials = Credentials.create(processorPrivateKey);
    }

    public Libralink.Envelope deposit(Libralink.Envelope envelope) throws Exception {

        /* TODO: heavy logic here */

        Libralink.EnvelopeContent responseEnvelopeContent = EnvelopeContentBuilder.newBuilder()
                .addEntity(Any.pack(envelope))
                .build();

        Libralink.Envelope responseEnvelope = EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(responseEnvelopeContent).build();

        return SignatureHelper.sign(responseEnvelope, processorCredentials, Libralink.SignatureReason.CONFIRM);
    }
}
