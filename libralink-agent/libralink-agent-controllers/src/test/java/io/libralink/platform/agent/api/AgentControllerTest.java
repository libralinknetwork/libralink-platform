package io.libralink.platform.agent.api;

import io.libralink.client.payment.protocol.api.account.RegisterKeyRequest;
import io.libralink.client.payment.protocol.api.balance.GetBalanceRequest;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.signature.SignatureHelper;
import io.libralink.client.payment.util.JsonUtils;
import io.libralink.platform.agent.api.protocol.AgentController;
import io.libralink.platform.agent.services.*;
import io.libralink.platform.wallet.integration.dto.BalanceDTO;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgentController.class)
@ContextConfiguration(classes = { ApiTestConfiguration.class })
public class AgentControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(AgentControllerTest.class);

    final private String PAYER_PK = "7af8df13f6aebcbd9edd369bb5f67bf7523517685491fea776bb547910ff5673";
    final private Credentials PAYER_CRED = Credentials.create(PAYER_PK);

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

    @Test
    public void test_register_endpoint() throws Exception {

        RegisterKeyRequest request = RegisterKeyRequest.builder()
                .addAddress(PAYER_CRED.getAddress())
                .addConfirmationId("075e5892-0e32-4a0f-aeb3-e25394a51a7c")
                .addHash("dhFhkEjUVq4jI0d7BBcDWA==")
                .build();

        Envelope envelope = Envelope.builder()
                .addContent(EnvelopeContent.builder()
                        .addSigReason(SignatureReason.NONE)
                        .addEntity(request)
                        .build())
                .addId(UUID.randomUUID())
                .build();
        Envelope signedEnvelope = SignatureHelper.sign(envelope, PAYER_CRED, SignatureReason.IDENTITY);
        String body = JsonUtils.toJson(signedEnvelope);
        LOG.info(body);

        mockMvc.perform(post("/protocol/agent/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void test_account_balance_endpoint() throws Exception {

        BalanceDTO getBalanceResponse = new BalanceDTO();
        getBalanceResponse.setPubKey(PAYER_CRED.getAddress());
        getBalanceResponse.setPending(BigDecimal.valueOf(100));
        getBalanceResponse.setAvailable(BigDecimal.valueOf(50));
        getBalanceResponse.setCurrency("USDC");

        when(agentService.getBalance(anyString())).thenReturn(getBalanceResponse);

        GetBalanceRequest request = GetBalanceRequest.builder()
            .addAddress(PAYER_CRED.getAddress())
                .build();

        EnvelopeContent envelopeContent = EnvelopeContent.builder()
                .addEntity(request)
                .build();

        Envelope envelope = Envelope.builder()
                .addContent(envelopeContent)
                .addId(UUID.randomUUID())
                .build();

        Envelope signedEnvelope = SignatureHelper.sign(envelope, PAYER_CRED, SignatureReason.IDENTITY);
        String body = JsonUtils.toJson(signedEnvelope);
        LOG.info(body);

        mockMvc.perform(post("/protocol/agent/balance")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk());
    }
}
