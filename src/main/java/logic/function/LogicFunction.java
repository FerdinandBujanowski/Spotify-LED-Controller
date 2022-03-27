package logic.function;

import control.node.NodeConnection;
import control.node.NodeControl;
import control.exceptions.JointConnectionFailedException;
import control.node.ThreeCoordinatePoint;
import control.type_enums.JointType;
import control.type_enums.NodeType;
import logic.LogicComponent;
import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.JointDataType;

import java.io.Serializable;
import java.util.ArrayList;

public class LogicFunction extends LogicComponent implements Serializable {

    private final int functionIndex;
    private final String functionName;
    private ArrayList<LogicNode> logicNodes;
    private ArrayList<LogicNode> hiddenCopiedNodes;
    private ArrayList<Integer> inputParameterIndexes, outputParameterIndexes;

    private ArrayList<NodeConnection> nodeConnections;
    private int jointHoveredNodeIndex, jointHoveredJointIndex;

    public LogicFunction(int functionIndex, String functionName, InputJoint[] inputJoints, OutputJoint[] outputJoints) {
        super(new InputJoint[0], new OutputJoint[0]);
        this.functionIndex = functionIndex;
        this.functionName = functionName;
        this.logicNodes = new ArrayList<>();
        this.hiddenCopiedNodes = new ArrayList<>();

        this.inputParameterIndexes = new ArrayList<>();
        this.outputParameterIndexes = new ArrayList<>();

        this.nodeConnections = new ArrayList<>();
        this.jointHoveredNodeIndex = -1;
        this.jointHoveredJointIndex = -1;

        for (InputJoint inputJoint : inputJoints) {
            this.addInputParameter(inputJoint);
        }
        for (OutputJoint outputJoint : outputJoints) {
            this.addOutputParameter(outputJoint);
        }
    }

    public int getFunctionIndex() {
        return this.functionIndex;
    }
    public String getFunctionName() {
        return this.functionName;
    }

    public ArrayList<LogicNode> getLogicNodes() {
        return this.logicNodes;
    }
    public ArrayList<NodeConnection> getNodeConnections() {
        return this.nodeConnections;
    }

    public int getJointHoveredNodeIndex() {
        return this.jointHoveredNodeIndex;
    }
    public void setJointHoveredNodeIndex(int jointHoveredNodeIndex) {
        this.jointHoveredNodeIndex = jointHoveredNodeIndex;
    }
    public int getJointHoveredJointIndex() {
        return this.jointHoveredJointIndex;
    }
    public void setJointHoveredJointIndex(int jointHoveredJointIndex) {
        this.jointHoveredJointIndex = jointHoveredJointIndex;
    }

    public void addInputParameter(InputJoint inputJoint) {
        this.addInputJoint(inputJoint);
        int nextFreeIndex = this.logicNodes.isEmpty() ? 0 : this.logicNodes.get(this.logicNodes.size() - 1).getNodeIndex() + 1;
        this.logicNodes.add(new LogicNode(
                nextFreeIndex,
                new InputJoint[0],
                new OutputJoint[] { new OutputJoint(inputJoint.getJointDataType(), inputJoint.getName()) },
                "Input Parameter",
                NodeType._INPUT_PARAMETER_NODE,
                new Object[] {
                        this.functionIndex,
                        JointType.getJointTypeByTypeClass(inputJoint.getJointDataType().getClass()),
                        inputJoint.getName()
                }
        ) {});
        this.inputParameterIndexes.add(this.logicNodes.size() - 1);
    }

    public void addOutputParameter(OutputJoint outputJoint) {
        this.addOuptputJoint(outputJoint);
        int nextFreeIndex = this.logicNodes.isEmpty() ? 0 : this.logicNodes.get(this.logicNodes.size() - 1).getNodeIndex() + 1;
        this.logicNodes.add(new LogicNode(
                nextFreeIndex,
                new InputJoint[] { new InputJoint(outputJoint.getJointDataType(), outputJoint.getName()) },
                new OutputJoint[0],
                "Output Parameter",
                NodeType._OUTPUT_PARAMETER_NODE,
                new Object[] {
                        this.functionIndex,
                        JointType.getJointTypeByTypeClass(outputJoint.getJointDataType().getClass()),
                        outputJoint.getName()
                }
        ) {});
        this.outputParameterIndexes.add(this.logicNodes.size() - 1);
    }

