package control.save;

import control.node.NodeConnection;
import control.node.ThreeCoordinatePoint;
import logic.function.LogicFunction;
import logic.node.LogicNode;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class NodeSaveUnit implements Serializable {

    private final ArrayList<LogicNode> logicNodes;
    private final ArrayList<NodeConnection> nodeConnections;
    private final ArrayList<LogicFunction> logicFunctions;
    private final ArrayList<ThreeCoordinatePoint> trackNodeIndexes;

    public NodeSaveUnit(
            ArrayList<LogicNode> logicNodes,
            ArrayList<NodeConnection> nodeConnections,
            ArrayList<LogicFunction> logicFunctions,
            ArrayList<ThreeCoordinatePoint> trackNodeIndexes
    ) {
        this.logicNodes = logicNodes;
        this.nodeConnections = nodeConnections;
        this.logicFunctions = logicFunctions;
        this.trackNodeIndexes = trackNodeIndexes;
    }

    public ArrayList<LogicNode> getLogicNodes() {
        return this.logicNodes;
    }
    public ArrayList<NodeConnection> getNodeConnections() {
        return this.nodeConnections;
    }
    public ArrayList<LogicFunction> getLogicFunctions() {
        return this.logicFunctions;
    }
    public ArrayList<ThreeCoordinatePoint> getTrackNodeIndexes() {
        return this.trackNodeIndexes;
    }
}
