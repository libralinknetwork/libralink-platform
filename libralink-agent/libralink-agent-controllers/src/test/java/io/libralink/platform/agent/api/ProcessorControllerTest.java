package io.libralink.platform.agent.api;

import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.api.GetProcessorRequestBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
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

import java.util.Base64;
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

        Libralink.GetProcessorsRequest request = GetProcessorRequestBuilder.newBuilder()
                .addAddress(PAYER_CRED.getAddress())
                .build();

        Libralink.Envelope envelope = EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(EnvelopeContentBuilder.newBuilder()
                        .addSigReason(Libralink.SignatureReason.NONE)
                        .addEntity(Any.pack(request))
                        .build())
                .addId(UUID.randomUUID())
                .build();
        Libralink.Envelope signedEnvelope = SignatureHelper.sign(envelope, PAYER_CRED, Libralink.SignatureReason.IDENTITY);
        String base64Body = Base64.getEncoder().encodeToString(signedEnvelope.toByteArray());
        LOG.info("Base64 - " + base64Body);
        LOG.info("Json - " + JsonUtils.toJson(signedEnvelope));

        mockMvc.perform(post("/protocol/processor/trusted")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(base64Body))
                .andExpect(status().isOk());
    }
}
