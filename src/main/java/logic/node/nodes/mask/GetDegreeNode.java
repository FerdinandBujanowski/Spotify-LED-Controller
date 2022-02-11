package logic.node.nodes.mask;

import logic.mask.LogicMask;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.MaskJointDataType;

public class GetDegreeNode extends LogicNode {

    public GetDegreeNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new MaskJointDataType(), "Mask")
                },
                new OutputJoint[] {
                        new OutputJoint(new IntegerJointDataType(), "Degree")
                }
        );
    }

    @Override
    public IntegerJointDataType[] function(InputJoint[] inputJoints) {
        LogicMask mask = (LogicMask) inputJoints[0].getJointDataType().getData();
        return new IntegerJointDataType[] { new IntegerJointDataType(mask.getDegree()) };
    }
}
