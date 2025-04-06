package io.libralink.platform.agent.api;

import io.libralink.platform.agent.services.AgentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessorController {

    @Autowired
    private AgentStatusService agentStatusService;

    @GetMapping("/processor/trusted")
    public String getTrustedProcessors() throws Exception {
        return "OK";
    }
}
