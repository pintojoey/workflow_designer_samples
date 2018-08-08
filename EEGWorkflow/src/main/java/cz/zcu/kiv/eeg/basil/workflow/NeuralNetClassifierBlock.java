package cz.zcu.kiv.eeg.basil.workflow;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockExecute;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockInput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockOutput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;
import cz.zcu.kiv.WorkflowDesigner.Visualizations.Table;
import org.deeplearning4j.eval.Evaluation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Average a list of epochs using one stimuli marker
 * @author lvareka
 *
 */
@BlockType(type="NeuralNetClassifier",family = "Classification", runAsJar = true)
public class NeuralNetClassifierBlock implements Serializable {

	@BlockInput(name = "Markers",type="EEGMarker[]")
	private List<EEGMarker> markers;

	@BlockInput(name = "EEGData", type = "EEGDataList")
	private EEGDataPackageList epochs;

    @BlockInput(name="Layers", type="NeuralNetworkLayerChain")
    private NeuralNetworkLayerChain layerChain;

	public NeuralNetClassifierBlock(){
		//Required Empty Default Constructor for Workflow Designer
	}

	@BlockExecute
    public Table process(){
		ITrainCondition trainCondition = new ErpTrainCondition();
		for (EEGDataPackage dataPackage : epochs.getEegDataPackage()) {

			String marker;
			if(dataPackage.getMarkers() == null || dataPackage.getMarkers().get(0) == null)
				continue;
			marker = dataPackage.getMarkers().get(0).getName();

			FeatureVector fv = new FeatureVector();
			WaveletTransformFeatureExtraction dwt = new WaveletTransformFeatureExtraction();

			FeatureVector features = dwt.extractFeatures(dataPackage);
			fv.addFeatures(features);

			for(EEGMarker marker1:markers){
                trainCondition.addSample(fv, marker1.getName(), marker);
            }

		}
		SDADeepLearning4jClassifier classification = new SDADeepLearning4jClassifier(layerChain.layerArraylist);
        Evaluation eval = classification.train(trainCondition.getFeatureVectors(), 10);
        Table table = new Table();
        List<List<String>>rows=new ArrayList<>();
        rows.add(Arrays.asList("Precision",String.valueOf(eval.precision())));
        rows.add(Arrays.asList("Recall",String.valueOf(eval.recall())));
        rows.add(Arrays.asList("Accuracy",String.valueOf(eval.accuracy())));
        rows.add(Arrays.asList("F1 Score",String.valueOf(eval.f1())));

        //Confusion Matrix
        rows.add(Arrays.asList("True +ve",String.valueOf(eval.truePositives().size())));
        rows.add(Arrays.asList("True -ve",String.valueOf(eval.trueNegatives().size())));
        rows.add(Arrays.asList("False +ve",String.valueOf(eval.falsePositives().size())));
        rows.add(Arrays.asList("False -ve",String.valueOf(eval.falseNegatives().size())));
        table.setRows(rows);

        return table;
    }


    public List<EEGMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<EEGMarker> markers) {
        this.markers = markers;
    }

    public EEGDataPackageList getEpochs() {
        return epochs;
    }

    public void setEpochs(EEGDataPackageList epochs) {
        this.epochs = epochs;
    }


}
