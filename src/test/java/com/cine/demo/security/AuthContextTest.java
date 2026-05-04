package com.cine.demo.security;

import com.cine.demo.model.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthContextTest {

    @AfterEach
    void cleanup() {
        AuthContext.clear();
    }

    @Test
    void get_returnsNull_whenNothingSet() {
        assertThat(AuthContext.get()).isNull();
    }

    @Test
    void isAuthenticated_returnsFalse_whenNothingSet() {
        assertThat(AuthContext.isAuthenticated()).isFalse();
    }

    @Test
    void set_storesAuthenticatedUser_andGetReturnsIt() {
        AuthenticatedUser user = AuthenticatedUser.builder()
                .id(7L).email("u@t.com").role(Role.ADMIN).build();

        AuthContext.set(user);

        assertThat(AuthContext.get()).isSameAs(user);
        assertThat(AuthContext.isAuthenticated()).isTrue();
    }

    @Test
    void clear_removesStoredUser() {
        AuthContext.set(AuthenticatedUser.builder()
                .id(1L).email("x@t.com").role(Role.CLIENTE).build());

        AuthContext.clear();

        assertThat(AuthContext.get()).isNull();
        assertThat(AuthContext.isAuthenticated()).isFalse();
    }

    @Test
    void set_overwritesPreviousUser() {
        AuthenticatedUser first = AuthenticatedUser.builder()
                .id(1L).email("a@t.com").role(Role.CLIENTE).build();
        AuthenticatedUser second = AuthenticatedUser.builder()
                .id(2L).email("b@t.com").role(Role.ADMIN).build();

        AuthContext.set(first);
        AuthContext.set(second);

        assertThat(AuthContext.get()).isSameAs(second);
    }

    @Test
    void context_isThreadLocal_andNotSharedAcrossThreads() throws Exception {
        AuthContext.set(AuthenticatedUser.builder()
                .id(1L).email("main@t.com").role(Role.CLIENTE).build());

        AuthenticatedUser[] otherThreadValue = new AuthenticatedUser[1];
        Thread thread = new Thread(() -> otherThreadValue[0] = AuthContext.get());
        thread.start();
        thread.join();

        assertThat(otherThreadValue[0]).isNull();
        assertThat(AuthContext.get().getEmail()).isEqualTo("main@t.com");
    }
}
