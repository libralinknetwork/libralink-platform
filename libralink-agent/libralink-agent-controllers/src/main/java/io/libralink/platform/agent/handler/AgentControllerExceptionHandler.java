package io.libralink.platform.agent.handler;

import com.google.protobuf.Any;
import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.proto.builder.api.ErrorResponseBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeBuilder;
import io.libralink.client.payment.proto.builder.envelope.EnvelopeContentBuilder;
import io.libralink.client.payment.proto.builder.exception.BuilderException;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class AgentControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AgentControllerExceptionHandler.class);

    @ExceptionHandler(AgentProtocolException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Libralink.Envelope> handleAgentProtocolException(AgentProtocolException e) throws BuilderException {
        LOG.warn(e.getMessage());

        Libralink.ErrorResponse errorResponse = ErrorResponseBuilder.newBuilder()
            .addCode(e.getCode())
            .addMessage(e.getMessage())
            .build();

        Libralink.Envelope errorEnvelope = EnvelopeBuilder.newBuilder()
            .addId(UUID.randomUUID())
            .addContent(EnvelopeContentBuilder.newBuilder()
                .addEntity(Any.pack(errorResponse)).build())
            .build();


        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Access-Control-Allow-Origin", "*")
            .body(errorEnvelope);
    }
}
