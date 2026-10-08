package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Peticion de devolucion.
 *
 * El importe es opcional: si no se indica, se devuelve todo lo que quede por
 * devolver. Antes no existia el campo y Stripe recibia la orden sin importe, lo
 * que devolvia siempre el cargo completo y hacia imposible una devolucion
 * parcial, por ejemplo de una entrada de cuatro.
 *
 * Aqui solo se valida que sea positivo. Que no exceda lo devolvible lo
 * comprueba el servicio, que es quien conoce las devoluciones ya hechas.
 */
@Builder
public record RefundRequest(

        @NotNull(message = "purchaseId es obligatorio")
        Long purchaseId,

        @Positive(message = "El importe a devolver debe ser positivo")
        BigDecimal amount,

        String reason

) {}
