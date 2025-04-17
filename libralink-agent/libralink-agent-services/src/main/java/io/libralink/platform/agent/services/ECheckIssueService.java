package io.libralink.platform.agent.services;

import io.libralink.client.payment.protocol.echeck.ECheck;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.protocol.processing.ProcessingDetails;
import io.libralink.client.payment.signature.SignatureHelper;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.platform.agent.converters.ECheckConverter;
import io.libralink.platform.agent.data.entity.Agent;
import io.libralink.platform.agent.data.repository.AgentRepository;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.security.service.TokenService;
import io.libralink.platform.wallet.integration.api.WalletClient;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class ECheckIssueService {

    @Autowired
    private WalletClient walletClient;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TokenService tokenService;

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
            throw new AgentProtocolException("Invalid Body", 999);
        }
        ECheck eCheck  = eCheckOption.get();

        Optional<ProcessingDetails> processingOption = EnvelopeUtils.findEntityByType(envelope, ProcessingDetails.class)
                .map(processing -> (ProcessingDetails) processing);
        if (processingOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Body", 999);
        }
        ProcessingDetails processingDetails = processingOption.get();

        /* Only one Processor supported at the moment */
        if (!processorCredentials.getAddress().equals(eCheck.getPayerProcessor()) ||
                !processorCredentials.getAddress().equals(eCheck.getPayeeProcessor())) {
            throw new AgentProtocolException("Unknown Payer/Payee processor", 999);
        }

        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        if (eCheck.getExpiresAt() < now) {
            throw new AgentProtocolException("Expired E-Check", 999);
        }

        Optional<Agent> payerAgentOptional = agentRepository.findByAddress(eCheck.getPayer());
        if (payerAgentOptional.isEmpty()) {
            throw new AgentProtocolException("Unknown Payer", 999);
        }
        Agent payerAgent = payerAgentOptional.get();

        IntegrationECheckDTO eCheckDTO = ECheckConverter.toDTO(eCheck, payerAgent.getAccountId(), envelope.getId(),
                processingDetails.getFee().getAmount(), processingDetails.getFee().getFeeType());

        /* Registering E-Check and blocking Payer funds */
        try {
            walletClient.register(eCheckDTO, tokenService.issueSystemToken());
        } catch (Exception ex) {
            throw new AgentProtocolException("E-Check Issue Error", 999);
        }

        EnvelopeContent responseEnvelopeContent = EnvelopeContent.builder()
                .addEntity(envelope)
                .build();

        Envelope responseEnvelope = Envelope.builder()
                .addContent(responseEnvelopeContent).build();

        return SignatureHelper.sign(responseEnvelope, processorCredentials, SignatureReason.CONFIRM);
    }
}
