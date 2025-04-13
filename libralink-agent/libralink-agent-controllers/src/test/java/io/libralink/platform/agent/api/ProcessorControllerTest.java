package io.libralink.platform.agent.api;

import io.libralink.client.payment.protocol.api.processor.GetProcessorsRequest;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.signature.SignatureHelper;
import io.libralink.client.payment.util.JsonUtils;
import io.libralink.platform.agent.api.protocol.ProcessorController;
import io.libralink.platform.agent.services.*;
import io.libralink.platform.common.Tuple2;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessorController.class)
@ContextConfiguration(classes = { ApiTestConfiguration.class })
public class ProcessorControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessorControllerTest.class);

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
    public void test_get_processors_endpoint() throws Exception {

        when(processorService.getTrustedProcessors()).thenReturn(List.of(Tuple2.create("trusted_proc_key", true)));

        GetProcessorsRequest request = GetProcessorsRequest.builder()
                .addAddress(PAYER_CRED.getAddress())
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

        mockMvc.perform(post("/protocol/processor/trusted")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
