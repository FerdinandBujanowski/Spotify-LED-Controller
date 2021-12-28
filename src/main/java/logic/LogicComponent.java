package logic;

import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;

import java.io.Serializable;

public abstract class LogicComponent implements Serializable {

    private final InputJoint[] inputJoints;
    private final OutputJoint[] outputJoints;

    public LogicComponent(InputJoint[] inputJoints, OutputJoint[] outputJoints) {
        this.inputJoints = inputJoints;
        this.outputJoints = outputJoints;
    }

    public InputJoint[] getInputJoints() {
        return this.inputJoints;
    }
    public OutputJoint[]getOutputJoints() {
        return this.outputJoints;
    }
}
