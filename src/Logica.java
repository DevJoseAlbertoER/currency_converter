
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Logica {

    private static final String API_KEY = "c28057d5d098957ceaabcefc"; // Coloca aquí tu API key
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    private final Scanner scanner = new Scanner(System.in);

    public void iniciar() {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elija una opción válida: ");
            switch (opcion) {
                case 1 -> convertir("USD", "MXN");
                case 2 -> convertir("MXN", "USD");
                case 3 -> convertir("USD", "EUR");
                case 4 -> convertir("EUR", "USD");
                case 5 -> convertir("USD", "CNY");
                case 6 -> convertir("CNY", "USD");
                case 7 -> System.out.println("Saliendo... ¡Gracias por usar el conversor!");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 7);
    }

    private void mostrarMenu() {
        System.out.println("\nSea bienvenido/a al Conversor de Moneda =)");
        System.out.println("*****************************************************");
        System.out.println("1. Dólar ⇒ Peso Mexicano");
        System.out.println("2. Peso Mexicano ⇒ Dólar");
        System.out.println("3. Dólar ⇒ Euro");
        System.out.println("4. Euro ⇒ Dólar");
        System.out.println("5. Dólar ⇒ Yuan");
        System.out.println("6. Yuan ⇒ Dólar");
        System.out.println("7. Salir");
        System.out.println("*****************************************************");
    }

    private void convertir(String from, String to) {
        BigDecimal cantidad = leerDecimal("Ingrese el valor que deseas convertir: ");

        try {
            BigDecimal tasa = obtenerTasa(from, to);
            if (tasa != null) {
                BigDecimal resultado = cantidad.multiply(tasa).setScale(2, RoundingMode.HALF_UP);
                System.out.printf("El valor de %.2f %s corresponde al valor final de ⇒ %.2f %s%n",
                        cantidad, from, resultado, to);
            } else {
                System.out.println("No se pudo obtener la tasa de conversión.");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al conectar con la API: " + e.getMessage());
        }
    }

    private BigDecimal obtenerTasa(String from, String to) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + from))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response.body(), JsonObject.class);

        if (json.has("conversion_rates")) {
            JsonObject rates = json.getAsJsonObject("conversion_rates");
            if (rates.has(to)) {
                return rates.get(to).getAsBigDecimal();
            }
        }
        return null;
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número entero válido.");
                scanner.nextLine();
            }
        }
    }

    private BigDecimal leerDecimal(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return scanner.nextBigDecimal();
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número decimal válido.");
                scanner.nextLine();
            }
        }
    }
}

