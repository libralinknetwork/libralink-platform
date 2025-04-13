package io.libralink.platform.agent.api;

import io.libralink.client.payment.protocol.api.echeck.DepositRequest;
import io.libralink.client.payment.protocol.echeck.DepositApproval;
import io.libralink.client.payment.protocol.echeck.ECheck;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.protocol.processing.ProcessingDetails;
import io.libralink.client.payment.protocol.processing.ProcessingFee;
import io.libralink.client.payment.signature.SignatureHelper;
import io.libralink.client.payment.util.JsonUtils;
import io.libralink.platform.agent.api.protocol.ECheckController;
import io.libralink.platform.agent.services.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.web3j.crypto.Credentials;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ECheckController.class)
@ContextConfiguration(classes = { ApiTestConfiguration.class })
public class ECheckDepositTest {

    private static final Logger LOG = LoggerFactory.getLogger(ECheckDepositTest.class);

    /* Payer Credentials */
    final private String PAYER_PK = "7af8df13f6aebcbd9edd369bb5f67bf7523517685491fea776bb547910ff5673";
    final private Credentials PAYER_CRED = Credentials.create(PAYER_PK);

    /* PAYEE */
    final private String PAYEE_PK = "64496cc969654b231087af38ce654aa8d539fea0970d90366e42a5e39761f3f5";
    final private Credentials PAYEE_CRED = Credentials.create(PAYEE_PK);

    /* Processor Credentials */
    private String PROCESSOR_PK = "d601b629b288ce5ab659b4782e7f34cc2279ac729485302fdcc19d0fccb78b0d";
    final private Credentials PROCESSOR_CRED = Credentials.create(PROCESSOR_PK);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentService agentService;

    @MockBean
    private EnvelopeService envelopeService;

    @MockBean
    private ProcessorFeeService processorFeeService;

    @MockBean
    private ECheckIssueService eCheckIssueService;

    @MockBean
    private ECheckDepositService eCheckDepositService;

    @MockBean
    private ProcessorService processorService;

    LocalDateTime now = LocalDateTime.now();

    /* E-Check Definition */
    private ECheck eCheck = ECheck.builder()
            .addPayer(PAYER_CRED.getAddress())
            .addPayerProcessor(PROCESSOR_CRED.getAddress())
            .addPayee(PAYEE_CRED.getAddress())
            .addPayeeProcessor(PROCESSOR_CRED.getAddress())
            .addCurrency("USDC")
            .addFaceAmount(BigDecimal.valueOf(150))
            .addCreatedAt(now.toEpochSecond(ZoneOffset.UTC))
            .addExpiresAt(now.plusDays(3).toEpochSecond(ZoneOffset.UTC))
            .addNote("")
            .build();

    private Envelope unsignedECheckEnvelope = Envelope.builder()
            .addContent(EnvelopeContent.builder()
                    .addEntity(eCheck)
                    .build())
            .build();

    private Envelope payerIdentityECheckEnvelope = SignatureHelper.sign(unsignedECheckEnvelope, PAYER_CRED, SignatureReason.IDENTITY);

    private ProcessingDetails processingDetails = ProcessingDetails.builder()
            .addIntermediary(null)
            .addEnvelope(payerIdentityECheckEnvelope)
            .addFee(ProcessingFee.builder().addFeeType("percent").addAmount(BigDecimal.ONE).build())
            .build();

    private Envelope unsignedProcessorFeeEnvelope = Envelope.builder()
            .addContent(EnvelopeContent.builder()
                    .addEntity(processingDetails)
                    .build())
            .build();

    private Envelope processorSignedFeeEnvelope = SignatureHelper.sign(unsignedProcessorFeeEnvelope, PROCESSOR_CRED, SignatureReason.FEE_LOCK);

    private Envelope unsignedPayerConfirmEnvelope = Envelope.builder()
            .addContent(EnvelopeContent.builder()
                    .addEntity(processorSignedFeeEnvelope)
                    .build())
            .build();

    private Envelope signedPayerConfirmEnv = SignatureHelper.sign(unsignedPayerConfirmEnvelope, PAYER_CRED, SignatureReason.CONFIRM);

    private Envelope unsignedProcessorConfirmEnvelope = Envelope.builder()
            .addContent(EnvelopeContent.builder()
                    .addEntity(signedPayerConfirmEnv)
                    .build())
            .build();

    private Envelope signedProcessorConfirmEnvelope = SignatureHelper.sign(unsignedProcessorConfirmEnvelope, PROCESSOR_CRED, SignatureReason.CONFIRM);

    /* Deposit Approval Definition */
    private DepositApproval depositApproval = DepositApproval.builder()
            .addPayer(PAYER_CRED.getAddress())
            .addPayee(PAYEE_CRED.getAddress())
            .addAmount(BigDecimal.valueOf(100))
            .addCheckId(eCheck.getId())
            .addCreatedAt(now.toEpochSecond(ZoneOffset.UTC))
            .addNote(null)
            .build();

    private Envelope unsignedPayeeDepositApprovalEnvelope = Envelope.builder()
            .addContent(EnvelopeContent.builder()
                    .addEntity(depositApproval)
                    .build())
            .build();

    private Envelope payeeSignedDepositApprovalEnvelope = SignatureHelper.sign(unsignedPayeeDepositApprovalEnvelope, PAYEE_CRED, SignatureReason.CONFIRM);

    private Envelope unsignedPayerDepositApprovalEnvelope = Envelope.builder()
            .addContent(EnvelopeContent.builder()
                    .addEntity(payeeSignedDepositApprovalEnvelope)
                    .build())
            .build();

    private Envelope payerSignedDepositApprovalEnvelope = SignatureHelper.sign(unsignedPayerDepositApprovalEnvelope, PAYER_CRED, SignatureReason.CONFIRM);

    @Test
    public void test_deposit_request() throws Exception {

        DepositRequest depositRequest = DepositRequest.builder()
                .addCheck(signedProcessorConfirmEnvelope)
                .addDepositApprovals(List.of(payerSignedDepositApprovalEnvelope))
                .build();

        Envelope unsignedEnvelope = Envelope.builder()
                .addContent(EnvelopeContent.builder()
                        .addEntity(depositRequest)
                        .build())
                .build();

        Envelope signedEnvelope = SignatureHelper.sign(unsignedEnvelope, PAYEE_CRED, SignatureReason.IDENTITY);
        String body = JsonUtils.toJson(signedEnvelope);
        LOG.info(body);

        mockMvc.perform(post("/protocol/echeck/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    public ECheckDepositTest() throws Exception {
    }
}
