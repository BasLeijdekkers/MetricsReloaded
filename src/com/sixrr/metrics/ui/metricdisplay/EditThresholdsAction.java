package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.plugin.MetricsPlugin;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.ui.dialogs.ThresholdDialog;
import com.sixrr.metrics.utils.IconHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class EditThresholdsAction extends AnAction {
    private static final Icon CLOSE_ICON =
            IconHelper.getIcon("/compiler/error.png");
    private final MetricsToolWindow toolWindow;

    EditThresholdsAction(MetricsToolWindow toolWindow) {
        super(MetricsReloadedBundle.message("edit.thresholds.action"),
                MetricsReloadedBundle.message("edit.threshold.values.for.this.metric.profile"), CLOSE_ICON);
        this.toolWindow = toolWindow;
    }

    public void actionPerformed(AnActionEvent event) {
        final DataContext dataContext = event.getDataContext();
        final Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        assert project!=null;
        final MetricCategory category = toolWindow.getSelectedCategory();
        final MetricsProfile profile = toolWindow.getCurrentProfile();
        final List<MetricInstance> metrics = profile.getMetrics();
        final List<Metric> selectedMetrics = new ArrayList<Metric>();
        for (MetricInstance instance : metrics) {
            if (instance.isEnabled()) {
                final Metric metric = instance.getMetric();
                if (metric.getCategory().equals(category)) {
                    selectedMetrics.add(metric);
                }
            }
        }
        final ThresholdDialog dialog = new ThresholdDialog(project, selectedMetrics, profile, toolWindow.getCurrentRun().getResultsForCategory(category));
        dialog.show();
        if (dialog.isOK()) {
            MetricsProfileRepository.persistProfile(profile);
            toolWindow.refresh();
        } else {
            final MetricsPlugin plugin = project.getComponent(MetricsPlugin.class);
            plugin.getProfileRepository().reloadProfileFromStorage(profile);
          }
    }
}
