package control.node;

import control.SerializableFunction;
import control.exceptions.CannotDeleteNodeException;
import control.exceptions.FunctionNodeInUseException;
import control.exceptions.JointConnectionFailedException;
import control.type_enums.JointType;
import control.type_enums.NodeType;

import java.awt.*;
import java.util.ArrayList;

public interface NodeRequestAcceptor {

    void setNodeGraphicUnit(NodeGraphicUnit nodeGraphicUnit);
    void addFunctionGraphicUnit(NodeGraphicUnit nodeGraphicUnit);

    void addNode(int functionIndex, NodeType nodeType, Object[] extraParameters, Point position);

    ArrayList<NodeConnection> getNodeConnections(int functionIndex);
    String[] getInputJointNames(int functionIndex, int nodeIndex);
    String[] getOutputJointNames(int functionIndex, int nodeIndex);
    SerializableFunction<Integer, Double[][]> getMaskValuesFunctionForNode(int functionIndex, int nodeIndex);
    public SerializableFunction<Integer, Color[][]> getColorValuesFunctionForNode(int functionIndex, int nodeIndex);
    void updateInputJointHovered(int functionIndex, int nodeIndex, int inputJointIndex);
    void updateOutputJointReleased(int functionIndex, int nodeIndex, int outputJointIndex) throws FunctionNodeInUseException, JointConnectionFailedException;
    void deleteJointConnectionRequest(int functionIndex, int nodeIndex, int inputJointIndex) throws FunctionNodeInUseException;
    void deleteNodeRequest(int functionIndex, int nodeIndex) throws CannotDeleteNodeException, FunctionNodeInUseException;
    public void makeNodeConnection(NodeConnection nodeConnection) throws JointConnectionFailedException;

    NodeType getNodeType(int functionIndex, int nodeIndex);
    JointType getJointType(boolean input, int functionIndex, int nodeIndex, int jointIndex);

    int[] getNodeIndexArray(int functionIndex);
    int[] getNodeIndexesOfFunctionIndex(int functionIndex);

    String getSpecificNodeName(int functionIndex, int nodeIndex);

    ArrayList<Integer> getOffsetIndexes(int functionIndex);
    ArrayList<Integer> copyNodes(ArrayList<Integer> nodeIndexes, int functionIndex);
    ArrayList<ArrayList<Integer>> getNodeSets(int functionIndex);
    int getNodeRank(int functionIndex, int nodeIndex);
}
