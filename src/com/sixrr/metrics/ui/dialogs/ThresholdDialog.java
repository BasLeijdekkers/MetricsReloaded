package com.sixrr.metrics.ui.dialogs;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.metricModel.MetricsResult;
import com.sixrr.metrics.profile.MetricsProfile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class ThresholdDialog extends DialogWrapper {
    private JTable thresholdTable;
    private JPanel contentPanel;
    private final List<Metric> metrics;
    private final MetricsProfile profile;
    private final MetricsResult result;

    public ThresholdDialog(Project project, List<Metric> metrics, MetricsProfile profile, MetricsResult result) {
        super(project, false);
        this.metrics = metrics;
        this.profile = profile;
        this.result = result;
        final ThresholdTableModel model = new ThresholdTableModel(metrics, profile, result);
        thresholdTable.setModel(model);
        thresholdTable.updateUI();
        setTitle("Thresholds for profile " + profile.getName());
        init();
    }


    @Nullable
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    protected void doOKAction() {
        close(1);
    }

    public void doCancelAction() {
        close(0);
    }
}
