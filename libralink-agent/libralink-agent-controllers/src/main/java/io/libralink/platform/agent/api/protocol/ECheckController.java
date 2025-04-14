package io.libralink.platform.agent.api.protocol;

import io.libralink.client.payment.protocol.api.echeck.DepositRequest;
import io.libralink.client.payment.protocol.echeck.ECheck;
import io.libralink.client.payment.protocol.envelope.Envelope;
import io.libralink.client.payment.protocol.envelope.SignatureReason;
import io.libralink.client.payment.util.EnvelopeUtils;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.agent.services.ECheckDepositService;
import io.libralink.platform.agent.services.ECheckIssueService;
import io.libralink.platform.agent.services.ProcessorFeeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
     * @param envelope envelope with E-Check details, signed either by Payer or Payee
     * @return envelope with ProcessingDetails (one or multiple enclosed), signed by processor(s)
     *
     * @throws AgentProtocolException signifies bad request
     */
    @PostMapping(value = "/protocol/echeck/pre-issue", produces = "application/json")
    public Envelope preIssue(@RequestBody Envelope envelope) throws Exception {

        /* Find Payer Signature */
        Optional<ECheck> eCheckOption = EnvelopeUtils.findEntityByType(envelope, ECheck.class)
                .map(eCheck -> (ECheck) eCheck);
        if (eCheckOption.isEmpty()) {
            /* No E-Check details */
            throw new AgentProtocolException("Invalid Body", 999);
        }

        /* Verify E-Check is signed by either Payer or Payee */
        ECheck eCheck  = eCheckOption.get();
        Optional<Envelope> payerIdentityEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, eCheck.getPayer())
                .filter(env -> SignatureReason.IDENTITY.equals(env.getContent().getSigReason()));
        Optional<Envelope> payeeIdentityEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, eCheck.getPayee())
                .filter(env -> SignatureReason.IDENTITY.equals(env.getContent().getSigReason()));

        if (payeeIdentityEnvelopeOption.isEmpty() && payerIdentityEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        return processorFeeService.preProcess(envelope);
    }

    /**
     * The purpose of `/issue` endpoint is to receive the E-Check confirmed by Payer,
     * lock Payer funds and return with Processor's CONFIRM signature, to signify the E-Check being issued
     *
     * @param envelope envelope confirmed by Payer
     * @return envelope confirmed by Processor
     *
     * @throws AgentProtocolException signifies bad request
     */
    @PostMapping(value = "/protocol/echeck/issue", produces = "application/json")
    public Envelope issue(@RequestBody Envelope envelope) throws Exception {

        /* Get E-Check details */
        Optional<ECheck> eCheckOption = EnvelopeUtils.findEntityByType(envelope, ECheck.class)
                .map(eCheck -> (ECheck) eCheck);
        if (eCheckOption.isEmpty()) {
            /* No E-Check details */
            throw new AgentProtocolException("Invalid Body", 999);
        }
        ECheck eCheck  = eCheckOption.get();

        /* Find Envelope with Payer's CONFIRM sgnature */
        Optional<Envelope> payerConfirmEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, eCheck.getPayer())
                .filter(env -> SignatureReason.CONFIRM.equals(env.getContent().getSigReason()));
        if (payerConfirmEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }
        Envelope payerConfirmEnvelope = payerConfirmEnvelopeOption.get();

        /* Within Payer's Envelope, find Processor's FEE_LOCK signature,
        * so far we don't differentiate between Payer's and Payee's processor */
        Optional<Envelope> feeLockEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(payerConfirmEnvelope, eCheck.getPayerProcessor())
                .filter(env -> SignatureReason.FEE_LOCK.equals(env.getContent().getSigReason()));
        if (feeLockEnvelopeOption.isEmpty()) {
            throw new AgentProtocolException("Invalid Signature", 999);
        }

        return eCheckIssueService.issue(envelope);
    }

    @PostMapping(value = "/protocol/echeck/deposit", produces = "application/json")
    public Envelope deposit(@RequestBody Envelope envelope) throws Exception {

        /* Get DepositRequest */
        Optional<DepositRequest> depositRequestOption = EnvelopeUtils.findEntityByType(envelope, DepositRequest.class)
                .map(r -> (DepositRequest) r);
        if (depositRequestOption.isEmpty()) {
            /* No Deposit Request as part of Envelope */
            throw new AgentProtocolException("Invalid Body", 999);
        }
        DepositRequest depositRequest = depositRequestOption.get();

        Envelope eCheckEnvelope = (Envelope) depositRequest.getCheck();
        List<Envelope> depositApprovals = depositRequest.getDepositApprovals()
                .stream().map(approval -> (Envelope) approval).collect(Collectors.toList());

        Optional<ECheck> eCheckOption = EnvelopeUtils.findEntityByType(eCheckEnvelope, ECheck.class)
                .map(eCheck -> (ECheck) eCheck);
        if (eCheckOption.isEmpty()) {
            /* No E-Check details */
            throw new AgentProtocolException("Invalid Body", 999);
        }
        ECheck eCheck  = eCheckOption.get();

        /* Verify Payee signature */
        Optional<Envelope> payeeIdentityEnvelopeOption = EnvelopeUtils.findSignedEnvelopeByPub(envelope, eCheck.getPayee())
                .filter(env -> SignatureReason.IDENTITY.equals(env.getContent().getSigReason()));

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
