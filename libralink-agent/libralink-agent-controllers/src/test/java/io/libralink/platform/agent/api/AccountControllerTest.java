package io.libralink.platform.agent.api;

import io.libralink.client.payment.protocol.api.balance.GetBalanceRequest;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.signature.SignatureHelper;
import io.libralink.client.payment.util.JsonUtils;
import io.libralink.platform.agent.services.AgentStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.web3j.crypto.Credentials;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@ContextConfiguration(classes = TestApplicationConfig.class)
public class AccountControllerTest {

    final private String PAYER_PK = "7af8df13f6aebcbd9edd369bb5f67bf7523517685491fea776bb547910ff5673";
    final private Credentials PAYER_CRED = Credentials.create(PAYER_PK);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentStatusService agentStatusService;

    @Test
    public void test_register_endpoint() throws Exception {
//        when(myService.getData()).thenReturn("test data");

        mockMvc.perform(get("/account/register"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    public void test_account_balance_endpoint() throws Exception {

        GetBalanceRequest request = GetBalanceRequest.builder()
            .addPub(PAYER_CRED.getAddress())
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

        mockMvc.perform(post("/account/balance")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk());
    }
}
