package io.libralink.platform.security.service;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.security.service.dto.UserDTO;

public interface UserProvisioningService {

    /**
     * Provisions user and returns JWT token of newly created user
     * @param platform e.g. github
     * @param platformUserId e.g. Gthub userId
     * @return JWT token
     */
    UserDTO provisionUser(String platform, String platformUserId) throws ApplicationException;
}
