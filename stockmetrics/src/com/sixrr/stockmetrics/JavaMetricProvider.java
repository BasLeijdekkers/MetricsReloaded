/*
 * Copyright 2005-2021 Sixth and Red River Software, Bas Leijdekkers
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

/**
 * @author Bas Leijdekkers
 */
public class JavaMetricProvider implements MetricProvider {

    @NotNull
    @Override
    public List<Metric> getMetrics() {
        final List<Metric> metrics = new ArrayList<>(260);
        initializeClassMetrics(metrics);
        initializeInterfaceMetrics(metrics);
        initializeMethodMetrics(metrics);
        initializeModuleMetrics(metrics);
        initializePackageMetrics(metrics);
        initializeProjectMetrics(metrics);
        return metrics;
    }

    private static void initializeClassMetrics(Collection<Metric> metrics) {
        metrics.add(new AdjustedLevelOrderClassMetric());
        metrics.add(new AverageOperationComplexityMetric());
        metrics.add(new AverageOperationParametersMetric());
        metrics.add(new AverageOperationSizeMetric());
        metrics.add(new ClassSizeAttributesMetric());
        metrics.add(new ClassSizeOperationsAttributesMetric());
        metrics.add(new ClassSizeOperationsMetric());
        metrics.add(new CommentLinesOfCodeClassMetric());
        metrics.add(new CommentRatioClassMetric());
        metrics.add(new CouplingBetweenObjectsClassMetric());
        metrics.add(new DepthOfInheritanceMetric());
        metrics.add(new HalsteadBugsClassMetric());
        metrics.add(new HalsteadDifficultyClassMetric());
        metrics.add(new HalsteadEffortClassMetric());
        metrics.add(new HalsteadLengthClassMetric());
        metrics.add(new HalsteadVocabularyClassMetric());
        metrics.add(new HalsteadVolumeClassMetric());
        metrics.add(new JavadocLinesOfCodeClassMetric());
        metrics.add(new LackOfCohesionOfMethodsClassMetric());
        metrics.add(new LevelOrderClassMetric());
        metrics.add(new LinesOfCodeClassMetric());
        metrics.add(new MaximumOperationComplexityMetric());
        metrics.add(new MaximumOperationSizeMetric());
        metrics.add(new MessagePassingCouplingClassMetric());
        metrics.add(new NumAttributesAddedMetric());
        metrics.add(new NumAttributesInheritedMetric());
        metrics.add(new NumChildrenMetric());
        metrics.add(new NumCommandsClassMetric());
        metrics.add(new NumConstructorsMetric());
        metrics.add(new NumCyclicDependenciesClassMetric());
        metrics.add(new NumDependenciesClassMetric());
        metrics.add(new NumDependentsClassMetric());
        metrics.add(new NumInnerClassesMetric());
        metrics.add(new NumInterfacesImplementedMetric());
        metrics.add(new NumOperationsAddedMetric());
        metrics.add(new NumOperationsInheritedMetric());
        metrics.add(new NumOperationsOverriddenMetric());
        metrics.add(new NumPackageDependenciesClassMetric());
        metrics.add(new NumPackageDependentsClassMetric());
        metrics.add(new NumQueriesClassMetric());
        metrics.add(new NumStatementsClassMetric());
        metrics.add(new NumSubclassesMetric());
        metrics.add(new NumTestAssertsClassMetric());
        metrics.add(new NumTestMethodsClassMetric());
        metrics.add(new NumTransitiveDependenciesClassMetric());
        metrics.add(new NumTransitiveDependentsClassMetric());
        metrics.add(new NumTypeParametersClassMetric());
        metrics.add(new PercentFieldsJavadocedClassMetric());
        metrics.add(new PercentMethodsJavadocedClassMetric());
        metrics.add(new ResponseForClassMetric());
        metrics.add(new SourceLinesOfCodeClassMetric());
        metrics.add(new TodoCommentCountClassMetric());
        metrics.add(new TrueCommentRatioClassMetric());
        metrics.add(new WeightedMethodComplexityMetric());
    }

