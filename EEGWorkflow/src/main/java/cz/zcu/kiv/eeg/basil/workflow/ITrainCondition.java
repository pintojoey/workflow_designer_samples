package cz.zcu.kiv.eeg.basil.workflow;

import java.util.List;

/**
 * Created by Tomas Prokop on 15.08.2017.
 */
public interface ITrainCondition {
    boolean canAddSample(String targetMarker, String marker);
    void addSample(FeatureVector fv, String targetMarker, String marker);
    List<FeatureVector> getFeatureVectors();
    
}
