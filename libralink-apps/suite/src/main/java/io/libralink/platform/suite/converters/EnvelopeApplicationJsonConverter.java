package io.libralink.platform.suite.converters;

import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.util.JsonUtils;
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

@Component
public class EnvelopeApplicationJsonConverter extends AbstractHttpMessageConverter<Libralink.Envelope> {

    public EnvelopeApplicationJsonConverter() {
        super(MediaType.APPLICATION_JSON);
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
        byte[] json = null;
        try {
            json = JsonUtils.toJson(envelope).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.write(json);
    }
}
