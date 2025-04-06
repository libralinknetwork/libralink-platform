package io.libralink.platform.agent.api;

import io.libralink.platform.agent.services.AgentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ECheckController {

    @Autowired
    private AgentStatusService agentStatusService;

    @GetMapping("/echeck/pre-issue")
    public String preIssue() throws Exception {
        return "OK";
    }

    @GetMapping("/echeck/issue")
    public String issue() throws Exception {
        return "OK";
    }

    @GetMapping("/echeck/deposit")
    public String deposit() throws Exception {
        return "OK";
    }
}
