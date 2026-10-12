/**
 * Nodo de la pila: representa in token de una expresión aritmérica.
 * Un token puede ser un número literal, una variable númera,
 * un operador (+, -, *, /) o un paréntesis.
 */
public class Token {
    // Tipos posibles de token
    public enum Tipo {
        NUMERO, VARIABLE, OPERADOR, PARENTESIS_IZQ, PARENTESIS_DER
    }

    // Atributos
    private Tipo tipo;
    private String valor;
    private Token siguiente; // enlace estructural hacia el token que está debajo en la pila

    //Métodos
    // Constructor
    public Token(Tipo tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
        siguiente = null;
    }

    // Getters
    public Tipo getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public Token getSiguiente() {
        return siguiente;
    }

    // Setters

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void setSiguiente(Token siguiente) {
        this.siguiente = siguiente;
    }

    //Operaciones
    // Precedencia del operador: * y / pesan más que + y -
    public int getPrecedencia() {
        if (tipo != Tipo.OPERADOR) return 0;
        if (valor.equals("*") || valor.equals("/")) return 2;
        return 1;
    }

    // toString (el siguiente es estructural, no se imprime)
    @Override
    public String toString() {
        return valor;
    }
}
