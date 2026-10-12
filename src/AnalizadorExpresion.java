import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Analiza expresiones aritméticas usando pilas:
 * 1) tokeniza la expresión,
 * 2) valida paréntesis y sintaxis,
 * 3) convierte a notación postfija (algoritmo Shunting Yard),
 * 4) evalúa el resultado con otra pila.
 */
public class AnalizadorExpresion {

    // Atributos
    private String expresion;

    // Constructor
    public AnalizadorExpresion() {
        expresion = null;
    }

    // Getters
    public String getExpresion() {
        return expresion;
    }

    // Setters
    public void setExpresion(String expresion) {
        this.expresion = expresion;
    }

    // ---------- Operaciones públicas ----------

    // Ejecuta el análisis completo de la expresión guardada
    public void analizar(Scanner teclado) {
        if (expresion == null || expresion.trim().isEmpty()) {
            System.out.println("\nNo hay expresión. Use primero la opción 1.\n");
            return;
        }
        System.out.println("\nExpresión: " + expresion);

        ArrayList<Token> tokens = tokenizar(expresion);
        if (tokens == null) return;

        System.out.print("Tokens: ");
        for (Token t : tokens) System.out.print("[" + t + "] ");
        System.out.println();

        if (!validar(tokens)) return;
        System.out.println("\nResultado: EXPRESIÓN VÁLIDA\n");

        ArrayList<Token> postfija = convertirAPostfija(tokens);
        System.out.print("Notación postfija: ");
        for (Token t : postfija) System.out.print(t + " ");
        System.out.println();

        evaluar(postfija, teclado);
    }

    // ---------- Fase 1: tokenización ----------

