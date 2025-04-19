package io.libralink.platform.agent.services;

import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
import io.libralink.client.payment.proto.builder.fee.ProcessingFeeBuilder;
import io.libralink.client.payment.signature.SignatureHelper;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcessorFeeService {

    private final Credentials processorCredentials;

    @Value("${libralink.processor.fee.type}")
    private String feeType;

    @Value("${libralink.processor.fee.amount}")
    private BigDecimal amount;

    public ProcessorFeeService(@Value("${libralink.processor.key.private}") String processorPrivateKey) {
        processorCredentials = Credentials.create(processorPrivateKey);
    }

    public Libralink.Envelope preProcess(Libralink.Envelope envelope) throws Exception {

        Optional<Libralink.ECheck> eCheckOption = EnvelopeUtils.findEntityByType(envelope, Libralink.ECheck.class);
        if (eCheckOption.isEmpty()) {
            return envelope; /* Non-ECheck Envelope */
        }
        Libralink.ECheck eCheck = eCheckOption.get();

        /* Only one Processor supported at the moment */
        if (!processorCredentials.getAddress().equals(eCheck.getFromProc()) ||
                !processorCredentials.getAddress().equals(eCheck.getToProc())) {
            throw new AgentProtocolException("Unknown Payer/Payee processor", 999);
        }

        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        if (eCheck.getExpiresAt() < now) {
            throw new AgentProtocolException("Expired E-Check", 999);
        }

        Libralink.ProcessingFee processingDetails = ProcessingFeeBuilder.newBuilder()
                .addIntermediary(null) /* No network/cluster at this time */
                .addEnvelope(envelope)
                .addFeeType(feeType)
                .addAmount(amount)
                .build();

        Libralink.EnvelopeContent envelopeContent = EnvelopeContentBuilder.newBuilder()
                .addEntity(Any.pack(processingDetails))
                .build();

        Libralink.Envelope responseEnvelope = EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(envelopeContent).build();

        return SignatureHelper.sign(responseEnvelope, processorCredentials, Libralink.SignatureReason.FEE_LOCK);
    }
}
