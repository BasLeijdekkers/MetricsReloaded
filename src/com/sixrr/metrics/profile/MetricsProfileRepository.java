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

package com.sixrr.metrics.profile;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ExportableComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.MetricProvider;
import com.sixrr.metrics.PrebuiltMetricProfile;
import com.sixrr.metrics.config.MetricsReloadedConfig;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.metricModel.MetricInstanceImpl;
import com.sixrr.metrics.metricModel.MetricsCategoryNameUtil;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class MetricsProfileRepository implements MetricRepository, ExportableComponent {

    private static final Logger LOG = Logger.getInstance("#com.sixrr.metrics.profile.MetricsProfileRepository");

    @NonNls
    private static final String METRIC_PROFILE_DIR = PathManager.getConfigPath() + File.separator + "metrics";

    private final Map<String, MetricsProfile> profiles = new LinkedHashMap<String, MetricsProfile>(20);
    private String selectedProfile = "";
    private final Map<String, Metric> metrics = new HashMap();

    private MetricsProfileRepository() {
        initialize();
    }

    public static MetricsProfileRepository getInstance() {
        return ServiceManager.getService(MetricsProfileRepository.class);
    }

    /**
     * Allows for exporting metrics profiles. (<pre>File | Export settings...</pre>)
     * <p>
     * The metrics configuration files are located within <code>idea.config.path/metrics/...</code>
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
        final String previouslySelectedProfile = MetricsReloadedConfig.getInstance().getSelectedProfile();
        selectedProfile = profiles.containsKey(previouslySelectedProfile)
                ? previouslySelectedProfile
                : profiles.keySet().iterator().next();
    }

    private void loadMetricsFromProviders() {
        final Application application = ApplicationManager.getApplication();
        final MetricProvider[] metricProviders = application.getExtensions(MetricProvider.EXTENSION_POINT_NAME);
        for (MetricProvider provider : metricProviders) {
            final List<Metric> metrics = provider.getMetrics();
            for (Metric metric : metrics) {
                this.metrics.put(metric.getClass().getName(), metric);
            }
        }
    }

    private void reconcile(MetricsProfile profile) {
        for (final Metric metric : metrics.values()) {
            if (profile.getMetricInstance(metric) == null) {
                profile.addMetricInstance(new MetricInstanceImpl(metric));
            }
        }
    }

    private MetricsProfile buildProfile(String name) {
        final List<MetricInstance> result = new ArrayList<MetricInstance>(metrics.size());
        for (final Metric metric : metrics.values()) {
            result.add(new MetricInstanceImpl(metric));
        }
        return new MetricsProfileImpl(name, result);
    }

    private void addPrebuiltProfiles() {
        final Application application = ApplicationManager.getApplication();
        final MetricProvider[] metricProviders = application.getExtensions(MetricProvider.EXTENSION_POINT_NAME);
        for (MetricProvider provider : metricProviders) {
            final List<PrebuiltMetricProfile> prebuiltProfiles = provider.getPrebuiltProfiles();
            for (PrebuiltMetricProfile prebuiltProfile : prebuiltProfiles) {
                addPrebuiltProfile(prebuiltProfile);
            }
        }
    }

    private void addPrebuiltProfile(PrebuiltMetricProfile builtInProfile) {
        final String name = builtInProfile.getProfileName();
        final MetricsProfile existingProfile = profiles.get(name);
        final MetricsProfile profile = (existingProfile != null) ? existingProfile : buildProfile(name);
        final Set<String> metricIDs = builtInProfile.getMetricIDs();
        for (String metricID : metricIDs) {
            final MetricInstance instance = profile.getMetricInstance(metricID);
            assert instance != null : "no instance found for " + metricID;
            instance.setEnabled(true);
            final Double lowerThreshold = builtInProfile.getLowerThresholdForMetric(metricID);
            final Double upperThreshold = builtInProfile.getUpperThresholdForMetric(metricID);
            if (lowerThreshold != null) {
                instance.setLowerThresholdEnabled(true);
                instance.setLowerThreshold(lowerThreshold.doubleValue());
            }
            if (upperThreshold != null) {
                instance.setUpperThresholdEnabled(true);
                instance.setUpperThreshold(upperThreshold.doubleValue());
            }
        }
        profile.setBuiltIn(true);
        profiles.put(name, profile);
    }

    public String generateNewProfileName() {
        final String baseName =
                (selectedProfile == null || selectedProfile.isEmpty()) ? "Metrics" : selectedProfile;
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
        for (File file : files) {
            final MetricsProfile profile = MetricsProfileImpl.loadFromFile(file, this);
            if (profile != null) {
                final String profileName = profile.getName();
                profiles.put(profileName, profile);
            }
        }
    }

    private void reconcileProfiles() {
        final Collection<MetricsProfile> existingProfiles = profiles.values();
        for (final MetricsProfile profile : existingProfiles) {
            reconcile(profile);
        }
    }

    @Override
    @Nullable
    public Metric getMetric(String fqName) {
        return metrics.get(fqName);
    }

    public String[] getProfileNames() {
        final Set<String> keys = profiles.keySet();
        final int numKeys = keys.size();
        final String[] names = keys.toArray(new String[numKeys]);
        Arrays.sort(names);
        return names;
    }

    @Nullable
    public MetricsProfile getCurrentProfile() {
        return profiles.get(selectedProfile);
    }

    public void setSelectedProfile(String profileName) {
        selectedProfile = profileName;
        MetricsReloadedConfig.getInstance().setSelectedProfile(selectedProfile);
    }

    public void deleteProfile(MetricsProfile profile) {
        final String profileName = profile.getName();
        profiles.remove(profileName);
        final String randomProfileName = profiles.keySet().iterator().next();
        setSelectedProfile(randomProfileName);
        final File profileFile = getFileForProfile(profile);
        profileFile.delete();
    }

    public void reloadProfileFromStorage(MetricsProfile profile) {
        final File profileFile = getFileForProfile(profile);
        final MetricsProfile newProfile = MetricsProfileImpl.loadFromFile(profileFile, this);
        if (newProfile != null) {
            profiles.put(newProfile.getName(),  newProfile);
        }
    }

    public static void persistProfile(MetricsProfile profile) {
        final File profileFile = getFileForProfile(profile);
        try {
            profile.writeToFile(profileFile);
        } catch (IOException e) {
            LOG.warn(e);
        }
    }

    @NonNls
    private static File getFileForProfile(MetricsProfile profile) {
        final String profileName = profile.getName();
        return new File(METRIC_PROFILE_DIR, profileName + ".xml");
    }

    public void duplicateCurrentProfile(String newProfileName) {
        final MetricsProfile currentProfile = getCurrentProfile();
        assert currentProfile != null;
        final MetricsProfile newProfile;
        try {
            newProfile = currentProfile.clone();
        } catch (CloneNotSupportedException ignore) {
            return;
        }
        newProfile.setName(newProfileName);
        profiles.put(newProfileName, newProfile);
        persistProfile(newProfile);
        setSelectedProfile(newProfileName);
    }

    public void createEmptyProfile(String newProfileName) {
        final MetricsProfile newProfile = buildProfile(newProfileName);
        profiles.put(newProfileName, newProfile);
        persistProfile(newProfile);
        setSelectedProfile(newProfileName);
    }

    public void persistCurrentProfile() {
        final MetricsProfile currentProfile = getCurrentProfile();
        persistProfile(currentProfile);
    }

    public boolean profileExists(String profileName) {
        return profiles.containsKey(profileName);
    }

    public MetricsProfile getProfileForName(String profileName) {
        return profiles.get(profileName);
    }

    public void addProfile(MetricsProfile profile) {
        final String newProfileName = profile.getName();
        profiles.put(newProfileName, profile);
        setSelectedProfile(newProfileName);
    }

    public void printMetricsDescriptions() {
        final Collection<Metric> metrics = this.metrics.values();

        System.out.println(metrics.size() + "  metrics");
        MetricCategory currentCategory = null;
        for (final Metric metric : metrics) {
            final MetricCategory category = metric.getCategory();
            if (category != currentCategory) {
                System.out.println(MetricsCategoryNameUtil.getLongNameForCategory(category));
                currentCategory = category;
            }
            System.out.println("    " + metric.getDisplayName());
        }
    }
}
