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
import logic.node.nodes.plane.OverlayPlaneNode;
import logic.node.nodes.plane.SimplePlaneNode;
import logic.node.nodes.random.RandomIntegerNode;
import logic.node.nodes.random.RandomUnitNumberNode;
import logic.node.nodes.type_cast.*;
import logic.node.nodes.update.UpdateNode;

public enum NodeType {

    //BASIC OPERATIONS
    ADD_NODE("Add", "Basic Operations", AddNode.class),
    SUBTRACT_NODE("Subtract", "Basic Operations", SubtractNode.class),
    MULTIPLY_NODE("Multiply", "Basic Operations", MultiplyNode.class),
    DIVIDE_NODE("Divide", "Basic Operations", DivideNode.class),

    //BRANCH
    CONDITIONAL_BRANCH_NODE("Conditional Branch", "Branch", ConditionalBranchNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),
    RANDOM_BRANCH_NODE("Random Branch", "Branch", RandomBranchNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),
    PROBABILITY_BRANCH_NODE("Probability Branch", "Branch", ProbabilityBranchNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),

    //COLOR
    CONSTANT_COLOR_NODE("Constant Color", "Color", ConstantColorNode.class, new InputDialogType[] { InputDialogType.COLOR_TYPE_INPUT }),
    CREATE_COLOR_NODE("Create Color", "Color", CreateColorNode.class),
    SPLIT_COLOR_NODE("Split Color", "Color", SplitColorNode.class),

    //COMPARE
    EQUALS_NODE("Equals", "Compare", EqualsNode.class),
    GREATER_NODE("Greater Than", "Compare", GreaterNode.class),
    LESS_NODE("Less Than", "Compare", LessNode.class),

    //Constant
    CONSTANT_INTEGER_NODE("CONST Integer", "Constant", ConstantIntegerNode.class, new InputDialogType[] { InputDialogType.INTEGER_TYPE_INPUT }),
    CONSTANT_NUMBER_NODE("CONST Number", "Constant", ConstantNumberNode.class, new InputDialogType[] { InputDialogType.NUMBER_TYPE_INPUT }),
    CONSTANT_UNIT_NUMBER_NODE("CONST Unit Number", "Constant", ConstantUnitNumberNode.class, new InputDialogType[] {InputDialogType.UNIT_NUMBER_TYPE_INPUT }),
    PI_NODE("PI", "Constant", PiNode.class),

    //DEBUG
    DEBUG_NODE("Debug Node", "Debug", DebugNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),

    //INTERVAL
    CREATE_INTERVAL_NODE("Create Interval", "Interval", CreateIntervalNode.class),

    //LOGICAL OPERATORS
    LOGICAL_AND_NODE("AND", "Logical Operators", LogicalAndNode.class),
    LOGICAL_OR_NODE("OR", "Logical Operators", LogicalOrNode.class),
    LOGICAL_NOT_NODE("NOT", "Logical Operators", LogicalNotNode.class),

    //MASK
    SQUARE_MASK_NODE("Square Mask", "Mask", SquareMaskNode.class, true),
    CIRCLE_MASK_NODE("Circle Mask", "Mask", CircleMaskNode.class, true),
    MASK_ADDITION_NODE("Mask Addition", "Mask", MaskAdditionNode.class, true),
    MASK_SUBTRACTION_NODE("Mask Subtraction", "Mask", MaskSubtractionNode.class, true),
    MASK_DIFFERENCE_NODE("Mask Difference", "Mask", MaskDifferenceNode.class, true),
    MASK_INTERSECTION_NODE("Mask Intersection", "Mask", MaskIntersectionNode.class, true),
    MASK_X_MASK_NODE("Mask X Mask", "Mask", MultiplyMaskWithMaskNode.class, true),
    MASK_X_NUMBER_NODE("Mask X Number", "Mask", MultiplyMaskWithUnitNode.class, true),
    INVERTED_MASK_NODE("Inverted Mask", "Mask", InvertedMaskNode.class, true),
    MOVE_MASK_NODE("Move Mask", "Mask", MoveMaskNode.class, true),
    SCALE_MASK_NODE("Scale Mask", "Mask", ScaleMaskNode.class, new InputDialogType[] { InputDialogType.ROUND_PIXEL_INPUT }, true),
    ROTATE_MASK_NODE("Rotate Mask", "Mask", RotateMaskNode.class, new InputDialogType[] { InputDialogType.ROUND_PIXEL_INPUT }, true),
    SHOW_MASK_NODE("Show Mask", "Mask", ShowMaskNode.class, true),
    GET_DEGREE_NODE("Get Degree", "Mask", GetDegreeNode.class, false),