    private static void initializeInterfaceMetrics(Collection<Metric> metrics) {
        metrics.add(new AdjustedLevelOrderInterfaceMetric());
        metrics.add(new CommentLinesOfCodeInterfaceMetric());
        metrics.add(new CommentRatioInterfaceMetric());
        metrics.add(new CouplingBetweenObjectsInterfaceMetric());
        metrics.add(new InterfaceSizeAttributesMetric());
        metrics.add(new InterfaceSizeOperationsAttributesMetric());
        metrics.add(new InterfaceSizeOperationsMetric());
        metrics.add(new JavadocLinesOfCodeInterfaceMetric());
        metrics.add(new LevelOrderInterfaceMetric());
        metrics.add(new LinesOfCodeInterfaceMetric());
        metrics.add(new NumCommandsInterfaceMetric());
        metrics.add(new NumCyclicDependenciesInterfaceMetric());
        metrics.add(new NumDependenciesInterfaceMetric());
        metrics.add(new NumDependentsInterfaceMetric());
        metrics.add(new NumImplementationsMetric());
        metrics.add(new NumPackageDependenciesInterfaceMetric());
        metrics.add(new NumPackageDependentsInterfaceMetric());
        metrics.add(new NumQueriesInterfaceMetric());
        metrics.add(new NumSubinterfacesMetric());
        metrics.add(new NumTransitiveDependenciesInterfaceMetric());
        metrics.add(new NumTransitiveDependentsInterfaceMetric());
        metrics.add(new NumTypeParametersInterfaceMetric());
        metrics.add(new PercentFieldsJavadocedInterfaceMetric());
        metrics.add(new PercentMethodsJavadocedInterfaceMetric());
        metrics.add(new SourceLinesOfCodeInterfaceMetric());
        metrics.add(new TodoCommentCountInterfaceMetric());
        metrics.add(new TrueCommentRatioInterfaceMetric());
    }

    private static void initializeMethodMetrics(Collection<Metric> metrics) {
        metrics.add(new CognitiveComplexityMetric());
        metrics.add(new CommentLinesOfCodeMethodMetric());
        metrics.add(new CommentRatioMethodMetric());
        metrics.add(new ConditionalNestingDepthMetric());
        metrics.add(new ControlDensityMetric());
        metrics.add(new CyclomaticComplexityMetric());
        metrics.add(new DesignComplexityMetric());
        metrics.add(new EssentialCyclomaticComplexityMetric());
        metrics.add(new HalsteadBugsMethodMetric());
        metrics.add(new HalsteadDifficultyMethodMetric());
        metrics.add(new HalsteadEffortMethodMetric());
        metrics.add(new HalsteadLengthMethodMetric());
        metrics.add(new HalsteadVocabularyMethodMetric());
        metrics.add(new HalsteadVolumeMethodMetric());
        metrics.add(new JavadocLinesOfCodeMethodMetric());
        metrics.add(new LinesOfCodeMethodMetric());
        metrics.add(new LoopNestingDepthMetric());
        metrics.add(new NestingDepthMetric());
        metrics.add(new NumAssertsMetric());
        metrics.add(new NumBranchStatementsMetric());
        metrics.add(new NumControlStatementsMetric());
        metrics.add(new NumExceptionsCaughtMetric());
        metrics.add(new NumExceptionsThrownMetric());
        metrics.add(new NumExecutableStatementsMetric());
        metrics.add(new NumExpressionsMetric());
        metrics.add(new NumImplementationsMethodMetric());
        metrics.add(new NumLoopsMetric());
        metrics.add(new NumMethodCallsMetric());
        metrics.add(new NumNullChecksMetric());
        metrics.add(new NumOverridesMethodMetric());
        metrics.add(new NumParametersMetric());
        metrics.add(new NumReturnPointsMetric());
        metrics.add(new NumStatementsMetric());
        metrics.add(new NumTimesCalledMetric());
        metrics.add(new NumTimesCalledProductMetric());
        metrics.add(new NumTimesCalledTestMetric());
        metrics.add(new NumTypeCastExpressionsMetric());
        metrics.add(new NumTypeParametersMetric());
        metrics.add(new QCPCorrectnessMetric());
        metrics.add(new QCPMaintainabilityMetric());
        metrics.add(new QCPReliabilityMetric());
        metrics.add(new RelativeLinesOfCodeMetric());
        metrics.add(new SourceLinesOfCodeMethodMetric());
        metrics.add(new TodoCommentCountMethodMetric());
        metrics.add(new TrueCommentRatioMethodMetric());
    }

