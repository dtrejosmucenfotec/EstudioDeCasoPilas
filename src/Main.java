import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        AnalizadorExpresion analizador = new AnalizadorExpresion();
        menu(analizador);
    }

    // Menú principal de consola
    public static void menu(AnalizadorExpresion analizador) {
        Scanner teclado = new Scanner(System.in);
        int opcion = 0;

        while (opcion != 3) {
            System.out.println("=== ANALIZADOR DE EXPRESIONES ARITMETICAS");
            System.out.println("1. Introducir expresión");
            System.out.println("2. Analizar expresión");
            System.out.println("3. Salir");
            System.out.println("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = 0;
            }
            switch (opcion) {
                case 1:
                    System.out.print("\nEscriba la expresión (ej. (a + 5) * 3): ");
                    analizador.setExpresion(teclado.nextLine());
                    System.out.println("Expresión guardada.\n");
                    break;
                case 2:
                    analizador.analizar(teclado);
                    break;
                case 3:
                    System.out.println("\nHasta luego.");
                    break;
                default:
                    System.out.println("\nOpción inválida, intente de nuevo");
            }
        }
    }
}