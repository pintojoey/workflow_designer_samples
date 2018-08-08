package cz.zcu.kiv.eeg.basil.workflow;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockExecute;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockInput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;
import cz.zcu.kiv.WorkflowDesigner.Visualizations.Table;
import org.deeplearning4j.eval.Evaluation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Average a list of trainingEEGData using one stimuli marker
 * @author lvareka
 *
 */
@BlockType(type="NeuralNetClassifier",family = "Classification", runAsJar = true)
public class NeuralNetClassifierBlock implements Serializable {

	@BlockInput(name = "Markers",type="EEGMarker[]")
	private List<EEGMarker> markers;

	@BlockInput(name = "TrainingEEGData", type = "EEGDataList")
	private EEGDataPackageList trainingEEGData;

    @BlockInput(name = "TestingEEGData", type = "EEGDataList")
    private EEGDataPackageList testingEEGData;

    @BlockInput(name="Layers", type="NeuralNetworkLayerChain")
    private NeuralNetworkLayerChain layerChain;

	public NeuralNetClassifierBlock(){
		//Required Empty Default Constructor for Workflow Designer
	}

	@BlockExecute
    public Object process(){
		ITrainCondition trainCondition = new ErpTrainCondition();
		for (EEGDataPackage dataPackage : trainingEEGData.getEegDataPackage()) {

			String marker;
			if(dataPackage.getMarkers() == null || dataPackage.getMarkers().get(0) == null)
				continue;
			marker = dataPackage.getMarkers().get(0).getName();

			FeatureVector fv = new FeatureVector();
			WaveletTransformFeatureExtraction dwt = new WaveletTransformFeatureExtraction();
			FeatureVector features = dwt.extractFeatures(dataPackage);
			fv.addFeatures(features);

			//markers contains S 2 and S4
            for( EEGMarker markerBlock:markers) {
                trainCondition.addSample(fv, markerBlock.getName(), marker);
            }

		}
		SDADeepLearning4jClassifier classification = new SDADeepLearning4jClassifier(layerChain.layerArraylist);
        Evaluation eval = classification.train(trainCondition.getFeatureVectors(), 10);
        if(testingEEGData!=null){
            List<FeatureVector> testingVectors=new ArrayList<>();
            List<Double>testingTargets=new ArrayList<>();
            for (EEGDataPackage dataPackage : testingEEGData.getEegDataPackage()) {

                FeatureVector fv = new FeatureVector();
                WaveletTransformFeatureExtraction dwt = new WaveletTransformFeatureExtraction();
                FeatureVector features = dwt.extractFeatures(dataPackage);
                fv.addFeatures(features);

                testingVectors.add(fv);
                testingTargets.add(fv.getExpectedOutput()); //What to add here?
            }
            ClassificationStatistics statistics = classification.test(testingVectors, testingTargets);
            return statistics.toString();

        }

        Table table = new Table();
        List<List<String>>rows=new ArrayList<>();
        rows.add(Arrays.asList("Precision",String.valueOf(eval.precision())));
        rows.add(Arrays.asList("Recall",String.valueOf(eval.recall())));
        rows.add(Arrays.asList("Accuracy",String.valueOf(eval.accuracy())));
        rows.add(Arrays.asList("F1 Score",String.valueOf(eval.f1())));

        //Confusion Matrix
        rows.add(Arrays.asList("True +ve",String.valueOf(eval.getTruePositives().totalCount())));
        rows.add(Arrays.asList("True -ve",String.valueOf(eval.getTrueNegatives().totalCount())));
        rows.add(Arrays.asList("False +ve",String.valueOf(eval.getFalsePositives().totalCount())));
        rows.add(Arrays.asList("False -ve",String.valueOf(eval.getFalseNegatives().totalCount())));
        table.setRows(rows);
        table.setCaption("Testing Dataset Results");

        return table;
    }


    public List<EEGMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<EEGMarker> markers) {
        this.markers = markers;
    }

    public EEGDataPackageList getTrainingEEGData() {
        return trainingEEGData;
    }

    public void setTrainingEEGData(EEGDataPackageList trainingEEGData) {
        this.trainingEEGData = trainingEEGData;
    }


}
