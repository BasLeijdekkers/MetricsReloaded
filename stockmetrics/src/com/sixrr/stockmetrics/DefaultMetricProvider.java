/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.stockmetrics;

import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricProvider;
import com.sixrr.metrics.PrebuiltMetricProfile;
import com.sixrr.stockmetrics.classMetrics.*;
import com.sixrr.stockmetrics.i18n.StockMetricsBundle;
import com.sixrr.stockmetrics.interfaceMetrics.*;
import com.sixrr.stockmetrics.methodMetrics.*;
import com.sixrr.stockmetrics.moduleMetrics.*;
import com.sixrr.stockmetrics.packageMetrics.*;
import com.sixrr.stockmetrics.projectMetrics.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultMetricProvider implements MetricProvider {

    @NotNull
    @Override
    public List<Metric> getMetrics() {
        final List<Metric> metrics = new ArrayList<Metric>(283);
        initializeMethodMetrics(metrics);
        initializeClassMetrics(metrics);
        initializeInterfaceMetrics(metrics);
        initializePackageMetrics(metrics);
        initializeModuleMetrics(metrics);
        initializeProjectMetrics(metrics);
        return metrics;
    }

    private static void initializeClassMetrics(Collection<Metric> metrics) {
        metrics.add(new ClassSizeAttributesMetric());
        metrics.add(new ClassSizeOperationsMetric());
        metrics.add(new ClassSizeOperationsAttributesMetric());
        metrics.add(new DepthOfInheritanceMetric());
        metrics.add(new NumInterfacesImplementedMetric());
        metrics.add(new NumSubclassesMetric());
        metrics.add(new NumTestMethodsClassMetric());
        metrics.add(new NumTestAssertsClassMetric());
        metrics.add(new NumConstructorsMetric());
        metrics.add(new NumAttributesAddedMetric());
        metrics.add(new NumAttributesInheritedMetric());
        metrics.add(new NumOperationsAddedMetric());
        metrics.add(new NumOperationsOverriddenMetric());
        metrics.add(new NumOperationsInheritedMetric());
        metrics.add(new NumInnerClassesMetric());
        metrics.add(new NumStatementsClassMetric());
        metrics.add(new NumDependenciesClassMetric());
        metrics.add(new NumPackageDependenciesClassMetric());
        metrics.add(new NumTransitiveDependenciesClassMetric());
        metrics.add(new NumCyclicDependenciesClassMetric());
        metrics.add(new LevelOrderClassMetric());
        metrics.add(new AdjustedLevelOrderClassMetric());
        metrics.add(new NumDependentsClassMetric());
        metrics.add(new NumPackageDependentsClassMetric());
        metrics.add(new NumTransitiveDependentsClassMetric());
        metrics.add(new CouplingBetweenObjectsClassMetric());
        metrics.add(new AverageOperationSizeMetric());
        metrics.add(new MaximumOperationSizeMetric());
        metrics.add(new AverageOperationComplexityMetric());
        metrics.add(new MaximumOperationComplexityMetric());
        metrics.add(new LinesOfCodeClassMetric());
        metrics.add(new CommentRatioClassMetric());
        metrics.add(new TrueCommentRatioClassMetric());
        metrics.add(new WeightedMethodComplexityMetric());
        metrics.add(new SourceLinesOfCodeClassMetric());
        metrics.add(new CommentLinesOfCodeClassMetric());
        metrics.add(new JavadocLinesOfCodeClassMetric());
        metrics.add(new AverageOperationParametersMetric());
        metrics.add(new NumCommandsClassMetric());
        metrics.add(new NumQueriesClassMetric());
        metrics.add(new PercentFieldsJavadocedClassMetric());
        metrics.add(new PercentMethodsJavadocedClassMetric());
        metrics.add(new HalsteadBugsClassMetric());
        metrics.add(new HalsteadDifficultyClassMetric());
        metrics.add(new HalsteadLengthClassMetric());
        metrics.add(new HalsteadVolumeClassMetric());
        metrics.add(new HalsteadVocabularyClassMetric());
        metrics.add(new HalsteadEffortClassMetric());
        metrics.add(new TodoCommentCountClassMetric());
        metrics.add(new NumTypeParametersClassMetric());
        metrics.add(new MessagePassingCouplingClassMetric());
        metrics.add(new ResponseForClassMetric());
        metrics.add(new LackOfCohesionOfMethodsClassMetric());
    }

    private static void initializeInterfaceMetrics(Collection<Metric> metrics) {
        metrics.add(new InterfaceSizeAttributesMetric());
        metrics.add(new InterfaceSizeOperationsMetric());
        metrics.add(new InterfaceSizeOperationsAttributesMetric());
        metrics.add(new NumDependenciesInterfaceMetric());
        metrics.add(new NumPackageDependenciesInterfaceMetric());
        metrics.add(new NumTransitiveDependenciesInterfaceMetric());
        metrics.add(new NumCyclicDependenciesInterfaceMetric());
        metrics.add(new LevelOrderInterfaceMetric());
        metrics.add(new AdjustedLevelOrderInterfaceMetric());
        metrics.add(new NumDependentsInterfaceMetric());
        metrics.add(new NumPackageDependentsInterfaceMetric());
        metrics.add(new NumTransitiveDependentsInterfaceMetric());
        metrics.add(new CouplingBetweenObjectsInterfaceMetric());
        metrics.add(new NumSubinterfacesMetric());
        metrics.add(new NumImplementationsMetric());
        metrics.add(new CommentRatioInterfaceMetric());
        metrics.add(new TrueCommentRatioInterfaceMetric());
        metrics.add(new SourceLinesOfCodeInterfaceMetric());
        metrics.add(new LinesOfCodeInterfaceMetric());
        metrics.add(new CommentLinesOfCodeInterfaceMetric());
        metrics.add(new JavadocLinesOfCodeInterfaceMetric());
        metrics.add(new NumCommandsInterfaceMetric());
        metrics.add(new NumQueriesInterfaceMetric());
        metrics.add(new PercentFieldsJavadocedInterfaceMetric());
        metrics.add(new PercentMethodsJavadocedInterfaceMetric());
        metrics.add(new TodoCommentCountInterfaceMetric());
        metrics.add(new NumTypeParametersInterfaceMetric());
    }

    private static void initializeMethodMetrics(Collection<Metric> metrics) {
        metrics.add(new CyclomaticComplexityMetric());
        metrics.add(new DesignComplexityMetric());
        metrics.add(new EssentialCyclomaticComplexityMetric());
        metrics.add(new NestingDepthMetric());
        metrics.add(new NumOverridesMethodMetric());
        metrics.add(new NumImplementationsMethodMetric());
        metrics.add(new NumLoopsMetric());
        metrics.add(new NumAssertsMetric());
        metrics.add(new NumParametersMetric());
        metrics.add(new NumTypeParametersMetric());
        metrics.add(new NumExceptionsThrownMetric());
        metrics.add(new NumExceptionsCaughtMetric());
        metrics.add(new NumReturnPointsMetric());
        metrics.add(new LinesOfCodeMethodMetric());
        metrics.add(new NumBranchStatementsMetric());
        metrics.add(new NumControlStatementsMetric());
        metrics.add(new NumExecutableStatementsMetric());
        metrics.add(new NumStatementsMetric());
        metrics.add(new NumExpressionsMetric());
        metrics.add(new NumTypecastExpressionsMetric());
        metrics.add(new JavadocLinesOfCodeMethodMetric());
        metrics.add(new RelativeLinesOfCodeMetric());
        metrics.add(new CommentRatioMethodMetric());
        metrics.add(new CommentLinesOfCodeMethodMetric());
        metrics.add(new TodoCommentCountMethodMetric());
        metrics.add(new TrueCommentRatioMethodMetric());
        metrics.add(new SourceLinesOfCodeMethodMetric());
        metrics.add(new NumMethodCallsMetric());
        metrics.add(new ControlDensityMetric());
        metrics.add(new HalsteadBugsMethodMetric());
        metrics.add(new HalsteadDifficultyMethodMetric());
        metrics.add(new HalsteadLengthMethodMetric());
        metrics.add(new HalsteadVolumeMethodMetric());
        metrics.add(new HalsteadVocabularyMethodMetric());
        metrics.add(new HalsteadEffortMethodMetric());
        metrics.add(new QCPCorrectnessMetric());
        metrics.add(new QCPMaintainabilityMetric());
        metrics.add(new QCPReliabilityMetric());
        metrics.add(new NumTimesCalledMetric());
        metrics.add(new NumTimesCalledProductMetric());
        metrics.add(new NumTimesCalledTestMetric());
        metrics.add(new LoopNestingDepthMetric());
        metrics.add(new ConditionalNestingDepthMetric());
    }

    private static void initializePackageMetrics(Collection<Metric> metrics) {
        metrics.add(new NumAbstractClassesPackageMetric());
        metrics.add(new NumAbstractClassesRecursivePackageMetric());
        metrics.add(new NumClassesPackageMetric());
        metrics.add(new NumClassesRecursivePackageMetric());
        metrics.add(new NumProductClassesPackageMetric());
        metrics.add(new NumProductClassesRecursivePackageMetric());
        metrics.add(new NumTestMethodsPackageMetric());
        metrics.add(new NumTestMethodsRecursivePackageMetric());
        metrics.add(new NumTestAssertsPackageMetric());
        metrics.add(new NumTestAssertsRecursivePackageMetric());
        metrics.add(new NumTestCasesPackageMetric());
        metrics.add(new NumTestCasesRecursivePackageMetric());
        metrics.add(new NumTestClassesPackageMetric());
        metrics.add(new NumTestClassesRecursivePackageMetric());
        metrics.add(new NumConcreteClassesPackageMetric());
        metrics.add(new NumConcreteClassesRecursivePackageMetric());
        metrics.add(new NumInterfacesPackageMetric());
        metrics.add(new NumInterfacesRecursivePackageMetric());
        metrics.add(new NumTopLevelClassesPackageMetric());
        metrics.add(new NumTopLevelClassesRecursivePackageMetric());
        metrics.add(new NumTopLevelInterfacesPackageMetric());
        metrics.add(new NumTopLevelInterfacesRecursivePackageMetric());
        metrics.add(new AbstractnessMetric());
        metrics.add(new LinesOfCodePackageMetric());
        metrics.add(new LinesOfCodeRecursivePackageMetric());
        metrics.add(new LinesOfTestCodePackageMetric());
        metrics.add(new LinesOfTestCodeRecursivePackageMetric());
        metrics.add(new LinesOfProductCodePackageMetric());
        metrics.add(new LinesOfProductCodeRecursivePackageMetric());
        metrics.add(new CommentLinesOfCodePackageMetric());
        metrics.add(new CommentLinesOfCodeRecursivePackageMetric());
        metrics.add(new JavadocLinesOfCodePackageMetric());
        metrics.add(new JavadocLinesOfCodeRecursivePackageMetric());
        metrics.add(new CommentRatioRecursivePackageMetric());
        metrics.add(new CommentRatioPackageMetric());
        metrics.add(new TrueCommentRatioPackageMetric());
        metrics.add(new TrueCommentRatioRecursivePackageMetric());
        metrics.add(new SourceLinesOfCodePackageMetric());
        metrics.add(new SourceLinesOfCodeTestPackageMetric());
        metrics.add(new SourceLinesOfCodeProductPackageMetric());
        metrics.add(new SourceLinesOfCodeRecursivePackageMetric());
        metrics.add(new SourceLinesOfCodeTestRecursivePackageMetric());
        metrics.add(new SourceLinesOfCodeProductRecursivePackageMetric());
        metrics.add(new NumMethodsPackageMetric());
        metrics.add(new NumMethodsRecursivePackageMetric());
        metrics.add(new TestRatioPackageMetric());
        metrics.add(new EncapsulationRatioPackageMetric());
        metrics.add(new AfferentCouplingMetric());
        metrics.add(new EfferentCouplingMetric());
        metrics.add(new NumDependencyPackagesPackageMetric());
        metrics.add(new NumDependentPackagesPackageMetric());
        metrics.add(new InstabilityMetric());
        metrics.add(new DistanceMetric());
        metrics.add(new PercentClassesJavadocedPackageMetric());
        metrics.add(new PercentClassesJavadocedRecursivePackageMetric());
        metrics.add(new PercentMethodsJavadocedPackageMetric());
        metrics.add(new PercentMethodsJavadocedRecursivePackageMetric());
        metrics.add(new PercentFieldsJavadocedPackageMetric());
        metrics.add(new PercentFieldsJavadocedRecursivePackageMetric());
        metrics.add(new TotalCyclomaticComplexityPackageMetric());
        metrics.add(new AverageCyclomaticComplexityPackageMetric());
        metrics.add(new TodoCommentCountPackageMetric());
        metrics.add(new TodoCommentCountRecursivePackageMetric());
        metrics.add(new NumCyclicDependenciesPackageMetric());
        metrics.add(new LevelOrderPackageMetric());
        metrics.add(new AdjustedLevelOrderPackageMetric());
        metrics.add(new NumTransitiveDependentPackagesPackageMetric());
        metrics.add(new NumTransitiveDependencyPackagesPackageMetric());
        metrics.add(new NumRootClassesPackageMetric());
        metrics.add(new NumRootClassesRecursivePackageMetric());
        metrics.add(new NumLeafClassesPackageMetric());
        metrics.add(new NumLeafClassesRecursivePackageMetric());
        metrics.add(new NumEnumClassesPackageMetric());
        metrics.add(new NumEnumClassesRecursivePackageMetric());
        metrics.add(new NumAnnotationClassesPackageMetric());
        metrics.add(new NumAnnotationClassesRecursivePackageMetric());
    }

    private static void initializeModuleMetrics(Collection<Metric> metrics) {
        metrics.add(new NumAbstractClassesModuleMetric());
        metrics.add(new NumClassesModuleMetric());
        metrics.add(new NumHTMLFilesModuleMetric());
        metrics.add(new NumJavaFilesModuleMetric());
        metrics.add(new NumXMLFilesModuleMetric());
        metrics.add(new NumProductClassesModuleMetric());
        metrics.add(new NumTestClassesModuleMetric());
        metrics.add(new NumTestCasesModuleMetric());
        metrics.add(new NumTestMethodsModuleMetric());
        metrics.add(new NumTestAssertsModuleMetric());
        metrics.add(new NumConcreteClassesModuleMetric());
        metrics.add(new NumInterfacesModuleMetric());
        metrics.add(new NumTopLevelClassesModuleMetric());
        metrics.add(new NumTopLevelInterfacesModuleMetric());
        metrics.add(new LinesOfCodeModuleMetric());
        metrics.add(new LinesOfJavaModuleMetric());
        metrics.add(new LinesOfTestCodeModuleMetric());
        metrics.add(new LinesOfProductCodeModuleMetric());
        metrics.add(new LinesOfHTMLModuleMetric());
        metrics.add(new LinesOfXMLModuleMetric());
        metrics.add(new CommentLinesOfCodeModuleMetric());
        metrics.add(new JavadocLinesOfCodeModuleMetric());
        metrics.add(new CommentRatioModuleMetric());
        metrics.add(new TrueCommentRatioModuleMetric());
        metrics.add(new SourceLinesOfCodeModuleMetric());
        metrics.add(new SourceLinesOfCodeTestModuleMetric());
        metrics.add(new SourceLinesOfCodeProductModuleMetric());
        metrics.add(new NumMethodsModuleMetric());
        metrics.add(new TestRatioModuleMetric());
        metrics.add(new EncapsulationRatioModuleMetric());
        metrics.add(new PercentClassesJavadocedModuleMetric());
        metrics.add(new PercentMethodsJavadocedModuleMetric());
        metrics.add(new PercentFieldsJavadocedModuleMetric());
        metrics.add(new TotalCyclomaticComplexityModuleMetric());
        metrics.add(new AverageCyclomaticComplexityModuleMetric());
        metrics.add(new TodoCommentCountModuleMetric());
        metrics.add(new NumRootClassesModuleMetric());
        metrics.add(new NumLeafClassesModuleMetric());
        metrics.add(new NumEnumClassesModuleMetric());
        metrics.add(new NumAnnotationClassesModuleMetric());
    }

    private static void initializeProjectMetrics(Collection<Metric> metrics) {
        metrics.add(new NumAbstractClassesProjectMetric());
        metrics.add(new NumClassesProjectMetric());
        metrics.add(new NumHTMLFilesProjectMetric());
        metrics.add(new NumJavaFilesProjectMetric());
        metrics.add(new NumXMLFilesProjectMetric());
        metrics.add(new NumConcreteClassesProjectMetric());
        metrics.add(new NumInterfacesProjectMetric());
        metrics.add(new NumTopLevelInterfacesProjectMetric());
        metrics.add(new NumTopLevelClassesProjectMetric());
        metrics.add(new NumMethodsProjectMetric());
        metrics.add(new LinesOfCodeProjectMetric());
        metrics.add(new CommentLinesOfCodeProjectMetric());
        metrics.add(new JavadocLinesOfCodeProjectMetric());
        metrics.add(new CommentRatioProjectMetric());
        metrics.add(new TrueCommentRatioProjectMetric());
        metrics.add(new SourceLinesOfCodeProjectMetric());
        metrics.add(new SourceLinesOfCodeTestProjectMetric());
        metrics.add(new SourceLinesOfCodeProductProjectMetric());
        metrics.add(new NumPackagesMetric());
        metrics.add(new NumTestClassesProjectMetric());
        metrics.add(new NumTestCasesProjectMetric());
        metrics.add(new NumTestMethodsProjectMetric());
        metrics.add(new NumTestAssertsProjectMetric());
        metrics.add(new NumProductClassesProjectMetric());
        metrics.add(new LinesOfProductCodeProjectMetric());
        metrics.add(new LinesOfTestCodeProjectMetric());
        metrics.add(new LinesOfHTMLProjectMetric());
        metrics.add(new LinesOfJavaProjectMetric());
        metrics.add(new LinesOfXMLProjectMetric());
        metrics.add(new TestRatioProjectMetric());
        metrics.add(new PercentClassesJavadocedProjectMetric());
        metrics.add(new PercentMethodsJavadocedProjectMetric());
        metrics.add(new PercentFieldsJavadocedProjectMetric());
        metrics.add(new TotalCyclomaticComplexityProjectMetric());
        metrics.add(new AverageCyclomaticComplexityProjectMetric());
        metrics.add(new TodoCommentCountProjectMetric());
        metrics.add(new AttributeInheritanceFactorProjectMetric());
        metrics.add(new MethodInheritanceFactorProjectMetric());
        metrics.add(new AttributeHidingFactorProjectMetric());
        metrics.add(new MethodHidingFactorProjectMetric());
        metrics.add(new CouplingFactorProjectMetric());
        metrics.add(new PolymorphismFactorProjectMetric());
        metrics.add(new NumRootClassesProjectMetric());
        metrics.add(new NumLeafClassesProjectMetric());
        metrics.add(new NumEnumClassesProjectMetric());
        metrics.add(new NumAnnotationClassesProjectMetric());
    }

    @NotNull
    @Override
    public List<PrebuiltMetricProfile> getPrebuiltProfiles() {
        final List<PrebuiltMetricProfile> out = new ArrayList<PrebuiltMetricProfile>(10);
        out.add(createComplexityProfile());
        out.add(createCodeSizeProfile());
        out.add(createChidamberKemererProfile());
        out.add(createClassCountProfile());
        out.add(createDependencyProfile());
        out.add(createFileCountProfile());
        out.add(createJavadocProfile());
        out.add(createMartinProfile());
        out.add(createMoodProfile());
        out.add(createTestProfile());
        return out;
    }

    private static PrebuiltMetricProfile createComplexityProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("complexity.metrics.profile.name"));
        profile.addMetric("CyclomaticComplexity", null, 10.0);
        profile.addMetric("DesignComplexity", null, 8.0);
        profile.addMetric("EssentialCyclomaticComplexity", null, 3.0);
        profile.addMetric("WeightedMethodComplexity", null, 30.0);
        profile.addMetric("AverageOperationComplexity", null, 3.0);
        profile.addMetric("AverageCyclomaticComplexityProject", null, null);
        profile.addMetric("TotalCyclomaticComplexityProject", null, null);
        profile.addMetric("AverageCyclomaticComplexityModule", null, null);
        profile.addMetric("TotalCyclomaticComplexityModule", null, null);
        profile.addMetric("AverageCyclomaticComplexityPackage", null, null);
        profile.addMetric("TotalCyclomaticComplexityPackage", null, null);
        return profile;
    }

    private static PrebuiltMetricProfile createCodeSizeProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("lines.of.code.metrics.profile.name"));
        profile.addMetric("LinesOfCodeProject");
        profile.addMetric("LinesOfCodeModule");
        profile.addMetric("LinesOfCodePackage");
        profile.addMetric("LinesOfCodeRecursivePackage");
        profile.addMetric("LinesOfProductCodeProject");
        profile.addMetric("LinesOfProductCodeModule");
        profile.addMetric("LinesOfProductCodePackage");
        profile.addMetric("LinesOfProductCodeRecursivePackage");
        profile.addMetric("LinesOfTestCodeProject");
        profile.addMetric("LinesOfTestCodeModule");
        profile.addMetric("LinesOfTestCodePackage");
        profile.addMetric("LinesOfTestCodeRecursivePackage");
        profile.addMetric("LinesOfHTMLProject");
        profile.addMetric("LinesOfHTMLModule");
        profile.addMetric("LinesOfXMLProject");
        profile.addMetric("LinesOfXMLModule");
        return profile;
    }

    private static  PrebuiltMetricProfile createFileCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("file.count.metrics.profile.name"));
        profile.addMetric("NumHTMLFilesProject");
        profile.addMetric("NumHTMLFilesModule");
        profile.addMetric("NumJavaFilesProject");
        profile.addMetric("NumJavaFilesModule");
        profile.addMetric("NumXMLFilesProject");
        profile.addMetric("NumXMLFilesModule");
        return profile;
    }

    private static PrebuiltMetricProfile createClassCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("class.count.metrics.profile.name"));
        profile.addMetric("NumClassesProject");
        profile.addMetric("NumClassesModule");
        profile.addMetric("NumClassesPackage");
        profile.addMetric("NumClassesRecursivePackage");
        profile.addMetric("NumProductClassesProject");
        profile.addMetric("NumProductClassesModule");
        profile.addMetric("NumProductClassesPackage");
        profile.addMetric("NumProductClassesRecursivePackage");
        profile.addMetric("NumTestClassesProject");
        profile.addMetric("NumTestClassesModule");
        profile.addMetric("NumTestClassesPackage");
        profile.addMetric("NumTestClassesRecursivePackage");
        return profile;
    }

    private static PrebuiltMetricProfile createDependencyProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("dependency.metrics.profile.name"));
        profile.addMetric("NumDependenciesClass");
        profile.addMetric("NumDependentsClass");
        profile.addMetric("NumTransitiveDependenciesClass");
        profile.addMetric("NumTransitiveDependentsClass");
        profile.addMetric("NumCyclicDependenciesClass");
        profile.addMetric("NumDependenciesInterface");
        profile.addMetric("NumDependentsInterface");
        profile.addMetric("NumTransitiveDependenciesInterface");
        profile.addMetric("NumTransitiveDependentsInterface");
        profile.addMetric("NumCyclicDependenciesInterface");
        profile.addMetric("NumDependencyPackagesPackage");
        profile.addMetric("NumDependentPackagesPackage");
        return profile;
    }

    private static PrebuiltMetricProfile createMoodProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("mood.metrics.profile.name"));
        profile.addMetric("MethodHidingFactorProject");
        profile.addMetric("MethodInheritanceFactorProject");
        profile.addMetric("AttributeHidingFactorProject");
        profile.addMetric("AttributeInheritanceFactorProject");
        profile.addMetric("CouplingFactorProject");
        profile.addMetric("PolymorphismFactorProject");
        return profile;
    }

    private static PrebuiltMetricProfile createMartinProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("martin.packaging.metrics.profile.name"));
        profile.addMetric("AfferentCoupling");
        profile.addMetric("EfferentCoupling");
        profile.addMetric("Abstractness");
        profile.addMetric("Instability");
        profile.addMetric("Distance");
        return profile;
    }

    private static PrebuiltMetricProfile createTestProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("junit.testing.metrics.profile.name"));
        profile.addMetric("NumTestCasesProject");
        profile.addMetric("NumTestAssertsProject");
        profile.addMetric("NumTestMethodsProject");
        profile.addMetric("NumTestCasesPackage");
        profile.addMetric("NumTestAssertsPackage");
        profile.addMetric("NumTestMethodsPackage");
        profile.addMetric("NumTestCasesModule");
        profile.addMetric("NumTestAssertsModule");
        profile.addMetric("NumTestMethodsModule");
        profile.addMetric("NumTestAssertsClass");
        profile.addMetric("NumTestMethodsClass");
        return profile;
    }

    private static PrebuiltMetricProfile createChidamberKemererProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("chidamber.kemerer.metrics.profile.name"));
        profile.addMetric("ResponseForClass");
        profile.addMetric("CouplingBetweenObjectsClass");
        profile.addMetric("DepthOfInheritance");
        profile.addMetric("WeightedMethodComplexity");
        profile.addMetric("NumSubclasses");
        profile.addMetric("LackOfCohesionOfMethodsClass");
        return profile;
    }

    private static PrebuiltMetricProfile createJavadocProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("javadoc.coverage.metrics.profile.name"));
        profile.addMetric("JavadocLinesOfCodeMethod");
        profile.addMetric("JavadocLinesOfCodeClass");
        profile.addMetric("JavadocLinesOfCodeInterface");
        profile.addMetric("JavadocLinesOfCodePackage");
        profile.addMetric("JavadocLinesOfCodeModule");
        profile.addMetric("JavadocLinesOfCodeProject");
        profile.addMetric("PercentFieldsJavadocedClass");
        profile.addMetric("PercentMethodsJavadocedClass");
        profile.addMetric("PercentFieldsJavadocedInterface");
        profile.addMetric("PercentMethodsJavadocedInterface");
        profile.addMetric("PercentFieldsJavadocedPackage");
        profile.addMetric("PercentMethodsJavadocedPackage");
        profile.addMetric("PercentClassesJavadocedPackage");
        profile.addMetric("PercentFieldsJavadocedModule");
        profile.addMetric("PercentMethodsJavadocedModule");
        profile.addMetric("PercentClassesJavadocedModule");
        profile.addMetric("PercentFieldsJavadocedProject");
        profile.addMetric("PercentMethodsJavadocedProject");
        profile.addMetric("PercentClassesJavadocedProject");
        return profile;
    }
}
