package cz.zcu.kiv.commons.IOUtils;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;
import cz.zcu.kiv.WorkflowDesigner.Type;
import cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@BlockType(type="FileToString",family = "IOUtils")
public class FileToString {
    @BlockProperty(name = "File", type = "file" ,defaultValue = "")
    private File fileInput;

    @BlockOutput(name="String", type=Type.STRING, cardinality = WorkflowCardinality.ONE_TO_MANY)
    private String stringOutput;

    @BlockExecute
    public void process() throws IOException {
            stringOutput = FileUtils.readFileToString(fileInput,Charset.defaultCharset());
    }
}
