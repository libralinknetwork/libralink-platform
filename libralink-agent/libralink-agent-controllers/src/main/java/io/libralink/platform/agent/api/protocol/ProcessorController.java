package io.libralink.platform.agent.api.protocol;

import io.libralink.platform.agent.services.AgentStatusService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Processors")
@RestController
public class ProcessorController {

    @Autowired
    private AgentStatusService agentStatusService;

    @GetMapping("/protocol/processor/trusted")
    public String getTrustedProcessors() throws Exception {
        return "OK";
    }
}
