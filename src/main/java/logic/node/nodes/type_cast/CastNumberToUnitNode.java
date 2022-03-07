package logic.node.nodes.type_cast;

import control.type_enums.NodeType;
import logic.node.LogicNode;
import logic.node.joint.*;
import logic.node.joint.joint_types.*;

public class CastNumberToUnitNode extends LogicNode {

    public CastNumberToUnitNode(int nodeIndex) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "Number")
                },
                new OutputJoint[] {
                        new OutputJoint(new UnitNumberJointDataType(), "Unit Number")
                },
                NodeType.NUMBER_TO_UNIT_NODE
        );
    }

    @Override
    public UnitNumberJointDataType[] function(InputJoint[] inputJoints) {
        double input = (Double) inputJoints[0].getJointDataType().getData();
        return new UnitNumberJointDataType[] { new UnitNumberJointDataType(input) };
    }
}
