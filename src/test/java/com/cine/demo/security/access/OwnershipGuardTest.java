package com.cine.demo.security.access;

import com.cine.demo.model.Purchase;
import com.cine.demo.model.User;
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Comprobaciones de propiedad.
 *
 * Esta clase responde a "es tuyo", que es lo que el rol no puede responder:
 * todos los clientes son CLIENT, asi que sin esto uno podria leer y modificar
 * la ficha o las compras de cualquier otro cambiando el id de la URL. En la
 * matriz de autorizacion va mockeada, de modo que necesita sus propios tests.
 */
class OwnershipGuardTest {

    private PurchaseRepository purchaseRepository;
    private OwnershipGuard guard;

    @BeforeEach
    void setUp() {
        purchaseRepository = mock(PurchaseRepository.class);
        guard = new OwnershipGuard(purchaseRepository);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private static void authenticateAs(Long userId) {
        AuthenticatedUser user = AuthenticatedUser.builder()
                .id(userId).email("ana@test.com").role("CLIENT").build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, List.of(new SimpleGrantedAuthority("CLIENT"))));
    }

    // --- isSelf ---------------------------------------------------------------

    @Test
    void isSelf_trueForOwnId() {
        authenticateAs(5L);

        assertThat(guard.isSelf(5L)).isTrue();
    }

    @Test
    void isSelf_falseForSomeoneElsesId() {
        authenticateAs(5L);

        assertThat(guard.isSelf(99L)).isFalse();
    }

    @Test
    void isSelf_falseWithoutSession() {
        assertThat(guard.isSelf(5L)).isFalse();
    }

    @Test
    void isSelf_falseForNullId() {
        authenticateAs(5L);

        assertThat(guard.isSelf(null)).isFalse();
    }

    /**
     * Con un usuario simulado el principal es una cadena, no un
     * AuthenticatedUser. Lo correcto entonces es denegar, no romper la
     * peticion con una excepcion de casteo.
     */
    @Test
    void isSelf_falseWhenPrincipalIsNotAnAuthenticatedUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "ana@test.com", null, List.of(new SimpleGrantedAuthority("CLIENT"))));

        assertThat(guard.isSelf(5L)).isFalse();
    }

    @Test
    void isSelf_falseForAnonymousAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken(
                        "key", "anonymous", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        assertThat(guard.isSelf(5L)).isFalse();
    }

    // --- ownsPurchase ---------------------------------------------------------

    private static Purchase purchaseOf(Long ownerId) {
        return Purchase.builder()
                .id(42L)
                .user(ownerId == null ? null : User.builder().id(ownerId).build())
                .build();
    }

    @Test
    void ownsPurchase_trueForOwnPurchase() {
        authenticateAs(5L);
        when(purchaseRepository.findById(42L)).thenReturn(Optional.of(purchaseOf(5L)));

        assertThat(guard.ownsPurchase(42L)).isTrue();
    }

    @Test
    void ownsPurchase_falseForSomeoneElsesPurchase() {
        authenticateAs(5L);
        when(purchaseRepository.findById(42L)).thenReturn(Optional.of(purchaseOf(99L)));

        assertThat(guard.ownsPurchase(42L)).isFalse();
    }

    /** Una compra inexistente no se confirma como ajena ni como propia. */
    @Test
    void ownsPurchase_falseWhenThePurchaseDoesNotExist() {
        authenticateAs(5L);
        when(purchaseRepository.findById(42L)).thenReturn(Optional.empty());

        assertThat(guard.ownsPurchase(42L)).isFalse();
    }

    /** Una compra de invitado, sin usuario asociado, no es de nadie. */
    @Test
    void ownsPurchase_falseForAGuestPurchase() {
        authenticateAs(5L);
        when(purchaseRepository.findById(42L)).thenReturn(Optional.of(purchaseOf(null)));

        assertThat(guard.ownsPurchase(42L)).isFalse();
    }

    @Test
    void ownsPurchase_falseWithoutSession() {
        assertThat(guard.ownsPurchase(42L)).isFalse();
    }

    @Test
    void ownsPurchase_falseForNullId() {
        authenticateAs(5L);

        assertThat(guard.ownsPurchase(null)).isFalse();
    }

    /** No debe consultar la base de datos si no hay sesion que comparar. */
    @Test
    void ownsPurchase_doesNotQueryTheDatabaseWithoutSession() {
        guard.ownsPurchase(42L);

        org.mockito.Mockito.verifyNoInteractions(purchaseRepository);
    }
}
