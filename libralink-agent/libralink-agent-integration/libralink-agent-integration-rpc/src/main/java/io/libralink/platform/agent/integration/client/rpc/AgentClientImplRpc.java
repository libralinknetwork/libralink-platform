package io.libralink.platform.agent.integration.client.rpc;

import io.libralink.platform.agent.integration.client.api.AgentClient;
import io.libralink.platform.agent.services.AgentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AgentClientImplRpc implements AgentClient {

    @Autowired
    private AgentStatusService agentStatusService;


    public Map<String, String> getStatus() throws Exception {
        return agentStatusService.getStatus();
    }
}
