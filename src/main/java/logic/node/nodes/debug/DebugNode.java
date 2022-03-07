package logic.node.nodes.debug;

import control.type_enums.JointType;
import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.JointDataType;

public class DebugNode extends LogicNode {

    public DebugNode(int nodeIndex, JointType jointType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Input")
                },
                new OutputJoint[] {
                        new OutputJoint(JointType.getCopyOfDataTypeByJointType(jointType), "Output")
                },
                NodeType.DEBUG_NODE,
                new Object[] { jointType }
        );
    }

    @Override
    public JointDataType[] function(InputJoint[] inputJoints) {
        System.out.println("[DEBUG: value is " + inputJoints[0].getJointDataType().getData().toString() + "]");
        return new JointDataType[] { inputJoints[0].getJointDataType() };
    }
}
