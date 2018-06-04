package cz.zcu.kiv.Classification;

import cz.zcu.kiv.FeatureExtraction.IFeatureExtraction;
import cz.zcu.kiv.Utils.ClassificationStatistics;
import cz.zcu.kiv.Utils.SparkInitializer;
import cz.zcu.kiv.WorkflowDesigner.Annotations.*;
import cz.zcu.kiv.WorkflowDesigner.BlockData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.regression.LabeledPoint;
import scala.Tuple2;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import static cz.zcu.kiv.WorkflowConstants.DataField.*;
import static cz.zcu.kiv.WorkflowConstants.DataType.*;
import static cz.zcu.kiv.WorkflowConstants.Field.*;
import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;
import static cz.zcu.kiv.WorkflowConstants.WorkflowBlock.SVM_CLASSIFIER;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_MANY;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_ONE;
import static cz.zcu.kiv.WorkflowConstants.WorkflowFamily.MACHINE_LEARNING;

/***********************************************************************************************************************
 *
 * This file is part of the Spark_EEG_Analysis project

 * ==========================================
 *
 * Copyright (C) 2018 by University of West Bohemia (http://www.zcu.cz/en/)
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * SVMClassifier, 2017/06/27 12:13 Dorian Beganovic
 *
 **********************************************************************************************************************/
@BlockType(type = SVM_CLASSIFIER, family = MACHINE_LEARNING, runAsJar = true)
public class SVMClassifier implements IClassifier,Serializable {

    private static Log logger = LogFactory.getLog(SVMClassifier.class);

    @BlockInput( name = FEATURE_EXTRACTOR_OUTPUT, type = FEATURE_EXTRACTOR , cardinality = ONE_TO_ONE)
    private static IFeatureExtraction fe;

    @BlockOutput( name =CLASSIFICATION_MODEL_OUTPUT, type = CLASSIFCATION_MODEL, cardinality = ONE_TO_MANY)
    private static SVMModel model;

    private HashMap<String,String> config;

    @BlockProperty(name = ITERATIONS_FIELD, type = NUMBER , defaultValue = "10")
    private int ITERATIONS;

    @BlockProperty(name = STEP_SIZE_FIELD , type = NUMBER, defaultValue =  "1")
    private int STEP_SIZE;

    @BlockProperty(name = REG_PARAMETERS_FIELD, type = NUMBER, defaultValue = "1")
    private double REG_PARAMETERS;

    @BlockProperty(name = MINI_BATCH_FRACTION_FIELD, type = NUMBER, defaultValue = "1")
    private double MINI_BATCH_FRACTION;


    private static Function<double[][], double[]> featureExtractionFunc = new Function<double[][], double[]>() {
        public double[] call(double[][] epoch) {
            return fe.extractFeatures(epoch);
        }
    };

    @BlockInput(name = RAW_EPOCHS_OUTPUT, type = EPOCH_LIST, cardinality = ONE_TO_ONE)
    private List<double[][]>epochs;

    @BlockInput(name = RAW_TARGETS_OUTPUT, type = TARGET_LIST, cardinality = ONE_TO_ONE)
    private List<Double>targets;

    private static Function<Tuple2<Double, double[]>, LabeledPoint> unPackFunction = new Function<Tuple2<Double, double[]>, LabeledPoint>() {
        @Override
        public LabeledPoint call(Tuple2<Double, double[]> v1) throws Exception {
            return new LabeledPoint(v1._1(),new DenseVector(v1._2()));
        }
    };

    private static Function<LabeledPoint, Tuple2<Object, Object>> classifyFunction = new Function<LabeledPoint, Tuple2<Object, Object>>() {
        public Tuple2<Object, Object> call(LabeledPoint p) {
            Double prediction = model.predict(p.features());
            return new Tuple2<Object, Object>(prediction, p.label());
        }
    };