    private static void initializeModuleMetrics(Collection<Metric> metrics) {
        metrics.add(new AverageCyclomaticComplexityModuleMetric());
        metrics.add(new EncapsulationRatioModuleMetric());
        metrics.add(new JavadocLinesOfCodeModuleMetric());
        metrics.add(new LinesOfJavaModuleMetric());
        metrics.add(new NumAbstractClassesModuleMetric());
        metrics.add(new NumAnnotationClassesModuleMetric());
        metrics.add(new NumClassesModuleMetric());
        metrics.add(new NumConcreteClassesModuleMetric());
        metrics.add(new NumEnumClassesModuleMetric());
        metrics.add(new NumInterfacesModuleMetric());
        metrics.add(new NumJavaFilesModuleMetric());
        metrics.add(new NumLeafClassesModuleMetric());
        metrics.add(new NumMethodsModuleMetric());
        metrics.add(new NumProductClassesModuleMetric());
        metrics.add(new NumRootClassesModuleMetric());
        metrics.add(new NumTestAssertsModuleMetric());
        metrics.add(new NumTestCasesModuleMetric());
        metrics.add(new NumTestClassesModuleMetric());
        metrics.add(new NumTestMethodsModuleMetric());
        metrics.add(new NumTopLevelClassesModuleMetric());
        metrics.add(new NumTopLevelInterfacesModuleMetric());
        metrics.add(new PercentClassesJavadocedModuleMetric());
        metrics.add(new PercentFieldsJavadocedModuleMetric());
        metrics.add(new PercentMethodsJavadocedModuleMetric());
        metrics.add(new TotalCyclomaticComplexityModuleMetric());
    }

    private static void initializePackageMetrics(Collection<Metric> metrics) {
        metrics.add(new AbstractnessMetric());
        metrics.add(new AdjustedLevelOrderPackageMetric());
        metrics.add(new AfferentCouplingMetric());
        metrics.add(new AverageCyclomaticComplexityPackageMetric());
        metrics.add(new CommentLinesOfCodePackageMetric());
        metrics.add(new CommentLinesOfCodeRecursivePackageMetric());
        metrics.add(new CommentRatioPackageMetric());
        metrics.add(new CommentRatioRecursivePackageMetric());
        metrics.add(new DistanceMetric());
        metrics.add(new EfferentCouplingMetric());
        metrics.add(new EncapsulationRatioPackageMetric());
        metrics.add(new InstabilityMetric());
        metrics.add(new JavadocLinesOfCodePackageMetric());
        metrics.add(new JavadocLinesOfCodeRecursivePackageMetric());
        metrics.add(new LevelOrderPackageMetric());
        metrics.add(new LinesOfCodePackageMetric());
        metrics.add(new LinesOfCodeRecursivePackageMetric());
        metrics.add(new LinesOfProductCodePackageMetric());
        metrics.add(new LinesOfProductCodeRecursivePackageMetric());
        metrics.add(new LinesOfTestCodePackageMetric());
        metrics.add(new LinesOfTestCodeRecursivePackageMetric());
        metrics.add(new NumAbstractClassesPackageMetric());
        metrics.add(new NumAbstractClassesRecursivePackageMetric());
        metrics.add(new NumAnnotationClassesPackageMetric());
        metrics.add(new NumAnnotationClassesRecursivePackageMetric());
        metrics.add(new NumAnonymousClassesPackageMetric());
        metrics.add(new NumClassesPackageMetric());
        metrics.add(new NumClassesRecursivePackageMetric());
        metrics.add(new NumConcreteClassesPackageMetric());
        metrics.add(new NumConcreteClassesRecursivePackageMetric());
        metrics.add(new NumCyclicDependenciesPackageMetric());
        metrics.add(new NumDependencyPackagesPackageMetric());
        metrics.add(new NumDependentPackagesPackageMetric());
        metrics.add(new NumEnumClassesPackageMetric());
        metrics.add(new NumEnumClassesRecursivePackageMetric());
        metrics.add(new NumInterfacesPackageMetric());
        metrics.add(new NumInterfacesRecursivePackageMetric());
        metrics.add(new NumLambdasPackageMetric());
        metrics.add(new NumLeafClassesPackageMetric());
        metrics.add(new NumLeafClassesRecursivePackageMetric());
        metrics.add(new NumMethodsPackageMetric());
        metrics.add(new NumMethodsRecursivePackageMetric());
        metrics.add(new NumProductClassesPackageMetric());
        metrics.add(new NumProductClassesRecursivePackageMetric());
        metrics.add(new NumRootClassesPackageMetric());
        metrics.add(new NumRootClassesRecursivePackageMetric());
        metrics.add(new NumTestAssertsPackageMetric());
        metrics.add(new NumTestAssertsRecursivePackageMetric());
        metrics.add(new NumTestCasesPackageMetric());
        metrics.add(new NumTestCasesRecursivePackageMetric());
        metrics.add(new NumTestClassesPackageMetric());
        metrics.add(new NumTestClassesRecursivePackageMetric());
        metrics.add(new NumTestMethodsPackageMetric());
        metrics.add(new NumTestMethodsRecursivePackageMetric());
        metrics.add(new NumTopLevelClassesPackageMetric());
        metrics.add(new NumTopLevelClassesRecursivePackageMetric());
        metrics.add(new NumTopLevelInterfacesPackageMetric());
        metrics.add(new NumTopLevelInterfacesRecursivePackageMetric());
        metrics.add(new NumTransitiveDependencyPackagesPackageMetric());
        metrics.add(new NumTransitiveDependentPackagesPackageMetric());
        metrics.add(new PercentClassesJavadocedPackageMetric());
        metrics.add(new PercentClassesJavadocedRecursivePackageMetric());
        metrics.add(new PercentFieldsJavadocedPackageMetric());
        metrics.add(new PercentFieldsJavadocedRecursivePackageMetric());
        metrics.add(new PercentMethodsJavadocedPackageMetric());
        metrics.add(new PercentMethodsJavadocedRecursivePackageMetric());
        metrics.add(new SourceLinesOfCodePackageMetric());
        metrics.add(new SourceLinesOfCodeProductPackageMetric());
        metrics.add(new SourceLinesOfCodeProductRecursivePackageMetric());
        metrics.add(new SourceLinesOfCodeRecursivePackageMetric());
        metrics.add(new SourceLinesOfCodeTestPackageMetric());
        metrics.add(new SourceLinesOfCodeTestRecursivePackageMetric());
        metrics.add(new TestRatioPackageMetric());
        metrics.add(new TodoCommentCountPackageMetric());
        metrics.add(new TodoCommentCountRecursivePackageMetric());
        metrics.add(new TotalCyclomaticComplexityPackageMetric());
        metrics.add(new TrueCommentRatioPackageMetric());
        metrics.add(new TrueCommentRatioRecursivePackageMetric());
    }

