package io.libralink.platform.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.api.DepositRequestBuilder;
import io.libralink.client.payment.proto.builder.echeck.ECheckBuilder;
import io.libralink.client.payment.proto.builder.echeck.PaymentRequestBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
import io.libralink.client.payment.proto.builder.fee.ProcessingFeeBuilder;
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
import java.util.UUID;

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

    @Autowired
    private ObjectMapper objectMapper;

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
    private Libralink.ECheck eCheck = ECheckBuilder.newBuilder()
            .addCorrelationId(UUID.randomUUID())
            .addFrom(PAYER_CRED.getAddress())
            .addFromProc(PROCESSOR_CRED.getAddress())
            .addTo(PAYEE_CRED.getAddress())
            .addToProc(PROCESSOR_CRED.getAddress())
            .addCurrency("USDC")
            .addFaceAmount(BigDecimal.valueOf(150))
            .addCreatedAt(now.toEpochSecond(ZoneOffset.UTC))
            .addExpiresAt(now.plusDays(3).toEpochSecond(ZoneOffset.UTC))
            .addNote("")
            .build();

    private Libralink.Envelope unsignedECheckEnvelope = EnvelopeBuilder.newBuilder()
            .addId(UUID.randomUUID())
            .addContent(EnvelopeContentBuilder.newBuilder()
                    .addEntity(Any.pack(eCheck))
                    .build())
            .build();

    private Libralink.Envelope payerIdentityECheckEnvelope = SignatureHelper.sign(unsignedECheckEnvelope, PAYER_CRED, Libralink.SignatureReason.IDENTITY);

    private Libralink.ProcessingFee processingDetails = ProcessingFeeBuilder.newBuilder()
            .addIntermediary(null)
            .addEnvelope(payerIdentityECheckEnvelope)
            .addFeeType("percent")
            .addAmount(BigDecimal.ONE)
            .build();

    private Libralink.Envelope unsignedProcessorFeeEnvelope = EnvelopeBuilder.newBuilder()
            .addId(UUID.randomUUID())
            .addContent(EnvelopeContentBuilder.newBuilder()
                    .addEntity(Any.pack(processingDetails))
                    .build())
            .build();

    private Libralink.Envelope processorSignedFeeEnvelope = SignatureHelper.sign(unsignedProcessorFeeEnvelope, PROCESSOR_CRED, Libralink.SignatureReason.FEE_LOCK);

    private Libralink.Envelope unsignedPayerConfirmEnvelope = EnvelopeBuilder.newBuilder()
            .addId(UUID.randomUUID())
            .addContent(EnvelopeContentBuilder.newBuilder()
                    .addEntity(Any.pack(processorSignedFeeEnvelope))
                    .build())
            .build();

    private Libralink.Envelope signedPayerConfirmEnv = SignatureHelper.sign(unsignedPayerConfirmEnvelope, PAYER_CRED, Libralink.SignatureReason.CONFIRM);

    private Libralink.Envelope unsignedProcessorConfirmEnvelope = EnvelopeBuilder.newBuilder()
            .addId(UUID.randomUUID())
            .addContent(EnvelopeContentBuilder.newBuilder()
                    .addEntity(Any.pack(signedPayerConfirmEnv))
                    .build())
            .build();

    private Libralink.Envelope signedProcessorConfirmEnvelope = SignatureHelper.sign(unsignedProcessorConfirmEnvelope, PROCESSOR_CRED, Libralink.SignatureReason.CONFIRM);

    /* Deposit Approval Definition */
    private Libralink.PaymentRequest paymentRequest = PaymentRequestBuilder.newBuilder()
            .addFrom(PAYER_CRED.getAddress())
            .addTo(PAYEE_CRED.getAddress())
            .addAmount(BigDecimal.valueOf(100))
            .addCorrelationId(UUID.fromString(eCheck.getCorrelationId()))
            .addCreatedAt(now.toEpochSecond(ZoneOffset.UTC))
            .addNote(null)
            .build();

    private Libralink.Envelope unsignedPayeeDepositApprovalEnvelope = EnvelopeBuilder.newBuilder()
            .addId(UUID.randomUUID())
            .addContent(EnvelopeContentBuilder.newBuilder()
                    .addEntity(Any.pack(paymentRequest))
                    .build())
            .build();

    private Libralink.Envelope payeeSignedDepositApprovalEnvelope = SignatureHelper.sign(unsignedPayeeDepositApprovalEnvelope, PAYEE_CRED, Libralink.SignatureReason.CONFIRM);

    private Libralink.Envelope unsignedPayerDepositApprovalEnvelope = EnvelopeBuilder.newBuilder()
            .addId(UUID.randomUUID())
            .addContent(EnvelopeContentBuilder.newBuilder()
                    .addEntity(Any.pack(payeeSignedDepositApprovalEnvelope))
                    .build())
            .build();

    private Libralink.Envelope payerSignedDepositApprovalEnvelope = SignatureHelper.sign(unsignedPayerDepositApprovalEnvelope, PAYER_CRED, Libralink.SignatureReason.CONFIRM);

    @Test
    public void test_deposit_request() throws Exception {

        Libralink.DepositRequest depositRequest = DepositRequestBuilder.newBuilder()
                .addCheckEnvelope(signedProcessorConfirmEnvelope)
                .addRequestEnvelopes(List.of(payerSignedDepositApprovalEnvelope))
                .build();

        Libralink.Envelope unsignedEnvelope = EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(EnvelopeContentBuilder.newBuilder()
                        .addEntity(Any.pack(depositRequest))
                        .build())
                .build();

        Libralink.Envelope signedEnvelope = SignatureHelper.sign(unsignedEnvelope, PAYEE_CRED, Libralink.SignatureReason.IDENTITY);
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
