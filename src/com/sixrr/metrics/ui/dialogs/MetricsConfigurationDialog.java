/*
 * Copyright 2005-2022 Sixth and Red River Software, Bas Leijdekkers
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

package com.sixrr.metrics.ui.dialogs;

import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.FilterComponent;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ResourceUtil;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.profile.MetricInstance;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.ui.SearchUtil;
import com.sixrr.metrics.utils.MetricsCategoryNameUtil;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.*;

import static com.intellij.profile.codeInspection.ui.SingleInspectionProfilePanel.readHTML;
import static com.intellij.profile.codeInspection.ui.SingleInspectionProfilePanel.toHTML;

/**
 * todo if ok or apply is not pressed do not add or remove profiles!
 * todo use inspection like tree/renderer
 * todo pretty highlighting when filtering
 * todo resizeability/splitter
 */
public class MetricsConfigurationDialog extends DialogWrapper implements TreeSelectionListener {
    private static final Logger LOG = Logger.getInstance(MetricsConfigurationDialog.class);
    private final Map<MetricCategory, MetricTreeNode> categoryNodes = new EnumMap<>(MetricCategory.class);

    private JComboBox<MetricsProfile> profilesDropdown;
    private JEditorPane descriptionPane;
    private JButton deleteButton;
    private JButton saveAsButton;
    private JPanel contentPanel;
    private JFormattedTextField upperThresholdField;
    private JCheckBox upperThresholdEnabledCheckbox;
    private JFormattedTextField lowerThresholdField;
    private JCheckBox lowerThresholdEnabledCheckbox;
    private ActionToolbarImpl treeToolbar;
    private MetricInstance selectedMetricInstance = null;

    private final MetricsProfileRepository repository;
    @Nullable private MetricsProfile profile;
    private boolean currentProfileIsModified = false;
    private JBScrollPane treeScrollPane;
    private FilterComponent filterComponent;
    private Tree metricsTree;

    private final Action applyAction = new ApplyAction();

    public MetricsConfigurationDialog(Project project, MetricsProfileRepository repository) {
        super(project, true);
        this.repository = repository;
        profile = repository.getSelectedProfile();
        setupMetricsTree();

        descriptionPane.setContentType("text/html");
        descriptionPane.setText("<html><body></body></html>");
        setupProfilesDropdown();
        setupDeleteButton();
        setupAddButton();
        setupLowerThresholdEnabledButton();
        setupLowerThresholdField();
        setupUpperThresholdEnabledButton();
        setupUpperThresholdField();
        toggleDeleteButton();
        applyAction.setEnabled(false);
        lowerThresholdField.setEnabled(false);
        upperThresholdField.setEnabled(false);
        lowerThresholdEnabledCheckbox.setEnabled(false);
        upperThresholdEnabledCheckbox.setEnabled(false);
        init();
        setTitle(MetricsReloadedBundle.message("metrics.profiles"));
        descriptionPane.addHyperlinkListener(new DescriptionHyperlinkListener(project));
    }

    private void markProfileClean() {
        currentProfileIsModified = false;
        applyAction.setEnabled(false);
    }

    private void markProfileDirty() {
        currentProfileIsModified = true;
        applyAction.setEnabled(true);
    }

