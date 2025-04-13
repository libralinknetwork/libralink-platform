package io.libralink.platform.wallet.api;

import io.libralink.client.payment.util.JsonUtils;
import io.libralink.platform.wallet.integration.dto.IntegrationECheckDTO;
import io.libralink.platform.wallet.services.AccountService;
import io.libralink.platform.wallet.services.ECheckService;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemController.class)
@ContextConfiguration(classes = { ApiTestConfiguration.class })
public class SystemControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(SystemControllerTest.class);

    final private String PAYER_PK = "f42333764f7e562614b0e3f27d3febc9ba77c9b12b82da764bf7f35c94519db8";
    final private Credentials PAYER_CRED = Credentials.create(PAYER_PK);

    final private String PAYEE_PK = "ae1a8be99e38f6b0ad5edce77beb484dfba31bf3581a9d1c67d4a1b8c5121e7e";
    final private Credentials PAYEE_CRED = Credentials.create(PAYEE_PK);

    final private String PROCESSOR_PK = "694f2d4315adf7844d999d8e3a9a9fb5222344ff7742a07fad5861db140c2de";
    final private Credentials PROCESSOR_CRED = Credentials.create(PROCESSOR_PK);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ECheckService eCheckIssueService;

    @MockBean
    private AccountService accountService;

    @Test
    public void test_issue_e_check() throws Exception {

        IntegrationECheckDTO dto = new IntegrationECheckDTO();
        dto.setEnvelopeId(UUID.randomUUID().toString());
        dto.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        dto.setExpiresAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        dto.setFaceAmount(BigDecimal.valueOf(50));
        dto.setCurrency("USDC");
        dto.setNote("note");
        dto.setPayee(PAYEE_CRED.getAddress());
        dto.setPayeeProcessor(PROCESSOR_CRED.getAddress());
        dto.setPayer(PAYER_CRED.getAddress());
        dto.setPayerProcessor(PROCESSOR_CRED.getAddress());

        String body = JsonUtils.toJson(dto);

        mockMvc.perform(post("/internal/wallet/issue-e-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
