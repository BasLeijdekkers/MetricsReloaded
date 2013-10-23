/*
 * Copyright 2005-2013 Sixth and Red River Software, Bas Leijdekkers
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
import java.util.Collections;
import java.util.List;

public class DefaultMetricProvider implements MetricProvider {

    private static final String COMPLEXITY_PROFILE_NAME = StockMetricsBundle.message("complexity.metrics.profile.name");
    private static final String JAVADOC_PROFILE_NAME =
            StockMetricsBundle.message("javadoc.coverage.metrics.profile.name");
    private static final String CODE_SIZE_PROFILE_NAME =
            StockMetricsBundle.message("lines.of.code.metrics.profile.name");
    private static final String MARTIN_PROFILE_NAME =
            StockMetricsBundle.message("martin.packaging.metrics.profile.name");
    private static final String DEPENDENCY_PROFILE_NAME =
            StockMetricsBundle.message("dependency.metrics.profile.name");
    private static final String MOOD_PROFILE_NAME = StockMetricsBundle.message("mood.metrics.profile.name");
    private static final String TEST_PROFILE_NAME = StockMetricsBundle.message("junit.testing.metrics.profile.name");
    private static final String CLASS_COUNT_PROFILE_NAME =
            StockMetricsBundle.message("class.count.metrics.profile.name");
    private static final String CHIDAMBER_KEMERER_PROFILE_NAME =
            StockMetricsBundle.message("chidamber.kemerer.metrics.profile.name");

    private final List<Class<? extends Metric>> metricsClasses = new ArrayList<Class<? extends Metric>>();

    @NotNull
    @Override
    public List<Class<? extends Metric>> getMetricClasses() {
        if (metricsClasses.isEmpty()) {
            initializeMethodMetrics();
            initializeClassMetrics();
            initializeInterfaceMetrics();
            initializePackageMetrics();
            initializeModuleMetrics();
            initializeProjectMetrics();
        }
        return Collections.unmodifiableList(metricsClasses);
    }

    private void initializeClassMetrics() {
        metricsClasses.add(ClassSizeAttributesMetric.class);
        metricsClasses.add(ClassSizeOperationsMetric.class);
        metricsClasses.add(ClassSizeOperationsAttributesMetric.class);
        metricsClasses.add(DepthOfInheritanceMetric.class);
        metricsClasses.add(NumInterfacesImplementedMetric.class);
        metricsClasses.add(NumSubclassesMetric.class);
        metricsClasses.add(NumTestMethodsClassMetric.class);
        metricsClasses.add(NumTestAssertsClassMetric.class);
        metricsClasses.add(NumConstructorsMetric.class);
        metricsClasses.add(NumAttributesAddedMetric.class);
        metricsClasses.add(NumAttributesInheritedMetric.class);
        metricsClasses.add(NumOperationsAddedMetric.class);
        metricsClasses.add(NumOperationsOverriddenMetric.class);
        metricsClasses.add(NumOperationsInheritedMetric.class);
        metricsClasses.add(NumInnerClassesMetric.class);
        metricsClasses.add(NumStatementsClassMetric.class);
        metricsClasses.add(NumDependenciesClassMetric.class);
        metricsClasses.add(NumPackageDependenciesClassMetric.class);
        metricsClasses.add(NumTransitiveDependenciesClassMetric.class);
        metricsClasses.add(NumCyclicDependenciesClassMetric.class);
        metricsClasses.add(LevelOrderClassMetric.class);
        metricsClasses.add(AdjustedLevelOrderClassMetric.class);
        metricsClasses.add(NumDependentsClassMetric.class);
        metricsClasses.add(NumPackageDependentsClassMetric.class);
        metricsClasses.add(NumTransitiveDependentsClassMetric.class);
        metricsClasses.add(CouplingBetweenObjectsClassMetric.class);
        metricsClasses.add(AverageOperationSizeMetric.class);
        metricsClasses.add(MaximumOperationSizeMetric.class);
        metricsClasses.add(AverageOperationComplexityMetric.class);
        metricsClasses.add(MaximumOperationComplexityMetric.class);
        metricsClasses.add(LinesOfCodeClassMetric.class);
        metricsClasses.add(CommentRatioClassMetric.class);
        metricsClasses.add(TrueCommentRatioClassMetric.class);
        metricsClasses.add(WeightedMethodComplexityMetric.class);
        metricsClasses.add(SourceLinesOfCodeClassMetric.class);
        metricsClasses.add(CommentLinesOfCodeClassMetric.class);
        metricsClasses.add(JavadocLinesOfCodeClassMetric.class);
        metricsClasses.add(AverageOperationParametersMetric.class);
        metricsClasses.add(NumCommandsClassMetric.class);
        metricsClasses.add(NumQueriesClassMetric.class);
        metricsClasses.add(PercentFieldsJavadocedClassMetric.class);
        metricsClasses.add(PercentMethodsJavadocedClassMetric.class);
        metricsClasses.add(HalsteadBugsClassMetric.class);
        metricsClasses.add(HalsteadDifficultyClassMetric.class);
        metricsClasses.add(HalsteadLengthClassMetric.class);
        metricsClasses.add(HalsteadVolumeClassMetric.class);
        metricsClasses.add(HalsteadVocabularyClassMetric.class);
        metricsClasses.add(HalsteadEffortClassMetric.class);
        metricsClasses.add(TodoCommentCountClassMetric.class);
        metricsClasses.add(NumTypeParametersClassMetric.class);
        metricsClasses.add(MessagePassingCouplingClassMetric.class);
        metricsClasses.add(ResponseForClassMetric.class);
        metricsClasses.add(LackOfCohesionOfMethodsClassMetric.class);
    }

    private void initializeInterfaceMetrics() {
        metricsClasses.add(InterfaceSizeAttributesMetric.class);
        metricsClasses.add(InterfaceSizeOperationsMetric.class);
        metricsClasses.add(InterfaceSizeOperationsAttributesMetric.class);
        metricsClasses.add(NumDependenciesInterfaceMetric.class);
        metricsClasses.add(NumPackageDependenciesInterfaceMetric.class);
        metricsClasses.add(NumTransitiveDependenciesInterfaceMetric.class);
        metricsClasses.add(NumCyclicDependenciesInterfaceMetric.class);
        metricsClasses.add(LevelOrderInterfaceMetric.class);
        metricsClasses.add(AdjustedLevelOrderInterfaceMetric.class);
        metricsClasses.add(NumDependentsInterfaceMetric.class);
        metricsClasses.add(NumPackageDependentsInterfaceMetric.class);
        metricsClasses.add(NumTransitiveDependentsInterfaceMetric.class);
        metricsClasses.add(CouplingBetweenObjectsInterfaceMetric.class);
        metricsClasses.add(NumSubinterfacesMetric.class);
        metricsClasses.add(NumImplementationsMetric.class);
        metricsClasses.add(CommentRatioInterfaceMetric.class);
        metricsClasses.add(TrueCommentRatioInterfaceMetric.class);
        metricsClasses.add(SourceLinesOfCodeInterfaceMetric.class);
        metricsClasses.add(LinesOfCodeInterfaceMetric.class);
        metricsClasses.add(CommentLinesOfCodeInterfaceMetric.class);
        metricsClasses.add(JavadocLinesOfCodeInterfaceMetric.class);
        metricsClasses.add(NumCommandsInterfaceMetric.class);
        metricsClasses.add(NumQueriesInterfaceMetric.class);
        metricsClasses.add(PercentFieldsJavadocedInterfaceMetric.class);
        metricsClasses.add(PercentMethodsJavadocedInterfaceMetric.class);
        metricsClasses.add(TodoCommentCountInterfaceMetric.class);
        metricsClasses.add(NumTypeParametersInterfaceMetric.class);
    }

    private void initializeMethodMetrics() {
        metricsClasses.add(CyclomaticComplexityMetric.class);
        metricsClasses.add(DesignComplexityMetric.class);
        metricsClasses.add(EssentialCyclomaticComplexityMetric.class);
        metricsClasses.add(ExtendedCyclomaticComplexityMetric.class);
        metricsClasses.add(NestingDepthMetric.class);
        metricsClasses.add(NumOverridesMethodMetric.class);
        metricsClasses.add(NumImplementationsMethodMetric.class);
        metricsClasses.add(NumLoopsMetric.class);
        metricsClasses.add(NumAssertsMetric.class);
        metricsClasses.add(NumParametersMetric.class);
        metricsClasses.add(NumTypeParametersMetric.class);
        metricsClasses.add(NumExceptionsThrownMetric.class);
        metricsClasses.add(NumExceptionsCaughtMetric.class);
        metricsClasses.add(NumReturnPointsMetric.class);
        metricsClasses.add(LinesOfCodeMethodMetric.class);
        metricsClasses.add(NumBranchStatementsMetric.class);
        metricsClasses.add(NumControlStatementsMetric.class);
        metricsClasses.add(NumExecutableStatementsMetric.class);
        metricsClasses.add(NumStatementsMetric.class);
        metricsClasses.add(NumExpressionsMetric.class);
        metricsClasses.add(NumTypecastExpressionsMetric.class);
        metricsClasses.add(JavadocLinesOfCodeMethodMetric.class);
        metricsClasses.add(RelativeLinesOfCodeMetric.class);
        metricsClasses.add(CommentRatioMethodMetric.class);
        metricsClasses.add(CommentLinesOfCodeMethodMetric.class);
        metricsClasses.add(TodoCommentCountMethodMetric.class);
        metricsClasses.add(TrueCommentRatioMethodMetric.class);
        metricsClasses.add(SourceLinesOfCodeMethodMetric.class);
        metricsClasses.add(NumMethodCallsMetric.class);
        metricsClasses.add(ControlDensityMetric.class);
        metricsClasses.add(HalsteadBugsMethodMetric.class);
        metricsClasses.add(HalsteadDifficultyMethodMetric.class);
        metricsClasses.add(HalsteadLengthMethodMetric.class);
        metricsClasses.add(HalsteadVolumeMethodMetric.class);
        metricsClasses.add(HalsteadVocabularyMethodMetric.class);
        metricsClasses.add(HalsteadEffortMethodMetric.class);
        metricsClasses.add(QCPCorrectnessMetric.class);
        metricsClasses.add(QCPMaintainabilityMetric.class);
        metricsClasses.add(QCPReliabilityMetric.class);
        metricsClasses.add(NumTimesCalledMetric.class);
        metricsClasses.add(NumTimesCalledProductMetric.class);
        metricsClasses.add(NumTimesCalledTestMetric.class);
        metricsClasses.add(LoopNestingDepthMetric.class);
        metricsClasses.add(ConditionalNestingDepthMetric.class);
    }

    private void initializePackageMetrics() {
        metricsClasses.add(NumAbstractClassesPackageMetric.class);
        metricsClasses.add(NumAbstractClassesRecursivePackageMetric.class);
        metricsClasses.add(NumClassesPackageMetric.class);
        metricsClasses.add(NumClassesRecursivePackageMetric.class);
        metricsClasses.add(NumProductClassesPackageMetric.class);
        metricsClasses.add(NumProductClassesRecursivePackageMetric.class);
        metricsClasses.add(NumTestMethodsPackageMetric.class);
        metricsClasses.add(NumTestMethodsRecursivePackageMetric.class);
        metricsClasses.add(NumTestAssertsPackageMetric.class);
        metricsClasses.add(NumTestAssertsRecursivePackageMetric.class);
        metricsClasses.add(NumTestCasesPackageMetric.class);
        metricsClasses.add(NumTestCasesRecursivePackageMetric.class);
        metricsClasses.add(NumTestClassesPackageMetric.class);
        metricsClasses.add(NumTestClassesRecursivePackageMetric.class);
        metricsClasses.add(NumConcreteClassesPackageMetric.class);
        metricsClasses.add(NumConcreteClassesRecursivePackageMetric.class);
        metricsClasses.add(NumInterfacesPackageMetric.class);
        metricsClasses.add(NumInterfacesRecursivePackageMetric.class);
        metricsClasses.add(NumTopLevelClassesPackageMetric.class);
        metricsClasses.add(NumTopLevelClassesRecursivePackageMetric.class);
        metricsClasses.add(NumTopLevelInterfacesPackageMetric.class);
        metricsClasses.add(NumTopLevelInterfacesRecursivePackageMetric.class);
        metricsClasses.add(AbstractnessMetric.class);
        metricsClasses.add(LinesOfCodePackageMetric.class);
        metricsClasses.add(LinesOfCodeRecursivePackageMetric.class);
        metricsClasses.add(LinesOfTestCodePackageMetric.class);
        metricsClasses.add(LinesOfTestCodeRecursivePackageMetric.class);
        metricsClasses.add(LinesOfProductCodePackageMetric.class);
        metricsClasses.add(LinesOfProductCodeRecursivePackageMetric.class);
        metricsClasses.add(CommentLinesOfCodePackageMetric.class);
        metricsClasses.add(CommentLinesOfCodeRecursivePackageMetric.class);
        metricsClasses.add(JavadocLinesOfCodePackageMetric.class);
        metricsClasses.add(JavadocLinesOfCodeRecursivePackageMetric.class);
        metricsClasses.add(CommentRatioRecursivePackageMetric.class);
        metricsClasses.add(CommentRatioPackageMetric.class);
        metricsClasses.add(TrueCommentRatioPackageMetric.class);
        metricsClasses.add(TrueCommentRatioRecursivePackageMetric.class);
        metricsClasses.add(SourceLinesOfCodePackageMetric.class);
        metricsClasses.add(SourceLinesOfCodeTestPackageMetric.class);
        metricsClasses.add(SourceLinesOfCodeProductPackageMetric.class);
        metricsClasses.add(SourceLinesOfCodeRecursivePackageMetric.class);
        metricsClasses.add(SourceLinesOfCodeTestRecursivePackageMetric.class);
        metricsClasses.add(SourceLinesOfCodeProductRecursivePackageMetric.class);
        metricsClasses.add(NumMethodsPackageMetric.class);
        metricsClasses.add(NumMethodsRecursivePackageMetric.class);
        metricsClasses.add(TestRatioPackageMetric.class);
        metricsClasses.add(EncapsulationRatioPackageMetric.class);
        metricsClasses.add(AfferentCouplingMetric.class);
        metricsClasses.add(EfferentCouplingMetric.class);
        metricsClasses.add(NumDependencyPackagesPackageMetric.class);
        metricsClasses.add(NumDependentPackagesPackageMetric.class);
        metricsClasses.add(InstabilityMetric.class);
        metricsClasses.add(DistanceMetric.class);
        metricsClasses.add(PercentClassesJavadocedPackageMetric.class);
        metricsClasses.add(PercentClassesJavadocedRecursivePackageMetric.class);
        metricsClasses.add(PercentMethodsJavadocedPackageMetric.class);
        metricsClasses.add(PercentMethodsJavadocedRecursivePackageMetric.class);
        metricsClasses.add(PercentFieldsJavadocedPackageMetric.class);
        metricsClasses.add(PercentFieldsJavadocedRecursivePackageMetric.class);
        metricsClasses.add(TotalCyclomaticComplexityPackageMetric.class);
        metricsClasses.add(AverageCyclomaticComplexityPackageMetric.class);
        metricsClasses.add(TodoCommentCountPackageMetric.class);
        metricsClasses.add(TodoCommentCountRecursivePackageMetric.class);
        metricsClasses.add(NumCyclicDependenciesPackageMetric.class);
        metricsClasses.add(LevelOrderPackageMetric.class);
        metricsClasses.add(AdjustedLevelOrderPackageMetric.class);
        metricsClasses.add(NumTransitiveDependentPackagesPackageMetric.class);
        metricsClasses.add(NumTransitiveDependencyPackagesPackageMetric.class);
        metricsClasses.add(NumRootClassesPackageMetric.class);
        metricsClasses.add(NumRootClassesRecursivePackageMetric.class);
        metricsClasses.add(NumLeafClassesPackageMetric.class);
        metricsClasses.add(NumLeafClassesRecursivePackageMetric.class);
        metricsClasses.add(NumEnumClassesPackageMetric.class);
        metricsClasses.add(NumEnumClassesRecursivePackageMetric.class);
        metricsClasses.add(NumAnnotationClassesPackageMetric.class);
        metricsClasses.add(NumAnnotationClassesRecursivePackageMetric.class);
    }

    private void initializeModuleMetrics() {
        metricsClasses.add(NumAbstractClassesModuleMetric.class);
        metricsClasses.add(NumClassesModuleMetric.class);
        metricsClasses.add(NumJSPFilesModuleMetric.class);
        metricsClasses.add(NumHTMLFilesModuleMetric.class);
        metricsClasses.add(NumXMLFilesModuleMetric.class);
        metricsClasses.add(NumProductClassesModuleMetric.class);
        metricsClasses.add(NumTestClassesModuleMetric.class);
        metricsClasses.add(NumTestCasesModuleMetric.class);
        metricsClasses.add(NumTestMethodsModuleMetric.class);
        metricsClasses.add(NumTestAssertsModuleMetric.class);
        metricsClasses.add(NumConcreteClassesModuleMetric.class);
        metricsClasses.add(NumInterfacesModuleMetric.class);
        metricsClasses.add(NumTopLevelClassesModuleMetric.class);
        metricsClasses.add(NumTopLevelInterfacesModuleMetric.class);
        metricsClasses.add(LinesOfCodeModuleMetric.class);
        metricsClasses.add(LinesOfTestCodeModuleMetric.class);
        metricsClasses.add(LinesOfProductCodeModuleMetric.class);
        metricsClasses.add(LinesOfJSPModuleMetric.class);
        metricsClasses.add(LinesOfHTMLModuleMetric.class);
        metricsClasses.add(LinesOfXMLModuleMetric.class);
        metricsClasses.add(CommentLinesOfCodeModuleMetric.class);
        metricsClasses.add(JavadocLinesOfCodeModuleMetric.class);
        metricsClasses.add(CommentRatioModuleMetric.class);
        metricsClasses.add(TrueCommentRatioModuleMetric.class);
        metricsClasses.add(SourceLinesOfCodeModuleMetric.class);
        metricsClasses.add(SourceLinesOfCodeTestModuleMetric.class);
        metricsClasses.add(SourceLinesOfCodeProductModuleMetric.class);
        metricsClasses.add(NumMethodsModuleMetric.class);
        metricsClasses.add(TestRatioModuleMetric.class);
        metricsClasses.add(EncapsulationRatioModuleMetric.class);
        metricsClasses.add(PercentClassesJavadocedModuleMetric.class);
        metricsClasses.add(PercentMethodsJavadocedModuleMetric.class);
        metricsClasses.add(PercentFieldsJavadocedModuleMetric.class);
        metricsClasses.add(TotalCyclomaticComplexityModuleMetric.class);
        metricsClasses.add(AverageCyclomaticComplexityModuleMetric.class);
        metricsClasses.add(TodoCommentCountModuleMetric.class);
        metricsClasses.add(NumRootClassesModuleMetric.class);
        metricsClasses.add(NumLeafClassesModuleMetric.class);
        metricsClasses.add(NumEnumClassesModuleMetric.class);
        metricsClasses.add(NumAnnotationClassesModuleMetric.class);
    }

    private void initializeProjectMetrics() {
        metricsClasses.add(NumAbstractClassesProjectMetric.class);
        metricsClasses.add(NumClassesProjectMetric.class);
        metricsClasses.add(NumJSPFilesProjectMetric.class);
        metricsClasses.add(NumHTMLFilesProjectMetric.class);
        metricsClasses.add(NumXMLFilesProjectMetric.class);
        metricsClasses.add(NumConcreteClassesProjectMetric.class);
        metricsClasses.add(NumInterfacesProjectMetric.class);
        metricsClasses.add(NumTopLevelInterfacesProjectMetric.class);
        metricsClasses.add(NumTopLevelClassesProjectMetric.class);
        metricsClasses.add(NumMethodsProjectMetric.class);
        metricsClasses.add(LinesOfCodeProjectMetric.class);
        metricsClasses.add(CommentLinesOfCodeProjectMetric.class);
        metricsClasses.add(JavadocLinesOfCodeProjectMetric.class);
        metricsClasses.add(CommentRatioProjectMetric.class);
        metricsClasses.add(TrueCommentRatioProjectMetric.class);
        metricsClasses.add(SourceLinesOfCodeProjectMetric.class);
        metricsClasses.add(SourceLinesOfCodeTestProjectMetric.class);
        metricsClasses.add(SourceLinesOfCodeProductProjectMetric.class);
        metricsClasses.add(NumPackagesMetric.class);
        metricsClasses.add(NumTestClassesProjectMetric.class);
        metricsClasses.add(NumTestCasesProjectMetric.class);
        metricsClasses.add(NumTestMethodsProjectMetric.class);
        metricsClasses.add(NumTestAssertsProjectMetric.class);
        metricsClasses.add(NumProductClassesProjectMetric.class);
        metricsClasses.add(LinesOfProductCodeProjectMetric.class);
        metricsClasses.add(LinesOfTestCodeProjectMetric.class);
        metricsClasses.add(LinesOfJSPProjectMetric.class);
        metricsClasses.add(LinesOfHTMLProjectMetric.class);
        metricsClasses.add(LinesOfXMLProjectMetric.class);
        metricsClasses.add(TestRatioProjectMetric.class);
        metricsClasses.add(PercentClassesJavadocedProjectMetric.class);
        metricsClasses.add(PercentMethodsJavadocedProjectMetric.class);
        metricsClasses.add(PercentFieldsJavadocedProjectMetric.class);
        metricsClasses.add(TotalCyclomaticComplexityProjectMetric.class);
        metricsClasses.add(AverageCyclomaticComplexityProjectMetric.class);
        metricsClasses.add(TodoCommentCountProjectMetric.class);
        metricsClasses.add(AttributeInheritanceFactorProjectMetric.class);
        metricsClasses.add(MethodInheritanceFactorProjectMetric.class);
        metricsClasses.add(AttributeHidingFactorProjectMetric.class);
        metricsClasses.add(MethodHidingFactorProjectMetric.class);
        metricsClasses.add(CouplingFactorProjectMetric.class);
        metricsClasses.add(PolymorphismFactorProjectMetric.class);
        metricsClasses.add(NumRootClassesProjectMetric.class);
        metricsClasses.add(NumLeafClassesProjectMetric.class);
        metricsClasses.add(NumEnumClassesProjectMetric.class);
        metricsClasses.add(NumAnnotationClassesProjectMetric.class);
    }

    @Override
    public List<PrebuiltMetricProfile> getPrebuiltProfiles() {
        final List<PrebuiltMetricProfile> out = new ArrayList<PrebuiltMetricProfile>();
        out.add(createComplexityProfile());
        out.add(createCodeSizeProfile());
        out.add(createChidamberKemererProfile());
        out.add(createClassCountProfile());
        out.add(createDependencyProfile());
        out.add(createJavadocProfile());
        out.add(createMartinProfile());
        out.add(createMoodProfile());
        out.add(createTestProfile());
        return out;
    }

    private static PrebuiltMetricProfile createComplexityProfile() {
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(COMPLEXITY_PROFILE_NAME);
        profile.addMetric("CyclomaticComplexity", null, 10.0);
        profile.addMetric("DesignComplexity", null, 8.0);
        profile.addMetric("ExtendedCyclomaticComplexity", null, 10.0);
        profile.addMetric("EssentialCyclomaticComplexity", null, 3.0);
        profile.addMetric("WeightedMethodComplexity", null, 30.0);
        profile.addMetric("AverageOperationComplexity", null, 3.0);
        return profile;
    }

    private static PrebuiltMetricProfile createCodeSizeProfile() {
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(CODE_SIZE_PROFILE_NAME);
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
        profile.addMetric("LinesOfJSPProject");
        profile.addMetric("LinesOfJSPModule");
        profile.addMetric("LinesOfXMLProject");
        profile.addMetric("LinesOfXMLModule");
        return profile;
    }

    private static PrebuiltMetricProfile createClassCountProfile() {
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(CLASS_COUNT_PROFILE_NAME);
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
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(DEPENDENCY_PROFILE_NAME);
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
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(MOOD_PROFILE_NAME);
        profile.addMetric("MethodHidingFactorProject");
        profile.addMetric("MethodInheritanceFactorProject");
        profile.addMetric("AttributeHidingFactorProject");
        profile.addMetric("AttributeInheritanceFactorProject");
        profile.addMetric("CouplingFactorProject");
        profile.addMetric("PolymorphismFactorProject");
        return profile;
    }

    private static PrebuiltMetricProfile createMartinProfile() {
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(MARTIN_PROFILE_NAME);
        profile.addMetric("AfferentCoupling");
        profile.addMetric("EfferentCoupling");
        profile.addMetric("Abstractness");
        profile.addMetric("Instability");
        profile.addMetric("Distance");
        return profile;
    }

    private static PrebuiltMetricProfile createTestProfile() {
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(TEST_PROFILE_NAME);
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
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(CHIDAMBER_KEMERER_PROFILE_NAME);
        profile.addMetric("ResponseForClass");
        profile.addMetric("CouplingBetweenObjectsClass");
        profile.addMetric("DepthOfInheritance");
        profile.addMetric("WeightedMethodComplexity");
        profile.addMetric("NumSubclasses");
        profile.addMetric("LackOfCohesionOfMethodsClass");
        return profile;
    }

    private static PrebuiltMetricProfile createJavadocProfile() {
        final PrebuiltMetricProfile profile = new PrebuiltMetricProfile(JAVADOC_PROFILE_NAME);
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
