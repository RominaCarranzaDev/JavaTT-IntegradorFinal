package techlab.proyectoFinal.exception;

public class ProductNotFoundException extends CustomizedRuntimeException {

    public ProductNotFoundException(String message) {
        super("Producto no encontrado. " + message );
    }
}
