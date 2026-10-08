package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

/**
 * Peticion para iniciar el pago de una compra.
 *
 * Antes incluia un campo amount que el servicio usaba tal cual, sin compararlo
 * nunca con el total de la compra. Validado solo con @Positive, permitia pagar
 * un centimo por una compra de cincuenta euros: el webhook la marcaba igual
 * como pagada. El importe se calcula ahora en el servidor a partir de las
 * entradas y los productos asociados a la compra, asi que el cliente solo
 * indica cual es la compra.
 *
 * La divisa es opcional y por defecto euros. Se mantiene configurable porque
 * Stripe la exige de forma explicita, pero se valida contra el formato ISO para
 * que no llegue cualquier cadena.
 */
@Builder
public record CreatePaymentIntentRequest(

        @NotNull(message = "purchaseId es obligatorio")
        Long purchaseId,

        @Pattern(regexp = "^[A-Za-z]{3}$", message = "La divisa debe ser un codigo ISO de 3 letras")
        String currency

) {
    private static final String DEFAULT_CURRENCY = "eur";

    public String currencyOrDefault() {
        return currency == null || currency.isBlank()
                ? DEFAULT_CURRENCY
                : currency.toLowerCase();
    }
}
