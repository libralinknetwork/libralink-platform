package io.libralink.platform.agent.services;

import io.libralink.platform.common.Tuple2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import java.util.List;

@Service
public class ProcessorService {

    private final Credentials processorCredentials;

    public ProcessorService(@Value("${libralink.processor.key.private}") String processorPrivateKey) {
        processorCredentials = Credentials.create(processorPrivateKey);
    }

    public List<Tuple2<String, Boolean>> getTrustedProcessors() {

        return List.of(Tuple2.create(processorCredentials.getAddress(), true));
    }
}
