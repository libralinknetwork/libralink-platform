package io.libralink.platform.agent.handler;

import io.libralink.client.payment.protocol.api.error.ErrorResponse;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.EnvelopeContent;
import io.libralink.client.payment.protocol.exception.BuilderException;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AgentControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AgentControllerExceptionHandler.class);

    @ExceptionHandler(AgentProtocolException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleAgentProtocolException(AgentProtocolException e) throws BuilderException {
        LOG.warn(e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .addCode(e.getCode())
            .addMessage(e.getMessage())
            .build();

        Envelope errorEnvelope = Envelope.builder()
            .addContent(EnvelopeContent.builder()
                .addEntity(errorResponse).build())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Access-Control-Allow-Origin", "*")
            .body(errorEnvelope);
    }
}
