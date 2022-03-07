package logic.function;

import control.node.NodeConnection;
import control.node.NodeControl;
import control.exceptions.JointConnectionFailedException;
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
    private ArrayList<LogicNode> logicNodes;
    private ArrayList<LogicNode> hiddenCopiedNodes;
    private ArrayList<Integer> inputParameterIndexes, outputParameterIndexes;

    private ArrayList<NodeConnection> nodeConnections;
    private int jointHoveredNodeIndex, jointHoveredJointIndex;

    /**
     * @param functionIndex neuer globaler Index, alle Parameter-Nodes werden startend ab dieser Zahl
     *                      durchnummeriert
     * @param inputJoints Array von InputJoints, die der Funktion zuerst nur dazu dient, die richtige
     *                    Anzahl von Parameter-Nodes mit richtigen Datentypen zu erstellen, beim
     *                    BakeNode-Vorgang jedoch wieder bedeutend werden
     * @param outputJoints Array von OutputJoints, selbes wie für die InputJoints
     */
    public LogicFunction(int functionIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints) {
        super(inputJoints, outputJoints);
        this.functionIndex = functionIndex;
        this.logicNodes = new ArrayList<>();
        this.hiddenCopiedNodes = new ArrayList<>();

        this.inputParameterIndexes = new ArrayList<>();
        this.outputParameterIndexes = new ArrayList<>();

        this.nodeConnections = new ArrayList<>();
        this.jointHoveredNodeIndex = -1;
        this.jointHoveredJointIndex = -1;

        for(int i = 0; i < inputJoints.length; i++) {
            int finalI = i;
            this.logicNodes.add(new LogicNode(
                    finalI,
                    new InputJoint[0],
                    new OutputJoint[] { new OutputJoint(inputJoints[i].getJointDataType(), inputJoints[i].getName()) },
                    "Input Parameter",
                    NodeType._INPUT_PARAMETER_NODE,
                    new Object[] {JointType.getJointTypeByTypeClass(inputJoints[i].getJointDataType().getClass())}
            ) {});
            this.inputParameterIndexes.add(i);
        }

        int logicNodesSize = logicNodes.size();
        for(int i = 0; i < outputJoints.length; i++) {
            int finalI = i;
            this.logicNodes.add(new LogicNode(
                    finalI + logicNodesSize,
                    new InputJoint[] { new InputJoint(outputJoints[finalI].getJointDataType(), outputJoints[finalI].getName()) },
                    new OutputJoint[0],
                    "Output Parameter",
                    NodeType._OUTPUT_PARAMETER_NODE,
                    new Object[] { JointType.getJointTypeByTypeClass(outputJoints[i].getJointDataType().getClass()) }
            ) {});
            this.outputParameterIndexes.add(i + logicNodesSize);
        }
    }

    public int getFunctionIndex() {
        return this.functionIndex;
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

    public LogicNode bakeNode(int nodeIndex, String functionName) {

        //Alle Nodes der Funktion werden kopiert (inklusive der Verbindungen)
        //dabei werden alle neu erstellten Nodes mit neuem Index versehen, startend ab dem Index der zu
        //... erschaffenen Node PLUS 1
        ArrayList<LogicNode> hiddenNodes = NodeControl.getCopyOfLogicNodeList(nodeIndex + 1, this.logicNodes);

        //Joints werden kopiert
        //NOTIZ: Joints wurden bis zu diesem Zeitpunkt jedoch nur als "Richtwert" für die Menge / Datentypen
        //... der jeweils umgekehrten Parameter-Nodes im Funktionsmenü verwendet (tragen KEINE Daten)
        InputJoint[] newInputJoints = NodeControl.getCopyOfInputJointArray(this.getInputJoints());
        OutputJoint[] newOutputJoints = NodeControl.getCopyOfOutputJointArray(this.getOutputJoints());


        for(int inputParameterIndex : inputParameterIndexes) {
            int finalI = inputParameterIndex - inputParameterIndexes.get(0);
            LogicNode oldNode = hiddenNodes.get(inputParameterIndex);
            hiddenNodes.set(inputParameterIndex, new LogicNode(
                    oldNode.getNodeIndex(),
                    oldNode.getInputJoints(),
                    new OutputJoint[] { new OutputJoint(newInputJoints[finalI].getJointDataType(), newInputJoints[finalI].getName()) },
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
                    connectedInputJoint.tryJointConnection(hiddenNodes.get(inputParameterIndex).getOutputJoints()[0]);
                } catch (JointConnectionFailedException e) {
                    e.printStackTrace();
                }
            }
        }

        for(int i = 0; i < outputParameterIndexes.size(); i++) {
            int finalI = outputParameterIndexes.get(i) - outputParameterIndexes.get(0);
            LogicNode oldNode = hiddenNodes.get(outputParameterIndexes.get(i));
            hiddenNodes.set(outputParameterIndexes.get(i), new LogicNode(
                    oldNode.getNodeIndex(),
                    new InputJoint[] { new InputJoint(newOutputJoints[finalI].getJointDataType(), newOutputJoints[finalI].getName()) },
                    oldNode.getOutputJoints(),
                    oldNode.getSpecificName(),
                    oldNode.getNodeType(),
                    oldNode.getExtraParameters()
            ) {});
            try {
                hiddenNodes.get(outputParameterIndexes.get(i)).getInputJoints()[0].tryJointConnection(oldNode.getInputJoints()[0].getConnectedOutputJoint());
            } catch (JointConnectionFailedException e) {
                e.printStackTrace();
            }
        }

        this.hiddenCopiedNodes.addAll(hiddenNodes);

        return new LogicNode(
                nodeIndex,
                newInputJoints,
                newOutputJoints,
                "Function: " + functionName,
                NodeType._FUNCTION_NODE,
                new Object[] { functionIndex }
        ) {

            @Override
            public JointDataType[] function(InputJoint[] inputJoints) {

                for (int inputParameterIndex : inputParameterIndexes) {
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
