package expo;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;

import java.io.IOException;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_MANY;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_ONE;

@BlockType(type ="ExponentialBlock", family = "MATH")
public class ExponentialBlock {

    @BlockInput(name = "Operand1", type = NUMBER, cardinality = ONE_TO_ONE)
    private int op1=0;

    @BlockInput(name = "Operand2", type = NUMBER, cardinality = ONE_TO_ONE)
    private int op2=0;

    @BlockOutput(name = "Operand3", type = NUMBER, cardinality = ONE_TO_MANY)
    private static int op3=0;


    @BlockExecute
    public String process()  {
        op3=(int)Math.pow(op1,op2);
        return String.valueOf(op3);
    }

    public static int getOp3() {
        return op3;
    }
}
