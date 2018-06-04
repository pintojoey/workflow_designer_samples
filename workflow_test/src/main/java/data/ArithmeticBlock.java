package data;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;
import cz.zcu.kiv.WorkflowDesigner.Table;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;
import static cz.zcu.kiv.WorkflowDesigner.Type.STRING;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_MANY;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_ONE;

@BlockType(type ="ARITHMETIC", family = "MATH", runAsJar = true)
public class ArithmeticBlock {

    @BlockInput(name = "Operand1", type = NUMBER, cardinality = ONE_TO_ONE)
    private int op1=0;

    @BlockInput(name = "Operand2", type = NUMBER, cardinality = ONE_TO_ONE)
    private int op2=0;

    @BlockOutput(name = "Operand3", type = NUMBER, cardinality = ONE_TO_MANY)
    private static int op3=0;

    @BlockProperty(name ="Operation", type = STRING ,defaultValue = "add")
    private String operation;

    @BlockExecute
    public Table process() throws IOException {
        switch (operation){
            case "add":
                op3=op1+op2;
                break;
            case "subtract":
                op3=op1-op2;
                break;
            case "multiply":
                op3=op1*op2;
                break;
        }
        //File file = new File("results.txt");
        //ileUtils.writeStringToFile(file,String.valueOf(op3),Charset.defaultCharset());
        Table table=new Table();
        ArrayList<String>rowHeaders=new ArrayList<>();
        rowHeaders.add("1");
        rowHeaders.add("2");
        ArrayList<String>colHeaders=new ArrayList<>();
        colHeaders.add("ColA");
        colHeaders.add("ColB");
        ArrayList<ArrayList<String>>data=new ArrayList<>();
        ArrayList<String>row1=new ArrayList<>();
        row1.add(String.valueOf(op3));
        row1.add(String.valueOf(op3));
        data.add(row1);
        ArrayList<String>row2=new ArrayList<>();
        row2.add(String.valueOf(op3));
        row2.add(String.valueOf(op3));
        data.add(row2);
        table.setColumnHeaders(colHeaders);
        table.setRowHeaders(rowHeaders);
        table.setRows(data);
        return table;
    }

    public static int getOp3() {
        return op3;
    }
}
