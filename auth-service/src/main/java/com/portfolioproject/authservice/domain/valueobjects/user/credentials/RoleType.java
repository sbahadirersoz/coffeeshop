package com.portfolioproject.authservice.domain.valueobjects.user.credentials;

import com.portfolioproject.authservice.domain.security.AppPermissions;
import com.portfolioproject.authservice.domain.security.Permission;

import java.util.Set;

public enum RoleType {

    KITCHEN_STAFF(Set.of(
            AppPermissions.KITCHEN_VIEW,
            AppPermissions.KITCHEN_UPDATE_STATUS
    )),

    WAITER(Set.of(
            AppPermissions.ORDER_CREATE,
            AppPermissions.ORDER_CANCEL,
            AppPermissions.KITCHEN_VIEW
    )),

    ADMIN(Set.of(
            AppPermissions.KITCHEN_VIEW,
            AppPermissions.KITCHEN_UPDATE_STATUS,
            AppPermissions.ORDER_CREATE,
            AppPermissions.ORDER_CANCEL,
            AppPermissions.USER_PROFILE_UPDATE,
            AppPermissions.ADMIN_MANAGE_USERS
    ));

    private final Set<Permission> permissions;

    RoleType(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}