package io.libralink.platform.agent.integration.client.rpc;

import io.libralink.platform.agent.integration.client.api.AgentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AgentClientImplRpc implements AgentClient {

    public Map<String, String> getStatus() throws Exception {
        return new HashMap<>();
    }
}
