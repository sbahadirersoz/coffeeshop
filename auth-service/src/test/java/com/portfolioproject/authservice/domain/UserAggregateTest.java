package com.portfolioproject.authservice.domain;

import com.portfolioproject.authservice.domain.aggregates.UserAggregate;
import com.portfolioproject.authservice.domain.events.UserCreatedEvent;
import com.portfolioproject.authservice.domain.security.AppPermissions;
import com.portfolioproject.authservice.domain.valueobjects.user.UserStatus;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Password;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Contract: UserAggregate.create(Username, Password, RoleType) gives an ACTIVE user with exactly that role
// and records one UserCreatedEvent (getId(), getUsername()). permissionNames() returns the role's permission strings.
class UserAggregateTest {

    private static final Password HASH = Password.fromHash("$2a$10$abcdefghijklmnopqrstuv");

    private static Username name(String value) {
        return Username.create(value).getValue();
    }

    private static UserAggregate waiter() {
        return UserAggregate.create(name("waiter01"), HASH, RoleType.WAITER);
    }

    @Test
    void create_isActive_hasOnlyGivenRole() {
        UserAggregate user = waiter();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getRoles()).containsExactly(RoleType.WAITER);
        assertThat(user.getUsername().getValue()).isEqualTo("waiter01");
        assertThat(user.getPassword()).isEqualTo(HASH);
    }

    @Test
    void create_twice_givesDifferentIds() {
        assertThat(waiter().getId().getUserID()).isNotEqualTo(waiter().getId().getUserID());
    }

    @Test
    void create_recordsOneUserCreatedEvent_withSameIdAndUsername() {
        UserAggregate user = waiter();

        assertThat(user.getDomainEvents())
                .singleElement()
                .isInstanceOfSatisfying(UserCreatedEvent.class, event -> {
                    assertThat(event.getId().getUserID()).isEqualTo(user.getId().getUserID());
                    assertThat(event.getUsername()).isEqualTo(user.getUsername());
                });
    }

    @Test
    void waiter_canCreateOrder_cannotManageUsers() {
        UserAggregate user = waiter();

        assertThat(user.hasPermission(AppPermissions.ORDER_CREATE)).isTrue();
        assertThat(user.hasPermission(AppPermissions.ADMIN_MANAGE_USERS)).isFalse();
    }

    @Test
    void permissionNames_ofWaiter_areTheRolesPermissionStrings() {
        assertThat(waiter().permissionNames())
                .containsExactlyInAnyOrder("order:create", "order:cancel", "kitchen:view");
    }

    @Test
    void permissionNames_ofAdmin_includeManageUsers() {
        UserAggregate admin = UserAggregate.create(name("admin001"), HASH, RoleType.ADMIN);

        assertThat(admin.permissionNames()).contains("admin:manage-users");
    }
}