    //MATH FUNCTIONS
    SIN_NODE("Sine Wave", "Math Functions", SinNode.class),
    COS_NODE("Cosine Wave", "Math Functions", CosNode.class),
    LERP_NODE("Linear Interpolation", "Math Functions", LerpNode.class),
    ROUND_NODE("Round Number", "Math Functions", RoundNode.class, new InputDialogType[] { InputDialogType.ROUND_INPUT }),
    POWER_NODE("Power", "Math Functions", PowerNode.class),
    SQRT_NODE("Square Root", "Math Functions", SquareRootNode.class),
    MODULO_NODE("Modulo", "Math Functions", ModuloNode.class),

    //PLANE
    SIMPLE_PLANE_NODE("Simple Plane", "Plane", SimplePlaneNode.class, true),
    OVERLAY_PLANE_NODE("Overlay Plane", "Plane", OverlayPlaneNode.class, new InputDialogType[] { InputDialogType.COLOR_MIXING_INPUT }, true),

    //RANDOM
    RANDOM_INTEGER_NODE("Random Integer", "Random", RandomIntegerNode.class),
    RANDOM_UNIT_NUMBER_NODE("Random Unit", "Random", RandomUnitNumberNode.class),

    //TYPE CAST
    NUMBER_TO_UNIT_NODE("Number to Unit", "Type Cast", CastNumberToUnitNode.class),
    UNIT_TO_NUMBER_NODE("Unit to Number", "Type Cast", CastUnitToNumberNode.class),
    INT_TO_NUMBER_NODE("Int to Number", "Type Cast", CastIntToNumberNode.class),
    BOOLEAN_TO_NUMBER_NODE("Boolean to Number", "Type Cast", CastBooleanToNumberNode.class),

    //UPDATE
    UPDATE_NODE("Update Node", "Update", UpdateNode.class, new InputDialogType[] { InputDialogType.JOINT_TYPE_INPUT }),

    //COMMANDS
    _FUNCTION_NODE("Function", "Commands", null, new InputDialogType[] { InputDialogType.INTEGER_TYPE_INPUT, InputDialogType.INTEGER_TYPE_INPUT }),
    _INPUT_PARAMETER_NODE("Input Parameter", "Commands", null, new InputDialogType[] { InputDialogType.INTEGER_TYPE_INPUT, InputDialogType.JOINT_TYPE_INPUT, InputDialogType.STRING_TYPE_INPUT }),
    _OUTPUT_PARAMETER_NODE("Output Parameter", "Commands", null, new InputDialogType[] { InputDialogType.INTEGER_TYPE_INPUT, InputDialogType.JOINT_TYPE_INPUT, InputDialogType.STRING_TYPE_INPUT }),
    _TRACK_NODE("Track", "Commands", null, new InputDialogType[] { InputDialogType.INTEGER_TYPE_INPUT }),
    _LED_POSITION_NODE("LED Position", "Commands", null),
    _LAYER_NODE("Layer", "Commands", null);

    private final String name;
    private final String categoryName;
    private final Class nodeClass;

    private InputDialogType[] inputDialogTypes;
    private boolean maskOutput;

    NodeType(String name, String categoryName, Class nodeClass, InputDialogType[] inputDialogTypes, boolean maskOutput) {
        this.name = name;
        this.categoryName = categoryName;
        this.nodeClass = nodeClass;

        this.inputDialogTypes = inputDialogTypes;
        this.maskOutput = maskOutput;
    }

    NodeType(String name, String categoryName, Class nodeClass) {
        this(name, categoryName, nodeClass, new InputDialogType[0], false);
    }

    NodeType(String name, String categoryName, Class nodeClass, InputDialogType[] inputDialogTypes) {
        this(name, categoryName, nodeClass, inputDialogTypes, false);
    }

    NodeType(String name, String categoryName, Class nodeClass, boolean maskOutput) {
        this(name, categoryName, nodeClass, new InputDialogType[0], maskOutput);
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

    public static NodeType getNodeTypeByString(String nodeTypeString) {
        for(NodeType nodeType : NodeType.values()) {
            if(nodeType.toString().equals(nodeTypeString)) {
                return nodeType;
            }
        }
        return null;
    }
}
