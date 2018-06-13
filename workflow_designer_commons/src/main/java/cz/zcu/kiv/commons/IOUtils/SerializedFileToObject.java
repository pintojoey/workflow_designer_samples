package cz.zcu.kiv.commons.IOUtils;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockExecute;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockInput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockOutput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;
import cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.IOException;

@BlockType(type="SerializedFileToObject",family = "IOUtils")
public class SerializedFileToObject {
    @BlockInput(name = "File", type = "FILE", cardinality = WorkflowCardinality.ONE_TO_ONE)
    private File fileInput;

    @BlockOutput(name="Object", type="OBJECT", cardinality = WorkflowCardinality.ONE_TO_MANY)
    private Object objectOutput;

    @BlockExecute
    public void process() throws IOException {
            objectOutput = SerializationUtils.deserialize(FileUtils.readFileToByteArray(fileInput));
    }
}
