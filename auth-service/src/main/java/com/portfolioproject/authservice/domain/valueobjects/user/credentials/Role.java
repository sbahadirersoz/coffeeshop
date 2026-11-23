package com.portfolioproject.authservice.domain.valueobjects.user.credentials;


import java.util.Collections;
import java.util.Set;

public class Role {
    private final String roleName;
    private final Set<String> permissions;
    public Role(String roleName, Set<String> permissions) {
        this.roleName = roleName;
        this.permissions = permissions != null ? permissions : Collections.emptySet();
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    // İhtiyaç olduğunda bu rolün belirli bir izne sahip olup olmadığını kontrol edebilirsin
    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }
}