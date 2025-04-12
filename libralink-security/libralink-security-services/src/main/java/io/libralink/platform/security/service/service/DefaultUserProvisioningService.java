package io.libralink.platform.security.service.service;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.security.service.UserProvisioningService;
import io.libralink.platform.security.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DefaultUserProvisioningService implements UserProvisioningService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserProvisioningService.class);

    @Override
    public UserDTO provisionUser(String platform, String platformUserId) throws ApplicationException {
        /* TODO */

/*
        final Tuple2<String, String> idRole = userManagementService.createUser(platform, platformUserId);
        final UUID userId = UUID.fromString(idRole.getFirst());
        final String role = idRole.getSecond();
        final UUID accountNumber = UUID.randomUUID();

        final String systemToken = TokenUtils.issueSystemToken("libralink-suite", jwk);
        paymentClient.createAccountKeyPair(userId, accountNumber, systemToken);
        walletManagementService.createDefaultAccount(userId, accountNumber);
 */

        UserDTO user = new UserDTO();
        user.setRole("USER");
        user.setUserId(UUID.randomUUID());

        return user;
    }
}
