package cz.zcu.kiv.eeg.basil.workflow;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.STRING;
import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;

@BlockType(type="NeuralNetworkLayer", family = "classification")
public class NeuralNetworkLayerChain implements Serializable {
    static Log log=LogFactory.getLog(NeuralNetworkLayerChain.class);

    @BlockInput(name ="LayerChain",type="NeuralNetworkLayerChain")
    @BlockOutput(name ="LayerChain",type="NeuralNetworkLayerChain")
    NeuralNetworkLayerChain neuralNetworkLayerChain;

    List<Layer> layerArraylist;

    @BlockProperty(name="NumberOfInputs", type=NUMBER)
    Integer nIn;

    @BlockProperty(name="NumberOfOutputs", type=NUMBER)
    Integer nOut;

    @BlockProperty(name="LayerType", type=STRING)
    String layerType;

    @BlockProperty(name="DropOut", type=NUMBER)
    Double dropOut;

    @BlockProperty(name="ActivationFunction", type=STRING)
    String activationFunction;

    @BlockProperty(name="LossFunction", type=STRING)
    String lossFunction;

    @BlockExecute
    void process(){
        if(neuralNetworkLayerChain==null){
            neuralNetworkLayerChain=this;
        }
        if(neuralNetworkLayerChain.layerArraylist==null){
            neuralNetworkLayerChain.layerArraylist=new ArrayList<>();
        }
        Layer layer;
        switch (layerType){
            case "output" : layer=
                    new OutputLayer.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                           // .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .lossFunction(parseLossFunction(lossFunction))
                            .build();
                    break;
            case "dense" : layer=
                    new DenseLayer.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                          //  .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .build();
                break;
            case "auto_encoder" : layer=
                    new AutoEncoder.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                          //  .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .build();
                break;
//            case "rbm" : layer=
//                    new RBM.Builder()
//                            .nIn(nIn)
//                            .nOut(nOut)
//                            .dropOut(dropOut)
//                            .activation(parseActivation(activationFunction))
//                            .build();
//                break;
            case "graves_lstm" : layer=
                    new GravesLSTM.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                            .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .build();
                break;
            default:
                log.fatal("Unsupported layer type");
                layer=null;

        }
        if(layer!=null)
            neuralNetworkLayerChain.layerArraylist.add(layer);

    }

    private WeightInit parseWeightInit(String command){
        switch (command){
            case "xavier" : return WeightInit.XAVIER;
            case "zero" : return WeightInit.ZERO;
            case "sigmoid" : return WeightInit.SIGMOID_UNIFORM;
            case "uniform" : return WeightInit.UNIFORM;
            case "relu" : return WeightInit.RELU;
            default: return WeightInit.RELU;
        }
    }

    private Updater parseUpdater(String command){
        switch (command){
            case "sgd" : return Updater.SGD;
            case "adam" : return Updater.ADAM;
            case "nesterovs" : return Updater.NESTEROVS;
            case "adagrad" : return Updater.ADAGRAD;
            case "rmsprop" : return Updater.RMSPROP;
            default: return Updater.NESTEROVS;
        }
    }

    private OptimizationAlgorithm parseOptimizationAlgo(String command){
        switch (command){
            case "line_gradient_descent" : return OptimizationAlgorithm.LINE_GRADIENT_DESCENT;
            case "lbfgs" : return OptimizationAlgorithm.LBFGS;
            case "conjugate_gradient" : return OptimizationAlgorithm.CONJUGATE_GRADIENT;
            case "stochastic_gradient_descent" : return OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
            default: return OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
        }
    }

    public static LossFunctions.LossFunction parseLossFunction(String command){
        switch (command){
            case "mse" : return LossFunctions.LossFunction.MSE;
            case "xent" : return LossFunctions.LossFunction.XENT;
            case "squared_loss" : return LossFunctions.LossFunction.SQUARED_LOSS;
            case "negativeloglikelihood" : return LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD;
            default: return LossFunctions.LossFunction.MSE;
        }
    }


    public static Activation parseActivation(String command){
        switch (command){
            case "sigmoid" : return Activation.SIGMOID;
            case "softmax" : return Activation.SOFTMAX;
            case "relu" : return Activation.RELU;
            case "tanh" : return Activation.TANH;
            case "identity" : return Activation.IDENTITY;
            case "softplus" : return Activation.SOFTPLUS;
            case "elu" : return Activation.ELU;
            default: return Activation.SIGMOID;
        }
    }

    public List<Layer> getLayerArraylist() {
        return layerArraylist;
    }

}
