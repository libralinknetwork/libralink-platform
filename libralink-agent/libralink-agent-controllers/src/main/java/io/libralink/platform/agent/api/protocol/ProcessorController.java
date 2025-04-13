package io.libralink.platform.agent.api.protocol;

import io.libralink.client.payment.protocol.api.processor.GetProcessorsRequest;
import io.libralink.client.payment.protocol.api.processor.GetProcessorsResponse;
import io.libralink.client.payment.protocol.api.processor.ProcessorDetails;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.agent.services.ProcessorService;
import io.libralink.platform.common.Tuple2;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = "Processors")
@RestController
public class ProcessorController {

    @Autowired
    private ProcessorService processorService;

    @PostMapping(value = "/protocol/processor/trusted", produces = "application/json")
    public Envelope getTrustedProcessors(@RequestBody Envelope envelope) throws Exception {

        final Optional<String> addressOption = EnvelopeUtils.extractEntityAttribute(envelope, GetProcessorsRequest.class, GetProcessorsRequest::getAddress);
        if (addressOption.isEmpty()) {
            throw new AgentProtocolException();
        }

        /* Verify signature */
        final String pubKey = addressOption.get();
        Optional<Envelope> signedEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, pubKey);
        if (signedEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException();
        }

        List<Tuple2<String, Boolean>> trustedProcessors = processorService.getTrustedProcessors();
        List<ProcessorDetails> processorDetails = new ArrayList<>();
        for (Tuple2<String, Boolean> detail: trustedProcessors) {
            processorDetails.add(ProcessorDetails.builder()
                        .addAddress(detail.getFirst())
                        .addDefault(detail.getSecond())
                    .build());
        }

        GetProcessorsResponse response = GetProcessorsResponse.builder()
                .addProcessors(processorDetails)
                .build();

        return Envelope.builder()
                .addContent(
                        EnvelopeContent.builder()
                                .addEntity(response)
                                .addSigReason(SignatureReason.NONE)
                                .build()
                ).build();
    }
}
