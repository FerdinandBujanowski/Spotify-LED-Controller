package logic.node.nodes.math_functions;

import control.type_enums.RoundAlgorithmType;
import logic.node.LogicNode;
import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;
import logic.node.joint.joint_types.IntegerJointDataType;
import logic.node.joint.joint_types.NumberJointDataType;

public class RoundNode extends LogicNode {

    private final RoundAlgorithmType roundAlgorithmType;

    public RoundNode(int nodeIndex, RoundAlgorithmType roundAlgorithmType) {
        super(
                nodeIndex,
                new InputJoint[] {
                        new InputJoint(new NumberJointDataType(), "Number")
                },
                new OutputJoint[] {
                        new OutputJoint(
                                new IntegerJointDataType(),
                                "Output [" + roundAlgorithmType.toString().toUpperCase().substring(0, 3) + "]"
                        )
                }
        );
        this.roundAlgorithmType = roundAlgorithmType;
    }

    @Override
    public IntegerJointDataType[] function(InputJoint[] inputJoints) {
        double data = (Double) inputJoints[0].getJointDataType().getData();
        int roundedData = 0;
        switch(this.roundAlgorithmType) {
            case ROUND -> roundedData = (int)Math.round(data);
            case FLOOR -> roundedData = (int)Math.floor(data);
            case CEILING -> roundedData = (int)Math.ceil(data);
        }
        return new IntegerJointDataType[] {
                new IntegerJointDataType(roundedData)
        };
    }
}
