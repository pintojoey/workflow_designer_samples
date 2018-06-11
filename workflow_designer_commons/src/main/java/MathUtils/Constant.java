package MathUtils;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockExecute;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockOutput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockProperty;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_ONE;

@BlockType(type ="CONSTANT", family = "MATH")
public class Constant {


    @BlockProperty(name = "Value", type = NUMBER, defaultValue = "0")
    private int val=7;

    @BlockOutput(name = "Operand", type = NUMBER, cardinality = ONE_TO_ONE)
    private int op=0;

    @BlockExecute
    public void process(){
        op=val;
    }
}
