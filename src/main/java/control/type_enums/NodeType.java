package control.type_enums;

import logic.node.nodes.basic_operations.*;
import logic.node.nodes.branch.ConditionalBranchNode;
import logic.node.nodes.branch.ProbabilityBranchNode;
import logic.node.nodes.branch.RandomBranchNode;
import logic.node.nodes.color.ConstantColorNode;
import logic.node.nodes.color.CreateColorNode;
import logic.node.nodes.color.SplitColorNode;
import logic.node.nodes.compare.*;
import logic.node.nodes.interval.CreateIntervalNode;
import logic.node.nodes.constant.*;
import logic.node.nodes.debug.DebugNode;
import logic.node.nodes.logical_operators.*;
import logic.node.nodes.mask.*;
import logic.node.nodes.math_functions.*;
import logic.node.nodes.type_cast.*;
import logic.node.nodes.update.UpdateNode;

public enum NodeType {

    //BASIC OPERATIONS
    ADD_NODE(2, 1, "Add", "Basic Operations", AddNode.class),
    SUBTRACT_NODE(2, 1, "Subtract", "Basic Operations", SubtractNode.class),
    MULTIPLY_NODE(2, 1, "Multiply", "Basic Operations", MultiplyNode.class),
    DIVIDE_NODE(2, 1, "Divide", "Basic Operations", DivideNode.class),

    //BRANCH
    CONDITIONAL_BRANCH_NODE(3, 1, "Conditional Branch", "Branch", ConditionalBranchNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),
    RANDOM_BRANCH_NODE(2, 1, "Random Branch", "Branch", RandomBranchNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),
    PROBABILITY_BRANCH_NODE(3, 1, "Probability Branch", "Branch", ProbabilityBranchNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),

    //COLOR
    CONSTANT_COLOR_NODE(0, 1, "Constant Color", "Color", ConstantColorNode.class, new InputDialogType[] { InputDialogType.COLOR_TYPE_INPUT }),
    CREATE_COLOR_NODE(3, 1, "Create Color", "Color", CreateColorNode.class),
    SPLIT_COLOR_NODE(1, 3, "Split Color", "Color", SplitColorNode.class),

    //COMPARE
    EQUALS_NODE(2, 1, "Equals", "Compare", EqualsNode.class),
    GREATER_NODE(2, 1, "Greater Than", "Compare", GreaterNode.class),
    LESS_NODE(2, 1, "Less Than", "Compare", LessNode.class),

    //Constant
    CONSTANT_INTEGER_NODE(0, 1, "Constant Integer", "Constant", ConstantIntegerNode.class, new InputDialogType[] { InputDialogType.INTEGER_TYPE_INPUT }),
    CONSTANT_NUMBER_NODE(0, 1, "Constant Number", "Constant", ConstantNumberNode.class, new InputDialogType[] { InputDialogType.NUMBER_TYPE_INPUT }),
    CONSTANT_UNIT_NUMBER_NODE(0, 1, "Constant Unit Number", "Constant", ConstantUnitNumberNode.class, new InputDialogType[] {InputDialogType.UNIT_NUMBER_TYPE_INPUT }),
    PI_NODE(0, 1, "PI", "Constant", PiNode.class),

    //DEBUG
    DEBUG_NODE(1, 1, "Debug Node", "Debug", DebugNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),

    //INTERVAL
    CREATE_INTERVAL_NODE(2, 1, "Create Interval", "Interval", CreateIntervalNode.class),

    //LOGICAL OPERATORS
    LOGICAL_AND_NODE(2, 1, "AND", "Logical Operators", LogicalAndNode.class),
    LOGICAL_OR_NODE(2, 1, "OR", "Logical Operators", LogicalOrNode.class),
    LOGICAL_NOT_NODE(1, 1, "NOT", "Logical Operators", LogicalNotNode.class),

