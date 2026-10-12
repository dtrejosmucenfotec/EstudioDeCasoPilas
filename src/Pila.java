/**
 * Pila dinámica enlazada de tokens(Principio LIFO).
 * Solo interactúa con el elemento de la cima(top): no hay recorridos,
 * búsquedas ni métodos para mostrar la pila
 */
public class Pila {

    // Atributos
    private Token top;

    //Métodos
    //Constructor
    public Pila() {
        top = null;
    }

    // Operaciones
    // Diagnóstico interno: la pila está vacía cuando no hay cime
    private boolean estaVacia() {
        return top == null;
    }

    // Insert un token nuevo en la cima. El nodo se construye aquí dentro.
    public void push(Token.Tipo tipo, String valor) {
        Token nodo = new Token(tipo, valor);
        nodo.setSiguiente(top); // primero se conecta con la cima anterior
        top = nodo;             // luego pasa a ser la nueva cima
    }

    // Saca y returna el token de la cima; retorna null si la pila está vacía
    public Token pop() {
        if (estaVacia()) return null;
        Token nodo = top;
        top = top.getSiguiente();
        nodo.setSiguiente(null);
        return nodo;
    }

    // Retorna el token de la cima sin sacarlo; null si la pila está vacía
    public Token peek() {
        if(estaVacia()) return null;
        return top;
    }
}
