package io.libralink.platform.agent.api;

import io.libralink.client.payment.protocol.echeck.ECheck;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.protocol.exception.BuilderException;
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

    private ECheck eCheck = ECheck.builder()
            .addPayer(PAYER_CRED.getAddress())
            .addPayerProcessor(PROCESSOR_CRED.getAddress())
            .addPayee(PAYEE_CRED.getAddress())
            .addPayeeProcessor(PROCESSOR_CRED.getAddress())
            .addCurrency("USDC")
            .addFaceAmount(BigDecimal.valueOf(150))
            .addCreatedAt(now.toEpochSecond(ZoneOffset.UTC))
            .addExpiresAt(now.plusYears(10).toEpochSecond(ZoneOffset.UTC))
            .addNote("")
            .build();

    private Envelope unsignedEnvelope = Envelope.builder()
            .addContent(EnvelopeContent.builder()
                    .addEntity(eCheck)
                    .build())
            .build();

    @Test
    public void test_pre_process_echeck_signed_by_payer() throws Exception {

        when(processorFeeService.preProcess(any())).thenReturn(unsignedEnvelope);
        Envelope signedEnvelope = SignatureHelper.sign(unsignedEnvelope, PAYER_CRED, SignatureReason.IDENTITY);
        String body = JsonUtils.toJson(signedEnvelope);
        LOG.info(body);

        mockMvc.perform(post("/protocol/echeck/pre-issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void test_pre_process_echeck_signed_by_payee() throws Exception {

        when(processorFeeService.preProcess(any())).thenReturn(unsignedEnvelope);
        Envelope signedEnvelope = SignatureHelper.sign(unsignedEnvelope, PAYEE_CRED, SignatureReason.IDENTITY);
        String body = JsonUtils.toJson(signedEnvelope);

        mockMvc.perform(post("/protocol/echeck/pre-issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void test_issue_echeck_signed_by_payer() throws Exception {

        final String feeLockEnvelopeString = "{\n" +
                "    \"objectType\": \"Envelope\",\n" +
                "    \"id\": \"66d56a6b-a4f0-44c1-80fe-64393f76adb0\",\n" +
                "    \"content\": {\n" +
                "        \"entity\": {\n" +
                "            \"objectType\": \"ProcessingDetails\",\n" +
                "            \"id\": \"de927e67-697b-4514-8264-55d15b749897\",\n" +
                "            \"fee\": {\n" +
                "                \"feeType\": \"percent\",\n" +
                "                \"amount\": 1\n" +
                "            },\n" +
                "            \"intermediary\": null,\n" +
                "            \"envelope\": {\n" +
                "                \"objectType\": \"Envelope\",\n" +
                "                \"id\": \"ebf5a251-f93d-4340-9084-a02b9edfe2ec\",\n" +
                "                \"content\": {\n" +
                "                    \"entity\": {\n" +
                "                        \"objectType\": \"ECheck\",\n" +
                "                        \"id\": \"3819fd08-cc61-4da5-b23e-2011ab7563b7\",\n" +
                "                        \"faceAmount\": 150,\n" +
                "                        \"currency\": \"USDC\",\n" +
                "                        \"payer\": \"0xf39902b133fbdcf926c1f48665c98d1b028d905a\",\n" +
                "                        \"payerProcessor\": \"0x185cd459757a63ed73f2100f70d311983b37bca6\",\n" +
                "                        \"payee\": \"0x8f33dceeedfcf7185aa480ee16db9b9bb745756e\",\n" +
                "                        \"payeeProcessor\": \"0x185cd459757a63ed73f2100f70d311983b37bca6\",\n" +
                "                        \"createdAt\": 1744830279,\n" +
                "                        \"expiresAt\": 2060363079,\n" +
                "                        \"note\": \"\"\n" +
                "                    },\n" +
                "                    \"address\": \"0xf39902b133fbdcf926c1f48665c98d1b028d905a\",\n" +
                "                    \"pubKey\": null,\n" +
                "                    \"sigReason\": \"IDENTITY\",\n" +
                "                    \"algorithm\": \"SECP256K1\"\n" +
                "                },\n" +
                "                \"sig\": \"0x6068effd7c8444fd5f4d58c5dc7caa51ae6501012497d26e04be057c7a0fad473e9f7f4f6fc5d90afbd24281da942eaa77d5febc08c658953c304126b5568e091c\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"address\": \"0x185cd459757a63ed73f2100f70d311983b37bca6\",\n" +
                "        \"pubKey\": null,\n" +
                "        \"sigReason\": \"FEE_LOCK\",\n" +
                "        \"algorithm\": \"SECP256K1\"\n" +
                "    },\n" +
                "    \"sig\": \"0x8c4296512e06d07e773c5f460cc9b2543c8826fb3ab40d9b59eb81ed0709eec67abd104205deff857e444c0d3878ae67c7f888b4167cbb1ef22aaed0e98c57201b\"\n" +
                "}";

        Envelope feeLockEnvelope = JsonUtils.fromJson(feeLockEnvelopeString, Envelope.class);

        Envelope unsignedPayerEnvelope = Envelope.builder()
                .addContent(EnvelopeContent.builder()
                        .addEntity(feeLockEnvelope)
                        .build())
                .build();

        Envelope signedByPayerEnvelope = SignatureHelper.sign(unsignedPayerEnvelope, PAYER_CRED, SignatureReason.CONFIRM);
        String body = JsonUtils.toJson(signedByPayerEnvelope);
        LOG.info(body);

        when(eCheckIssueService.issue(any())).thenReturn(signedByPayerEnvelope);
        mockMvc.perform(post("/protocol/echeck/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    public ECheckControllerTest() throws BuilderException {
    }
}