    private static void initializeProjectMetrics(Collection<Metric> metrics) {
        metrics.add(new AttributeHidingFactorProjectMetric());
        metrics.add(new AttributeInheritanceFactorProjectMetric());
        metrics.add(new AverageCyclomaticComplexityProjectMetric());
        metrics.add(new CouplingFactorProjectMetric());
        metrics.add(new JavadocLinesOfCodeProjectMetric());
        metrics.add(new LinesOfJavaProjectMetric());
        metrics.add(new MethodHidingFactorProjectMetric());
        metrics.add(new MethodInheritanceFactorProjectMetric());
        metrics.add(new NumAbstractClassesProjectMetric());
        metrics.add(new NumAnnotationClassesProjectMetric());
        metrics.add(new NumClassesProjectMetric());
        metrics.add(new NumConcreteClassesProjectMetric());
        metrics.add(new NumEnumClassesProjectMetric());
        metrics.add(new NumInterfacesProjectMetric());
        metrics.add(new NumJavaFilesProjectMetric());
        metrics.add(new NumLeafClassesProjectMetric());
        metrics.add(new NumMethodsProjectMetric());
        metrics.add(new NumPackagesMetric());
        metrics.add(new NumProductClassesProjectMetric());
        metrics.add(new NumRootClassesProjectMetric());
        metrics.add(new NumTestAssertsProjectMetric());
        metrics.add(new NumTestCasesProjectMetric());
        metrics.add(new NumTestClassesProjectMetric());
        metrics.add(new NumTestMethodsProjectMetric());
        metrics.add(new NumTopLevelClassesProjectMetric());
        metrics.add(new NumTopLevelInterfacesProjectMetric());
        metrics.add(new PercentClassesJavadocedProjectMetric());
        metrics.add(new PercentFieldsJavadocedProjectMetric());
        metrics.add(new PercentMethodsJavadocedProjectMetric());
        metrics.add(new PolymorphismFactorProjectMetric());
        metrics.add(new TotalCyclomaticComplexityProjectMetric());
    }