    @Override
    public void setFeatureExtraction(IFeatureExtraction fe) {
        SVMClassifier.fe = fe;
    }

    @Override
    public void train(List<double[][]> epochs, List<Double> targets, IFeatureExtraction fe) {
        SVMClassifier.fe = fe;
        JavaRDD<double[][]> rddEpochs = SparkInitializer.getJavaSparkContext().parallelize(epochs);
        JavaRDD<Double> rddTargets = SparkInitializer.getJavaSparkContext().parallelize(targets);
        JavaRDD<double[]> features = rddEpochs.map(featureExtractionFunc);
        JavaPairRDD<Double, double[]> rawData = rddTargets.zip(features);
        JavaRDD<LabeledPoint> training = rawData.map(unPackFunction);
        // Run training algorithm to build the model
        if(config.containsKey("config_num_iterations") && config.containsKey("config_step_size") &&
        config.containsKey("config_reg_param") && config.containsKey("config_mini_batch_fraction")){
            logger.info("Creating the model with configuration");
                SVMClassifier.model = SVMWithSGD.train(
                        training.rdd(),
                        Integer.parseInt(config.get("config_num_iterations")),
                        Double.parseDouble(config.get("config_step_size")),
                        Double.parseDouble(config.get("config_reg_param")),
                        Double.parseDouble(config.get("config_mini_batch_fraction"))
                );
        }
        else {
            logger.info("Creating the model without configuration");
            SVMClassifier.model = new SVMWithSGD().run(training.rdd());
        }
    }

    @Override
    public ClassificationStatistics test(List<double[][]> epochs, List<Double> targets) {

        if(model==null){
            throw new IllegalStateException("The classifier has not been trained");
        }

        JavaRDD<double[][]> rddEpochs = SparkInitializer.getJavaSparkContext().parallelize(epochs);
        JavaRDD<Double> rddTargets = SparkInitializer.getJavaSparkContext().parallelize(targets);
        JavaRDD<double[]> features = rddEpochs.map(featureExtractionFunc);
        JavaPairRDD<Double, double[]> rawData = rddTargets.zip(features);
        JavaRDD<LabeledPoint> test = rawData.map(unPackFunction);
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = test.map(classifyFunction);
        // Get evaluation metrics
        MulticlassMetrics metrics = new MulticlassMetrics(predictionAndLabels.rdd());

        double[] confusionMatrix = metrics.confusionMatrix().toArray();
        int tn = (int) confusionMatrix[0];
        int fp = (int) confusionMatrix[1];
        int fn = (int) confusionMatrix[2];
        int tp = (int) confusionMatrix[3];
        ClassificationStatistics statistics = new ClassificationStatistics(tp,tn,fp,fn);

        return statistics;
    }

    @Override
    public void save(String file) throws IOException {
        FileUtils.deleteDirectory(new File(file));
        model.save(SparkInitializer.getSparkContext(),file);
    }

    @Override
    public void load(String file) {
        model = SVMModel.load(SparkInitializer.getSparkContext(),file);
    }

    @Override
    public IFeatureExtraction getFeatureExtraction() {
        return fe;
    }

    @Override
    public void setConfig(HashMap<String, String> config) {
        this.config = config;
    }

    @BlockExecute
    public File process() {
        setFeatureExtraction(fe);
        this.config=new HashMap<>();
        config.put("config_num_iterations",String.valueOf(ITERATIONS));
        config.put("config_step_size",String.valueOf(STEP_SIZE));
        config.put("config_reg_param",String.valueOf(REG_PARAMETERS));
        config.put("config_mini_batch_fraction",String.valueOf(MINI_BATCH_FRACTION));

        train(epochs, targets, getFeatureExtraction());
        try {
            File svmFile = new File("svm.model");
            FileOutputStream fis = new FileOutputStream(svmFile);
            ObjectOutputStream oos = new ObjectOutputStream(fis);
            oos.writeObject(model);
            return svmFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
