package io.libralink.platform.suite.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RBACAccessController {

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/suite/admin/endpoint")
    public String getAdminData() {
        return "Success";
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/suite/user/endpoint")
    public String getUserData() {
        return "Success";
    }
}
