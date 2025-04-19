package io.libralink.platform.agent.api.protocol;

import io.libralink.client.payment.proto.Libralink;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.agent.services.ECheckDepositService;
import io.libralink.platform.agent.services.ECheckIssueService;
import io.libralink.platform.agent.services.ProcessorFeeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = "E-Check")
@RestController
public class ECheckController {

    @Autowired
    private ProcessorFeeService processorFeeService;

    @Autowired
    private ECheckIssueService eCheckIssueService;

    @Autowired
    private ECheckDepositService eCheckDepositService;

    /**
     * The purpose of `/pre-issue` endpoint is to receive the E-Check with Payer or Payee IDENTITY signature,
     * enrich it with Fee details from all the intermediaries (processors) and return it back for Payer comfirmation
     *
     * @param body envelope with E-Check details, signed either by Payer or Payee
     * @return envelope with ProcessingDetails (one or multiple enclosed), signed by processor(s)
     *
     * @throws AgentProtocolException signifies bad request
     */
    @PostMapping(value = "/protocol/echeck/pre-issue", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public Libralink.Envelope preIssue(@RequestBody String body) throws Exception {

        Libralink.Envelope envelope = Libralink.Envelope.parseFrom(Base64.getDecoder().decode(body.getBytes()));

        /* Find Payer Signature */
        Optional<Libralink.ECheck> eCheckOption = EnvelopeUtils.findEntityByType(envelope, Libralink.ECheck.class);
        if (eCheckOption.isEmpty()) {
            /* No E-Check details */
            throw new AgentProtocolException("Invalid Body", 999);
        }

        /* Verify E-Check is signed by either Payer or Payee */
        Libralink.ECheck eCheck  = eCheckOption.get();
        Optional<Libralink.Envelope> payerIdentityEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, eCheck.getFrom())
                .filter(env -> Libralink.SignatureReason.IDENTITY.equals(env.getContent().getReason()));
        Optional<Libralink.Envelope> payeeIdentityEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, eCheck.getTo())
                .filter(env -> Libralink.SignatureReason.IDENTITY.equals(env.getContent().getReason()));

        if (payeeIdentityEnvelopeOption.isEmpty() && payerIdentityEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        return processorFeeService.preProcess(envelope);
    }

    /**
     * The purpose of `/issue` endpoint is to receive the E-Check confirmed by Payer,
     * lock Payer funds and return with Processor's CONFIRM signature, to signify the E-Check being issued
     *
     * @param body envelope confirmed by Payer
     * @return envelope confirmed by Processor
     *
     * @throws AgentProtocolException signifies bad request
     */
    @PostMapping(value = "/protocol/echeck/issue", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public Libralink.Envelope issue(@RequestBody String body) throws Exception {

        Libralink.Envelope envelope = Libralink.Envelope.parseFrom(Base64.getDecoder().decode(body.getBytes()));
        /* Get E-Check details */
        Optional<Libralink.ECheck> eCheckOption = EnvelopeUtils.findEntityByType(envelope, Libralink.ECheck.class);
        if (eCheckOption.isEmpty()) {
            /* No E-Check details */
            throw new AgentProtocolException("Invalid Body", 999);
        }
        Libralink.ECheck eCheck  = eCheckOption.get();

        /* Find Envelope with Payer's CONFIRM signature */
        Optional<Libralink.Envelope> payerConfirmEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, eCheck.getFrom())
                .filter(env -> Libralink.SignatureReason.CONFIRM.equals(env.getContent().getReason()));
        if (payerConfirmEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }
        Libralink.Envelope payerConfirmEnvelope = payerConfirmEnvelopeOption.get();

        /* Within Payer's Envelope, find Processor's FEE_LOCK signature,
        * so far we don't differentiate between Payer's and Payee's processor */
        Optional<Libralink.Envelope> feeLockEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(payerConfirmEnvelope, eCheck.getFromProc())
                .filter(env -> Libralink.SignatureReason.FEE_LOCK.equals(env.getContent().getReason()));
        if (feeLockEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        return eCheckIssueService.issue(envelope);
    }

    @PostMapping(value = "/protocol/echeck/deposit", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public Libralink.Envelope deposit(@RequestBody String body) throws Exception {

        Libralink.Envelope envelope = Libralink.Envelope.parseFrom(Base64.getDecoder().decode(body.getBytes()));
        /* Get DepositRequest */
        Optional<Libralink.DepositRequest> depositRequestOption = EnvelopeUtils.findEntityByType(envelope, Libralink.DepositRequest.class);
        if (depositRequestOption.isEmpty()) {
            /* No Deposit Request as part of Envelope */
            throw new AgentProtocolException("Invalid Body", 999);
        }
        Libralink.DepositRequest depositRequest = depositRequestOption.get();

        Libralink.Envelope eCheckEnvelope = depositRequest.getCheckEnvelope();
        List<Libralink.Envelope> requestEnvelopes = depositRequest.getRequestEnvelopesList();

        Optional<Libralink.ECheck> eCheckOption = EnvelopeUtils.findEntityByType(eCheckEnvelope, Libralink.ECheck.class);
        if (eCheckOption.isEmpty()) {
            /* No E-Check details */
            throw new AgentProtocolException("Invalid Body", 999);
        }
        Libralink.ECheck eCheck  = eCheckOption.get();

        /* Verify Payee signature */
        Optional<Libralink.Envelope> payeeIdentityEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, eCheck.getTo())
                .filter(env -> Libralink.SignatureReason.IDENTITY.equals(env.getContent().getReason()));

        if (payeeIdentityEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        /* TODO: DepositRequest signature */
        /* TODO: Verify E-Check, plus signatures */
        /* TODO: Verify each DepositApproval, Payer & Payee signatures */

        /* TODO: Perform deposit action */

        return eCheckDepositService.deposit(envelope);
    }
}
