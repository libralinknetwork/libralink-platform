package io.libralink.platform.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.echeck.ECheckBuilder;
import io.libralink.client.payment.proto.builder.echeck.ECheckSplitBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
import io.libralink.client.payment.proto.builder.exception.BuilderException;
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
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ECheckController.class)
@ContextConfiguration(classes = { ApiTestConfiguration.class })
public class ECheckControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ECheckControllerTest.class);

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

    private Libralink.ECheck eCheck = ECheckBuilder.newBuilder()
            .addFrom(PAYER_CRED.getAddress())
            .addFromProc(PROCESSOR_CRED.getAddress())
            .addTo(PAYEE_CRED.getAddress())
            .addToProc(PROCESSOR_CRED.getAddress())
            .addCurrency("USDC")
            .addFaceAmount(BigDecimal.valueOf(150))
            .addCreatedAt(now.toEpochSecond(ZoneOffset.UTC))
            .addExpiresAt(now.plusYears(10).toEpochSecond(ZoneOffset.UTC))
            .addNote("")
            .addCorrelationId(UUID.randomUUID())
            .addSplits(List.of(ECheckSplitBuilder.newBuilder()
                    .addTo(PAYEE_CRED.getAddress())
                    .addToProc(PROCESSOR_CRED.getAddress())
                    .addAmount(BigDecimal.valueOf(150))
                .build()
            )).build();

    private Libralink.Envelope unsignedEnvelope = EnvelopeBuilder.newBuilder()
            .addId(UUID.randomUUID())
            .addContent(EnvelopeContentBuilder.newBuilder()
                    .addEntity(Any.pack(eCheck))
                    .build())
            .build();

    @Test
    public void test_pre_process_echeck_signed_by_payer() throws Exception {

        when(processorFeeService.preProcess(any())).thenReturn(unsignedEnvelope);
        Libralink.Envelope signedEnvelope = SignatureHelper.sign(unsignedEnvelope, PAYER_CRED, Libralink.SignatureReason.IDENTITY);
        String base64Body = Base64.getEncoder().encodeToString(signedEnvelope.toByteArray());
        LOG.info("Base64 - " + base64Body);
        LOG.info("Json - " + JsonUtils.toJson(signedEnvelope));

        mockMvc.perform(post("/protocol/echeck/pre-issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(base64Body))
                .andExpect(status().isOk());
    }

    @Test
    public void test_pre_process_echeck_signed_by_payee() throws Exception {

        when(processorFeeService.preProcess(any())).thenReturn(unsignedEnvelope);
        Libralink.Envelope signedEnvelope = SignatureHelper.sign(unsignedEnvelope, PAYEE_CRED, Libralink.SignatureReason.IDENTITY);
        String body = JsonUtils.toJson(signedEnvelope);

        mockMvc.perform(post("/protocol/echeck/pre-issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void test_issue_echeck_signed_by_payer() throws Exception {

        final String feeLockEnvelopeString = "CiQ2N2JjNGRmOS03YWFiLTQ4MTAtOTU5Yy0wZDZkYTcyYTY4MTcSjAYK0AUKQ3R5cGUuZ29vZ2xlYXBpcy5jb20vaW8ubGlicmFsaW5rLmNsaWVudC5wYXltZW50LnByb3RvLlByb2Nlc3NpbmdGZWUSiAUKB3BlcmNlbnQSATEi+QQKJDA5ZDUxMWY1LTk2NzYtNGQ0MC04NzA5LTgwNTNhYmY1NzQzMBLJAwqNAwo8dHlwZS5nb29nbGVhcGlzLmNvbS9pby5saWJyYWxpbmsuY2xpZW50LnBheW1lbnQucHJvdG8uRUNoZWNrEswCCgMxNTASBFVTREMaKjB4ZjM5OTAyYjEzM2ZiZGNmOTI2YzFmNDg2NjVjOThkMWIwMjhkOTA1YSIqMHgxODVjZDQ1OTc1N2E2M2VkNzNmMjEwMGY3MGQzMTE5ODNiMzdiY2E2KioweDhmMzNkY2VlZWRmY2Y3MTg1YWE0ODBlZTE2ZGI5YjliYjc0NTc1NmUyKjB4MTg1Y2Q0NTk3NTdhNjNlZDczZjIxMDBmNzBkMzExOTgzYjM3YmNhNjpdCgMxNTASKjB4OGYzM2RjZWVlZGZjZjcxODVhYTQ4MGVlMTZkYjliOWJiNzQ1NzU2ZRoqMHgxODVjZDQ1OTc1N2E2M2VkNzNmMjEwMGY3MGQzMTE5ODNiMzdiY2E2QK+0i8AGSK+AxtYHWiQ4OGE3MDMwMC04YzU0LTQzODctYTRkMC02Mzk3NGFiMTRlYjkSKjB4ZjM5OTAyYjEzM2ZiZGNmOTI2YzFmNDg2NjVjOThkMWIwMjhkOTA1YSIJU0VDUDI1NksxKAEahAEweDgwMGFhZjM5OWExYjA4YWJjMTM0YWY5MTFjNjIwMDhkOWMwNDIyYjcxZjQ4MjNhOTcxYTZiYTMyOTgxYWQ4ZTU2MzdhZWU0OTkxYjM3ZTYzNWRmODRkNzVmM2M1ODg3OWZiNGQ1NDdhMjNkNjAxOGE3OTM4NDA5NTUwMDgzOTBjMWMSKjB4MTg1Y2Q0NTk3NTdhNjNlZDczZjIxMDBmNzBkMzExOTgzYjM3YmNhNiIJU0VDUDI1NksxKAIahAEweDI4MDJiNDc1YTJkYzllMmEzNjRhNTMxMzFhOWY2Y2QxMDcxYmYxYjIzNmE5OWUwYWVlNzRkMzNkODFhODI1NDc2NDRmYTU3NTY4YzhiMzA0Yjc4NTA3ZmUyODAyMjIzMzNmNTBhMjRjZjQ0MDY0MmZiY2U4MjU1ZGIyNzhhMWEwMWM=";
        byte[] decodedBytes = Base64.getDecoder().decode(feeLockEnvelopeString);
        Libralink.Envelope feeLockEnvelope = Libralink.Envelope.parseFrom(decodedBytes);

        Libralink.Envelope unsignedPayerEnvelope = EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(EnvelopeContentBuilder.newBuilder()
                        .addEntity(Any.pack(feeLockEnvelope))
                        .build())
                .build();

        Libralink.Envelope signedByPayerEnvelope = SignatureHelper.sign(unsignedPayerEnvelope, PAYER_CRED, Libralink.SignatureReason.CONFIRM);
        String base64Body = Base64.getEncoder().encodeToString(signedByPayerEnvelope.toByteArray());
        LOG.info("Base64 - " + base64Body);
        LOG.info("Json - " + JsonUtils.toJson(signedByPayerEnvelope));

        when(eCheckIssueService.issue(any())).thenReturn(signedByPayerEnvelope);
        mockMvc.perform(post("/protocol/echeck/issue")
                        .contentType(MediaType.TEXT_PLAIN)
                        .accept(MediaType.TEXT_PLAIN)
                        .content(base64Body))
                .andExpect(status().isOk());
    }

    public ECheckControllerTest() throws BuilderException, BuilderException {
    }
}
