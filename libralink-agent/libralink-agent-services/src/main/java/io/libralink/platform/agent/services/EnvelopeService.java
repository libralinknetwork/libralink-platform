package io.libralink.platform.agent.services;

import io.libralink.platform.agent.data.entity.DepositApprovalEnvelope;
import io.libralink.platform.agent.data.entity.ECheckEnvelope;
import io.libralink.platform.agent.data.repository.DepositApprovalEnvelopeRepository;
import io.libralink.platform.agent.data.repository.ECheckEnvelopeRepository;
import io.libralink.platform.agent.exceptions.EnvelopeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EnvelopeService {

    @Autowired
    private ECheckEnvelopeRepository eCheckEnvelopeRepository;

    @Autowired
    private DepositApprovalEnvelopeRepository depositApprovalEnvelopeRepository;

    public String getEnvelopeSource(UUID envelopeId) throws EnvelopeNotFoundException {

        Optional<String> envelopeSourceOptional = eCheckEnvelopeRepository.findById(envelopeId.toString())
                .map(ECheckEnvelope::getSource);

        if (envelopeSourceOptional.isEmpty()) {
            envelopeSourceOptional = depositApprovalEnvelopeRepository.findById(envelopeId.toString())
                    .map(DepositApprovalEnvelope::getSource);
        }

        if (envelopeSourceOptional.isEmpty()) {
            throw new EnvelopeNotFoundException("Envelope with id " + envelopeId + " not found");
        }

        return envelopeSourceOptional.get();
    }
}
