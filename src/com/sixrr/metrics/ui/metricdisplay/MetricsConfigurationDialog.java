/*
 * Copyright 2005-2016 Bas Leijdekkers, Sixth and Red River Software
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

package com.sixrr.metrics.ui.metricdisplay;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.FilterComponent;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.containers.Convertor;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.metricModel.MetricsCategoryNameUtil;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.*;
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
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * todo if ok or apply is not pressed do not add or remove profiles!
 * todo use inspection like tree/renderer
 * todo pretty highlighting when filtering
 * todo resizeability/splitter
 */
public class MetricsConfigurationDialog extends DialogWrapper implements TreeSelectionListener {
    private static final Logger logger = Logger.getInstance("MetricsReloaded");

    private JComboBox profilesDropdown;
    private JTextPane descriptionTextArea;
    private JButton deleteButton;
    private JButton saveAsButton;
    private JPanel contentPanel;
    private JFormattedTextField upperThresholdField;
    private JCheckBox upperThresholdEnabledCheckbox;
    private JFormattedTextField lowerThresholdField;
    private JCheckBox lowerThresholdEnabledCheckbox;
    @NonNls private JLabel urlLabel;
    private JButton resetButton;
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
        profile = repository.getCurrentProfile();
        setupMetricsTree();

        setDescriptionFromResource("/metricsDescriptions/Blank.html");
        setupProfilesDropdown();
        setupDeleteButton();
        setupAddButton();
        setupResetButton();
        setupLowerThresholdEnabledButton();
        setupLowerThresholdField();
        setupUpperThresholdEnabledButton();
        setupUpperThresholdField();
        setupURLLabel();
        toggleDeleteButton();
        applyAction.setEnabled(false);
        resetButton.setEnabled(false);
        lowerThresholdField.setEnabled(false);
        upperThresholdField.setEnabled(false);
        lowerThresholdEnabledCheckbox.setEnabled(false);
        upperThresholdEnabledCheckbox.setEnabled(false);
        urlLabel.setText("");
        init();
        setTitle(MetricsReloadedBundle.message("metrics.profiles"));
    }

    private void markProfileClean() {
        currentProfileIsModified = false;
        resetButton.setEnabled(false);
        applyAction.setEnabled(false);
    }

    private void markProfileDirty() {
        currentProfileIsModified = true;
        resetButton.setEnabled(true);
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
        checkboxModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (selectedMetricInstance != null) {
                    final boolean selected = checkboxModel.isSelected();
                    if (selectedMetricInstance.isLowerThresholdEnabled() != selected) {
                        selectedMetricInstance.setLowerThresholdEnabled(selected);
                        markProfileDirty();
                    }
                    lowerThresholdField.setEnabled(selected && selectedMetricInstance.isEnabled());
                }
            }
        });
    }

    private void setupUpperThresholdEnabledButton() {
        final ButtonModel checkboxModel = upperThresholdEnabledCheckbox.getModel();
        checkboxModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (selectedMetricInstance != null) {
                    final boolean selected = checkboxModel.isSelected();
                    if (selectedMetricInstance.isUpperThresholdEnabled() != selected) {
                        selectedMetricInstance.setUpperThresholdEnabled(selected);
                        markProfileDirty();
                    }
                    upperThresholdField.setEnabled(selected && selectedMetricInstance.isEnabled());
                }
            }
        });
    }

    private void setupMetricsTree() {
        metricsTree = new MetricsTree();
        treeScrollPane.setViewportView(metricsTree);
        populateTree("");
        final MyTreeCellRenderer renderer = new MyTreeCellRenderer();
        metricsTree.setCellRenderer(renderer);
        metricsTree.setRootVisible(true);
        metricsTree.setShowsRootHandles(false);
        metricsTree.putClientProperty("JTree.lineStyle", "Angled");

        metricsTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    final TreePath treePath = metricsTree.getLeadSelectionPath();
                    final MetricTreeNode node = (MetricTreeNode) treePath.getLastPathComponent();
                    toggleNode(metricsTree, node);
                    e.consume();
                }
            }
        });
        //noinspection ResultOfObjectAllocationIgnored
        new TreeSpeedSearch(metricsTree, new Convertor<TreePath, String>() {
            @Override
            public String convert(TreePath treePath) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                final Object userObject = node.getUserObject();
                if (userObject instanceof MetricInstance) {
                    final MetricInstance metricInstance = (MetricInstance) userObject;
                    return metricInstance.getMetric().getDisplayName();
                } else {
                    return userObject.toString();
                }
            }
        });
        metricsTree.setSelectionRow(0);
    }

    private void populateTree(String filter) {
        final MetricTreeNode root =
                new MetricTreeNode(MetricsReloadedBundle.message("metrics"), true);
        final Map<MetricCategory, MetricTreeNode> categoryNodes =
                new EnumMap<MetricCategory, MetricTreeNode>(MetricCategory.class);

        if (profile != null) {
            final List<MetricInstance> metrics = profile.getMetrics();
            for (final MetricInstance metricInstance : metrics) {
                final Metric metric = metricInstance.getMetric();
                if (!isMetricAccepted(metric, filter)) {
                    continue;
                }
                final MetricCategory category = metric.getCategory();
                MetricTreeNode categoryNode = categoryNodes.get(category);
                if (categoryNode == null) {
                    categoryNode = new MetricTreeNode(
                            MetricsCategoryNameUtil.getLongNameForCategory(category), true);
                    root.add(categoryNode);
                    categoryNodes.put(category, categoryNode);
                }
                final MetricTreeNode metricNode =
                        new MetricTreeNode(metricInstance, metricInstance.isEnabled());
                categoryNode.add(metricNode);
            }
        }

        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        metricsTree.setModel(treeModel);
        metricsTree.addTreeSelectionListener(this);
        final TreeSelectionModel selectionModel = metricsTree.getSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        for (int j = root.getChildCount() - 1; j >= 0; j--) {
            final MetricTreeNode categoryNode = (MetricTreeNode) root.getChildAt(j);
            if (categoryNode.getChildCount() == 0) {
                root.remove(categoryNode);
            }
        }
        final TreePath rootPath = new TreePath(root);
        metricsTree.expandPath(rootPath);
        for (MetricTreeNode categoryNode : categoryNodes.values()) {
            metricsTree.expandPath(rootPath.pathByAddingChild(categoryNode));
        }
    }

    private void rebindMetricsTree() {
        final TreeModel model = metricsTree.getModel();
        final MetricTreeNode root = (MetricTreeNode) model.getRoot();
        final int numCategories = root.getChildCount();
        for (int i = 0; i < numCategories; i++) {
            final MetricTreeNode category = (MetricTreeNode) root.getChildAt(i);
            final int numMetrics = category.getChildCount();
            for (int j = 0; j < numMetrics; j++) {
                final MetricTreeNode metricNode = (MetricTreeNode) category.getChildAt(j);
                final MetricInstance currentMetricInstance = (MetricInstance) metricNode.getUserObject();
                final MetricInstance newMetric =
                        profile.getMetricForClass(currentMetricInstance.getMetric().getClass());
                metricNode.setUserObject(newMetric);
                assert newMetric != null;
                metricNode.enabled = newMetric.isEnabled();
            }
        }
        metricsTree.treeDidChange();
    }

    private void setupProfilesDropdown() {
        final String[] profiles = repository.getProfileNames();
        final MutableComboBoxModel profilesModel = new DefaultComboBoxModel(profiles);
        profilesDropdown.setModel(profilesModel);
        final MetricsProfile currentProfile = repository.getCurrentProfile();
        profilesDropdown.setSelectedItem(currentProfile.getName());
        toggleDeleteButton();
        profilesDropdown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    return;
                }
                final String selectedProfile = (String) profilesDropdown.getSelectedItem();
                final String currentProfileName = profile.getName();
                if (!selectedProfile.equals(currentProfileName)) {
                    repository.setSelectedProfile(selectedProfile);
                    profile = repository.getCurrentProfile();
                    markProfileClean();
                }
                rebindMetricsTree();
                toggleDeleteButton();
            }
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

        private ApplyAction() {
            super(MetricsReloadedBundle.message("apply"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            doApplyAction();
        }
    }

    protected void doApplyAction() {
        processDoNotAskOnOk(NEXT_USER_EXIT_CODE);
        MetricsProfileRepository.persistProfile(profile);
        markProfileClean();
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

    public void updateSelection(String newProfileName) {
        markProfileClean();
        profile = repository.getCurrentProfile();
        profilesDropdown.addItem(newProfileName);
        profilesDropdown.setSelectedItem(newProfileName);
        rebindMetricsTree();
        toggleDeleteButton();
    }

    private void setupResetButton() {
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repository.reloadProfileFromStorage(profile);
                markProfileClean();
                populateTree(filterComponent.getFilter());
            }
        });
    }

    private void setupURLLabel() {
        urlLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                final String helpURL = selectedMetricInstance.getMetric().getHelpURL();
                if (helpURL != null) {
                    BrowserUtil.launchBrowser("http://" + helpURL);
                }
            }
        });
    }

    private void toggleDeleteButton() {
//        deleteButton.setEnabled(repository.getProfileNames().length != 0);
//        final boolean anyProfilesLeft = repository.getProfileNames().length != 0;
//        if (!anyProfilesLeft) {
//            deleteButton.setEnabled(anyProfilesLeft);
//            return;
//        }
        deleteButton.setEnabled(profile != null && !profile.isBuiltIn());
    }

    private void setupDeleteButton() {
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String currentProfileName = profile.getName();
                repository.deleteProfile(profile);
                profile = repository.getCurrentProfile();
                markProfileClean();
                profilesDropdown.removeItem(currentProfileName);
                profilesDropdown.setSelectedItem(profile.getName());
                toggleDeleteButton();
                rebindMetricsTree();
            }
        });
    }

    private void selectMetric(MetricInstance metricInstance) {
        selectedMetricInstance = metricInstance;
        final Metric metric = metricInstance.getMetric();
        final String url = metric.getHelpURL();
        final String displayString = metric.getHelpDisplayString();
        if (url != null) {
            urlLabel.setText("<html><a href = \'" + url + "\'>" + displayString + "</a></html>");
        } else {
            urlLabel.setText("");
        }
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

        @NonNls final String descriptionName = "/metricsDescriptions/" + metric.getID() + ".html";
        setDescriptionFromResource(descriptionName, metric);
    }

    private void setDescriptionFromResource(@NonNls String resourceName) {
        try {
            final URL resourceURL = getClass().getResource(resourceName);
            descriptionTextArea.setPage(resourceURL);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void setDescriptionFromResource(String resourceName, Metric metric) {
        try {
            final URL resourceURL = metric.getClass().getResource(resourceName);
            descriptionTextArea.setPage(resourceURL);
        } catch (Exception ignore) {
            setDescriptionFromResource("/metricsDescriptions/UnderConstruction.html");
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
        setDescriptionFromResource("/metricsDescriptions/Blank.html");
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


    @NotNull
    @Override
    public Action[] createActions() {
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

    private void toggleNode(JTree tree, MetricTreeNode node) {
        final Object userObject = node.getUserObject();
        if (userObject instanceof MetricInstance) {
            final MetricInstance tool = (MetricInstance) userObject;
            node.enabled = !node.enabled;
            if (node.enabled) {
                final MetricTreeNode parent = (MetricTreeNode) node.getParent();
                if (!parent.equals(tree.getModel().getRoot())) {
                    parent.enabled = true;
                }
                tool.setEnabled(true);
                upperThresholdEnabledCheckbox.setEnabled(true);
                lowerThresholdEnabledCheckbox.setEnabled(true);
            } else {
                tool.setEnabled(false);
                upperThresholdEnabledCheckbox.setEnabled(false);
                lowerThresholdEnabledCheckbox.setEnabled(false);
            }
            markProfileDirty();
        } else {
            node.enabled = !node.enabled;
            final Enumeration children = node.children();
            while (children.hasMoreElements()) {
                final MetricTreeNode child = (MetricTreeNode) children.nextElement();
                child.enabled = node.enabled;
                if (child.getUserObject()instanceof MetricInstance) {
                    ((MetricInstance) child.getUserObject()).setEnabled(node.enabled);
                }
                else
                {
                    final Enumeration grandchildren = child.children();
                    while (grandchildren.hasMoreElements()) {
                        final MetricTreeNode grandChild = (MetricTreeNode) grandchildren.nextElement();
                        grandChild.enabled = node.enabled;
                        if (grandChild.getUserObject()instanceof MetricInstance) {
                            ((MetricInstance) grandChild.getUserObject()).setEnabled(node.enabled);
                        }
                    }
                }
            }
        }
        tree.repaint();
    }

    public void createUIComponents() {
        filterComponent = new MyFilterComponent();
        final AnAction expandActon = new AnAction(MetricsReloadedBundle.message("expand.all.action"),
                MetricsReloadedBundle.message("expand.all.description"), AllIcons.Actions.Expandall) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                final MetricTreeNode root = (MetricTreeNode) metricsTree.getModel().getRoot();
                final TreePath rootPath = new TreePath(root);
                for (Enumeration e = root.children(); e.hasMoreElements();) {
                    final MetricTreeNode childNode = (MetricTreeNode) e.nextElement();
                    final TreePath path = rootPath.pathByAddingChild(childNode);
                    metricsTree.expandPath(path);
                }
            }
        };
        final AnAction collapseAction = new AnAction(MetricsReloadedBundle.message("collapse.all.action"),
                MetricsReloadedBundle.message("collapse.all.description"), AllIcons.Actions.Collapseall) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                final MetricTreeNode root = (MetricTreeNode) metricsTree.getModel().getRoot();
                final TreePath rootPath = new TreePath(root);
                for (Enumeration e = root.children(); e.hasMoreElements();) {
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

        private MyFilterComponent() {
            super("METRICS_FILTER_HISTORY", 10);
        }

        @Override
        public void filter() {
            populateTree(getFilter());
        }
    }

    private static class MyTreeCellRenderer extends JPanel implements TreeCellRenderer {

        private final JLabel myLabel;
        private final JCheckBox myCheckbox;

        @SuppressWarnings("OverridableMethodCallInConstructor")
         MyTreeCellRenderer() {
            super(new BorderLayout());
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

            myCheckbox.setBackground(UIManager.getColor("Tree.textBackground"));
            setBackground(UIManager.getColor(selected ? "Tree.selectionBackground" : "Tree.textBackground"));
            final Color foreground = UIManager.getColor(selected ? "Tree.selectionForeground" : "Tree.textForeground");
            setForeground(foreground);
            myCheckbox.setForeground(foreground);
            myLabel.setForeground(foreground);
            myCheckbox.setEnabled(true);

            if (object instanceof MetricInstance) {
                final MetricInstance tool = (MetricInstance) object;
                myLabel.setFont(tree.getFont());
                myLabel.setText(tool.getMetric().getDisplayName());
            } else {
                final Font font = tree.getFont();
                final Font boldFont = new Font(font.getName(), Font.BOLD, font.getSize());
                myLabel.setFont(boldFont);
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
            if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                final int row = getRowForLocation(e.getX(), e.getY());
                if (row >= 0) {
                    final Rectangle rowBounds = getRowBounds(row);
                    final MyTreeCellRenderer renderer = (MyTreeCellRenderer) getCellRenderer();
                    renderer.setBounds(rowBounds);
                    final Rectangle checkBounds = renderer.myCheckbox.getBounds();

                    checkBounds.setLocation(rowBounds.getLocation());

                    if (checkBounds.contains(e.getPoint())) {
                        final MetricTreeNode node = (MetricTreeNode) getPathForRow(row)
                                .getLastPathComponent();
                        toggleNode(this, node);
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

        private MetricTreeNode(Object userObject, boolean enabled) {
            super(userObject);
            this.enabled = enabled;
        }
    }

    private static boolean isMetricAccepted(Metric metric, String filter) {
        final String lowerCaseFilterString = filter.toLowerCase();
        if (metric.getDisplayName().toLowerCase().indexOf(lowerCaseFilterString) >= 1) {
            return true;
        }
        @NonNls final String descriptionName = "/metricsDescriptions/" + metric.getID() + ".html";
        try {
            final InputStream resourceStream = metric.getClass().getResourceAsStream(descriptionName);
            try {
                return readStreamContents(resourceStream).toLowerCase().contains(lowerCaseFilterString);
            } finally {
                if (resourceStream != null) {
                    resourceStream.close();
                } else {
                    logger.warn("no description found for " + metric.getID());
                }
            }
        } catch (IOException e) {
            logger.warn("problem reading metric description", e);
            return false;
        }
    }

    private static String readStreamContents(InputStream resourceStream) throws IOException {
        if (resourceStream == null) {
            return "";
        }
        final StringBuilder out = new StringBuilder();
        while (true) {
            final int c = resourceStream.read();
            if (c == -1) {
                break;
            }
            out.append((char) c);
        }
        return out.toString();
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
                repository.duplicateCurrentProfile(newProfileName);
                updateSelection(newProfileName);
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
            final String newProfileName = Messages.showInputDialog(saveAsButton,
                    MetricsReloadedBundle.message("enter.new.profile.name"),
                    MetricsReloadedBundle.message("create.new.metrics.profile"),
                    Messages.getQuestionIcon(), repository.generateNewProfileName("Metrics"), null);
            if (newProfileName == null) {
                return;
            }
            if (repository.profileExists(newProfileName)) {
                Messages.showErrorDialog(
                        MetricsReloadedBundle.message("unable.to.create.profile.dialog.message",
                                newProfileName),
                        MetricsReloadedBundle.message("unable.to.create.profile.dialog.title"));
            } else {
                repository.createEmptyProfile(newProfileName);
                updateSelection(newProfileName);
            }
        }
    }
}
