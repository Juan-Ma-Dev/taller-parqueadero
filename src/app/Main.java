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

    static int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
            }
            imprimir("Dato inválido. Ingrese un número entre " + minimo + " y " + maximo + ".");
        }
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

    static int celdaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tipoVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }

    static boolean formatoFechaHoraValido(String texto) {
        if (texto.length() != 16
                || texto.charAt(4) != '-'
                || texto.charAt(7) != '-'
                || texto.charAt(10) != ' '
                || texto.charAt(13) != ':') {
            return false;
        }
        int anio = leerDigitos(texto, 0, 4);
        int mes = leerDigitos(texto, 5, 2);
        int dia = leerDigitos(texto, 8, 2);
        int hora = leerDigitos(texto, 11, 2);
        int minuto = leerDigitos(texto, 14, 2);

        return anio >= 1 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31
                && hora >= HORA_APERTURA && hora <= HORA_CIERRE
                && (hora != HORA_CIERRE || minuto <= 0)
                && minuto >= 0 && minuto <= 59;
    }

    static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (!formatoFechaHoraValido(texto)) {
                imprimir("Fecha u hora inválidas. Use yyyy-MM-dd HH:mm y el horario 08:00-22:00.");
                continue;
            }
            LocalDateTime fecha = LocalDateTime.parse(texto, FORMATO_FECHA_HORA);
            if (!fecha.format(FORMATO_FECHA_HORA).equals(texto)) {
                imprimir("Esa fecha no existe en el calendario.");
                continue;
            }
            return fecha;
        }
    }

    static LocalDateTime obtenerFechaHoraActual() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    static String leerTipoVehiculo() {
        int tipo = leerOpcion("Tipo de vehículo (1. Carro, 2. Moto): ", 1, 2);
        return tipo == 1 ? CARRO : MOTO;
    }

    static boolean dentroDeHorario(LocalDateTime fecha) {
        int hora = fecha.getHour();
        int minuto = fecha.getMinute();
        return hora >= HORA_APERTURA
                && (hora < HORA_CIERRE || (hora == HORA_CIERRE && minuto == 0));
    }

    static int leerDigitos(String texto, int inicio, int cantidad) {
        int valor = 0;
        for (int i = inicio; i < inicio + cantidad; i++) {
            char caracter = texto.charAt(i);
            if (caracter < '0' || caracter > '9') {
                return -1;
            }
            valor = valor * 10 + caracter - '0';
        }
        return valor;
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

    static void registrarSalida() {
        int numero = leerOpcion("Número de celda (1-" + CAPACIDAD_TOTAL + "): ", 1, CAPACIDAD_TOTAL);
        int celda = numero - 1;
        if (tipoVehiculo[celda] == null) {
            imprimir("La celda " + numero + " está libre; no hay vehículo para liquidar.");
            return;
        }

        LocalDateTime ingreso = fechaIngreso[celda];
        LocalDateTime salida;
        while (true) {
            salida = leerFechaHora("Fecha y hora de salida (yyyy-MM-dd HH:mm): ");
            if (salida.isBefore(ingreso)) {
                imprimir("La salida no puede ser anterior al ingreso (" + ingreso.format(FORMATO_FECHA_HORA) + ").");
            } else {
                break;
            }
        }

        String tipo = tipoVehiculo[celda];
        double total = liquidar(numero, tipo, ingreso, salida);

        if (tipo.equals(CARRO)) {
            recaudoCarros += total;
        } else {
            recaudoMotos += total;
        }
        tipoVehiculo[celda] = null;
        fechaIngreso[celda] = null;
        celdasOcupadas--;
        celdasLibres++;
    }
}
