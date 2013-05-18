/**
 * Copyright (c) 2013, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.science.ml.classifier.simple;

import com.cloudera.science.ml.classifier.core.Classifier;
import com.cloudera.science.ml.classifier.core.OnlineLearner;
import com.cloudera.science.ml.classifier.core.LinearClassifier;
import com.cloudera.science.ml.classifier.core.WeightVector;
import com.cloudera.science.ml.core.vectors.LabeledVector;

/**
 *
 */
public class SVMOnlineLearner implements SimpleOnlineLearner {
  private final WeightVector weights;
  private final LinearClassifier classifier;
  private final OnlineLearner.Params params;
  private int iteration;
  
  public SVMOnlineLearner(OnlineLearner.Params params) {
    this.weights = params.createWeights();
    this.classifier = new LinearClassifier(weights);
    this.params = params;
    this.iteration = 0;
  }
  
  @Override
  public Classifier getClassifier() {
    return classifier;
  }

  @Override
  public boolean update(LabeledVector x) {
    iteration++;
    double eta = params.eta(iteration);
    double p = x.getLabel() * weights.innerProduct(x);    
    weights.regularizeL2(eta, params.lambda());    
    // If x has non-zero loss, perform gradient step in direction of x.
    if (p < 1.0 && x.getLabel() != 0.0) {
      weights.addVector(x, (eta * x.getLabel())); 
    }
    params.updateWeights(weights, iteration);
    return (p < 1.0 && x.getLabel() != 0.0);
  }
}