package control.node;

import control.SerializableFunction;
import control.exceptions.CannotDeleteNodeException;
import control.exceptions.FunctionNodeInUseException;
import control.exceptions.JointConnectionFailedException;
import control.save.NodeSaveUnit;
import control.type_enums.*;
import gui.node_components.GraphicNode;
import logic.function.LogicFunction;
import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class NodeControl implements Serializable, NodeRequestAcceptor {

    private NodeGraphicUnit nodeGraphicUnit;
    private ArrayList<NodeGraphicUnit> functionGraphicUnits;

    public static final int JOINT_WIDTH = 14;
    public static final int NODE_TOP_HEIGHT = 25, NODE_CENTER_HEIGHT = 30, NODE_BOTTOM_HEIGHT = 15;

    private ArrayList<LogicNode> logicNodes;
    private ArrayList<NodeConnection> nodeConnections;
    private ArrayList<LogicFunction> logicFunctions;
    private ArrayList<ThreeCoordinatePoint> trackNodeIndexes;

    private int currentSongMs;
    private ArrayList<Double> trackIntensities;
    private int jointHoveredNodeIndex, jointHoveredJointIndex;

    public NodeControl() {
        this.functionGraphicUnits = new ArrayList<>();

        this.logicNodes = new ArrayList<>();
        this.nodeConnections = new ArrayList<>();
        this.logicFunctions = new ArrayList<>();
        this.trackNodeIndexes = new ArrayList<>();

        this.currentSongMs = 0;
        this.trackIntensities = new ArrayList<>();
        this.jointHoveredNodeIndex = -1;
        this.jointHoveredJointIndex = -1;
    }

    public void reinitialize(NodeSaveUnit nodeSaveUnit) {
        this.logicNodes = nodeSaveUnit.getLogicNodes();
        this.nodeConnections = nodeSaveUnit.getNodeConnections();
        this.logicFunctions = nodeSaveUnit.getLogicFunctions();
        this.trackNodeIndexes = nodeSaveUnit.getTrackNodeIndexes();

        for(ThreeCoordinatePoint trackNodeIndex : this.trackNodeIndexes) {
            int indexInArrayList = this.getNodeIndexInArrayList(trackNodeIndex.getX(), trackNodeIndex.getY());
            LogicNode oldNode = this.findNode(trackNodeIndex.getX(), trackNodeIndex.getY());
            LogicNode newNode = new LogicNode(
                    oldNode.getNodeIndex(),
                    oldNode.getInputJoints(),
                    oldNode.getOutputJoints(),
                    oldNode.getSpecificName(),
                    oldNode.getNodeType(),
                    oldNode.getExtraParameters()
            ) {
                @Override
                public JointDataType[] function(InputJoint[] nullInputJoints) {
                    double intensity = 0.d;
                    if(trackIntensities.get(trackNodeIndex.getZ()) != null) {
                        intensity = trackIntensities.get(trackNodeIndex.getZ());
                    }
                    return new UnitNumberJointDataType[] { new UnitNumberJointDataType(intensity) };
                }
            };
            if(indexInArrayList != -1) {
                if(trackNodeIndex.getX() == -1) {
                    this.logicNodes.set(indexInArrayList, newNode);
                } else {
                    this.findFunction(trackNodeIndex.getY()).getLogicNodes().set(indexInArrayList, newNode);
                }
            }
        }
    }

    public int getNextFreeNodeIndex(int functionIndex) {
        if(functionIndex == -1) {
            if(this.logicNodes.isEmpty()) return 0;
            return this.logicNodes.get(this.logicNodes.size() - 1).getNodeIndex() + 1;
        } else {
            LogicFunction currentFunction = this.findFunction(functionIndex);
            if(currentFunction.getLogicNodes().isEmpty()) return 0;
            LogicNode lastNode = currentFunction.getLogicNodes().get(currentFunction.getLogicNodes().size() - 1);
            return lastNode.getNodeIndex() + 1;
        }
    }
    public int getNextFreeFunctionIndex() {
        if(this.logicFunctions.isEmpty()) return 0;
        return this.logicFunctions.get(this.logicFunctions.size() -1).getFunctionIndex() + 1;
    }

    public void addNode(int functionIndex, NodeType nodeType, Object[] extraParameters) {
        LogicNode newNode = null;
        int newNodeIndex = this.getNextFreeNodeIndex(functionIndex);

        Class[] parameterClasses = new Class[extraParameters.length + 1];
        Object[] newParameters = new Object[parameterClasses.length];

        parameterClasses[0] = int.class;
        newParameters[0] = newNodeIndex;

        for(int i = 0; i < extraParameters.length; i++) {
            parameterClasses[i + 1] = extraParameters[i].getClass();
            newParameters[i + 1] = extraParameters[i];
        }
        if(nodeType.getNodeClass() != null) {
            String specificNodeName = "";
            try {
                newNode = (LogicNode) nodeType.getNodeClass().getDeclaredConstructor(parameterClasses).newInstance(newParameters);
                specificNodeName = !newNode.getSpecificName().equals("") ? newNode.getSpecificName() : nodeType.getName();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            if(functionIndex == -1) {
                this.logicNodes.add(newNode);
                this.nodeGraphicUnit.addGraphicNode(functionIndex, newNodeIndex, nodeType, specificNodeName);
            } else {
                this.findFunction(functionIndex).getLogicNodes().add(newNode);
                this.functionGraphicUnits.get(functionIndex).addGraphicNode(functionIndex, newNodeIndex, nodeType, specificNodeName);
            }
        } else {
            switch(nodeType) {
                case _FUNCTION_NODE -> {
                    int funcIndex = (int)extraParameters[0];
                    LogicFunction function = this.findFunction(funcIndex);
                    if(function != null) {
                        this.addFunctionNode(funcIndex, (int)extraParameters[1]);
                    }
                }
                case _INPUT_PARAMETER_NODE -> {
                    int funcIndex = (int)extraParameters[0];
                    LogicFunction logicFunction = this.findFunction(funcIndex);
                    JointType jointType = (JointType) extraParameters[1];
                    InputJoint inputJoint = new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), (String) extraParameters[2]);

                    newNodeIndex = this.getNextFreeNodeIndex(funcIndex);
                    logicFunction.addInputParameter(inputJoint);

                    String nodeName = this.findNode(funcIndex, newNodeIndex).getSpecificName();
                    if(funcIndex == -1) {
                        this.nodeGraphicUnit.addGraphicNode(funcIndex, newNodeIndex, nodeType, nodeName);
                    } else {
                        this.functionGraphicUnits.get(funcIndex).addGraphicNode(funcIndex, newNodeIndex, nodeType, nodeName);
                    }
                }
                case _OUTPUT_PARAMETER_NODE -> {
                    int funcIndex = (int)extraParameters[0];
                    LogicFunction logicFunction = this.findFunction(funcIndex);
                    JointType jointType = (JointType) extraParameters[1];
                    OutputJoint outputJoint = new OutputJoint(JointType.getCopyOfDataTypeByJointType(jointType), (String) extraParameters[2]);

                    newNodeIndex = this.getNextFreeNodeIndex(funcIndex);
                    logicFunction.addOutputParameter(outputJoint);

                    String nodeName = this.findNode(funcIndex, newNodeIndex).getSpecificName();
                    if(funcIndex == -1) {
                        this.nodeGraphicUnit.addGraphicNode(funcIndex, newNodeIndex, nodeType, nodeName);
                    } else {
                        this.functionGraphicUnits.get(funcIndex).addGraphicNode(funcIndex, newNodeIndex, nodeType, nodeName);
                    }
                }
                case _TRACK_NODE -> {
                    int trackIndex = (int)extraParameters[0];
                    this.addTrackNode(trackIndex);
                }
                case _LAYER_NODE -> {
                }
            }
        }
    }
    public void addFunction(int newFunctionIndex, String functionName, String[] inputNames, JointType[] inputTypes, String[] outputNames, JointType[] outputTypes) {

        InputJoint[] inputJoints = new InputJoint[inputNames.length];
        for(int i = 0; i < inputJoints.length; i++) {
            JointDataType inputJointDataType = null;
            try {
                inputJointDataType = (JointDataType) inputTypes[i].getTypeClass().newInstance();
                inputJoints[i] = new InputJoint(inputJointDataType, inputNames[i]);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        OutputJoint[] outputJoints = new OutputJoint[outputNames.length];
        for(int i = 0; i < outputJoints.length; i++) {
            JointDataType outputJointDataType = null;
            try {
                outputJointDataType = (JointDataType) outputTypes[i].getTypeClass().newInstance();
                outputJoints[i] = new OutputJoint(outputJointDataType, outputNames[i]);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        LogicFunction newFunction = new LogicFunction(newFunctionIndex, functionName, inputJoints, outputJoints);
        this.logicFunctions.add(newFunction);

        int[] nodeIndexes = this.getNodeIndexesOfFunctionIndex(newFunctionIndex);
        for(int index : nodeIndexes) {
            NodeType nodeType = this.getNodeType(newFunctionIndex, index);
            String nodeName = nodeType == null ? this.getSpecificNodeName(newFunctionIndex, index) : nodeType.getName();
            this.functionGraphicUnits.get(newFunctionIndex).addGraphicNode(newFunctionIndex, index, nodeType, nodeName);
        }
    }

    public void addFunctionNode(int functionIndexOrigin, int functionIndexGoal) {
        int newNodeIndex = this.getNextFreeNodeIndex(functionIndexGoal);
        LogicFunction logicFunction = this.findFunction(functionIndexOrigin);
        LogicNode logicNodeToAdd = logicFunction.bakeNode(newNodeIndex, functionIndexGoal);
        if(functionIndexGoal == -1) {
            this.logicNodes.add(logicNodeToAdd);
            this.nodeGraphicUnit.addGraphicNode(functionIndexGoal, newNodeIndex, NodeType._FUNCTION_NODE, logicFunction.getFunctionName());
        } else {
            this.findFunction(functionIndexGoal).getLogicNodes().add(logicNodeToAdd);
            this.functionGraphicUnits.get(functionIndexGoal).addGraphicNode(functionIndexGoal, newNodeIndex, NodeType._FUNCTION_NODE, logicFunction.getFunctionName());
        }

    }

    public void addTrackNode(int trackIndex) {

        String nodeName = "Track " + (trackIndex + 1);
        int newNodeIndex = this.getNextFreeNodeIndex(-1);
        this.trackNodeIndexes.add(new ThreeCoordinatePoint(-1, newNodeIndex, trackIndex));
        LogicNode trackNode = new LogicNode(
                newNodeIndex,
                new InputJoint[] {},
                new OutputJoint[] {
                        new OutputJoint(new UnitNumberJointDataType(), "Intensity")
                },
                nodeName,
                NodeType._TRACK_NODE,
                new Object[] { trackIndex }
        ) {
            @Override
            public JointDataType[] function(InputJoint[] nullInputJoints) {
                double intensity = 0.d;
                if(trackIntensities.get(trackIndex) != null) {
                    intensity = trackIntensities.get(trackIndex);
                }
                return new UnitNumberJointDataType[] { new UnitNumberJointDataType(intensity) };
            }
        };
        this.logicNodes.add(trackNode);
        this.nodeGraphicUnit.addGraphicNode(-1, newNodeIndex, NodeType._TRACK_NODE, nodeName);
    }

    public void addLayerNode(SerializableFunction<Object, Integer> setMaskFunction, SerializableFunction<Color, Integer> setColorFunction, String layerName) {
        int newNodeIndex = this.getNextFreeNodeIndex(-1);
        LogicNode layerNode = new LogicNode(
                newNodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new ColorJointDataType(), "Color")
                },
                new OutputJoint[] {},
                layerName,
                NodeType._LAYER_NODE
        ) {

            @Override
            public void onInputChangeEvent() {
                super.onInputChangeEvent();
                setMaskFunction.apply(this.getInputJoints()[0].getJointDataType().getData());
                setColorFunction.apply((Color)this.getInputJoints()[1].getJointDataType().getData());
            }
        };

        this.logicNodes.add(layerNode);
    }


    public LogicNode findNode(int functionIndex, int nodeIndex) {
        if(functionIndex == -1) {
            for(LogicNode logicNode : this.logicNodes) {
                if(logicNode.getNodeIndex() == nodeIndex) {
                    return logicNode;
                }
            }
        } else {
            for(LogicNode logicNode : this.findFunction(functionIndex).getLogicNodes()) {
                if(logicNode.getNodeIndex() == nodeIndex) {
                    return logicNode;
                }
            }
        }
        return null;
    }

    public int getNodeIndexInArrayList(int functionIndex, int nodeIndex) {
        if(functionIndex == -1) {
            for(int i = 0; i < this.logicNodes.size(); i++) {
                if(logicNodes.get(i).getNodeIndex() == nodeIndex) {
                    return i;
                }
            }
        } else {
            for(int i = 0; i < this.findFunction(functionIndex).getLogicNodes().size(); i++) {
                if(this.findFunction(functionIndex).getLogicNodes().get(i).getNodeIndex() == nodeIndex) {
                    return i;
                }
            }
        }
        return -1;
    }

    public LogicFunction findFunction(int functionIndex) {
        for(LogicFunction logicFunction : this.logicFunctions) {
            if(logicFunction.getFunctionIndex() == functionIndex) {
                return logicFunction;
            }
        }
        return null;
    }

    public int[] getFunctionIndexArray() {
        int[] functionIndexArray = new int[this.logicFunctions.size()];
        for(int i = 0; i < functionIndexArray.length; i++) {
            functionIndexArray[i] = this.logicFunctions.get(i).getFunctionIndex();
        }
        return functionIndexArray;
    }

    private boolean functionNodeInUse(int functionIndex) {
        for(LogicNode logicNode : this.logicNodes) {
            if(logicNode.getNodeType() == NodeType._FUNCTION_NODE
            && (int)logicNode.getExtraParameters()[0] == functionIndex) {
                return true;
            }
        }
        for(LogicFunction logicFunction : this.logicFunctions) {
            for(LogicNode logicNode : logicFunction.getLogicNodes()) {
                if(logicNode.getNodeType() == NodeType._FUNCTION_NODE
                        && (int)logicNode.getExtraParameters()[0] == functionIndex) {
                    return true;
                }
            }
        }
        return false;
    }

    public void tick(int ms, double[] updatedTrackIntensities) {
        this.currentSongMs = ms;
        for(int i = 0; i < updatedTrackIntensities.length; i++) {
            if(i < this.trackIntensities.size()) {
                this.trackIntensities.set(i, updatedTrackIntensities[i]);
            } else {
                this.trackIntensities.add(updatedTrackIntensities[i]);
            }
        }
        for(ThreeCoordinatePoint trackNodeIndex : this.trackNodeIndexes) {
            LogicNode currentTrackNode = this.findNode(trackNodeIndex.getX(), trackNodeIndex.getY());
            if(currentTrackNode != null) {
                currentTrackNode.onInputChangeEvent();
            }
        }
    }

    public static ArrayList<LogicNode> getCopyOfLogicNodeList(int startIndex, ArrayList<LogicNode> logicNodes) {
        ArrayList<LogicNode> outputList = new ArrayList<>();
        int currentIndex = startIndex;

        //x = -1, unwichtig für diesen Kontext; y = nodeIndex der alten Node; z = Input/Output-Index
        ArrayList<NodeConnection> connections = new ArrayList<>();

        //Kopie wird erstellt
        for(LogicNode logicNode : logicNodes) {
            LogicNode newLogicNode;
            try {
                newLogicNode = logicNode.getClass().getDeclaredConstructor(int.class).newInstance(currentIndex);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                newLogicNode = new LogicNode(
                        currentIndex,
                        NodeControl.getCopyOfInputJointArray(logicNode.getInputJoints()),
                        NodeControl.getCopyOfOutputJointArray(logicNode.getOutputJoints()),
                        logicNode.getSpecificName(),
                        logicNode.getNodeType(),
                        logicNode.getExtraParameters()
                ) {
                    @Override
                    public JointDataType[] function(InputJoint[] inputJoints) {
                        return logicNode.function(inputJoints);
                    }
                };
            }
            outputList.add(newLogicNode);
            currentIndex++;

            //Connections werden abgeschrieben
            for(int inputJointIndex = 0; inputJointIndex < logicNode.getInputJoints().length; inputJointIndex++) {
                OutputJoint connectedOutputJoint = logicNode.getInputJoints()[inputJointIndex].getConnectedOutputJoint();
                if(connectedOutputJoint != null) {
                    LogicNode connectedNode = connectedOutputJoint.getParentNode();
                    int outputJointIndex = connectedNode.getOutputJointIndex(connectedOutputJoint);

                    NodeConnection newNodeConnection = new NodeConnection(
                            new ThreeCoordinatePoint(-1, logicNodes.indexOf(connectedNode), outputJointIndex),
                            new ThreeCoordinatePoint(-1, logicNodes.indexOf(logicNode), inputJointIndex)
                    );
                    if(!connections.contains(newNodeConnection)) {
                        connections.add(newNodeConnection);
                    }
                }
            }
        }
        //Connections werden kopiert
        for(NodeConnection connection : connections) {
            ThreeCoordinatePoint outputCoordinates = connection.getOutputCoordinates();
            ThreeCoordinatePoint inputCoordinates = connection.getInputCoordinates();

            try {
                outputList.get(inputCoordinates.getY()).getInputJoints()[inputCoordinates.getZ()]
                        .tryJointConnection(outputList.get(outputCoordinates.getY()).getOutputJoints()[outputCoordinates.getZ()], connection);
            } catch (JointConnectionFailedException e) {
                e.printStackTrace();
            }
        }

        return outputList;
    }
    public static InputJoint[] getCopyOfInputJointArray(InputJoint[] inputJoints) {
        InputJoint[] copy = new InputJoint[inputJoints.length];

        for(int i = 0; i < inputJoints.length; i++) {
            try {
                copy[i] = new InputJoint(inputJoints[i].getJointDataType().getClass().newInstance(), inputJoints[i].getName());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return copy;
    }
    public static OutputJoint[] getCopyOfOutputJointArray(OutputJoint[] outputJoints) {
        OutputJoint[] copy = new OutputJoint[outputJoints.length];

        for(int i = 0; i < outputJoints.length; i++) {
            try {
                copy[i] = new OutputJoint(outputJoints[i].getJointDataType().getClass().newInstance(), outputJoints[i].getName());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return copy;
    }


    public NodeSaveUnit createNodeSaveUnit() {
        return new NodeSaveUnit(
                this.logicNodes,
                this.nodeConnections,
                this.logicFunctions,
                this.trackNodeIndexes
        );
    }

    public void makeNodeConnection(NodeConnection nodeConnection) throws JointConnectionFailedException {
        ThreeCoordinatePoint output = nodeConnection.getOutputCoordinates();
        ThreeCoordinatePoint input = nodeConnection.getInputCoordinates();

        this.findNode(input.getX(), input.getY()).getInputJoints()[input.getZ()]
                .tryJointConnection(this.findNode(output.getX(), output.getY()).getOutputJoints()[output.getZ()], nodeConnection);
        if(input.getX() == -1) {
            this.nodeConnections.add(nodeConnection);
        } else {
            this.findFunction(input.getX()).getNodeConnections().add(nodeConnection);
        }
    }

    @Override
    public void setNodeGraphicUnit(NodeGraphicUnit nodeGraphicUnit) {
        this.nodeGraphicUnit = nodeGraphicUnit;
    }

    @Override
    public void addFunctionGraphicUnit(NodeGraphicUnit nodeGraphicUnit) {
        this.functionGraphicUnits.add(nodeGraphicUnit);
    }

    @Override
    public ArrayList<NodeConnection> getNodeConnections(int functionIndex) {
        if(functionIndex == -1) {
            return this.nodeConnections;
        } else {
            return this.findFunction(functionIndex).getNodeConnections();
        }
    }

    @Override
    public String[] getInputJointNames(int functionIndex, int nodeIndex) {
        LogicNode currentNode = this.findNode(functionIndex, nodeIndex);
        String[] outputString = new String[currentNode.getInputJoints().length];
        for(int i = 0; i < outputString.length; i++) {
            outputString[i] = currentNode.getInputJoints()[i].getName();
        }
        return outputString;
    }

    @Override
    public String[] getOutputJointNames(int functionIndex, int nodeIndex) {
        LogicNode currentNode = this.findNode(functionIndex, nodeIndex);
        String[] outputString = new String[currentNode.getOutputJoints().length];
        for(int i = 0; i < outputString.length; i++) {
            outputString[i] = currentNode.getOutputJoints()[i].getName();
        }
        return outputString;
    }

    @Override
    public SerializableFunction<Integer, Double[][]> getMaskValuesFunctionForNode(int functionIndex, int nodeIndex) {
        LogicNode logicNode = this.findNode(functionIndex, nodeIndex);
        return logicNode::getMaskValues;
    }

    @Override
    public void updateInputJointHovered(int functionIndex, int nodeIndex, int inputJointIndex) {
        if(functionIndex == -1) {
            this.jointHoveredNodeIndex = nodeIndex;
            this.jointHoveredJointIndex = inputJointIndex;
        } else {
            this.findFunction(functionIndex).setJointHoveredNodeIndex(nodeIndex);
            this.findFunction(functionIndex).setJointHoveredJointIndex(inputJointIndex);
        }
    }

    @Override
    public void updateOutputJointReleased(int functionIndex, int nodeIndex, int outputJointIndex) throws FunctionNodeInUseException, JointConnectionFailedException{
        if(functionIndex == -1) {
            if(this.jointHoveredNodeIndex != -1 && this.jointHoveredJointIndex != -1) {
                this.makeNodeConnection(new NodeConnection(
                        new ThreeCoordinatePoint(functionIndex, nodeIndex, outputJointIndex),
                        new ThreeCoordinatePoint(functionIndex, this.jointHoveredNodeIndex, this.jointHoveredJointIndex)
                ));
            }
        } else {
            LogicFunction currentFunction = this.findFunction(functionIndex);
            if(currentFunction.getJointHoveredNodeIndex() != -1 && currentFunction.getJointHoveredJointIndex() != -1) {
                if(this.functionNodeInUse(functionIndex)) {
                    throw new FunctionNodeInUseException();
                }
                this.makeNodeConnection(new NodeConnection(
                        new ThreeCoordinatePoint(functionIndex, nodeIndex, outputJointIndex),
                        new ThreeCoordinatePoint(functionIndex, currentFunction.getJointHoveredNodeIndex(), currentFunction.getJointHoveredJointIndex())
                ));
            }
        }
    }

    @Override
    public void deleteJointConnectionRequest(int functionIndex, int nodeIndex, int inputJointIndex) throws FunctionNodeInUseException {
        ArrayList<NodeConnection> connections = (functionIndex == -1 ? this.nodeConnections : this.findFunction(functionIndex).getNodeConnections());
        int indexToDelete = -1;
        for(NodeConnection nodeConnection : connections) {
            if(
                    (int)nodeConnection.getInputCoordinates().getX() == functionIndex
                            && (int)nodeConnection.getInputCoordinates().getY() == nodeIndex
                            && (int)nodeConnection.getInputCoordinates().getZ() == inputJointIndex
            ) {
                if(functionIndex != -1 && this.functionNodeInUse(functionIndex)) {
                    throw new FunctionNodeInUseException();
                }
                this.findNode(functionIndex, nodeIndex).getInputJoints()[inputJointIndex].deleteJointConnection();
                indexToDelete = connections.indexOf(nodeConnection);
            }
        }
        if(indexToDelete != -1) connections.remove(indexToDelete);
    }

    @Override
    public void deleteNodeRequest(int functionIndex, int nodeIndex) throws CannotDeleteNodeException, FunctionNodeInUseException {
        LogicNode logicNode = this.findNode(functionIndex, nodeIndex);

        String stillConnectedMessage = "Node still connected to other Nodes!";
        for(InputJoint inputJoint : logicNode.getInputJoints()) {
            if(inputJoint.getConnectedOutputJoint() != null) {
                throw new CannotDeleteNodeException(stillConnectedMessage);
            }
        }
        for(OutputJoint outputJoint : logicNode.getOutputJoints()) {
            if(!outputJoint.getConnectedInputJoints().isEmpty()) {
                throw new CannotDeleteNodeException(stillConnectedMessage);
            }
        }

        if(functionIndex == -1) {
            this.logicNodes.remove(logicNode);
        } else {
            if(this.functionNodeInUse(functionIndex)) {
                throw new FunctionNodeInUseException();
            }
            this.findFunction(functionIndex).getLogicNodes().remove(logicNode);
        }
    }
    @Override
    public NodeType getNodeType(int functionIndex, int nodeIndex) {
        LogicNode currentNode = this.findNode(functionIndex, nodeIndex);
        return NodeType.getNodeTypeByTypeClass(currentNode.getClass());
    }
    @Override
    public JointType getJointType(boolean input, int functionIndex, int nodeIndex, int jointIndex) {
        LogicNode currentNode = this.findNode(functionIndex, nodeIndex);
        JointDataType jointDataType;
        if(input) {
            jointDataType = currentNode.getInputJoints()[jointIndex].getJointDataType();
        } else {
            jointDataType = currentNode.getOutputJoints()[jointIndex].getJointDataType();
        }
        return JointType.getJointTypeByTypeClass(jointDataType.getClass());
    }

    @Override
    public int[] getNodeIndexArray(int functionIndex) {
        ArrayList<LogicNode> logicNodes;
        if(functionIndex == -1) {
            logicNodes = this.logicNodes;
        } else {
            logicNodes = this.findFunction(functionIndex).getLogicNodes();
        }
        int[] nodeIndexArray = new int[logicNodes.size()];
        for(int i = 0; i < nodeIndexArray.length; i++) {
            nodeIndexArray[i] = logicNodes.get(i).getNodeIndex();
        }
        return nodeIndexArray;
    }
    @Override
    public int[] getNodeIndexesOfFunctionIndex(int functionIndex) {
        ArrayList<LogicNode> logicNodes = this.findFunction(functionIndex).getLogicNodes();
        int[] indexes = new int[logicNodes.size()];
        for(int i = 0; i < indexes.length; i++) {
            indexes[i] = logicNodes.get(i).getNodeIndex();
        }
        return indexes;
    }

    @Override
    public String getSpecificNodeName(int functionIndex, int nodeIndex) {
        LogicNode logicNode = this.findNode(functionIndex, nodeIndex);
        return logicNode == null ? "" : logicNode.getSpecificName();
    }

    @Override
    public ArrayList<Integer> getOffsetIndexes(int functionIndex) {
        return null;
    }

    @Override
    public ArrayList<Integer> copyNodes(ArrayList<Integer> nodeIndexes, int functionIndex) {
        ArrayList<Integer> newNodeIndexes = new ArrayList<>();
        ArrayList<LogicNode> templateNodes = new ArrayList<>();
        ArrayList<NodeConnection> templateConnections = new ArrayList<>();
        TwoIntegerCorrespondence newIndexCorrespondence = new TwoIntegerCorrespondence();

        for(int nodeIndex : nodeIndexes) {
            LogicNode templateNode = this.findNode(functionIndex, nodeIndex);
            if (templateNode != null) {
                templateNodes.add(templateNode);
            }
        }
        for(LogicNode logicNode : templateNodes) {
            int newNodeIndex = this.getNextFreeNodeIndex(functionIndex);
            newIndexCorrespondence.addValue(logicNode.getNodeIndex(), newNodeIndex);
            this.addNode(functionIndex, logicNode.getNodeType(), logicNode.getExtraParameters());
            newNodeIndexes.add(newNodeIndex);

            GraphicNode oldGraphicNode;
            GraphicNode newGraphicNode;
            if(functionIndex == -1) {
                oldGraphicNode = this.nodeGraphicUnit.findGraphicNode(logicNode.getNodeIndex());
                newGraphicNode = this.nodeGraphicUnit.findGraphicNode(newNodeIndex);
                this.nodeGraphicUnit.moveGraphicNode(newGraphicNode, new Point(oldGraphicNode.getX() - 50, oldGraphicNode.getY() - 50));
            } else {
                oldGraphicNode = this.functionGraphicUnits.get(functionIndex).findGraphicNode(logicNode.getNodeIndex());
                newGraphicNode = this.functionGraphicUnits.get(functionIndex).findGraphicNode(newNodeIndex);
                this.functionGraphicUnits.get(functionIndex).moveGraphicNode(newGraphicNode, new Point(oldGraphicNode.getX() - 50, oldGraphicNode.getY() - 50));
            }
        }
        for(LogicNode logicNode : templateNodes) {
            for(InputJoint inputJoint : logicNode.getInputJoints()) {
                OutputJoint connectedOutputJoint = inputJoint.getConnectedOutputJoint();
                if(connectedOutputJoint != null) {
                    LogicNode parentNode = connectedOutputJoint.getParentNode();
                    if(nodeIndexes.contains(parentNode.getNodeIndex())) {
                        templateConnections.add(new NodeConnection(
                                new ThreeCoordinatePoint(
                                        functionIndex,
                                        newIndexCorrespondence.getCorrespondingValue(parentNode.getNodeIndex()),
                                        parentNode.getOutputJointIndex(connectedOutputJoint)
                                ),
                                new ThreeCoordinatePoint
                                        (functionIndex,
                                                newIndexCorrespondence.getCorrespondingValue(logicNode.getNodeIndex()),
                                                logicNode.getInputJointIndex(inputJoint)
                                        )
                        ));
                    }
                }
            }
        }
        for(NodeConnection nodeConnection : templateConnections) {
            try {
                this.makeNodeConnection(nodeConnection);
            } catch (JointConnectionFailedException e) {
                e.printStackTrace();
            }
        }

        return newNodeIndexes;
    }

    @Override
    public ArrayList<ArrayList<Integer>> getNodeSets(int functionIndex) {
        ArrayList<LogicNode> nodesCopy = functionIndex == -1 ? this.logicNodes : this.findFunction(functionIndex).getLogicNodes();
        ArrayList<ArrayList<Integer>> nodeSets = new ArrayList<>();
        for(LogicNode logicNode : nodesCopy) {
            int nodeSetIndex = -1;
            for(int i = 0; i < nodeSets.size(); i++) {
                LogicNode testNode = this.findNode(functionIndex, nodeSets.get(i).get(0));
                if(testNode.isConnectedTo(logicNode, new ArrayList<>())) {
                    nodeSetIndex = i;
                }
            }
            if(nodeSetIndex == -1) {
                nodeSets.add(new ArrayList<>());
                nodeSetIndex = nodeSets.size() - 1;
            }
            nodeSets.get(nodeSetIndex).add(logicNode.getNodeIndex());
        }
        return nodeSets;
    }

    @Override
    public int getNodeRank(int functionIndex, int nodeIndex) {
        LogicNode logicNode = this.findNode(functionIndex, nodeIndex);
        return logicNode.getMaxNodesConnected(false) - logicNode.getMaxNodesConnected(true);
    }
}
