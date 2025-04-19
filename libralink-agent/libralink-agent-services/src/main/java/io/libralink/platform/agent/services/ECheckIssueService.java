package io.libralink.platform.agent.services;

import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
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
import java.util.UUID;

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

    public Libralink.Envelope issue(Libralink.Envelope envelope) throws Exception {

        /* Get E-Check details */
        Optional<Libralink.ECheck> eCheckOption = EnvelopeUtils.findEntityByType(envelope, Libralink.ECheck.class);
        if (eCheckOption.isEmpty()) {
            /* No E-Check details */
            throw new AgentProtocolException("Invalid Body", 999);
        }
        Libralink.ECheck eCheck  = eCheckOption.get();

        Optional<Libralink.ProcessingFee> processingOption = EnvelopeUtils.findEntityByType(envelope, Libralink.ProcessingFee.class);
        if (processingOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Body", 999);
        }
        Libralink.ProcessingFee processingDetails = processingOption.get();

        /* Only one Processor supported at the moment */
        if (!processorCredentials.getAddress().equals(eCheck.getFromProc()) ||
                !processorCredentials.getAddress().equals(eCheck.getToProc())) {
            throw new AgentProtocolException("Unknown Payer/Payee processor", 999);
        }

        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        if (eCheck.getExpiresAt() < now) {
            throw new AgentProtocolException("Expired E-Check", 999);
        }

        Optional<Agent> payerAgentOptional = agentRepository.findByAddress(eCheck.getFrom());
        if (payerAgentOptional.isEmpty()) {
            throw new AgentProtocolException("Unknown Payer", 999);
        }
        Agent payerAgent = payerAgentOptional.get();

        IntegrationECheckDTO eCheckDTO = ECheckConverter.toDTO(eCheck, payerAgent.getAccountId(), envelope.getId(),
                processingDetails.getAmount(), processingDetails.getFeeType());

        /* Registering E-Check and blocking Payer funds */
        try {
            walletClient.register(eCheckDTO, tokenService.issueSystemToken());
        } catch (Exception ex) {
            throw new AgentProtocolException("E-Check Issue Error", 999);
        }

        Libralink.EnvelopeContent responseEnvelopeContent = EnvelopeContentBuilder.newBuilder()
                .addEntity(Any.pack(envelope))
                .build();

        Libralink.Envelope responseEnvelope = EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(responseEnvelopeContent).build();

        return SignatureHelper.sign(responseEnvelope, processorCredentials, Libralink.SignatureReason.CONFIRM);
    }
}
