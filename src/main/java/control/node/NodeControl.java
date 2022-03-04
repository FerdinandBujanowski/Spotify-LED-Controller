package control.node;

import control.SerializableFunction;
import control.exceptions.CannotDeleteNodeException;
import control.exceptions.JointConnectionFailedException;
import control.save.NodeSaveUnit;
import control.type_enums.*;
import logic.function.LogicFunction;
import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class NodeControl implements Serializable {

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
                    oldNode.getSpecificName()
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
            return currentFunction.getLogicNodes().get(currentFunction.getLogicNodes().size() - 1).getNodeIndex() + 1;
        }
    }
    public int getNextFreeFunctionIndex() {
        if(this.logicFunctions.isEmpty()) return 0;
        return this.logicFunctions.get(this.logicFunctions.size() -1).getFunctionIndex() + 1;
    }

    public void addNode(int functionIndex, NodeType nodeType, int newNodeIndex, Object[] extraParameters) {
        LogicNode newNode = null;

        Class[] parameterClasses = new Class[extraParameters.length + 1];
        Object[] newParameters = new Object[parameterClasses.length];

        parameterClasses[0] = int.class;
        newParameters[0] = newNodeIndex;

        for(int i = 0; i < extraParameters.length; i++) {
            parameterClasses[i + 1] = extraParameters[i].getClass();
            newParameters[i + 1] = extraParameters[i];
        }
        try {
            newNode = (LogicNode) nodeType.getNodeClass().getDeclaredConstructor(parameterClasses).newInstance(newParameters);
            newNode.setNodeTypeName(nodeType.toString());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        if(functionIndex == -1) {
            this.logicNodes.add(newNode);
        } else {
            this.findFunction(functionIndex).getLogicNodes().add(newNode);
        }
    }
    public void addFunction(int newFunctionIndex, String[] inputNames, JointType[] inputTypes, String[] outputNames, JointType[] outputTypes) {
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

        this.logicFunctions.add(new LogicFunction(newFunctionIndex, inputJoints, outputJoints));
    }
    public void addFunctionNode(int functionIndexOrigin, int functionIndexGoal, int newNodeIndex, String functionName) {
        LogicNode logicNodeToAdd = this.findFunction(functionIndexOrigin).bakeNode(newNodeIndex, functionName);
        if(functionIndexGoal == -1) {
            this.logicNodes.add(logicNodeToAdd);
        } else {
            this.findFunction(functionIndexGoal).getLogicNodes().add(logicNodeToAdd);
        }
    }

    public void addTrackNode(int trackIndex, int newNodeIndex, String trackName) {

        this.trackNodeIndexes.add(new ThreeCoordinatePoint(-1, newNodeIndex, trackIndex));
        LogicNode trackNode = new LogicNode(
                newNodeIndex,
                new InputJoint[] {},
                new OutputJoint[] {
                        new OutputJoint(new UnitNumberJointDataType(), "Intensity")
                },
                trackName
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
    }

    public void addLayerNode(int newNodeIndex, SerializableFunction<Object, Integer> setMaskFunction, SerializableFunction<Color, Integer> setColorFunction, String layerName) {
        LogicNode layerNode = new LogicNode(
                newNodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask"),
                        new InputJoint(new ColorJointDataType(), "Color")
                },
                new OutputJoint[] {},
                layerName
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


    public SerializableFunction<Integer, Double[][]> getMaskValuesFunctionForNode(int functionIndex, int nodeIndex) {
        LogicNode logicNode = this.findNode(functionIndex, nodeIndex);
        return logicNode::getMaskValues;
    }

    public String[] getInputJointNames(int functionIndex, int nodeIndex) {
        LogicNode currentNode = this.findNode(functionIndex, nodeIndex);
        String[] outputString = new String[currentNode.getInputJoints().length];
        for(int i = 0; i < outputString.length; i++) {
            outputString[i] = currentNode.getInputJoints()[i].getName();
        }
        return outputString;
    }
    public String[] getOutputJointNames(int functionIndex, int nodeIndex) {
        LogicNode currentNode = this.findNode(functionIndex, nodeIndex);
        String[] outputString = new String[currentNode.getOutputJoints().length];
        for(int i = 0; i < outputString.length; i++) {
            outputString[i] = currentNode.getOutputJoints()[i].getName();
        }
        return outputString;
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
    public int[] getNodeIndexesOfFunctionIndex(int functionIndex) {
        ArrayList<LogicNode> logicNodes = this.findFunction(functionIndex).getLogicNodes();
        int[] indexes = new int[logicNodes.size()];
        for(int i = 0; i < indexes.length; i++) {
            indexes[i] = logicNodes.get(i).getNodeIndex();
        }
        return indexes;
    }

    public NodeType getNodeType(int functionIndex, int nodeIndex) {
        LogicNode currentNode = this.findNode(functionIndex, nodeIndex);
        return NodeType.getNodeTypeByTypeClass(currentNode.getClass());
    }
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

    public ArrayList<NodeConnection> getNodeConnections(int functionIndex) {
        if(functionIndex == -1) {
            return this.nodeConnections;
        } else {
            return this.findFunction(functionIndex).getNodeConnections();
        }
    }

    public void updateInputJointHovered(int functionIndex, int nodeIndex, int inputJointIndex) {
        if(functionIndex == -1) {
            this.jointHoveredNodeIndex = nodeIndex;
            this.jointHoveredJointIndex = inputJointIndex;
        } else {
            this.findFunction(functionIndex).setJointHoveredNodeIndex(nodeIndex);
            this.findFunction(functionIndex).setJointHoveredJointIndex(inputJointIndex);
        }
    }
    public void updateOutputJointReleased(int functionIndex, int nodeIndex, int outputJointIndex) {
        if(functionIndex == -1) {
            if(this.jointHoveredNodeIndex != -1 && this.jointHoveredJointIndex != -1) {
                try {
                    this.findNode(functionIndex, this.jointHoveredNodeIndex).getInputJoints()[this.jointHoveredJointIndex]
                            .tryJointConnection(this.findNode(functionIndex, nodeIndex).getOutputJoints()[outputJointIndex]);
                    this.nodeConnections.add(new NodeConnection(
                            new ThreeCoordinatePoint(functionIndex, nodeIndex, outputJointIndex),
                            new ThreeCoordinatePoint(functionIndex, this.jointHoveredNodeIndex, this.jointHoveredJointIndex))
                    );
                } catch (JointConnectionFailedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            LogicFunction currentFunction = this.findFunction(functionIndex);
            if(currentFunction.getJointHoveredNodeIndex() != -1 && currentFunction.getJointHoveredJointIndex() != -1) {
                try {
                    this.findNode(functionIndex, currentFunction.getJointHoveredNodeIndex()).getInputJoints()[currentFunction.getJointHoveredJointIndex()]
                            .tryJointConnection(this.findNode(functionIndex, nodeIndex).getOutputJoints()[outputJointIndex]);
                    currentFunction.getNodeConnections().add(
                            new NodeConnection(
                                    new ThreeCoordinatePoint(functionIndex, nodeIndex, outputJointIndex),
                                    new ThreeCoordinatePoint(functionIndex, currentFunction.getJointHoveredNodeIndex(), currentFunction.getJointHoveredJointIndex())
                            )
                    );
                } catch (JointConnectionFailedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteJointConnection(int functionIndex, int nodeIndex, int inputJointIndex) {
        ArrayList<NodeConnection> connections = (functionIndex == -1 ? this.nodeConnections : this.findFunction(functionIndex).getNodeConnections());
        int indexToDelete = -1;
        for(NodeConnection nodeConnection : connections) {
            if(
                    (int)nodeConnection.getInputCoordinates().getX() == functionIndex
                    && (int)nodeConnection.getInputCoordinates().getY() == nodeIndex
                    && (int)nodeConnection.getInputCoordinates().getZ() == inputJointIndex
            ) {
                this.findNode(functionIndex, nodeIndex).getInputJoints()[inputJointIndex].deleteJointConnection();
                indexToDelete = connections.indexOf(nodeConnection);
            }
        }
        if(indexToDelete != -1) connections.remove(indexToDelete);
    }
    public void deleteNode(int functionIndex, int nodeIndex) throws CannotDeleteNodeException {
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
            this.findFunction(functionIndex).getLogicNodes().remove(logicNode);
        }
    }

    public String getSpecificNodeName(int functionIndex, int nodeIndex) {
        return this.findNode(functionIndex, nodeIndex).getSpecificName();
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
                        logicNode.getSpecificName()
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
                        .tryJointConnection(outputList.get(outputCoordinates.getY()).getOutputJoints()[outputCoordinates.getZ()]);
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

    public NodeSaveUnit createNodeSaveUnit() {
        return new NodeSaveUnit(
                this.logicNodes,
                this.nodeConnections,
                this.logicFunctions,
                this.trackNodeIndexes
        );
    }
}
