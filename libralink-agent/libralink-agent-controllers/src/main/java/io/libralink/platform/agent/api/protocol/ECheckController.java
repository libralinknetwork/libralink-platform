package io.libralink.platform.agent.api.protocol;

import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.agent.services.AgentStatusService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "E-Check")
@RestController
public class ECheckController {

    @Autowired
    private AgentStatusService agentStatusService;

    @PostMapping(value = "/protocol/echeck/pre-issue", produces = "application/json")
    public Envelope preIssue(@RequestBody Envelope envelope) throws AgentProtocolException {

        return null;
    }

    @GetMapping(value = "/protocol/echeck/issue", produces = "application/json")
    public Envelope issue(@RequestBody Envelope envelope) throws AgentProtocolException {

        return null;
    }

    @GetMapping(value = "/protocol/echeck/deposit", produces = "application/json")
    public Envelope deposit(@RequestBody Envelope envelope) throws AgentProtocolException {

        return null;
    }
}
