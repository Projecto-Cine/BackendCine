package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

/**
 * Alta de un cliente desde la tienda.
 *
 * Lo que este DTO deliberadamente NO acepta es tan importante como lo que
 * acepta. La version anterior incluia userType e isStudent, y el frontend
 * enviaba ademas role. Ninguno de los tres puede venir del cliente:
 *
 *  - role decidiria su propio nivel de acceso.
 *  - userType y la condicion de estudiante determinan el precio de la entrada
 *    (6 EUR frente a 9 EUR de adulto), asi que declararse estudiante es una
 *    rebaja que nadie ha verificado. El tipo se deriva de la fecha de
 *    nacimiento, y la condicion de estudiante la acredita el personal.
 *
 * birthDate es obligatoria porque de ella depende el control de calificacion
 * por edad en la venta de entradas.
 */
@Builder
public record RegisterRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
        String name,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 80, message = "Los apellidos deben tener entre 2 y 80 caracteres")
        String lastName,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es valido")
        @Size(max = 120, message = "El email no puede superar los 120 caracteres")
        String email,

        /*
         * Politica minima razonable. Antes solo habia @NotBlank, de modo que una
         * contrasena de un solo caracter era aceptada. El limite superior existe
         * porque bcrypt ignora lo que pase de 72 bytes, y conviene rechazarlo de
         * forma explicita en lugar de truncar en silencio.
         */
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
                message = "La contrasena debe incluir al menos una letra y un numero")
        String password,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate birthDate

) {}
