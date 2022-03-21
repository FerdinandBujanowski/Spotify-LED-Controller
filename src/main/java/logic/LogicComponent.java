package logic;

import logic.node.joint.InputJoint;
import logic.node.joint.OutputJoint;

import java.io.Serializable;
import java.util.Arrays;

public abstract class LogicComponent implements Serializable {

    private InputJoint[] inputJoints;
    private OutputJoint[] outputJoints;

    public LogicComponent(InputJoint[] inputJoints, OutputJoint[] outputJoints) {
        this.inputJoints = inputJoints;
        this.outputJoints = outputJoints;
    }

    public InputJoint[] getInputJoints() {
        return this.inputJoints;
    }
    public void addInputJoint(InputJoint inputJoint) {
        InputJoint[] newInputJoints = Arrays.copyOf(this.inputJoints, this.inputJoints.length + 1);
        newInputJoints[this.inputJoints.length] = inputJoint;
        this.inputJoints = newInputJoints;
    }

    public OutputJoint[]getOutputJoints() {
        return this.outputJoints;
    }
    public void addOuptputJoint(OutputJoint outputJoint) {
        OutputJoint[] newOuptputJoints = Arrays.copyOf(this.outputJoints, this.outputJoints.length + 1);
        newOuptputJoints[this.outputJoints.length] = outputJoint;
        this.outputJoints = newOuptputJoints;
    }
}
