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
            .addExpiresAt(now.plusDays(3).toEpochSecond(ZoneOffset.UTC))
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
                "    \"id\": \"084da740-4ced-487a-9123-6f631203ebc7\",\n" +
                "    \"content\": {\n" +
                "        \"entity\": {\n" +
                "            \"objectType\": \"ProcessingDetails\",\n" +
                "            \"id\": \"19056b20-d372-4daa-83e3-e03e489a574d\",\n" +
                "            \"fee\": {\n" +
                "                \"feeType\": \"percent\",\n" +
                "                \"amount\": 1\n" +
                "            },\n" +
                "            \"intermediary\": null,\n" +
                "            \"envelope\": {\n" +
                "                \"objectType\": \"Envelope\",\n" +
                "                \"id\": \"117b7769-6dab-4f03-8c11-8de6316052b2\",\n" +
                "                \"content\": {\n" +
                "                    \"entity\": {\n" +
                "                        \"objectType\": \"ECheck\",\n" +
                "                        \"id\": \"cd33fa80-7178-406a-bdd6-4dbbd7d38633\",\n" +
                "                        \"faceAmount\": 150,\n" +
                "                        \"currency\": \"USDC\",\n" +
                "                        \"payer\": \"0xf39902b133fbdcf926c1f48665c98d1b028d905a\",\n" +
                "                        \"payerProcessor\": \"0x185cd459757a63ed73f2100f70d311983b37bca6\",\n" +
                "                        \"payee\": \"0x8f33dceeedfcf7185aa480ee16db9b9bb745756e\",\n" +
                "                        \"payeeProcessor\": \"0x185cd459757a63ed73f2100f70d311983b37bca6\",\n" +
                "                        \"createdAt\": 1744559637,\n" +
                "                        \"expiresAt\": 1744818837,\n" +
                "                        \"note\": \"\"\n" +
                "                    },\n" +
                "                    \"address\": \"0xf39902b133fbdcf926c1f48665c98d1b028d905a\",\n" +
                "                    \"pubKey\": null,\n" +
                "                    \"sigReason\": \"IDENTITY\",\n" +
                "                    \"algorithm\": \"SECP256K1\"\n" +
                "                },\n" +
                "                \"sig\": \"0xf22219e35c1a8bc73a32a16f7e1ec7fc7973709446d1964d230a9d02f0e174f7157e9affea2484303c95ae2677c994b6b0a015fad6241102da4f8d227d6f04f91b\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"address\": \"0x185cd459757a63ed73f2100f70d311983b37bca6\",\n" +
                "        \"pubKey\": null,\n" +
                "        \"sigReason\": \"FEE_LOCK\",\n" +
                "        \"algorithm\": \"SECP256K1\"\n" +
                "    },\n" +
                "    \"sig\": \"0x86b874bcdd1aeb1667bcae88b85487eb3cec1c6d3de60bd1a8c381f97aa946026ec6f6a0067e488ceafe7f04d84cd2bffb00b3d30bbc3c5a064eccbe9b3324401b\"\n" +
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
