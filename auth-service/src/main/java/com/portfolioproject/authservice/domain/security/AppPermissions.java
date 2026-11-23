package com.portfolioproject.authservice.domain.security;

public final class AppPermissions {
    private AppPermissions() {}

    public static final Permission KITCHEN_VIEW =  new Permission("kitchen:view") ;
    public static final Permission KITCHEN_UPDATE_STATUS = new Permission("kitchen:update-status");

    public static final Permission ORDER_CREATE =new Permission ("order:create");
    public static final Permission ORDER_CANCEL =new Permission ("order:cancel");

    public static final Permission USER_PROFILE_UPDATE =new Permission ("user:profile-update");
    public static final Permission ADMIN_MANAGE_USERS = new Permission("admin:manage-users");
}