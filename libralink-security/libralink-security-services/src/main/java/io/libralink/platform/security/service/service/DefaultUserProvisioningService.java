package io.libralink.platform.security.service.service;

import io.libralink.platform.common.ApplicationException;
import io.libralink.platform.security.data.entity.User;
import io.libralink.platform.security.data.repository.UserRepository;
import io.libralink.platform.security.service.UserProvisioningService;
import io.libralink.platform.security.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

public class DefaultUserProvisioningService implements UserProvisioningService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserProvisioningService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO provisionUser(String platform, String platformUserId) throws ApplicationException {

        Optional<User> userOptional = userRepository.findByPlatformId(platform, platformUserId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserDTO(UUID.fromString(user.getId()), user.getRole());

        } else {

            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setPlatform(platform);
            user.setPlatformId(platformUserId);
            user.setRole("USER");
            user = userRepository.save(user);

            return new UserDTO(UUID.fromString(user.getId()), user.getRole());
        }
    }
}
