package data;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;

@BlockType(type ="CONSTANT", family = "MATH", runAsJar = true)
public class ConstantBlock {


    @BlockProperty(name = "Value", type = NUMBER, defaultValue = "0")
    private int val=7;

    @BlockOutput(name = "Operand", type = NUMBER)
    private int op=0;

    @BlockExecute
    public void process(){
        op=val;
    }
}
