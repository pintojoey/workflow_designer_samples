package cz.zcu.kiv.Utils;

import java.io.Serializable;

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
 * ClassificationStatistics, 2017/06/18 14:31 Dorian Beganovic
 *
 **********************************************************************************************************************/
public class ClassificationStatistics implements Serializable {

    private int truePositives;
    private int trueNegatives;
    private int falsePositives;
    private int falseNegatives;
    private double MSE;

    // only for testing
    private double class1sum;
    private double class2sum;

    public ClassificationStatistics() {
        this.truePositives = 0;
        this.trueNegatives = 0;
        this.falsePositives = 0;
        this.falseNegatives = 0;
        this.MSE = 0;

        this.class1sum = 0;
        this.class2sum = 0;
    }

    public ClassificationStatistics(int truePositives, int trueNegatives, int falsePositives, int falseNegatives) {
        this.truePositives = truePositives;
        this.trueNegatives = trueNegatives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
        this.MSE = 0;
        this.class1sum = 0;
        this.class2sum = 0;
    }

    public double calcAccuracy() {
        return ((double) truePositives + trueNegatives) / getNumberOfPatterns();
    }

    public int getNumberOfPatterns() {
        return (truePositives + trueNegatives + falsePositives + falseNegatives);
    }

    public void add(double realOutput, double expectedOutput) {
        this.MSE += Math.pow(expectedOutput - realOutput, 2);
        if (Math.round(expectedOutput) == 0 && Math.round(expectedOutput) == Math.round(realOutput)) {
            this.trueNegatives++;
            class1sum += realOutput;
        } else if (Math.round(expectedOutput) == 0 && Math.round(expectedOutput) != Math.round(realOutput)) {
            this.falsePositives++;
            class1sum += realOutput;
        } else if (Math.round(expectedOutput) == 1 && Math.round(expectedOutput) == Math.round(realOutput)) {
            this.truePositives++;
            class2sum += realOutput;
        } else if (Math.round(expectedOutput) == 1 && Math.round(expectedOutput) != Math.round(realOutput)) {
            this.falseNegatives++;
            class2sum += realOutput;
        }
    }

    @Override
    public String toString() {
        return "Number of patterns: " + getNumberOfPatterns() + "\n"
                + "True positives: " + this.truePositives + "\n"
                + "True negatives: " + this.trueNegatives + "\n"
                + "False positives: " + this.falsePositives + "\n"
                + "False negatives: " + this.falseNegatives + "\n"
                + "Accuracy: " + calcAccuracy() * 100 + "%\n"
                + "MSE: " + this.MSE / getNumberOfPatterns() + "\n"
                + "Non-targets: " + class1sum + "\n"
                + "Targets: " + class2sum + "\n";
    }

    public int getTruePositives() {
        return truePositives;
    }

    public void setTruePositives(int truePositives) {
        this.truePositives = truePositives;
    }

    public int getTrueNegatives() {
        return trueNegatives;
    }

    public void setTrueNegatives(int trueNegatives) {
        this.trueNegatives = trueNegatives;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(int falsePositives) {
        this.falsePositives = falsePositives;
    }

    public int getFalseNegatives() {
        return falseNegatives;
    }

    public void setFalseNegatives(int falseNegatives) {
        this.falseNegatives = falseNegatives;
    }

    public double getMSE() {
        return MSE;
    }

    public void setMSE(double MSE) {
        this.MSE = MSE;
    }

    public double getClass1sum() {
        return class1sum;
    }

    public void setClass1sum(double class1sum) {
        this.class1sum = class1sum;
    }

    public double getClass2sum() {
        return class2sum;
    }

    public void setClass2sum(double class2sum) {
        this.class2sum = class2sum;
    }
}
