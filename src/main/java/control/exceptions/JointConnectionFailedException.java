package control.exceptions;

import control.node.NodeConnection;

public class JointConnectionFailedException extends Exception {

    private NodeConnection nodeConnection;

    public JointConnectionFailedException(String message, NodeConnection nodeConnection) {
        super(message);
        this.nodeConnection = nodeConnection;
    }

    public JointConnectionFailedException(String message) {
        this(message, null);
    }

    public NodeConnection getNodeConnection() {
        return this.nodeConnection;
    }
}
