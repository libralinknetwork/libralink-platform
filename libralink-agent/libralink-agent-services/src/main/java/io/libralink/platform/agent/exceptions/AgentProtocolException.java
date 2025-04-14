package io.libralink.platform.agent.exceptions;


public class AgentProtocolException extends Exception {

    private final int code;

    public AgentProtocolException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
