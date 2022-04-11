package logic.node;

import control.type_enums.NodeType;
import logic.LogicComponent;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.JointDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public abstract class LogicNode extends LogicComponent {

    private final int nodeIndex;
    private final String specificName;

    private NodeType nodeType;
    Object[] extraParameters;

    public LogicNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints, String specificName, NodeType nodeType, Object[] extraParameters) {
        super(inputJoints, outputJoints);
        this.nodeIndex = nodeIndex;
        this.specificName = specificName;

        for(int i = 0; i < outputJoints.length; i++) {
            outputJoints[i].setParentNode(this, i);
        }
        for(int i = 0; i < inputJoints.length; i++) {
            inputJoints[i].setParentNode(this, i);
        }

        this.nodeType = nodeType;
        this.extraParameters = extraParameters;
    }

    public LogicNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints, NodeType nodeType, Object[] extraParameters) {
        this(nodeIndex, inputJoints, outputJoints, "", nodeType, extraParameters);
    }

    public LogicNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints, String specificName, NodeType nodeType) {
        this(nodeIndex, inputJoints, outputJoints, specificName, nodeType, new Object[0]);
    }

    public LogicNode(int nodeIndex, InputJoint[] inputJoints, OutputJoint[] outputJoints, NodeType nodeType) {
        this(nodeIndex, inputJoints, outputJoints, "", nodeType);
    }
    
    public int getNodeIndex() {
        return this.nodeIndex;
    }
    public String getSpecificName() {
        return this.specificName;
    }

    public NodeType getNodeType() {
        return this.nodeType;
    }
    public Object[] getExtraParameters() {
        return this.extraParameters;
    }

    public void onInputChangeEvent() {
        //System.out.println("Node " + this.nodeIndex + ": input change detected");

        //alle InputJoints ziehen sich von verbundenen OutputJoints Daten
        for(int i = 0; i < this.getInputJoints().length; i++) {
            if(this.getInputJoints()[i] != null && this.getInputJoints()[i].getConnectedOutputJoint() != null) {
                this.getInputJoints()[i].getJointDataType().setData(this.getInputJoints()[i].getConnectedOutputJoint().getJointDataType().getData());
            } else if(this.getInputJoints()[i].getConnectedOutputJoint() == null) {
                this.getInputJoints()[i].getJointDataType().setData(this.getInputJoints()[i].getJointDataType().getDefaultData());
            }
        }

        //Daten der eigenen OutputJoints werden berechnet
        JointDataType[] jointDataTypes = this.function(this.getInputJoints());

        for(int i = 0; i < jointDataTypes.length; i++) {

            //Daten der eigenen OutputJoints werden gespeichert
            if(this.getOutputJoints()[i] != null) {
                if(jointDataTypes[i] != null) {
                    this.getOutputJoints()[i].getJointDataType().setData(jointDataTypes[i].getData());
                }
            }

            ArrayList<LogicNode> connectedNodes = new ArrayList<>();
            //für jeden OutputJoint...
            for(OutputJoint outputJoint : getOutputJoints()) {
                //wird für jeden verbundenen InputJoint...
                for(InputJoint currentConnectedInputJoint : outputJoint.getConnectedInputJoints()) {
                    //...die entsprechende ParentNode in Array gespeichert (solange sie nicht schon in Array ist)
                    if(currentConnectedInputJoint.getParentNode() != null && !connectedNodes.contains(currentConnectedInputJoint.getParentNode())) {
                        connectedNodes.add(currentConnectedInputJoint.getParentNode());
                    }
                }
            }
            //Alle verbundenen Nodes aktualisieren ebenfalls Daten
            for(LogicNode currentNode : connectedNodes) {
                currentNode.onInputChangeEvent();
            }
        }
    }

    /**
    Mit der Methode "function" berechnet jede Node spezifisch aus allen Inputs ein Array aus Outputs,
     entsprechend den OutputJoints
     **/
    public JointDataType[] function(InputJoint[] inputJoints) {
        return new JointDataType[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicNode logicNode = (LogicNode) o;
        return nodeIndex == logicNode.nodeIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeIndex);
    }

    public int getInputJointIndex(InputJoint inputJoint) {
        for(int i = 0; i < this.getInputJoints().length; i++) {
            if(this.getInputJoints()[i].equals(inputJoint)) return i;
        }
        return -1;
    }

    public int getOutputJointIndex(OutputJoint outputJoint) {
        for(int i = 0; i < this.getOutputJoints().length; i++) {
            if(this.getOutputJoints()[i].equals(outputJoint)) return i;
        }
        return -1;
    }

    public Double[][] getMaskValues(Integer nullInteger) {
        return new Double[0][0];
    }

    public boolean isConnectedTo(LogicNode otherNode, ArrayList<LogicNode> alreadyCheckedNodes) {
        ArrayList<LogicNode> connectedNodes = new ArrayList<>(alreadyCheckedNodes);
        connectedNodes.add(this);
        ArrayList<LogicNode> nodesNotChecked = new ArrayList<>();

        for(InputJoint inputJoint : this.getInputJoints()) {
            LogicNode connectedNode = inputJoint.getConnectedOutputJoint() == null ? null : inputJoint.getConnectedOutputJoint().getParentNode();
            if(connectedNode != null && !connectedNodes.contains(connectedNode)) {
                nodesNotChecked.add(connectedNode);
            }
        }
        for(OutputJoint outputJoint : this.getOutputJoints()) {
            for(InputJoint inputJoint : outputJoint.getConnectedInputJoints()) {
                LogicNode connectedNode = inputJoint.getParentNode();
                if(connectedNode != null && !connectedNodes.contains(connectedNode)) {
                    nodesNotChecked.add(connectedNode);
                }
            }
        }

        if(nodesNotChecked.isEmpty()) {
            return false;
        }
        if(nodesNotChecked.contains(otherNode)) {
            return true;
        }

        ArrayList<Boolean> booleans = new ArrayList<>();
        connectedNodes.addAll(nodesNotChecked);
        for(LogicNode connectedNode : nodesNotChecked) {
            booleans.add(connectedNode.isConnectedTo(otherNode, connectedNodes));
        }
        return booleans.contains(true);
    }

    public int getMaxNodesConnected(boolean right) {
        ArrayList<LogicNode> connectedNodes = new ArrayList<>();
        if(right) {
            OutputJoint[] outputJoints = this.getOutputJoints();
            for(OutputJoint outputJoint : outputJoints) {
                for(InputJoint connectedJoint : outputJoint.getConnectedInputJoints()) {
                    LogicNode connectedNode = connectedJoint.getParentNode();
                    if(!connectedNodes.contains(connectedNode)) {
                        connectedNodes.add(connectedNode);
                    }
                }
            }
        } else {
            InputJoint[] inputJoints = this.getInputJoints();
            for(InputJoint inputJoint : inputJoints) {
                OutputJoint connectedOutput = inputJoint.getConnectedOutputJoint();
                if(connectedOutput != null) {
                    LogicNode connectedNode = connectedOutput.getParentNode();
                    if(!connectedNodes.contains(connectedNode)) {
                        connectedNodes.add(connectedNode);
                    }
                }
            }
        }
        if(connectedNodes.isEmpty()) {
            return 0;
        } else {
            ArrayList<Integer> maxValues = new ArrayList<>();
            for(LogicNode connectedNode : connectedNodes) {
                maxValues.add(1 + connectedNode.getMaxNodesConnected(right));
            }
            return Collections.max(maxValues);
        }
    }
}