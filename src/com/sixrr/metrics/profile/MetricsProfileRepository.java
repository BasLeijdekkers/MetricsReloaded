/*
 * Copyright 2005-2011 Sixth and Red River Software, Bas Leijdekkers
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
import com.intellij.openapi.diagnostic.Logger;
import com.sixrr.metrics.MetricProvider;
import com.sixrr.metrics.PrebuiltMetricProfile;
import com.sixrr.metrics.config.MetricsReloadedConfig;
import com.sixrr.metrics.metricModel.MetricInstance;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"OverlyComplexMethod", "OverlyCoupledMethod", "OverlyComplexClass",
        "OverlyCoupledClass", "ClassWithTooManyMethods"})
public class MetricsProfileRepository {

    private static final Logger LOG =
            Logger.getInstance("#com.sixrr.metrics.profile.MetricsProfileRepository");

    @NonNls public static final String METRIC_PROFILE_DIR =
            PathManager.getConfigPath() + File.separator + "metrics";

    private final Map<String, MetricsProfile> profiles = new LinkedHashMap<String, MetricsProfile>(20);
    private final MetricsProfileTemplate template;
    private String selectedProfile = "";
    private final MetricsReloadedConfig configuration;

    public static void main(String[] args) {
        final MetricsProfileRepository repository = new MetricsProfileRepository(null);
        repository.initialize();
        repository.printMetricsDescriptions();
    }

    private void printMetricsDescriptions() {
        template.printMetricsDescriptions();
    }

    public MetricsProfileRepository(MetricsReloadedConfig configuration) {
        this.configuration = configuration;
        template = new MetricsProfileTemplate();
        template.loadMetricsFromProviders();
    }

    public void initialize() {
        loadProfiles();
        reconcileProfiles();
        addPrebuiltProfiles();
        if (configuration != null) {
            final String previouslySelectedProfile = configuration.getSelectedProfile();
            if (profiles.containsKey(previouslySelectedProfile)) {
                selectedProfile = previouslySelectedProfile;
            } else {
                selectedProfile = profiles.keySet().iterator().next();
            }
        }
    }

    private void addPrebuiltProfiles() {
        final Application application = ApplicationManager.getApplication();
        final MetricProvider[] metricProviders =
                application.getExtensions(MetricProvider.EXTENSION_POINT_NAME);
        for (MetricProvider provider : metricProviders) {
            final List<PrebuiltMetricProfile> prebuiltProfiles = provider.getPrebuiltProfiles();
            for (PrebuiltMetricProfile prebuiltProfile : prebuiltProfiles) {
                addPrebuiltProfile(prebuiltProfile);
            }
        }
    }

    private void addPrebuiltProfile(PrebuiltMetricProfile builtInProfile) {
        final String name = builtInProfile.getProfileName();
        if (profiles.containsKey(name)) {
            return;
        }
        final MetricsProfile profile = template.instantiate(name);
        final Set<String> metricNames = builtInProfile.getMetricIDs();
        for (String metricName : metricNames) {
            final MetricInstance instance = profile.getMetricForName(metricName);
            if (instance == null) {
                continue;
            }
            instance.setEnabled(true);
            final Double lowerThreshold = builtInProfile.getLowerThresholdForMetric(metricName);
            final Double upperThreshold = builtInProfile.getUpperThresholdForMetric(metricName);
            if (lowerThreshold != null) {
                instance.setLowerThresholdEnabled(true);
                instance.setLowerThreshold(lowerThreshold);
            }
            if (upperThreshold != null) {
                instance.setUpperThresholdEnabled(true);
                instance.setUpperThreshold(upperThreshold);
            }
        }
        profile.setBuiltIn(true);
        profiles.put(name, profile);
    }

    private void loadProfiles() {
        final File metricsDir = new File(METRIC_PROFILE_DIR);
        if (!metricsDir.exists()) {
            metricsDir.mkdir();
        }
        final File[] files = metricsDir.listFiles();
        for (File file : files) {
            final MetricsProfile profile = MetricsProfileImpl.loadFromFile(file);
            if (profile != null) {
                final String profileName = profile.getName();
                profiles.put(profileName, profile);
            }
        }
    }

    private void reconcileProfiles() {
        final Collection<MetricsProfile> existingProfiles = profiles.values();
        for (final MetricsProfile profile : existingProfiles) {
            template.reconcile(profile);
        }
    }

    public String[] getProfileNames() {
        final Set<String> keys = profiles.keySet();
        final int numKeys = keys.size();
        final String[] names = keys.toArray(new String[numKeys]);
        Arrays.sort(names);
        return names;
    }

    public MetricsProfile getCurrentProfile() {
        return profiles.get(selectedProfile);
    }

    public void setSelectedProfile(String profileName) {
        selectedProfile = profileName;
        configuration.setSelectedProfile(selectedProfile);
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
        final MetricsProfile newProfile = MetricsProfileImpl.loadFromFile(profileFile);
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
        final MetricsProfile newProfile;
        try {
            newProfile = (MetricsProfile) ((MetricsProfileImpl) currentProfile).clone();
        } catch (CloneNotSupportedException ignore) {
            return;
        }
        newProfile.setName(newProfileName);
        profiles.put(newProfileName, newProfile);
        persistProfile(newProfile);
        setSelectedProfile(newProfileName);
    }

    public void createEmptyProfile(String newProfileName) {
        final MetricsProfile newProfile = template.instantiate(newProfileName);
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
}
