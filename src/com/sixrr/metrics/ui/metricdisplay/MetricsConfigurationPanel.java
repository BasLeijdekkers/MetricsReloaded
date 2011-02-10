/*
 * Copyright 2005-2011, Bas Leijdekkers, Sixth and Red River Software
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
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.util.containers.Convertor;
import com.intellij.util.ui.Tree;
import com.sixrr.metrics.Metric;
import com.sixrr.metrics.MetricCategory;
import com.sixrr.metrics.metricModel.MetricsCategoryNameUtil;
import com.sixrr.metrics.utils.MetricsReloadedBundle;
import com.sixrr.metrics.metricModel.MetricComparator;
import com.sixrr.metrics.metricModel.MetricInstance;
import com.sixrr.metrics.profile.MetricsProfile;
import com.sixrr.metrics.profile.MetricsProfileRepository;
import com.sixrr.metrics.utils.IconHelper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class MetricsConfigurationPanel  extends DialogWrapper implements TreeSelectionListener {
    private static final Logger logger = Logger.getInstance("MetricsReloaded");

    private JComboBox profilesDropdown;
    private JButton cancelButton;
    private JButton runButton;
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
    private JButton applyButton;
    private ActionToolbarImpl treeToolbar;
    private ActionToolbarImpl filterToolbar;
    private MetricInstance selectedMetricInstance = null;

    private MetricsProfileRepository repository;
    private Project project;
    private MetricsProfile profile;
    private boolean currentProfileIsModified = false;
    private JComboBox filterComboBox;
    private JTree tree;
    private JScrollPane treeScrollPane;
    private Tree metricsTree;
    private String currentFilterString = "";
    private String[] filters = new String[0];

    public MetricsConfigurationPanel(Project project, MetricsProfileRepository repository) {
        super(project, false);
        this.project = project;
        this.repository = repository;
        profile = this.repository.getCurrentProfile();
        setupMetricsTree();

        setDescriptionFromResource("/metricsDescriptions/Blank.html");
        setupProfilesDropdown();
        setupDeleteButton();
        setupAddButton();
        setupResetButton();
        setupCancelButton();
        setupOkButton();
        setupApplyButton();
        setupLowerThresholdEnabledButton();
        setupLowerThresholdField();
        setupUpperThresholdEnabledButton();
        setupUpperThresholdField();
        setupURLLabel();
        toggleRunButton();
        toggleDeleteButton();
        toggleApplyButton();
        toggleResetButton();
        lowerThresholdField.setEnabled(false);
        upperThresholdField.setEnabled(false);
        lowerThresholdEnabledCheckbox.setEnabled(false);
        upperThresholdEnabledCheckbox.setEnabled(false);
        urlLabel.setText("");
        init();
        setTitle(MetricsReloadedBundle.message("metrics.profiles"));
        //  final JRootPane rootPane = contentPanel.getRootPane();
      //  rootPane.setDefaultButton(runButton);
    }

    private void setupFilterComboBox() {
        filterComboBox = new JComboBox();
        filterComboBox.setEditable(true);
        filterComboBox.setEnabled(true);
        filters = new String[]{""};
        final MutableComboBoxModel filtersModel = new DefaultComboBoxModel(filters);
        filterComboBox.setModel(filtersModel);
        filterComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                applyFilter();
            }
        });
    }

    private void setupLowerThresholdField() {
        final NumberFormat formatter = NumberFormat.getIntegerInstance();
        formatter.setParseIntegerOnly(true);
        final DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(new NumberFormatter(formatter));
        lowerThresholdField.setFormatterFactory(formatterFactory);

        final DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            private void textChanged() {
                final Number value = (Number) lowerThresholdField.getValue();
                if (value != null) {
                    final double threshold = value.doubleValue();
                    selectedMetricInstance.setLowerThreshold(threshold);
                    currentProfileIsModified = true;
                }
            }
        };
        final Document thresholdDocument = lowerThresholdField.getDocument();
        thresholdDocument.addDocumentListener(listener);
    }

    private void setupUpperThresholdField() {
        final NumberFormat formatter = NumberFormat.getIntegerInstance();
        formatter.setParseIntegerOnly(true);
        final DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(new NumberFormatter(formatter));
        upperThresholdField.setFormatterFactory(formatterFactory);

        final DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            private void textChanged() {
                final Number value = (Number) upperThresholdField.getValue();
                if (value != null) {
                    final double threshold = value.doubleValue();
                    selectedMetricInstance.setUpperThreshold(threshold);
                    currentProfileIsModified = true;
                }
            }
        };
        final Document thresholdDocument = upperThresholdField.getDocument();
        thresholdDocument.addDocumentListener(listener);
    }

    private void setupLowerThresholdEnabledButton() {
        final ButtonModel checkboxModel = lowerThresholdEnabledCheckbox.getModel();
        checkboxModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (selectedMetricInstance != null) {
                    final boolean selected = checkboxModel.isSelected();
                    selectedMetricInstance.setLowerThresholdEnabled(selected);
                    lowerThresholdField.setEnabled(selectedMetricInstance.isLowerThresholdEnabled());
                    if (selected) {
                        lowerThresholdField.setText(Double.toString(selectedMetricInstance.getLowerThreshold()));
                    } else {
                        lowerThresholdField.setText("");
                    }
                    currentProfileIsModified = true;
                }
            }
        });
    }

    private void setupUpperThresholdEnabledButton() {
        final ButtonModel checkboxModel = upperThresholdEnabledCheckbox.getModel();
        checkboxModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (selectedMetricInstance != null) {
                    final boolean selected = checkboxModel.isSelected();
                    selectedMetricInstance.setUpperThresholdEnabled(selected);
                    upperThresholdField.setEnabled(selectedMetricInstance.isUpperThresholdEnabled());
                    if (selected) {
                        upperThresholdField.setText(Double.toString(selectedMetricInstance.getUpperThreshold()));
                    } else {
                        upperThresholdField.setText("");
                    }
                }
                currentProfileIsModified = true;
            }
        });
    }

    private void setupMetricsTree() {

        metricsTree = new MetricsTree();
        treeScrollPane.setViewportView(metricsTree);
        final MyTreeCellRenderer renderer = new MyTreeCellRenderer();
        metricsTree.setCellRenderer(renderer);
        metricsTree.setRootVisible(true);
        metricsTree.setShowsRootHandles(false);
        //noinspection HardCodedStringLiteral
        metricsTree.putClientProperty("JTree.lineStyle", "Angled");

        metricsTree.addKeyListener(new KeyAdapter() {
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
            public String convert(TreePath treePath) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                final Object userObject = node.getUserObject();
                if (userObject instanceof MetricInstance) {
                    return ((MetricInstance) userObject).getMetric().getDisplayName();
                } else {
                    return userObject.toString();
                }
            }
        });
        populateTree();
        metricsTree.setSelectionRow(0);
    }

    private void populateTree() {
        final MetricTreeNode root = new MetricTreeNode(MetricsReloadedBundle.message("metrics"), true);
        final Map<MetricCategory, MetricTreeNode> categoryNodes =
                new EnumMap<MetricCategory, MetricTreeNode>(MetricCategory.class);

        if (profile != null) {
            final List<MetricInstance> metrics = profile.getMetrics();
            final MetricInstance[] metricsArray = metrics.toArray(new MetricInstance[metrics.size()]);
            Arrays.sort(metricsArray, new MetricComparator());
            for (final MetricInstance metricInstance : metricsArray) {
                final Metric metric = metricInstance.getMetric();
                if (isMetricAccepted(metric)) {
                    final MetricCategory category = metric.getCategory();
                    MetricTreeNode categoryNode = categoryNodes.get(category);
                    if (categoryNode == null) {
                        categoryNode = new MetricTreeNode(
                                MetricsCategoryNameUtil.getLongNameForCategory(category), true);
                        root.add(categoryNode);
                        categoryNodes.put(category, categoryNode);
                    }
                    final MetricTreeNode metricNode = new MetricTreeNode(metricInstance, metricInstance.isEnabled());
                    categoryNode.add(metricNode);
                }
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
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    return;
                }
                final String selectedProfile = (String) profilesDropdown.getSelectedItem();
                final String currentProfileName = profile.getName();
                if (!selectedProfile.equals(currentProfileName)) {
                    repository.setSelectedProfile(selectedProfile);
                    profile = repository.getCurrentProfile();
                    currentProfileIsModified = false;
                }
                rebindMetricsTree();
                toggleDeleteButton();
                toggleRunButton();
                toggleResetButton();
                toggleApplyButton();
            }
        });
    }

    private void setupOkButton() {
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentProfileIsModified) {
                    MetricsProfileRepository.persistProfile(profile);
                }
                close(0);
            }
        });
    }

    private void setupApplyButton() {
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MetricsProfileRepository.persistProfile(profile);
                currentProfileIsModified = false;
                toggleApplyButton();
                toggleResetButton();
            }
        });
    }

    private void setupCancelButton() {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repository.reloadProfileFromStorage(profile);
                close(0);
            }
        });
    }

    private void setupAddButton() {
        saveAsButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                super.mousePressed(event);
                final JPopupMenu popup = new JPopupMenu();
                popup.add(new JMenuItem(new CopyProfileAction(repository, MetricsConfigurationPanel.this, project)));
                popup.add(new JMenuItem(new NewProfileAction(repository, MetricsConfigurationPanel.this, project)));
                popup.show(saveAsButton, 0, saveAsButton.getHeight());
            }
        });
    }

    public void updateSelection(String newProfileName) {
        currentProfileIsModified = false;
        profile = repository.getCurrentProfile();
        profilesDropdown.addItem(newProfileName);
        profilesDropdown.setSelectedItem(newProfileName);
        rebindMetricsTree();
        toggleDeleteButton();
        toggleRunButton();
    }

    private void setupResetButton() {
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repository.reloadProfileFromStorage(profile);
                currentProfileIsModified = false;
            }
        });
    }

    private void setupURLLabel() {
        urlLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                final String helpURL = selectedMetricInstance.getMetric().getHelpURL();
                if (helpURL != null) {
                    BrowserUtil.launchBrowser("http://" + helpURL);
                }
            }
        });
    }

    private void toggleDeleteButton() {
        deleteButton.setEnabled(repository.getProfileNames().length != 0);
    }

    private void toggleResetButton() {
        resetButton.setEnabled(currentProfileIsModified);
    }

    private void toggleApplyButton() {
        applyButton.setEnabled(currentProfileIsModified);
    }

    private void toggleRunButton() {
        boolean metricEnabled = false;
        final List<MetricInstance> metrics = profile.getMetrics();
        for (final MetricInstance metricInstance : metrics) {
            if (metricInstance.isEnabled()) {
                metricEnabled = true;
            }
        }
        runButton.setEnabled(metricEnabled);
    }

    private void setupDeleteButton() {
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String currentProfileName = profile.getName();
                repository.deleteProfile(profile);
                profile = repository.getCurrentProfile();
                currentProfileIsModified = false;
                profilesDropdown.removeItem(currentProfileName);
                profilesDropdown.setSelectedItem(profile.getName());
                toggleDeleteButton();
                toggleResetButton();
                toggleApplyButton();
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
        final double threshold = metricInstance.getUpperThreshold();
        final boolean thresholdEnabled = metricInstance.isUpperThresholdEnabled();
        upperThresholdEnabledCheckbox.setSelected(thresholdEnabled);
        upperThresholdEnabledCheckbox.setEnabled(true);
        final String thresholdString = Double.toString(threshold);
        upperThresholdField.setEnabled(thresholdEnabled);
        if (thresholdEnabled) {
            upperThresholdField.setText(thresholdString);
        } else {
            upperThresholdField.setText("");
        }
        final double lowerThreshold = metricInstance.getLowerThreshold();
        final boolean lowerThresholdEnabled = metricInstance.isLowerThresholdEnabled();
        lowerThresholdEnabledCheckbox.setSelected(lowerThresholdEnabled);
        lowerThresholdEnabledCheckbox.setEnabled(true);
        final String lowerThresholdString = Double.toString(lowerThreshold);
        lowerThresholdField.setEnabled(thresholdEnabled);
        if (thresholdEnabled) {
            lowerThresholdField.setText(lowerThresholdString);
        } else {
            lowerThresholdField.setText("");
        }
        @NonNls final String descriptionName = "/metricsDescriptions/" + metric.getID() + ".html";
        final boolean resourceFound = setDescriptionFromResource(descriptionName, metric);
        if (!resourceFound) {
            setDescriptionFromResource("/metricsDescriptions/UnderConstruction.html");
        }
    }

    private boolean setDescriptionFromResource(@NonNls String resourceName) {
        try {
            final URL resourceURL = getClass().getResource(resourceName);
            descriptionTextArea.setPage(resourceURL);
            return true;
        } catch (IOException ignore) {
            return false;
        }
    }

    private boolean setDescriptionFromResource(String resourceName, Metric metric) {
        try {
            final URL resourceURL = metric.getClass().getResource(resourceName);
            descriptionTextArea.setPage(resourceURL);
            return true;
        } catch (IOException ignore) {
            return false;
        }
    }

    private void clearSelection() {
        selectedMetricInstance = null;
        lowerThresholdField.setText("");
        lowerThresholdField.setEnabled(false);
        lowerThresholdEnabledCheckbox.setEnabled(false);
        upperThresholdField.setText("");
        upperThresholdField.setEnabled(false);
        upperThresholdEnabledCheckbox.setEnabled(false);
        setDescriptionFromResource("/metricsDescriptions/Blank.html");
    }

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


    public Action[] createActions() {
        return new Action[0];
    }

    public String getTitle() {
        return MetricsReloadedBundle.message("metrics.configuration.panel.title");
    }

    @Nullable
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    @NonNls
    protected String getDimensionServiceKey() {

        return "MetricsConfigurationPanel";
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
            } else {
                tool.setEnabled(false);
            }
            currentProfileIsModified = true;
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
                        MetricTreeNode grandChild = (MetricTreeNode) grandchildren.nextElement();
                        grandChild.enabled = node.enabled;
                        if (grandChild.getUserObject()instanceof MetricInstance) {
                            ((MetricInstance) grandChild.getUserObject()).setEnabled(node.enabled);
                        }
                    }
                }
            }
        }
        toggleRunButton();
        toggleApplyButton();
        toggleResetButton();
        tree.repaint();
    }

    public void createUIComponents() {
        final Icon expandIcon = IconHelper.getIcon("/icons/inspector/expandall.png");
        final AnAction expandActon = new AnAction(MetricsReloadedBundle.message("expand.all.action"),
                MetricsReloadedBundle.message("expand.all.description"), expandIcon) {
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
        final Icon collapseIcon = IconHelper.getIcon("/icons/inspector/collapseall.png");

        final AnAction collapseAction = new AnAction(MetricsReloadedBundle.message("collapse.all.action"),
                MetricsReloadedBundle.message("collapse.all.description"), collapseIcon) {
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
        final Icon filterIcon = IconHelper.getIcon("/ant/filter.png");

        final AnAction filterAction = new AnAction(MetricsReloadedBundle.message("apply.filter.action"),
                MetricsReloadedBundle.message("apply.filter.description"), filterIcon) {
            public void actionPerformed(AnActionEvent anActionEvent) {
                applyFilter();
            }
        };
        final ActionManager actionManager = ActionManager.getInstance();

        final DefaultActionGroup expandCollapseGroup = new DefaultActionGroup();
        expandCollapseGroup.add(expandActon);
        expandCollapseGroup.add(collapseAction);

        treeToolbar =
                (ActionToolbarImpl) actionManager
                        .createActionToolbar("EXPAND_COLLAPSE_GROUP", expandCollapseGroup, true);
        final DefaultActionGroup filterGroup = new DefaultActionGroup();
        filterGroup.add(filterAction);
        filterToolbar =
                (ActionToolbarImpl) actionManager.createActionToolbar("FILTER_GROUP", filterGroup, true);
        setupFilterComboBox();
    }

    private void applyFilter() {
        final JTextComponent component = (JTextComponent) filterComboBox.getEditor().getEditorComponent();

        String newFilterString = component.getText();
        if (newFilterString == null) {
            newFilterString = "";
        }
        if (currentFilterString.equals(newFilterString)) {
            return;
        }
        currentFilterString = newFilterString;
        boolean found = false;
        for (String filter : filters) {
            if (currentFilterString.equals(filter)) {
                found = true;
            }
        }
        if (!found) {
            final String[] newFilters = new String[filters.length + 1];
            System.arraycopy(filters, 0, newFilters, 0, filters.length);
            newFilters[newFilters.length - 1] = currentFilterString;
            Arrays.sort(newFilters);
            filters = newFilters;
            final MutableComboBoxModel filtersModel = new DefaultComboBoxModel(filters);
            filterComboBox.setModel(filtersModel);
        }
        filterComboBox.setSelectedItem(newFilterString);
        populateTree();
    }

    private static class MyTreeCellRenderer extends JPanel implements TreeCellRenderer {
        private final JLabel myLabel;
        private final JCheckBox myCheckbox;

        @SuppressWarnings({"OverridableMethodCallInConstructor"})
         MyTreeCellRenderer() {
            super(new BorderLayout());
            myCheckbox = new JCheckBox();
            myLabel = new JLabel();
            add(myCheckbox, BorderLayout.WEST);
            add(myLabel, BorderLayout.CENTER);
        }

        @SuppressWarnings({"HardCodedStringLiteral"})
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
        public Dimension getPreferredScrollableViewportSize() {
            Dimension size = super.getPreferredScrollableViewportSize();
            size = new Dimension(size.width + 10, size.height);
            return size;
        }

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

    private boolean isMetricAccepted(Metric metric) {
        final String lowerCaseFilterString = currentFilterString.toLowerCase();
        if (metric.getDisplayName().toLowerCase().indexOf(lowerCaseFilterString) >= 1) {
            return true;
        }
        @NonNls final String descriptionName = "/metricsDescriptions/" + metric.getID() + ".html";
        final InputStream resourceStream = metric.getClass().getResourceAsStream(descriptionName);
        return readStreamContents(resourceStream).toLowerCase().contains(lowerCaseFilterString);
    }

    private static String readStreamContents(InputStream resourceStream) {
        if (resourceStream == null) {
            return "";
        }
        try {
            final StringBuffer out = new StringBuffer();
            while (true) {
                final int c = resourceStream.read();
                if (c == -1) {
                    break;
                }
                out.append((char) c);
            }
            return out.toString();
        }
        catch (IOException e) {
            logger.error(e);
        }
        return "";
    }

    private class CopyProfileAction extends AbstractAction {

        private final MetricsProfileRepository repository;
        private final Project project;

        CopyProfileAction(MetricsProfileRepository repository,
                          MetricsConfigurationPanel metricsConfigurationPanel,
                          Project project) {
            super(MetricsReloadedBundle.message("copy.profile.action"));
            this.repository = repository;
            this.project = project;
        }

        public void actionPerformed(ActionEvent event) {
            final String newProfileName = Messages.showInputDialog(saveAsButton,
                    MetricsReloadedBundle.message("enter.new.profile.name"),
                    MetricsReloadedBundle.message("create.new.metrics.profile"),
                    Messages.getQuestionIcon());
            if (newProfileName == null) {
                return;
            }
            repository.duplicateCurrentProfile(newProfileName);
            updateSelection(newProfileName);
        }
    }

    private class NewProfileAction extends AbstractAction {

        private final MetricsProfileRepository repository;
        private final Project project;

        NewProfileAction(MetricsProfileRepository repository,
                         MetricsConfigurationPanel metricsConfigurationPanel,
                         Project project) {
            super(MetricsReloadedBundle.message("new.profile.action"));
            this.repository = repository;
            this.project = project;
        }

        public void actionPerformed(ActionEvent event) {
            final String newProfileName = Messages.showInputDialog(saveAsButton,
                    MetricsReloadedBundle.message("enter.new.profile.name"),
                    MetricsReloadedBundle.message("create.new.metrics.profile"),
                    Messages.getQuestionIcon());
            if (newProfileName == null) {
                return;
            }
            repository.createEmptyProfile(newProfileName);
            updateSelection(newProfileName);
        }
    }
}
