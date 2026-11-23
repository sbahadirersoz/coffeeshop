package com.portfolioproject.authservice.domain.aggregates;

import com.portfolioproject.authservice.domain.events.NewUserAggregateCreatedEvent;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Password;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.authservice.domain.valueobjects.user.UserID;
import com.portfolioproject.authservice.domain.valueobjects.user.UserStatus;
import com.portfolioproject.commonshared.Abstracts.AggregateRoot;
import com.portfolioproject.authservice.domain.security.Permission;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserAggregate extends AggregateRoot<UserID>
{
    private Password password;
    private Username username;
    private UserStatus status;

    private Set<RoleType> roles =  new HashSet<>();
    private UserAggregate() {}



    public boolean hasPermission(Permission perm)
    {
        return roles.stream().anyMatch(t -> t.getPermissions().contains(perm));
    }

    private UserAggregate(UserID id, Username username, Password password, UserStatus status, Set<RoleType> roles) {
        super(id);
        this.password = password;
        this.username = username;
        this.status = status;
        this.roles = roles;
    }

    private UserAggregate(Password password, Username username, Set<RoleType>  roles) {
        this.password = password;
        this.username = username;
        this.status = UserStatus.ACTIVE;
        this.roles =roles!= null ?  new HashSet<>(roles):new HashSet<>();
    }
    private UserAggregate(UserID id, Username username, Password password) {
        super(id);
        this.password = password;
        this.username = username;
        this.status = UserStatus.ACTIVE;

    }
    public static UserAggregate create(Password password, Username username)
    {
        UserID id = UserID.create();
        UserAggregate createdUnit = new UserAggregate(id,  username,password);
        NewUserAggregateCreatedEvent.create(id ,username);
        return createdUnit ;
    }


    public static UserAggregate reconstitute
            (
                    UserID id, Username username,
                    Password password,UserStatus status,
                    Set<RoleType> roles
            ) {
        return new UserAggregate(id, username, password, status, roles);
    }

    public void assignRole(RoleType role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }


    private void setPassword(Password password) {
        this.password = password;
    }
    private void setUsername(Username username) {
        this.username = username;
    }
    public Password getPassword() {
        return password;
    }
    public Username getUsername() {
        return username;
    }
    public UserStatus getStatus() {return status;}

    public Set<RoleType> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

}
