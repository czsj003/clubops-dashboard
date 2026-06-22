package com.clubops.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAccountTypeTests {

    @Test
    void newUsersDefaultToNormal() {
        User user = User.builder().build();

        user.prePersist();

        assertThat(user.getAccountType()).isEqualTo(UserAccountType.NORMAL);
    }

    @Test
    void existingVipTypeIsPreserved() {
        User user = User.builder()
                .accountType(UserAccountType.VIP)
                .build();

        user.prePersist();

        assertThat(user.getAccountType()).isEqualTo(UserAccountType.VIP);
    }
}