    //MASK
    PLAIN_MASK_NODE(2, 1, "_PLAIN_MASK_", "Mask", MaskNode.class, true),
    MASK_ADDITION_NODE(2, 1, "Mask Addition", "Mask", MaskAdditionNode.class, true),
    MASK_SUBTRACTION_NODE(2, 1, "Mask Subtraction", "Mask", MaskSubtractionNode.class, true),
    MASK_DIFFERENCE_NODE(2, 1, "Mask Difference", "Mask", MaskDifferenceNode.class, true),
    MASK_UNION_NODE(2, 1, "Mask Union", "Mask", MaskUnionNode.class, true),
    INVERTED_MASK_NODE(2, 1, "Inverted Mask", "Mask", InvertedMaskNode.class, true),
    MOVE_MASK_NODE(3, 1, "Move Mask", "Mask", MoveMaskNode.class, true),
    SCALE_MASK_NODE(3, 1, "Scale Mask", "Mask", ScaleMaskNode.class, true),
    SHOW_MASK_NODE(1, 0, "Show Mask", "Mask", ShowMaskNode.class, true),
    GET_DEGREE_NODE(1, 1, "Get Degree", "Mask", GetDegreeNode.class, false),


    //MATH FUNCTIONS
    //TODO : round (+ up/down), power, square root, modulo
    SIN_NODE(1, 1, "Sine Wave", "Math Functions", SinNode.class),
    COS_NODE(1, 1, "Cosine Wave", "Math Functions", CosNode.class),
    LERP_NODE(3, 1, "Linear Interpolation", "Math Functions", LerpNode.class),

    //TYPE CAST
    //TODO : Boolean-Int, Boolean-Unit
    NUMBER_TO_UNIT_NODE(1, 1, "Number to Unit", "Type Cast", CastNumberToUnitNode.class),
    UNIT_TO_NUMBER_NODE(1, 1, "Unit to Number", "Type Cast", CastUnitToNumberNode.class),
    INT_TO_NUMBER_NODE(1, 1, "Int to Number", "Type Cast", CastIntToNumberNode.class),
    BOOLEAN_TO_NUMBER_NODE(1, 1, "Boolean to Number", "Type Cast", CastBooleanToNumberNode.class),

    //UPDATE
    UPDATE_NODE(2, 1, "Update Node", "Update", UpdateNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT });

    private final int numberInputNodes;
    private final int numberOutputNodes;
    private final String name;
    private final String categoryName;
    private final Class nodeClass;

    private InputDialogType[] inputDialogTypes;
    private boolean maskOutput;

    NodeType(int numberInputNodes, int numberOutputNodes, String name, String categoryName, Class nodeClass, InputDialogType[] inputDialogTypes, boolean maskOutput) {
        this.numberInputNodes = numberInputNodes;
        this.numberOutputNodes = numberOutputNodes;
        this.name = name;
        this.categoryName = categoryName;
        this.nodeClass = nodeClass;

        this.inputDialogTypes = inputDialogTypes;
        this.maskOutput = maskOutput;
    }

    NodeType(int numberInputNodes, int numberOutputNodes, String name, String categoryName, Class nodeClass) {
        this(numberInputNodes, numberOutputNodes, name, categoryName, nodeClass, new InputDialogType[0], false);
    }

    NodeType(int numberInputNodes, int numberOutputNodes, String name, String categoryName, Class nodeClass, InputDialogType[] inputDialogTypes) {
        this(numberInputNodes, numberOutputNodes, name, categoryName, nodeClass, inputDialogTypes, false);
    }

    NodeType(int numberInputNodes, int numberOutputNodes, String name, String categoryName, Class nodeClass, boolean maskOutput) {
        this(numberInputNodes, numberOutputNodes, name, categoryName, nodeClass, new InputDialogType[0], maskOutput);
    }

    public int getNumberInputNodes() {
        return this.numberInputNodes;
    }
    public int getNumberOutputNodes() {
        return this.numberOutputNodes;
    }
    public String getName() {
        return this.name;
    }
    public String getCategoryName() {
        return this.categoryName;
    }
    public Class getNodeClass() { return this.nodeClass; }

    public InputDialogType[] getInputDialogTypes() {
        return this.inputDialogTypes;
    }
    public boolean hasMaskOutput() {
        return this.maskOutput;
    }

    public static NodeType getNodeTypeByTypeClass(Class nodeClass) {
        for(NodeType nodeType : NodeType.values()) {
            if(nodeType.getNodeClass() == nodeClass) {
                return nodeType;
            }
        }
        return null;
    }
}
