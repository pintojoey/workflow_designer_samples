package cz.zcu.kiv.commons.MathUtils;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockExecute;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockOutput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockProperty;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;

@BlockType(type ="DelayedConstant", family = "MATH")
public class DelayedConstant {


    @BlockProperty(name = "Value", type = NUMBER, defaultValue = "0")
    private int val=7;

    @BlockProperty(name = "Delay", type = NUMBER, defaultValue = "0")
    private int delay=0;

    @BlockOutput(name = "Operand", type = NUMBER)
    private int op=0;

    @BlockExecute
    public void process(){
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        op=val;
    }
}
