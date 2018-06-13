package cz.zcu.kiv.commons.IOUtils;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockExecute;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockInput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockOutput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;
import cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@BlockType(type="ObjectToSerializedFile",family = "IOUtils")
public class ObjectToSerializedFile {

    @BlockInput(name="Object", type="OBJECT", cardinality = WorkflowCardinality.ONE_TO_ONE)
    private Object objectInput;

    @BlockOutput(name = "File", type = "FILE", cardinality = WorkflowCardinality.ONE_TO_MANY)
    private File fileOutput;

    @BlockExecute
    public void process() throws IOException {
        fileOutput = new File("file_"+new Date().getTime()+".tmp");
        FileOutputStream fos = FileUtils.openOutputStream(fileOutput);
        SerializationUtils.serialize((Serializable) objectInput,fos);
        fos.close();
    }
}