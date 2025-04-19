package io.libralink.platform.agent.services;

import io.libralink.platform.agent.data.entity.Agent;
import io.libralink.platform.agent.data.entity.AgentRegisterConfirm;
import io.libralink.platform.agent.data.enums.ChallengeStatus;
import io.libralink.platform.agent.data.repository.AgentRegisterChallengeRepository;
import io.libralink.platform.agent.data.repository.AgentRepository;
import io.libralink.platform.agent.exceptions.AgentProtocolException;
import io.libralink.platform.security.service.TokenService;
import io.libralink.platform.wallet.integration.api.WalletClient;
import io.libralink.platform.wallet.integration.dto.BalanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static io.libralink.platform.agent.utils.ConfirmationHashUtils.getHash;

@Service
public class AgentService {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private WalletClient walletClient;
    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgentRegisterChallengeRepository challengeRepository;

    public void registerAgent(String address, String publicKey, String algorithm, String confirmationId, String hash) throws Exception {

        Optional<Agent> agentOptional = agentRepository.findByAddress(address);
        if (agentOptional.isPresent()) {
            throw new AgentProtocolException("Agent with address " + address + " already registered", 999);
        }

        Optional<AgentRegisterConfirm> challengeOptional = challengeRepository.findById(confirmationId);
        if (challengeOptional.isEmpty()) {
            throw new AgentProtocolException("Invalid Confirmation Details", 999);
        }

        /* Calculate & Compare Hash */
        AgentRegisterConfirm challenge = challengeOptional.get();
        String expectedHash = getHash(challenge.getId(), challenge.getExpiresAt(), challenge.getSalt(), challenge.getAccountId());
        if (!expectedHash.equals(hash)) {
            throw new AgentProtocolException("Invalid Confirmation Details", 999);
        }

        final String agentId = UUID.randomUUID().toString();

        challenge.setStatus(ChallengeStatus.REGISTERED);
        challenge.setUsedByAgentId(agentId);

        Agent agent = new Agent();
        agent.setId(agentId);
        agent.setEnabled(Boolean.TRUE);
        agent.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        agent.setAddress(address);
        agent.setAlgorithm(algorithm != null ? algorithm.toString(): null);
        agent.setPublicKey(publicKey);
        agent.setAccountId(challenge.getAccountId());
        agentRepository.save(agent);
    }

    public BalanceDTO getBalance(String pubKey) throws Exception {
        return walletClient.getBalance(pubKey, tokenService.issueSystemToken());
    }
}
