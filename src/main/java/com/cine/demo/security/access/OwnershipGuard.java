package com.cine.demo.security.access;

import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comprobaciones de propiedad para expresiones de @PreAuthorize.
 *
 * Tener el rol correcto no basta: un cliente autenticado es CLIENT igual que
 * todos los demas clientes, asi que sin esto podria leer y modificar la ficha o
 * las compras de cualquier otro con solo cambiar el id de la URL. El rol
 * responde a "que clase de usuario eres" y esto a "es tuyo".
 *
 * Se registra como bean "ownership" para poder invocarlo desde las anotaciones:
 *     @PreAuthorize("hasAuthority('GERENCIA') or @ownership.isSelf(#id)")
 */
@Component("ownership")
@RequiredArgsConstructor
public class OwnershipGuard {

    private final PurchaseRepository purchaseRepository;

    /** True si el id indicado es el del usuario autenticado. */
    public boolean isSelf(Long userId) {
        AuthenticatedUser current = currentUser();
        return current != null && userId != null && userId.equals(current.id());
    }

    /** True si la compra indicada pertenece al usuario autenticado. */
    @Transactional(readOnly = true)
    public boolean ownsPurchase(Long purchaseId) {
        AuthenticatedUser current = currentUser();
        if (current == null || purchaseId == null) {
            return false;
        }
        return purchaseRepository.findById(purchaseId)
                .map(purchase -> purchase.getUser() != null
                        && current.id().equals(purchase.getUser().getId()))
                .orElse(false);
    }

    /**
     * Usuario autenticado, o null si no hay sesion.
     *
     * Devuelve null en lugar de lanzar cuando el principal no es un
     * AuthenticatedUser: ocurre con usuarios simulados en tests de slice, y en
     * ese caso lo correcto es denegar, no romper la peticion.
     */
    private AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        return principal instanceof AuthenticatedUser user ? user : null;
    }
}