    public LogicNode bakeNode(int nodeIndex, int goalIndex) {

        //Alle Nodes der Funktion werden kopiert (inklusive der Verbindungen)
        //dabei werden alle neu erstellten Nodes mit neuem Index versehen, startend ab dem Index der zu
        //... erschaffenen Node PLUS 1
        ArrayList<LogicNode> hiddenNodes = NodeControl.getCopyOfLogicNodeList(nodeIndex + 1, this.logicNodes);

        //Joints werden kopiert
        //NOTIZ: Joints wurden bis zu diesem Zeitpunkt jedoch nur als "Richtwert" für die Menge / Datentypen
        //... der jeweils umgekehrten Parameter-Nodes im Funktionsmenü verwendet (tragen KEINE Daten)
        InputJoint[] newInputJoints = NodeControl.getCopyOfInputJointArray(this.getInputJoints());
        OutputJoint[] newOutputJoints = NodeControl.getCopyOfOutputJointArray(this.getOutputJoints());


        for(int i = 0; i < this.inputParameterIndexes.size(); i++) {
            int finalI = i;
            LogicNode oldNode = hiddenNodes.get(this.inputParameterIndexes.get(i));
            hiddenNodes.set(this.inputParameterIndexes.get(i), new LogicNode(
                    oldNode.getNodeIndex(),
                    oldNode.getInputJoints(),
                    new OutputJoint[] { new OutputJoint(newInputJoints[i].getJointDataType(), newInputJoints[i].getName()) },
                    oldNode.getSpecificName(),
                    oldNode.getNodeType(),
                    oldNode.getExtraParameters()
            ) {
                @Override
                public JointDataType[] function(InputJoint[] nullInputJoints) {
                    JointDataType jointDataType = null;
                    try {
                        jointDataType = newInputJoints[finalI].getJointDataType().getClass().newInstance();
                        jointDataType.setData(newInputJoints[finalI].getJointDataType().getData());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return new JointDataType[] { jointDataType };
                }
            });
            while(!oldNode.getOutputJoints()[0].getConnectedInputJoints().isEmpty()) {
                InputJoint connectedInputJoint = oldNode.getOutputJoints()[0].getConnectedInputJoints().get(0);
                try {
                    connectedInputJoint.deleteJointConnection();
                    NodeConnection nodeConnection = new NodeConnection(new ThreeCoordinatePoint(0, 0, 0), new ThreeCoordinatePoint(0, 0, 0));
                    connectedInputJoint.tryJointConnection(hiddenNodes.get(this.inputParameterIndexes.get(i)).getOutputJoints()[0], nodeConnection);
                } catch (JointConnectionFailedException e) {
                    e.printStackTrace();
                }
            }
        }

        for(int i = 0; i < outputParameterIndexes.size(); i++) {
            LogicNode oldNode = hiddenNodes.get(outputParameterIndexes.get(i));
            hiddenNodes.set(outputParameterIndexes.get(i), new LogicNode(
                    oldNode.getNodeIndex(),
                    new InputJoint[] { new InputJoint(newOutputJoints[i].getJointDataType(), newOutputJoints[i].getName()) },
                    oldNode.getOutputJoints(),
                    oldNode.getSpecificName(),
                    oldNode.getNodeType(),
                    oldNode.getExtraParameters()
            ) {});
            try {
                NodeConnection nodeConnection = new NodeConnection(new ThreeCoordinatePoint(0, 0, 0), new ThreeCoordinatePoint(0, 0, 0));
                hiddenNodes.get(outputParameterIndexes.get(i)).getInputJoints()[0].tryJointConnection(oldNode.getInputJoints()[0].getConnectedOutputJoint(), nodeConnection);
            } catch (JointConnectionFailedException e) {
                e.printStackTrace();
            }
        }

        this.hiddenCopiedNodes.addAll(hiddenNodes);

        return new LogicNode(
                nodeIndex,
                newInputJoints,
                newOutputJoints,
                "Function: " + this.functionName,
                NodeType._FUNCTION_NODE,
                new Object[] { functionIndex, goalIndex }
        ) {

            @Override
            public JointDataType[] function(InputJoint[] inputJoints) {

                for(Integer inputParameterIndex : inputParameterIndexes) {
                    hiddenNodes.get(inputParameterIndex).onInputChangeEvent();
                }

                JointDataType[] outputJointDataTypes = new JointDataType[outputParameterIndexes.size()];
                for(int i = 0; i < outputParameterIndexes.size(); i++) {
                    try {
                        outputJointDataTypes[i] = newOutputJoints[i].getJointDataType().getClass().newInstance();
                        outputJointDataTypes[i].setData(hiddenNodes.get(outputParameterIndexes.get(i)).getInputJoints()[0].getJointDataType().getData());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return outputJointDataTypes;
            }
        };
    }
}
