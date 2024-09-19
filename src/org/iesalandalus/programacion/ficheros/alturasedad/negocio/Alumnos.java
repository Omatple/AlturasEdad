package org.iesalandalus.programacion.ficheros.alturasedad.negocio;

import org.iesalandalus.programacion.ficheros.alturasedad.dominio.Alumno;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Alumnos {
    private static final String CARPETA_DATOS = "datos";
    private static final String FICHERO_ALUMNOS = String.format("%s%s%s", CARPETA_DATOS, File.separator, "alumnos.csv");
    private static final String PREFIJO_FICHEROS = "alumnos";
    private static final String EXTENSION_FICHEROS = ".xml";
    private static final String SEPARADOR_CSV = ";";
    private static final String RAIZ = "alumnos";
    private static final String ALUMNO = "alumno";
    private static final String NOMBRE = "nombre";
    private static final String ALTURA = "altura";
    private final Map<Integer, List<Alumno>> alumnosEdad;
    private final List<Alumno> coleccionAlumnos;

    public Alumnos() {
        coleccionAlumnos = new ArrayList<>();
        alumnosEdad = new HashMap<>();
    }

    public void leer() {
        try (BufferedReader entrada = new BufferedReader(new FileReader(FICHERO_ALUMNOS))) {
            String linea;
            int numRegistro = 1;
            while ((linea = entrada.readLine()) != null) {
                String[] campos = linea.split(SEPARADOR_CSV);
                insertar(campos, numRegistro++);
            }
            System.out.printf("Fichero %s leído correctamente.%n", FICHERO_ALUMNOS);
        } catch (FileNotFoundException e) {
            System.out.printf("No es posible leer el fichero %s%n", FICHERO_ALUMNOS);
        } catch (IOException e) {
            System.out.printf("Error inesperado de Entrada/Salida sobre el fichero %s%n", FICHERO_ALUMNOS);
        }
    }

    private void insertar(String[] campos, int numRegistro) {
        try {
            coleccionAlumnos.add(getAlumno(campos));
        } catch (OperationNotSupportedException e) {
            System.out.printf("ERROR al procesar el registro número %s: %s%n", numRegistro, e.getMessage());
        }
    }

    private Alumno getAlumno(String[] campos) throws OperationNotSupportedException {
        Alumno alumno;
        try {
            String nombre = campos[0];
            LocalDate fechaNacimiento = LocalDate.parse(campos[1], Alumno.FORMATO_FECHA);
            float altura = Float.parseFloat(campos[2].replace(",", "."));
            alumno = new Alumno(nombre, fechaNacimiento, altura);
        } catch (DateTimeParseException e) {
            throw new OperationNotSupportedException("Formato de fecha incorrecto");
        } catch (NumberFormatException e) {
            throw new OperationNotSupportedException("Formato de altura incorrecto.");
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new OperationNotSupportedException(e.getMessage());
        }
        return alumno;
    }

    public void clasificar() {
        for (Alumno alumno : coleccionAlumnos) {
            List<Alumno> alumnos = this.alumnosEdad.getOrDefault(alumno.edad(), new ArrayList<>());
            alumnos.add(alumno);
            this.alumnosEdad.put(alumno.edad(), alumnos);
        }
        ordenarAlumnosClasificados();
    }

    private void ordenarAlumnosClasificados() {
        for (List<Alumno> alumnos : alumnosEdad.values()) {
            alumnos.sort(Comparator.comparing(Alumno::altura).thenComparing(Alumno::nombre));
        }
    }

    public void escribir() {
        for (Map.Entry<Integer, List<Alumno>> entry : alumnosEdad.entrySet()) {
            Document documentoXml = crearDocumentoXml(entry.getValue());
            UtilidadesXml.escribirDocumentoXml(documentoXml, String.format("%s%s%s%s%s", CARPETA_DATOS, File.separator, PREFIJO_FICHEROS, entry.getKey(), EXTENSION_FICHEROS));
        }
    }

    private Document crearDocumentoXml(List<Alumno> alumnos) {
        DocumentBuilder constructor = UtilidadesXml.crearConstructorDocumentoXml();
        Document documentoXml = null;
        if (constructor != null) {
            documentoXml = constructor.newDocument();
            documentoXml.appendChild(documentoXml.createElement(RAIZ));
            for (Alumno alumno : alumnos) {
                Element elementoAlumno = getElemento(documentoXml, alumno);
                documentoXml.getDocumentElement().appendChild(elementoAlumno);
            }
        }
        return documentoXml;
    }

    private Element getElemento(Document documentoXml, Alumno alumno) {
        Element elementoAlumno = documentoXml.createElement(ALUMNO);
        elementoAlumno.setAttribute(NOMBRE, alumno.nombre());
        elementoAlumno.setAttribute(ALTURA, String.format(Locale.US, "%.2f", alumno.altura()));
        return elementoAlumno;
    }
}
