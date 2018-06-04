package cz.zcu.kiv.WorkflowConstants;
/***********************************************************************************************************************
 *
 * This file is part of the EEG_Analysis project

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
 * DataField, 2018/17/05 6:32 Joey Pinto
 *
 * This class declares constants to be used as names for input/output fields
 **********************************************************************************************************************/

public class DataField {
    public static final String SIGNAL_INPUT = "SignalIn";
    public static final String SIGNAL_OUTPUT = "SignalOut";
    public static final String CLASSIFICATION_MODEL_OUTPUT = "ClassifyModelOut";
    public static final String CLASSIFICATION_STATISTICS_OUTPUT = "ClassifyStatsOut";
    public static final String FEATURE_EXTRACTOR_OUTPUT = "FeatureExtractorOut";
    public static final String RAW_EPOCHS_OUTPUT = "RawEpochsOut";
    public static final String RAW_TARGETS_OUTPUT = "RawTargetsOut";
}
