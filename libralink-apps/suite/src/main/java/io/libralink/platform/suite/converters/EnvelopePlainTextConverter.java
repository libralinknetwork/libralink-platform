package io.libralink.platform.suite.converters;

import io.libralink.client.payment.proto.Libralink;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EnvelopePlainTextConverter extends AbstractHttpMessageConverter<Libralink.Envelope> {

    public EnvelopePlainTextConverter() {
        super(MediaType.TEXT_PLAIN);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Libralink.Envelope.class.isAssignableFrom(clazz);
    }

    @Override
    protected Libralink.Envelope readInternal(Class<? extends Libralink.Envelope> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected void writeInternal(Libralink.Envelope envelope, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream out = httpOutputMessage.getBody();
        String encodedBody = Base64.getEncoder().encodeToString(envelope.toByteArray());
        out.write(encodedBody.getBytes(StandardCharsets.UTF_8));
    }
}
