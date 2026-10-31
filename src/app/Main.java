package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Main {

    static final int CAPACIDAD_TOTAL = 10;
    static final double TARIFA_HORA_CARRO = 4000;
    static final double TARIFA_DIA_CARRO = 30000;
    static final double TARIFA_HORA_MOTO = 2000;
    static final double TARIFA_DIA_MOTO = 15000;
    static final int UMBRAL_HORAS_DIA = 8;
    static final int HORA_APERTURA = 8;
    static final int HORA_CIERRE = 22;

    static final String CARRO = "Carro";
    static final String MOTO = "Moto";

    static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    static final Scanner SCANNER = new Scanner(System.in);

    static String[] tipoVehiculo = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] fechaIngreso = new LocalDateTime[CAPACIDAD_TOTAL];

    static int celdasOcupadas;
    static int celdasLibres;
    static double recaudoCarros;
    static double recaudoMotos;

    public static void main(String[] args) {
        inicializarDatos();
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion("Seleccione una opción: ", 1, 5);
            switch (opcion) {
                case 1 -> registrarEntrada();
                case 2 -> registrarSalida();
                case 3 -> reporteOcupacion();
                case 4 -> reporteFinanciero();
                case 5 -> imprimir("Operación finalizada, vuelva pronto.");
            }
        } while (opcion != 5);
        SCANNER.close();

    }

    static void inicializarDatos() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            tipoVehiculo[i] = null;
            fechaIngreso[i] = null;
        }
        celdasOcupadas = 0;
        celdasLibres = CAPACIDAD_TOTAL;
        recaudoCarros = 0;
        recaudoMotos = 0;
    }

    static void mostrarMenu() {
        imprimir("1. Registrar entrada");
        imprimir("2. Registrar salida");
        imprimir("3. Reporte de ocupación");
        imprimir("4. Reporte financiero");
        imprimir("5. Salir");
    }

    static void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

//No iba en el commit anterior, en este si
    static int celdaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tipoVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }

    static LocalDateTime obtenerFechaHoraActual() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    static void registrarEntrada() {
        int celda = celdaDisponible();
        if (celda == -1) {
            imprimir("El parqueadero está lleno. No es posible registrar el ingreso.");
            return;
        }
        LocalDateTime ingreso = obtenerFechaHoraActual();
        if (!dentroDeHorario(ingreso)) {
            imprimir("El parqueadero está cerrado (horario " + HORA_APERTURA + ":00 a "
                    + HORA_CIERRE + ":00). No es posible registrar el ingreso.");
            return;
        }
        String tipo = leerTipoVehiculo();

        tipoVehiculo[celda] = tipo;
        fechaIngreso[celda] = ingreso;
        celdasOcupadas++;
        celdasLibres--;

        imprimir(tipo + " registrado en la celda " + (celda + 1)
                + " el " + ingreso.format(FORMATO_FECHA_HORA) + ".");
    }
}
