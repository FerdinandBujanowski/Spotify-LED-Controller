package control.exceptions;

public class FunctionNodeInUseException extends Exception {
    public FunctionNodeInUseException() {
        super("Can't modify function while function node(s) in use");
    }
}