    private void setupLowerThresholdField() {
        final NumberFormat format = NumberFormat.getIntegerInstance();
        format.setParseIntegerOnly(true);
        final DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(new NumberFormatter(format));
        lowerThresholdField.setFormatterFactory(formatterFactory);

        final DocumentListener listener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            private void textChanged() {
                try {
                    lowerThresholdField.commitEdit();
                } catch (ParseException ignore) {
                    return;
                }
                final Number value = (Number) lowerThresholdField.getValue();
                if (value == null) {
                    return;
                }
                final double newValue = value.doubleValue();
                final double currentValue = selectedMetricInstance.getLowerThreshold();
                if (Math.abs(currentValue - newValue) <= 0.001) {
                    return;
                }
                selectedMetricInstance.setLowerThreshold(newValue);
                markProfileDirty();
            }
        };
        final Document thresholdDocument = lowerThresholdField.getDocument();
        thresholdDocument.addDocumentListener(listener);
    }

    private void setupUpperThresholdField() {
        final NumberFormat format = NumberFormat.getIntegerInstance();
        format.setParseIntegerOnly(true);
        final DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(new NumberFormatter(format));
        upperThresholdField.setFormatterFactory(formatterFactory);

        final DocumentListener listener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            private void textChanged() {
                try {
                    upperThresholdField.commitEdit();
                } catch (ParseException ignore) {
                    return;
                }
                final Number value = (Number) upperThresholdField.getValue();
                if (value == null) {
                    return;
                }
                final double threshold = value.doubleValue();
                if (Math.abs(selectedMetricInstance.getUpperThreshold() - threshold) <= 0.001) {
                    return;
                }
                selectedMetricInstance.setUpperThreshold(threshold);
                markProfileDirty();
            }
        };
        final Document thresholdDocument = upperThresholdField.getDocument();
        thresholdDocument.addDocumentListener(listener);
    }

    private void setupLowerThresholdEnabledButton() {
        final ButtonModel checkboxModel = lowerThresholdEnabledCheckbox.getModel();
        checkboxModel.addChangeListener(e -> {
            if (selectedMetricInstance != null) {
                final boolean selected = checkboxModel.isSelected();
                if (selectedMetricInstance.isLowerThresholdEnabled() != selected) {
                    selectedMetricInstance.setLowerThresholdEnabled(selected);
                    markProfileDirty();
                }
                lowerThresholdField.setEnabled(selected && selectedMetricInstance.isEnabled());
            }
        });
    }

    private void setupUpperThresholdEnabledButton() {
        final ButtonModel checkboxModel = upperThresholdEnabledCheckbox.getModel();
        checkboxModel.addChangeListener(e -> {
            if (selectedMetricInstance != null) {
                final boolean selected = checkboxModel.isSelected();
                if (selectedMetricInstance.isUpperThresholdEnabled() != selected) {
                    selectedMetricInstance.setUpperThresholdEnabled(selected);
                    markProfileDirty();
                }
                upperThresholdField.setEnabled(selected && selectedMetricInstance.isEnabled());
            }
        });
    }

    private void setupMetricsTree() {
        metricsTree = new MetricsTree();
        treeScrollPane.setViewportView(metricsTree);
        populateTree("");
        final ProfileTreeCellRenderer renderer = new ProfileTreeCellRenderer();
        metricsTree.setCellRenderer(renderer);
        metricsTree.setRootVisible(false);
        metricsTree.setShowsRootHandles(true);
        metricsTree.putClientProperty("JTree.lineStyle", "Angled");

        metricsTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    toggleNodes(metricsTree, metricsTree.getSelectedNodes(MetricTreeNode.class, null));
                    e.consume();
                }
            }
        });
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent event) {
                final TreePath path = metricsTree.getLeadSelectionPath();
                final MetricTreeNode node = (MetricTreeNode) path.getLastPathComponent();
                if (node.isLeaf()) {
                    toggleNodes(metricsTree, node);
                }
                return true;
            }
        }.installOn(metricsTree);
        //noinspection ResultOfObjectAllocationIgnored
        new TreeSpeedSearch(metricsTree, treePath -> {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            final Object userObject = node.getUserObject();
            if (userObject instanceof MetricInstance) {
                final MetricInstance metricInstance = (MetricInstance) userObject;
                return metricInstance.getMetric().getDisplayName();
            } else {
                return userObject.toString();
            }
        });
        metricsTree.setSelectionRow(0);
    }

    private void populateTree(String filter) {
        final MetricTreeNode root = new MetricTreeNode(MetricsReloadedBundle.message("metrics"), true);
        categoryNodes.clear();

        if (profile != null) {
            final List<MetricInstance> metrics = profile.getMetricInstances();
            final List<String> filterTokens = SearchUtil.tokenizeFilter(filter);
            for (MetricInstance metricInstance : metrics) {
                final Metric metric = metricInstance.getMetric();
                if (!isMetricAccepted(metric, filterTokens)) {
                    continue;
                }
                final MetricCategory category = metric.getCategory();
                MetricTreeNode categoryNode = categoryNodes.get(category);
                if (categoryNode == null) {
                    categoryNode = new MetricTreeNode(MetricsCategoryNameUtil.getLongNameForCategory(category), true);
                    root.add(categoryNode);
                    categoryNodes.put(category, categoryNode);
                }
                final MetricTreeNode metricNode = new MetricTreeNode(metricInstance, metricInstance.isEnabled());
                categoryNode.add(metricNode);
            }
        }

        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        metricsTree.setModel(treeModel);
        metricsTree.addTreeSelectionListener(this);
        enableCategoryNodes();

        final TreePath rootPath = new TreePath(root);
        metricsTree.expandPath(rootPath);
        for (MetricTreeNode categoryNode : categoryNodes.values()) {
            metricsTree.expandPath(rootPath.pathByAddingChild(categoryNode));
        }
    }

    private void rebindMetricsTree() {
        assert profile != null;
        final TreeModel model = metricsTree.getModel();
        final MetricTreeNode root = (MetricTreeNode) model.getRoot();
        final int numCategories = root.getChildCount();
        for (int i = 0; i < numCategories; i++) {
            final MetricTreeNode category = (MetricTreeNode) root.getChildAt(i);
            final int numMetrics = category.getChildCount();
            for (int j = 0; j < numMetrics; j++) {
                final MetricTreeNode metricNode = (MetricTreeNode) category.getChildAt(j);
                final MetricInstance currentMetricInstance = (MetricInstance) metricNode.getUserObject();
                final MetricInstance newMetric = profile.getMetricInstance(currentMetricInstance.getMetric());
                assert newMetric != null : currentMetricInstance;
                metricNode.setUserObject(newMetric);
                metricNode.enabled = newMetric.isEnabled();
            }
        }
        enableCategoryNodes();
        metricsTree.treeDidChange();
    }

    private void setupProfilesDropdown() {
        final MetricsProfile[] profiles = repository.getProfiles();
        final MutableComboBoxModel<MetricsProfile> profilesModel = new DefaultComboBoxModel<>(profiles);
        profilesDropdown.setModel(profilesModel);
        profilesDropdown.setRenderer(new ProfileListCellRenderer());
        final MetricsProfile currentProfile = repository.getSelectedProfile();
        if (currentProfile != null) {
            profilesDropdown.setSelectedItem(currentProfile);
        }
        toggleDeleteButton();
        profilesDropdown.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                return;
            }
            final MetricsProfile selectedProfile = (MetricsProfile) profilesDropdown.getSelectedItem();
            if (selectedProfile != null && !selectedProfile.equals(profile)) {
                repository.setSelectedProfile(selectedProfile);
                profile = repository.getSelectedProfile();
                markProfileClean();
            }
            rebindMetricsTree();
            toggleDeleteButton();
        });
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (currentProfileIsModified) {
            MetricsProfileRepository.persistProfile(profile);
        }
    }

    protected class ApplyAction extends DialogWrapperAction {

        ApplyAction() {
            super(MetricsReloadedBundle.message("apply"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            processDoNotAskOnOk(NEXT_USER_EXIT_CODE);
            MetricsProfileRepository.persistProfile(profile);
            markProfileClean();
        }
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
        repository.reloadProfileFromStorage(profile);
    }

    private void setupAddButton() {
        saveAsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                super.mousePressed(event);
                final JPopupMenu popup = new JPopupMenu();
                popup.add(new JMenuItem(new CopyProfileAction(repository)));
                popup.add(new JMenuItem(new NewProfileAction(repository)));
                popup.show(saveAsButton, 0, saveAsButton.getHeight());
            }
        });
    }

    private void updateSelection(MetricsProfile newProfile) {
        profilesDropdown.addItem(newProfile);
        profilesDropdown.setSelectedItem(newProfile);
    }

    private void toggleDeleteButton() {
        deleteButton.setEnabled(profile != null && !profile.isPrebuilt());
    }

    private void setupDeleteButton() {
        deleteButton.addActionListener(e -> {
            if (profile == null) {
                return;
            }
            final MetricsProfile currentProfile = profile;
            repository.deleteProfile(currentProfile);
            profilesDropdown.removeItem(currentProfile);
        });
    }

    private void selectMetric(MetricInstance metricInstance) {
        selectedMetricInstance = metricInstance;
        final Metric metric = metricInstance.getMetric();
        final boolean metricInstanceEnabled = metricInstance.isEnabled();

        final double upperThreshold = metricInstance.getUpperThreshold();
        final boolean upperThresholdEnabled = metricInstance.isUpperThresholdEnabled();
        upperThresholdEnabledCheckbox.setSelected(upperThresholdEnabled);
        upperThresholdEnabledCheckbox.setEnabled(metricInstanceEnabled);
        upperThresholdField.setValue(Double.valueOf(upperThreshold));
        upperThresholdField.setEnabled(upperThresholdEnabled && metricInstanceEnabled);

        final double lowerThreshold = metricInstance.getLowerThreshold();
        final boolean lowerThresholdEnabled = metricInstance.isLowerThresholdEnabled();
        lowerThresholdEnabledCheckbox.setSelected(lowerThresholdEnabled);
        lowerThresholdEnabledCheckbox.setEnabled(metricInstanceEnabled);
        lowerThresholdField.setValue(Double.valueOf(lowerThreshold));
        lowerThresholdField.setEnabled(lowerThresholdEnabled && metricInstanceEnabled);

        loadDescription(metric, descriptionPane);
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public static void loadDescription(Metric metric, JEditorPane descriptionPane) {
        final boolean success =
                loadDescription(metric, "/metricsDescriptions/" + metric.getID() + ".html", descriptionPane);
        if (!success) {
            loadDescription(metric, "/metricsDescriptions/UnderConstruction.html", descriptionPane);
        }
    }

    private static boolean loadDescription(Metric metric, String path, JEditorPane descriptionPane) {
        try {
            final URL resourceURL = metric.getClass().getResource(path);
            if (resourceURL == null) {
                return false;
            }
            final String description = ResourceUtil.loadText(resourceURL);
            readHTML(descriptionPane, toHTML(descriptionPane, description, false));
            return true;
        } catch (IOException e) {
            LOG.error(e);
            return false;
        }
    }

    private void clearSelection() {
        selectedMetricInstance = null;
        lowerThresholdField.setEnabled(false);
        lowerThresholdField.setText("");
        lowerThresholdEnabledCheckbox.setEnabled(false);
        upperThresholdField.setText("");
        upperThresholdField.setEnabled(false);
        upperThresholdEnabledCheckbox.setEnabled(false);
        descriptionPane.setContentType("text/html");
        descriptionPane.setText("<html><body></body></html>");
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        final TreePath selectionPath = e.getPath();
        final MetricTreeNode lastPathComponent = (MetricTreeNode) selectionPath.getLastPathComponent();
        final Object userObject = lastPathComponent.getUserObject();
        if (userObject instanceof MetricInstance) {
            selectMetric((MetricInstance) userObject);
        } else {
            clearSelection();
        }
    }


    @Override
    public Action @NotNull [] createActions() {
        if (SystemInfo.isMac) {
            return new Action[] {getCancelAction(), applyAction, getOKAction()};
        } else {
            return new Action[] {getOKAction(), applyAction, getCancelAction()};
        }
    }

    @Override
    public String getTitle() {
        return MetricsReloadedBundle.message("metrics.configuration.panel.title");
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    @Override
    @NonNls
    protected String getDimensionServiceKey() {
        return "MetricsReloaded.MetricsConfigurationDialog";
    }

    private void toggleNodes(JTree tree, MetricTreeNode... nodes) {
        if (profile.isPrebuilt()) {
            return;
        }
        final Set<MetricTreeNode> parents = new HashSet<>();
        for (MetricTreeNode node : nodes) {
            if (node.getChildCount() > 0) {
                parents.add(node);
            }
        }
        for (MetricTreeNode node : nodes) {
            final MetricTreeNode parent = (MetricTreeNode) node.getParent();
            if (parents.contains(parent)) {
                continue;
            }
            node.enabled = !node.enabled;
            final Object userObject = node.getUserObject();
            if (userObject instanceof MetricInstance) {
                final MetricInstance tool = (MetricInstance) userObject;
                tool.setEnabled(node.enabled);
                upperThresholdEnabledCheckbox.setEnabled(node.enabled);
                lowerThresholdEnabledCheckbox.setEnabled(node.enabled);
                markProfileDirty();
            } else {
                final Enumeration<?> children = node.children();
                while (children.hasMoreElements()) {
                    final MetricTreeNode child = (MetricTreeNode) children.nextElement();
                    child.enabled = node.enabled;
                    if (child.getUserObject() instanceof MetricInstance) {
                        ((MetricInstance) child.getUserObject()).setEnabled(node.enabled);
                    } else {
                        final Enumeration<?> grandchildren = child.children();
                        while (grandchildren.hasMoreElements()) {
                            final MetricTreeNode grandChild = (MetricTreeNode) grandchildren.nextElement();
                            grandChild.enabled = node.enabled;
                            if (grandChild.getUserObject() instanceof MetricInstance) {
                                ((MetricInstance) grandChild.getUserObject()).setEnabled(node.enabled);
                            }
                        }
                    }
                }
            }
        }
        enableCategoryNodes();
        tree.repaint();
    }

    private void enableCategoryNodes() {
        for (MetricTreeNode node : categoryNodes.values()) {
            node.enabled = false;
            final Enumeration<TreeNode> children = node.children();
            while (children.hasMoreElements()) {
                final MetricTreeNode child = (MetricTreeNode) children.nextElement();
                if (child.enabled) {
                    node.enabled = true;
                    break;
                }
            }
        }
    }

    public void createUIComponents() {
        filterComponent = new MyFilterComponent();
        final DumbAwareAction expandActon = new DumbAwareAction(IdeBundle.messagePointer("action.expand.all"),
                                                                AllIcons.Actions.Expandall) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                final MetricTreeNode root = (MetricTreeNode) metricsTree.getModel().getRoot();
                final TreePath rootPath = new TreePath(root);
                for (final Enumeration<?> e = root.children(); e.hasMoreElements();) {
                    final MetricTreeNode childNode = (MetricTreeNode) e.nextElement();
                    final TreePath path = rootPath.pathByAddingChild(childNode);
                    metricsTree.expandPath(path);
                }
            }
        };
        final DumbAwareAction collapseAction = new DumbAwareAction(IdeBundle.messagePointer("action.collapse.all"),
                                                                   AllIcons.Actions.Collapseall) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                final MetricTreeNode root = (MetricTreeNode) metricsTree.getModel().getRoot();
                final TreePath rootPath = new TreePath(root);
                for (final Enumeration<?> e = root.children(); e.hasMoreElements();) {
                    final MetricTreeNode childNode = (MetricTreeNode) e.nextElement();
                    final TreePath path = rootPath.pathByAddingChild(childNode);
                    metricsTree.collapsePath(path);
                }
            }
        };
        final ActionManager actionManager = ActionManager.getInstance();

        final DefaultActionGroup expandCollapseGroup = new DefaultActionGroup();
        expandCollapseGroup.add(expandActon);
        expandCollapseGroup.add(collapseAction);

        treeToolbar = (ActionToolbarImpl) actionManager
                        .createActionToolbar("EXPAND_COLLAPSE_GROUP", expandCollapseGroup, true);
    }

    private class MyFilterComponent extends FilterComponent {

        MyFilterComponent() {
            super("METRICS_FILTER_HISTORY", 10);
        }

        @Override
        public void filter() {
            populateTree(getFilter());
        }
    }

    private class ProfileTreeCellRenderer extends JPanel implements TreeCellRenderer {

        private final JLabel myLabel;
        private final JCheckBox myCheckbox;

        @SuppressWarnings("OverridableMethodCallInConstructor")
        ProfileTreeCellRenderer() {
            super(new BorderLayout(4, 4));
            myCheckbox = new JCheckBox();
            myLabel = new JLabel();
            add(myCheckbox, BorderLayout.WEST);
            add(myLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            final MetricTreeNode node = (MetricTreeNode) value;
            final Object object = node.getUserObject();

            myCheckbox.setSelected(node.enabled);

            myCheckbox.setBackground(UIManager.getColor(selected ? "Tree.selectionBackground" : "Tree.textBackground"));
            setBackground(UIManager.getColor(selected ? "Tree.selectionBackground" : "Tree.textBackground"));
            final Color foreground = UIManager.getColor(selected ? "Tree.selectionForeground" : "Tree.textForeground");
            setForeground(foreground);
            myCheckbox.setForeground(foreground);
            myLabel.setForeground(foreground);
            myCheckbox.setEnabled(profile != null && !profile.isPrebuilt());

            if (object instanceof MetricInstance) {
                final MetricInstance tool = (MetricInstance) object;
                myLabel.setFont(tree.getFont());
                myLabel.setText(tool.getMetric().getDisplayName());
            } else {
                myLabel.setFont(tree.getFont().deriveFont(Font.BOLD));
                myLabel.setText((String) object);
            }

            return this;
        }
    }

    private class MetricsTree extends Tree {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            Dimension size = super.getPreferredScrollableViewportSize();
            size = new Dimension(size.width + 10, size.height);
            return size;
        }

        @Override
        protected void processMouseEvent(MouseEvent e) {
            if (e.getID() == MouseEvent.MOUSE_CLICKED && profile != null && !profile.isPrebuilt()) {
                final int row = getRowForLocation(e.getX(), e.getY());
                if (row >= 0) {
                    final Rectangle rowBounds = getRowBounds(row);
                    final ProfileTreeCellRenderer renderer = (ProfileTreeCellRenderer) getCellRenderer();
                    renderer.setBounds(rowBounds);
                    final Rectangle checkBounds = renderer.myCheckbox.getBounds();
                    checkBounds.setLocation(rowBounds.getLocation());

                    if (checkBounds.contains(e.getPoint())) {
                        toggleNodes(this, (MetricTreeNode) getPathForRow(row).getLastPathComponent());
                        e.consume();
                        setSelectionRow(row);
                    }
                }
            }

            if (!e.isConsumed()) {
                super.processMouseEvent(e);
            }
        }
    }

    private static class MetricTreeNode extends DefaultMutableTreeNode {
        private boolean enabled;

        MetricTreeNode(Object userObject, boolean enabled) {
            super(userObject);
            this.enabled = enabled;
        }
    }

    private static boolean isMetricAccepted(Metric metric, List<String> filterTokens) {
        String description = null;
        for (String filterToken : filterTokens) {
            if (StringUtil.containsIgnoreCase(metric.getAbbreviation(), filterToken)) {
                continue;
            }
            if (StringUtil.containsIgnoreCase(metric.getDisplayName(), filterToken)) {
                continue;
            }
            if (StringUtil.containsIgnoreCase(MetricsCategoryNameUtil.getLongNameForCategory(metric.getCategory()), filterToken)) {
                continue;
            }
            if (description == null) {
                @NonNls final String descriptionName = "/metricsDescriptions/" + metric.getID() + ".html";
                try {
                    final InputStream resourceStream = metric.getClass().getResourceAsStream(descriptionName);
                    try {
                        if (resourceStream == null) {
                            description = "";
                        }
                        else {
                            description = StringUtil.stripHtml(StreamUtil.readText(resourceStream, "UTF-8"), false);
                        }
                    } finally {
                        if (resourceStream != null) {
                            resourceStream.close();
                        } else {
                            LOG.warn("no description found for " + metric.getID());
                        }
                    }
                } catch (IOException e) {
                    LOG.warn("problem reading metric description", e);
                    return false;
                }
            }
            if (StringUtil.containsIgnoreCase(description, filterToken)) {
                continue;
            }
            return false;
        }
        return true;
    }

    private class CopyProfileAction extends AbstractAction {

        private final MetricsProfileRepository repository;

        CopyProfileAction(MetricsProfileRepository repository) {
            super(MetricsReloadedBundle.message("copy.profile.action"));
            this.repository = repository;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            final String newProfileName = Messages.showInputDialog(saveAsButton,
                    MetricsReloadedBundle.message("enter.new.profile.name"),
                    MetricsReloadedBundle.message("create.new.metrics.profile"),
                    Messages.getQuestionIcon(), repository.generateNewProfileName(), null);
            if (newProfileName == null) {
                return;
            }
            if (repository.profileExists(newProfileName)) {
                Messages.showErrorDialog(
                        MetricsReloadedBundle.message("unable.to.create.profile.dialog.message",
                                newProfileName),
                        MetricsReloadedBundle.message("unable.to.create.profile.dialog.title"));
            } else {
                updateSelection(repository.duplicateSelectedProfile(newProfileName));
            }
        }
    }

    private class NewProfileAction extends AbstractAction {

        private final MetricsProfileRepository repository;

        NewProfileAction(MetricsProfileRepository repository) {
            super(MetricsReloadedBundle.message("new.profile.action"));
            this.repository = repository;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            final String newProfileName = Messages.showInputDialog(
                    saveAsButton,
                    MetricsReloadedBundle.message("enter.new.profile.name"),
                    MetricsReloadedBundle.message("create.new.metrics.profile"),
                    Messages.getQuestionIcon(),
                    repository.generateNewProfileName(MetricsReloadedBundle.message("default.new.profile.name")),
                    null);
            if (newProfileName == null) {
                return;
            }
            if (repository.profileExists(newProfileName)) {
                Messages.showErrorDialog(
                        MetricsReloadedBundle.message("unable.to.create.profile.dialog.message",
                                newProfileName),
                        MetricsReloadedBundle.message("unable.to.create.profile.dialog.title"));
            } else {
                updateSelection(repository.createEmptyProfile(newProfileName));
            }
        }
    }
}
