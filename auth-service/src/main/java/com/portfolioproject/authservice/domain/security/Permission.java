package com.portfolioproject.authservice.domain.security;

public  final class Permission
{
    public String permissionInfo;
    public Permission(String permissionInfo) {
        this.permissionInfo = permissionInfo;
    }

    public String getPermissionInfo() {
        return permissionInfo;
    }
}
