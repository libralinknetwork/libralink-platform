package io.libralink.platform.agent.api.protocol;

import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.api.GetProcessorResponseBuilder;
import io.libralink.client.payment.proto.builder.api.ProcessorDetailsBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.agent.services.ProcessorService;
import io.libralink.platform.common.Tuple2;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Api(tags = "Processors")
@RestController
public class ProcessorController {

    @Autowired
    private ProcessorService processorService;

    @PostMapping(value = "/protocol/processor/trusted", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public Libralink.Envelope getTrustedProcessors(@RequestBody String body) throws Exception {

        Libralink.Envelope envelope = Libralink.Envelope.parseFrom(Base64.getDecoder().decode(body.getBytes()));
        final Optional<String> addressOption = EnvelopeUtils.extractEntityAttribute(envelope, Libralink.GetProcessorsRequest.class, Libralink.GetProcessorsRequest::getAddress);
        if (addressOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Body", 999);
        }

        /* Verify signature */
        final String pubKey = addressOption.get();
        Optional<Libralink.Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, pubKey);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        List<Tuple2<String, Boolean>> trustedProcessors = processorService.getTrustedProcessors();
        List<Libralink.ProcessorDetails> processorDetails = new ArrayList<>();
        for (Tuple2<String, Boolean> detail: trustedProcessors) {
            processorDetails.add(ProcessorDetailsBuilder.newBuilder()
                        .addAddress(detail.getFirst())
                    .build());
        }

        Libralink.GetProcessorsResponse response = GetProcessorResponseBuilder.newBuilder()
                .addProcessors(processorDetails)
                .build();

        return EnvelopeBuilder.newBuilder()
                .addId(UUID.randomUUID())
                .addContent(
                        EnvelopeContentBuilder.newBuilder()
                                .addEntity(Any.pack(response))
                                .addSigReason(Libralink.SignatureReason.NONE)
                                .build()
                ).build();
    }
}
