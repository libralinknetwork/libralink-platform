package io.libralink.platform.agent.api;

import io.libralink.platform.agent.services.EnvelopeService;
import io.libralink.platform.common.ApplicationException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Api(tags = "Envelopes")
@RestController
public class EnvelopeController {

    private static final Logger LOG = LoggerFactory.getLogger(EnvelopeController.class);

    @Autowired
    private EnvelopeService envelopeService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/agent/envelope/{envelopeId}/source")
    public String getEnvelope(
            @PathVariable(name = "envelopeId") UUID envelopeId,
            @AuthenticationPrincipal Principal principal) throws ApplicationException {

        return envelopeService.getEnvelopeSource(envelopeId);
    }
}
