package org.iesalandalus.programacion.ficheros.alturasedad.dominio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record Alumno(String nombre, LocalDate fechaNacimiento, float altura) {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Alumno {
        validarNombre(nombre);
        validarFechaNacimiento(fechaNacimiento);
        validarAltura(altura);
    }

    private void validarNombre(String nombre) {
        Objects.requireNonNull(nombre, "El nombre no puede ser nulo.");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar en blanco.");
        }
    }

    private void validarFechaNacimiento(LocalDate fechaNacimiento) {
        Objects.requireNonNull(fechaNacimiento, "La fecha de nacimiento no puede ser nula.");
        if (edad(fechaNacimiento) < 16 || edad(fechaNacimiento) > 18) {
            throw new IllegalArgumentException("La edad debe estar comprendida entre 16 y 18 años (ambos inclusive).");
        }
    }

    private int edad(LocalDate fechaNacimiento) {
        return (int) ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now());
    }

    public int edad() {
        return edad(fechaNacimiento);
    }

    private void validarAltura(float altura) {
        if (!(altura >= 1 && altura <= 2.5f)) {
            throw new IllegalArgumentException("La altura debe estar comprendida entre 1 m y 2,5 m (ambos inclusive).");
        }
    }
}
