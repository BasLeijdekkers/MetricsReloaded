/*
 * Copyright 2005, Sixth and Red River Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sixrr.metrics;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;

/**
 * The MetricsResultHolder is the mechanism by which metrics values get reported for later display and processing.  There
 * are a pair of metric posting methods for each metric category, one for simple values and one for ratio values.
 */
public interface MetricsResultsHolder {

    /**
     * Post a simple value for a project metric.
     *
     * @param metric the metric to post the value for.   It should have a category of MetricCategory.Project.
     * @param value  the value to post.
     */
    void postProjectMetric(Metric metric, double value);

    /**
     * Post a simple value for a module metric.
     *
     * @param metric the metric to post the value for.  It should have a category of MetricCategory.Module.
     * @param module the module for which the metric value is calculated.
     * @param value  the value to post.
     */
    void postModuleMetric(Metric metric, Module module, double value);

    /**
     * Post a simple value for a package metric.
     *
     * @param metric   the metric to post the value for.   It should have a category of MetricCategory.Package.
     * @param aPackage the package for which the metric value is calculated.
     * @param value    the value to post.
     */
    void postPackageMetric(Metric metric, PsiPackage aPackage, double value);

    /**
     * Post a simple value for a class metric.
     *
     * @param metric the metric to post the value for.   It should have a category of MetricCategory.Class.
     * @param aClass the class for which the metric value is calculated.
     * @param value  the value to post.
     */
    void postClassMetric(Metric metric, PsiClass aClass, double value);

    /**
     * Post a simple value for an interface metric.
     *
     * @param metric      the metric to post the value for.   It should have a category of MetricCategory.Interface.
     * @param anInterface the interface for which the metric value is calculated.
     * @param value       the value to post.
     */
    void postInterfaceMetric(Metric metric, PsiClass anInterface, double value);

    /**
     * Post a simple value for a method metric.
     *
     * @param metric the metric to post the value for.   It should have a category of MetricCategory.Method.
     * @param method the method for which the metric value is calculated.
     * @param value  the value to post.
     */
    void postMethodMetric(Metric metric, PsiMethod method, double value);

    /**
     * Post a ratio value for a project metric.
     *
     * @param metric      the metric to post the value for.   It should have a category of MetricCategory.Project.
     * @param numerator   The numerator of the value to post.  Should be an integer.
     * @param denominator The denominator of the value to post.  Should be a positive integer.
     */
    void postProjectMetric(Metric metric, double numerator, double denominator);

    /**
     * Post a ratio value for a module metric.
     *
     * @param metric      the metric to post the value for.  It should have a category of MetricCategory.Module.
     * @param module      the module for which the metric value is calculated.
     * @param numerator   The numerator of the value to post.  Should be an integer.
     * @param denominator The denominator of the value to post.  Should be a positive integer.
     */
    void postModuleMetric(Metric metric, Module module, double numerator, double denominator);

    /**
     * Post a ratio value for a package metric.
     *
     * @param metric      the metric to post the value for.   It should have a category of MetricCategory.Package.
     * @param aPackage    the package for which the metric value is calculated.
     * @param numerator   The numerator of the value to post.  Should be an integer.
     * @param denominator The denominator of the value to post.  Should be a positive integer.
     */
    void postPackageMetric(Metric metric, PsiPackage aPackage, double numerator, double denominator);

    /**
     * Post a ratio value for a class metric.
     *
     * @param metric      the metric to post the value for.   It should have a category of MetricCategory.Class.
     * @param aClass      the class for which the metric value is calculated.
     * @param numerator   The numerator of the value to post.  Should be an integer.
     * @param denominator The denominator of the value to post.  Should be a positive integer.
     */
    void postClassMetric(Metric metric, PsiClass aClass, double numerator, double denominator);

    /**
     * Post a ratio value for a interface metric.
     *
     * @param metric      the metric to post the value for.   It should have a category of MetricCategory.Interface.
     * @param anInterface the interface for which the metric value is calculated.
     * @param numerator   The numerator of the value to post.  Should be an integer.
     * @param denominator The denominator of the value to post.  Should be a positive integer.
     */
    void postInterfaceMetric(Metric metric, PsiClass anInterface, double numerator, double denominator);

    /**
     * Post a ratio value for a method metric.
     *
     * @param metric      the metric to post the value for.   It should have a category of MetricCategory.Method.
     * @param method      the method for which the metric value is calculated.
     * @param numerator   The numerator of the value to post.  Should be an integer.
     * @param denominator The denominator of the value to post.  Should be a positive integer.
     */
    void postMethodMetric(Metric metric, PsiMethod method, double numerator, double denominator);
}
