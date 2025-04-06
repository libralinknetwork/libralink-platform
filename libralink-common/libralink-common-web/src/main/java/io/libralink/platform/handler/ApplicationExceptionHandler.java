package io.libralink.platform.handler;

import io.libralink.platform.common.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleApplicationException(ApplicationException e) {
        LOG.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Access-Control-Allow-Origin", "*")
            .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}
