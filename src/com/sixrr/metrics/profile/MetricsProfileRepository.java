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

package com.sixrr.metrics.profile;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ExportableComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.SmartList;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.MetricProvider;
import com.sixrr.metrics.PrebuiltMetricProfile;
import com.sixrr.metrics.config.MetricsReloadedConfig;
import com.sixrr.metrics.utils.MetricsCategoryNameUtil;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class MetricsProfileRepository implements MetricRepository, ExportableComponent {

    private static final Logger LOG = Logger.getInstance(MetricsProfileRepository.class);

    @NonNls
    private static final String METRIC_PROFILE_DIR = PathManager.getConfigPath() + File.separator + "metrics";

    private final Map<String, MetricsProfile> profiles = new LinkedHashMap<>();
    private final Map<String, MetricsProfile> prebuiltProfiles = new HashMap<>();
    private MetricsProfile selectedProfile = null;
    private final Map<String, Metric> metrics = new HashMap<>();

    private MetricsProfileRepository() {
        initialize();
    }

    public static MetricsProfileRepository getInstance() {
        return ServiceManager.getService(MetricsProfileRepository.class);
    }

    /**
     * Allows for exporting metrics profiles. (<pre>File | Export settings...</pre>)
     * <p>
     * The metrics configuration files are located within {@code idea.config.path/metrics/...}
     */
    @NotNull
    @Override
    public File[] getExportFiles() {
        @NonNls final String dirName = PathManager.getConfigPath() + File.separator + "metrics";
        final File metricsDirectory = new File(dirName);
        final File[] files = metricsDirectory.listFiles();
        final File[] out;
        if (files == null) {
            out = new File[1];
        } else {
            out = new File[files.length + 1];
            System.arraycopy(files, 0, out, 1, files.length);
        }
        out[0] = metricsDirectory;
        return out;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return MetricsReloadedBundle.message("metrics.profiles.configuration.presentable.name");
    }

    private void initialize() {
        loadMetricsFromProviders();
        loadProfiles();
        reconcileProfiles();
        addPrebuiltProfiles();
        final MetricsReloadedConfig config = MetricsReloadedConfig.getInstance();
        final String previouslySelectedProfile = config.getSelectedProfile();
        if (!config.isSelectedProfilePrebuilt()) {
            final MetricsProfile profile = profiles.get(previouslySelectedProfile);
            if (profile != null) {
                selectedProfile = profile;
                return;
            }
        }
        final MetricsProfile profile = prebuiltProfiles.get(previouslySelectedProfile);
        selectedProfile = (profile != null) ? profile : prebuiltProfiles.values().iterator().next();
    }

    private void loadMetricsFromProviders() {
        final MetricProvider[] metricProviders = MetricProvider.EXTENSION_POINT_NAME.getExtensions();
        for (MetricProvider provider : metricProviders) {
            final List<Metric> metrics = provider.getMetrics();
            for (Metric metric : metrics) {
                this.metrics.put(metric.getClass().getName(), metric);
            }
        }
    }

    private void reconcile(MetricsProfile profile) {
        for (Metric metric : metrics.values()) {
            if (profile.getMetricInstance(metric) == null) {
                profile.addMetricInstance(new MetricInstanceImpl(metric));
            }
        }
    }

    private MetricsProfile buildProfile(String name) {
        final List<MetricInstance> result = new ArrayList<>(metrics.size());
        for (Metric metric : metrics.values()) {
            result.add(new MetricInstanceImpl(metric));
        }
        return new MetricsProfileImpl(name, result);
    }

    private void addPrebuiltProfiles() {
        for (MetricProvider provider : MetricProvider.EXTENSION_POINT_NAME.getExtensions()) {
            for (PrebuiltMetricProfile prebuiltProfile : provider.getPrebuiltProfiles()) {
                addPrebuiltProfile(prebuiltProfile);
            }
        }
    }

    private void addPrebuiltProfile(PrebuiltMetricProfile prebuiltProfile) {
        final String name = prebuiltProfile.getProfileName();
        final MetricsProfile existingProfile = prebuiltProfiles.get(name);
        final MetricsProfile profile;
        if (existingProfile != null) {
            profile = existingProfile;
        } else {
            profile = buildProfile(name);
            profile.setPrebuilt(true);
            prebuiltProfiles.put(name, profile);
        }
        final Set<String> metricIDs = prebuiltProfile.getMetricIDs();
        for (String metricID : metricIDs) {
            final MetricInstance instance = profile.getMetricInstance(metricID);
            assert instance != null : "no instance found for " + metricID;
            assert !instance.isEnabled() : "instance already enabled for " + metricID;
            instance.setEnabled(true);
            final Double lowerThreshold = prebuiltProfile.getLowerThresholdForMetric(metricID);
            final Double upperThreshold = prebuiltProfile.getUpperThresholdForMetric(metricID);
            if (lowerThreshold != null) {
                instance.setLowerThresholdEnabled(true);
                instance.setLowerThreshold(lowerThreshold.doubleValue());
            }
            if (upperThreshold != null) {
                instance.setUpperThresholdEnabled(true);
                instance.setUpperThreshold(upperThreshold.doubleValue());
            }
        }
    }

    public String generateNewProfileName() {
        final String baseName = (selectedProfile == null) ? "Metrics" : selectedProfile.getName();
        return generateNewProfileName(baseName);
    }

    public String generateNewProfileName(@NotNull String baseName) {
        int index = 1;
        String newName = baseName;
        while (profiles.containsKey(newName)) {
            index++;
            newName = baseName + " (" + index + ")";
        }
        return newName;
    }

    private void loadProfiles() {
        final File metricsDir = new File(METRIC_PROFILE_DIR);
        if (!metricsDir.exists()) {
            return;
        }
        final File[] files = metricsDir.listFiles();
        if (files == null) {
            return;
        }
        final List<MetricsProfile> profiles = new SmartList<>();
        for (File file : files) {
            final MetricsProfile profile = MetricsProfileImpl.loadFromFile(file, this);
            if (profile != null) {
                profiles.add(profile);
            }
        }
        profiles.sort(Comparator.comparing(MetricsProfile::getName));
        for (MetricsProfile profile : profiles) {
            this.profiles.put(profile.getName(), profile);
        }
    }

    private void reconcileProfiles() {
        final Collection<MetricsProfile> existingProfiles = profiles.values();
        for (MetricsProfile profile : existingProfiles) {
            reconcile(profile);
        }
    }

    @Override
    @Nullable
    public Metric getMetric(String fqName) {
        return metrics.get(fqName);
    }

    public MetricsProfile[] getProfiles() {
        final Collection<MetricsProfile> values = profiles.values();
        final Collection<MetricsProfile> prebuiltValues = prebuiltProfiles.values();
        final SmartList<MetricsProfile> result = new SmartList<>(values);
        result.addAll(prebuiltValues);
        result.sort(Comparator.comparing(MetricsProfile::getName));
        return result.toArray(MetricsProfile.EMPTY_ARRAY);
    }

    @Nullable
    public MetricsProfile getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(MetricsProfile profile) {
        selectedProfile = profile;
        final MetricsReloadedConfig config = MetricsReloadedConfig.getInstance();
        config.setSelectedProfile(selectedProfile.getName());
        config.setSelectedProfilePrebuilt(selectedProfile.isPrebuilt());
    }

    public void deleteProfile(MetricsProfile profile) {
        profiles.remove(profile.getName());
        MetricsReloadedConfig.getInstance().removeDisplaySpecification(profile);
        getFileForProfile(profile).delete();
        setSelectedProfile(profiles.isEmpty()
                           ? prebuiltProfiles.values().iterator().next()
                           : profiles.values().iterator().next());
    }

    public void reloadProfileFromStorage(MetricsProfile profile) {
        final MetricsProfile newProfile = MetricsProfileImpl.loadFromFile(getFileForProfile(profile), this);
        if (newProfile != null) {
            profiles.put(newProfile.getName(),  newProfile);
        }
    }

    public static void persistProfile(MetricsProfile profile) {
        final File profileFile = getFileForProfile(profile);
        try {
            final File profileDirectory = new File(METRIC_PROFILE_DIR);
            if (!profileDirectory.exists() && !profileDirectory.mkdirs()) {
                return;
            }
            profile.writeToFile(profileFile);
        } catch (IOException e) {
            LOG.warn(e);
        }
    }

    @NonNls
    private static File getFileForProfile(MetricsProfile profile) {
        return new File(METRIC_PROFILE_DIR, profile.getName() + ".xml");
    }

    public MetricsProfile duplicateSelectedProfile(String newProfileName) {
        final MetricsProfile selectedProfile = getSelectedProfile();
        assert selectedProfile != null;
        final MetricsProfile newProfile;
        try {
            newProfile = selectedProfile.clone();
        } catch (CloneNotSupportedException ignore) {
            throw new AssertionError();
        }
        newProfile.setName(newProfileName);
        profiles.put(newProfileName, newProfile);
        persistProfile(newProfile);
        setSelectedProfile(newProfile);
        return newProfile;
    }

    public MetricsProfile createEmptyProfile(String newProfileName) {
        final MetricsProfile newProfile = buildProfile(newProfileName);
        profiles.put(newProfileName, newProfile);
        persistProfile(newProfile);
        setSelectedProfile(newProfile);
        return newProfile;
    }

    public boolean profileExists(String profileName) {
        return profiles.containsKey(profileName);
    }

    public MetricsProfile getProfileByName(String profileName) {
        final MetricsProfile profile = profiles.get(profileName);
        return (profile == null) ? prebuiltProfiles.get(profileName) : profile;
    }

    public void addProfile(MetricsProfile profile) {
        profiles.put(profile.getName(), profile);
        setSelectedProfile(profile);
    }

    public void printMetricsDescriptions() {
        final Collection<Metric> metrics = this.metrics.values();

        System.out.println(metrics.size() + "  metrics");
        MetricCategory currentCategory = null;
        for (Metric metric : metrics) {
            final MetricCategory category = metric.getCategory();
            if (category != currentCategory) {
                System.out.println(MetricsCategoryNameUtil.getLongNameForCategory(category));
                currentCategory = category;
            }
            System.out.println("    " + metric.getDisplayName());
        }
    }
}