    @NotNull
    @Override
    public List<PrebuiltMetricProfile> getPrebuiltProfiles() {
        final List<PrebuiltMetricProfile> out = new ArrayList<>(10);
        out.add(createChidamberKemererProfile());
        out.add(createClassCountProfile());
        out.add(createCodeSizeProfile());
        out.add(createComplexityProfile());
        out.add(createDependencyProfile());
        out.add(createFileCountProfile());
        out.add(createJavadocProfile());
        out.add(createMartinProfile());
        out.add(createMoodProfile());
        out.add(createTestProfile());
        return out;
    }

    private static PrebuiltMetricProfile createChidamberKemererProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("chidamber.kemerer.metrics.profile.name"));
        profile.addMetric("CouplingBetweenObjectsClass");
        profile.addMetric("DepthOfInheritance");
        profile.addMetric("LackOfCohesionOfMethodsClass");
        profile.addMetric("NumChildren");
        profile.addMetric("ResponseForClass");
        profile.addMetric("WeightedMethodComplexity");
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

    private static PrebuiltMetricProfile createCodeSizeProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("lines.of.code.metrics.profile.name"));
        profile.addMetric("CommentLinesOfCodeClass");
        profile.addMetric("CommentLinesOfCodeInterface");
        profile.addMetric("CommentLinesOfCodeMethod");
        profile.addMetric("CommentLinesOfCodePackage");
        profile.addMetric("CommentLinesOfCodeProject");
        profile.addMetric("CommentLinesOfCodeRecursivePackage");
        profile.addMetric("JavadocLinesOfCodeClass");
        profile.addMetric("JavadocLinesOfCodeInterface");
        profile.addMetric("JavadocLinesOfCodeMethod");
        profile.addMetric("JavadocLinesOfCodeModule");
        profile.addMetric("JavadocLinesOfCodePackage");
        profile.addMetric("JavadocLinesOfCodeProject");
        profile.addMetric("JavadocLinesOfCodeRecursivePackage");
        profile.addMetric("LinesOfCodeClass");
        profile.addMetric("LinesOfCodeInterface");
        profile.addMetric("LinesOfCodeMethod");
        profile.addMetric("LinesOfCodePackage");
        profile.addMetric("LinesOfCodeRecursivePackage");
        profile.addMetric("LinesOfJavaModule");
        profile.addMetric("LinesOfJavaProject");
        profile.addMetric("LinesOfProductCodePackage");
        profile.addMetric("LinesOfProductCodeRecursivePackage");
        profile.addMetric("LinesOfTestCodePackage");
        profile.addMetric("LinesOfTestCodeRecursivePackage");
        profile.addMetric("SourceLinesOfCodeInterface");
        profile.addMetric("SourceLinesOfCodeMethod");
        profile.addMetric("SourceLinesOfCodePackage");
        profile.addMetric("SourceLinesOfCodeProductPackage");
        profile.addMetric("SourceLinesOfCodeProductRecursivePackage");
        profile.addMetric("SourceLinesOfCodeTestPackage");
        profile.addMetric("SourceLinesOfCodeTestRecursivePackage");
        return profile;
    }

    private static PrebuiltMetricProfile createComplexityProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("complexity.metrics.profile.name"));
        profile.addMetric("AverageCyclomaticComplexityModule", null, null);
        profile.addMetric("AverageCyclomaticComplexityPackage", null, null);
        profile.addMetric("AverageCyclomaticComplexityProject", null, null);
        profile.addMetric("AverageOperationComplexity", null, Double.valueOf(3.0));
        profile.addMetric("CognitiveComplexity", null, Double.valueOf(15.0));
        profile.addMetric("CyclomaticComplexity", null, Double.valueOf(10.0));
        profile.addMetric("DesignComplexity", null, Double.valueOf(8.0));
        profile.addMetric("EssentialCyclomaticComplexity", null, Double.valueOf(3.0));
        profile.addMetric("MaximumOperationComplexity", null, null);
        profile.addMetric("TotalCyclomaticComplexityModule", null, null);
        profile.addMetric("TotalCyclomaticComplexityPackage", null, null);
        profile.addMetric("TotalCyclomaticComplexityProject", null, null);
        profile.addMetric("WeightedMethodComplexity", null, Double.valueOf(30.0));
        return profile;
    }

    private static PrebuiltMetricProfile createDependencyProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("dependency.metrics.profile.name"));
        profile.addMetric("NumCyclicDependenciesClass");
        profile.addMetric("NumCyclicDependenciesInterface");
        profile.addMetric("NumCyclicDependenciesPackage");
        profile.addMetric("NumDependenciesClass");
        profile.addMetric("NumDependenciesInterface");
        profile.addMetric("NumDependencyPackagesPackage");
        profile.addMetric("NumDependentPackagesPackage");
        profile.addMetric("NumDependentsClass");
        profile.addMetric("NumDependentsInterface");
        profile.addMetric("NumPackageDependenciesClass");
        profile.addMetric("NumPackageDependenciesInterface");
        profile.addMetric("NumPackageDependentsClass");
        profile.addMetric("NumPackageDependentsInterface");
        profile.addMetric("NumTransitiveDependenciesClass");
        profile.addMetric("NumTransitiveDependenciesInterface");
        profile.addMetric("NumTransitiveDependentPackagesPackage");
        profile.addMetric("NumTransitiveDependentsClass");
        profile.addMetric("NumTransitiveDependentsInterface");
        return profile;
    }

    private static PrebuiltMetricProfile createFileCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("file.count.metrics.profile.name"));
        profile.addMetric("NumJavaFilesModule");
        profile.addMetric("NumJavaFilesProject");
        return profile;
    }

    private static PrebuiltMetricProfile createJavadocProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("javadoc.coverage.metrics.profile.name"));
        profile.addMetric("JavadocLinesOfCodeClass");
        profile.addMetric("JavadocLinesOfCodeInterface");
        profile.addMetric("JavadocLinesOfCodeMethod");
        profile.addMetric("JavadocLinesOfCodeModule");
        profile.addMetric("JavadocLinesOfCodePackage");
        profile.addMetric("JavadocLinesOfCodeProject");
        profile.addMetric("PercentClassesJavadocedModule");
        profile.addMetric("PercentClassesJavadocedPackage");
        profile.addMetric("PercentClassesJavadocedProject");
        profile.addMetric("PercentFieldsJavadocedClass");
        profile.addMetric("PercentFieldsJavadocedInterface");
        profile.addMetric("PercentFieldsJavadocedModule");
        profile.addMetric("PercentFieldsJavadocedPackage");
        profile.addMetric("PercentFieldsJavadocedProject");
        profile.addMetric("PercentMethodsJavadocedClass");
        profile.addMetric("PercentMethodsJavadocedInterface");
        profile.addMetric("PercentMethodsJavadocedModule");
        profile.addMetric("PercentMethodsJavadocedPackage");
        profile.addMetric("PercentMethodsJavadocedProject");
        return profile;
    }

    private static PrebuiltMetricProfile createMartinProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("martin.packaging.metrics.profile.name"));
        profile.addMetric("Abstractness");
        profile.addMetric("AfferentCoupling");
        profile.addMetric("Distance");
        profile.addMetric("EfferentCoupling");
        profile.addMetric("Instability");
        return profile;
    }

    private static PrebuiltMetricProfile createMoodProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("mood.metrics.profile.name"));
        profile.addMetric("AttributeHidingFactorProject");
        profile.addMetric("AttributeInheritanceFactorProject");
        profile.addMetric("CouplingFactorProject");
        profile.addMetric("MethodHidingFactorProject");
        profile.addMetric("MethodInheritanceFactorProject");
        profile.addMetric("PolymorphismFactorProject");
        return profile;
    }

    private static PrebuiltMetricProfile createTestProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("junit.testing.metrics.profile.name"));
        profile.addMetric("NumTestAssertsClass");
        profile.addMetric("NumTestAssertsModule");
        profile.addMetric("NumTestAssertsPackage");
        profile.addMetric("NumTestAssertsProject");
        profile.addMetric("NumTestCasesModule");
        profile.addMetric("NumTestCasesPackage");
        profile.addMetric("NumTestCasesProject");
        profile.addMetric("NumTestMethodsClass");
        profile.addMetric("NumTestMethodsModule");
        profile.addMetric("NumTestMethodsPackage");
        profile.addMetric("NumTestMethodsProject");
        return profile;
    }
}