    private ArrayList<Token> tokenizar(String texto) {
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < texto.length()) {
            char c = texto.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                int inicio = i;
                int puntos = 0;
                while (i < texto.length() && (Character.isDigit(texto.charAt(i)) || texto.charAt(i) == '.')) {
                    if (texto.charAt(i) == '.') puntos++;
                    i++;
                }
                String numero = texto.substring(inicio, i);
                if (puntos > 1 || numero.equals(".")) {
                    System.out.println("\nResultado: EXPRESIÓN INVÁLIDA\nMotivo: número mal formado '" + numero + "'.\n");
                    return null;
                }
                tokens.add(new Token(Token.Tipo.NUMERO, numero));
            } else if (Character.isLetter(c) || c == '_') {
                int inicio = i;
                while (i < texto.length() && (Character.isLetterOrDigit(texto.charAt(i)) || texto.charAt(i) == '_')) i++;
                tokens.add(new Token(Token.Tipo.VARIABLE, texto.substring(inicio, i)));
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                tokens.add(new Token(Token.Tipo.OPERADOR, String.valueOf(c)));
                i++;
            } else if (c == '(') {
                tokens.add(new Token(Token.Tipo.PARENTESIS_IZQ, "("));
                i++;
            } else if (c == ')') {
                tokens.add(new Token(Token.Tipo.PARENTESIS_DER, ")"));
                i++;
            } else {
                System.out.println("\nResultado: EXPRESIÓN INVÁLIDA\nMotivo: carácter no permitido '" + c + "'.\n");
                return null;
            }
        }
        return tokens;
    }

    // ---------- Fase 2: validación (paréntesis con pila + sintaxis) ----------

    private boolean validar(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) return error("la expresión está vacía.");

        Pila parentesis = new Pila();
        boolean esperaOperando = true; // al inicio debe venir un operando o '('

        for (Token t : tokens) {
            switch (t.getTipo()) {
                case NUMERO:
                case VARIABLE:
                    if (!esperaOperando) return error("falta un operador antes de '" + t + "'.");
                    esperaOperando = false;
                    break;
                case OPERADOR:
                    if (esperaOperando) return error("el operador '" + t + "' no tiene operando a su izquierda.");
                    esperaOperando = true;
                    break;
                case PARENTESIS_IZQ:
                    if (!esperaOperando) return error("falta un operador antes de '('.");
                    parentesis.push(t.getTipo(), t.getValor());
                    System.out.println("  push '(' en la pila de paréntesis");
                    break;
                case PARENTESIS_DER:
                    if (esperaOperando) return error("falta un operando antes de ')'.");
                    if (parentesis.pop() == null) return error("hay un ')' sin su '(' correspondiente.");
                    System.out.println("  pop '(' : se empareja con ')'");
                    esperaOperando = false;
                    break;
            }
        }
        if (esperaOperando) return error("la expresión termina con un operador.");
        if (parentesis.peek() != null) return error("hay un '(' que nunca se cierra.");
        return true;
    }

    private boolean error(String motivo) {
        System.out.println("\nResultado: EXPRESIÓN INVÁLIDA\nMotivo: " + motivo + "\n");
        return false;
    }

    // ---------- Fase 3: conversión a postfija (Shunting Yard) ----------

    private ArrayList<Token> convertirAPostfija(ArrayList<Token> tokens) {
        ArrayList<Token> salida = new ArrayList<>();
        Pila operadores = new Pila();
        System.out.println("\nConversión a postfija (pila de operadores):");

        for (Token t : tokens) {
            switch (t.getTipo()) {
                case NUMERO:
                case VARIABLE:
                    salida.add(t);
                    System.out.println("  operando " + t + " -> salida");
                    break;
                case OPERADOR:
                    // Salen primero los operadores de la cima con igual o mayor precedencia
                    while (operadores.peek() != null
                            && operadores.peek().getTipo() == Token.Tipo.OPERADOR
                            && operadores.peek().getPrecedencia() >= t.getPrecedencia()) {
                        Token sale = operadores.pop();
                        salida.add(sale);
                        System.out.println("  pop " + sale + " -> salida");
                    }
                    operadores.push(t.getTipo(), t.getValor());
                    System.out.println("  push " + t);
                    break;
                case PARENTESIS_IZQ:
                    operadores.push(t.getTipo(), t.getValor());
                    System.out.println("  push (");
                    break;
                case PARENTESIS_DER:
                    while (operadores.peek() != null
                            && operadores.peek().getTipo() != Token.Tipo.PARENTESIS_IZQ) {
                        Token sale = operadores.pop();
                        salida.add(sale);
                        System.out.println("  pop " + sale + " -> salida");
                    }
                    operadores.pop(); // descarta el '('
                    System.out.println("  pop ( (se descarta)");
                    break;
            }
        }
        while (operadores.peek() != null) {
            Token sale = operadores.pop();
            salida.add(sale);
            System.out.println("  pop " + sale + " -> salida");
        }
        return salida;
    }

    // ---------- Fase 4: evaluación de la postfija ----------

    private void evaluar(ArrayList<Token> postfija, Scanner teclado) {
        HashMap<String, Double> valores = new HashMap<>();
        Pila pila = new Pila();
        System.out.println("\nEvaluación (pila de operandos):");

        for (Token t : postfija) {
            if (t.getTipo() == Token.Tipo.NUMERO) {
                pila.push(Token.Tipo.NUMERO, t.getValor());
                System.out.println("  push " + t);
            } else if (t.getTipo() == Token.Tipo.VARIABLE) {
                if (!valores.containsKey(t.getValor())) {
                    valores.put(t.getValor(), pedirValor(t.getValor(), teclado));
                }
                pila.push(Token.Tipo.NUMERO, String.valueOf(valores.get(t.getValor())));
                System.out.println("  push " + t + " (= " + valores.get(t.getValor()) + ")");
            } else {
                double derecho = Double.parseDouble(pila.pop().getValor());
                double izquierdo = Double.parseDouble(pila.pop().getValor());
                double resultado;
                switch (t.getValor()) {
                    case "+": resultado = izquierdo + derecho; break;
                    case "-": resultado = izquierdo - derecho; break;
                    case "*": resultado = izquierdo * derecho; break;
                    default:
                        if (derecho == 0) {
                            System.out.println("\nError: división entre cero.\n");
                            return;
                        }
                        resultado = izquierdo / derecho;
                }
                System.out.println("  pop " + derecho + " y " + izquierdo + " -> " + izquierdo + " " + t + " " + derecho
                        + " = " + resultado + " -> push");
                pila.push(Token.Tipo.NUMERO, String.valueOf(resultado));
            }
        }
        System.out.println("\nResultado de la expresión: " + pila.pop().getValor() + "\n");
    }

    private double pedirValor(String nombre, Scanner teclado) {
        while (true) {
            System.out.print("Valor para la variable " + nombre + ": ");
            try {
                return Double.parseDouble(teclado.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }
}
