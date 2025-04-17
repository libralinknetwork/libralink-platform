package io.libralink.platform.agent.services;

import io.libralink.client.payment.protocol.echeck.ECheck;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.protocol.processing.ProcessingDetails;
import io.libralink.client.payment.protocol.processing.ProcessingFee;
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

    public Envelope preProcess(Envelope envelope) throws Exception {

        Optional<ECheck> eCheckOption = EnvelopeUtils.findEntityByType(envelope, ECheck.class).map(eCheck -> (ECheck) eCheck);
        if (eCheckOption.isEmpty()) {
            return envelope; /* Non-ECheck Envelope */
        }
        ECheck eCheck = eCheckOption.get();

        /* Only one Processor supported at the moment */
        if (!processorCredentials.getAddress().equals(eCheck.getPayerProcessor()) ||
                !processorCredentials.getAddress().equals(eCheck.getPayeeProcessor())) {
            throw new AgentProtocolException("Unknown Payer/Payee processor", 999);
        }

        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        if (eCheck.getExpiresAt() < now) {
            throw new AgentProtocolException("Expired E-Check", 999);
        }

        ProcessingDetails processingDetails = ProcessingDetails.builder()
                .addIntermediary(null) /* No network/cluster at this time */
                .addEnvelope(envelope)
                .addFee(ProcessingFee.builder()
                    .addFeeType(feeType)
                    .addAmount(amount)
                    .build())
                .build();

        EnvelopeContent envelopeContent = EnvelopeContent.builder()
                .addEntity(processingDetails)
                .build();

        Envelope responseEnvelope = Envelope.builder()
                .addContent(envelopeContent).build();

        return SignatureHelper.sign(responseEnvelope, processorCredentials, SignatureReason.FEE_LOCK);
    }
}
