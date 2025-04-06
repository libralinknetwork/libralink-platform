package io.libralink.platform.agent.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AgentStatusService {

    public Map<String, String> getStatus() throws Exception {

        Map<String, String> response = new HashMap<>();
        response.put("Agent", "OK");
        return response;
    }
}
