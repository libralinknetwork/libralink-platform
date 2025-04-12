package io.libralink.platform.agent.api.protocol;

import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.platform.agent.services.AgentStatusService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Processors")
@RestController
public class ProcessorController {

    @Autowired
    private AgentStatusService agentStatusService;

    @PostMapping(value = "/protocol/processor/trusted", produces = "application/json")
    public Envelope getTrustedProcessors(@RequestBody Envelope envelope) throws Exception {

        return null;
    }
}
