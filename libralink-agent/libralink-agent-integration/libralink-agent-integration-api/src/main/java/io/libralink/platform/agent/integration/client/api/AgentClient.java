package io.libralink.platform.agent.integration.client.api;

import java.util.Map;

public interface AgentClient {

    Map<String, String> getStatus() throws Exception;
}
