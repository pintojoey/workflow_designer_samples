package cz.zcu.kiv.Classification;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;
import org.deeplearning4j.nn.conf.layers.*;

import java.util.ArrayList;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.STRING;
import static cz.zcu.kiv.Classification.NeuralNetworkClassifier.parseActivation;
import static cz.zcu.kiv.Classification.NeuralNetworkClassifier.parseLossFunction;
import static cz.zcu.kiv.WorkflowConstants.WorkflowFamily.MACHINE_LEARNING;
import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;

@BlockType(type="NeuralNetworkLayer", family = MACHINE_LEARNING)
public class NeuralNetworkLayerChain {

    @BlockInput(name ="LayerChain",type="NeuralNetworkLayerChain")
    @BlockOutput(name ="LayerChain",type="NeuralNetworkLayerChain")
    ArrayList<Layer> layerArraylist;

    @BlockProperty(name="NumberOfInputs", type=NUMBER)
    int nIn;

    @BlockProperty(name="NumberOfOutputs", type=NUMBER)
    int nOut;

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
        if(layerArraylist==null){
            layerArraylist=new ArrayList<>();
        }
        Layer layer;
        switch (layerType){
            case "output" : layer=
                    new OutputLayer.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                            .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .lossFunction(parseLossFunction(lossFunction))
                            .build();
                    break;
            case "dense" : layer=
                    new DenseLayer.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                            .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .build();
                break;
            case "auto_encoder" : layer=
                    new AutoEncoder.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                            .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .build();
                break;
            case "rbm" : layer=
                    new RBM.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                            .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .build();
                break;
            case "graves_lstm" : layer=
                    new GravesLSTM.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                            .dropOut(dropOut)
                            .activation(parseActivation(activationFunction))
                            .build();
                break;
            default: layer=
                    new OutputLayer.Builder()
                            .nIn(nIn)
                            .nOut(nOut)
                            .activation(parseActivation(activationFunction))
                            .lossFunction(parseLossFunction(lossFunction))
                            .build()
                    ;
        }
        layerArraylist.add(layer);
    }

}
