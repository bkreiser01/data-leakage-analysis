package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.github.SE4AIResearch.DataLeakage_Fall2023.actions.RunLeakageAction;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_sources.LeakageSource;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.QuickFixActionNotifier;
import com.github.SE4AIResearch.DataLeakage_Fall2023.parsers.LeakageInstanceCollector;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// implementing DumbAware makes the tool window not available until indexing is complete
public class LeakageToolWindow implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        LeakageToolWindowContent toolWindowContent = new LeakageToolWindowContent(project, toolWindow);

        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(new BorderLayout(0, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

//      contentPanel.add(toolWindowContent.createControlsPanel(toolWindow), BorderLayout.SOUTH);

        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }


    private static class LeakageToolWindowContent {


        private final Project project;
        private final JPanel contentPanel;
        private JBLabel timeLabel, fileNameLabel;
        private JBTable summaryTable, instanceTable;
        private DefaultTableModel instanceTableModel, summaryTableModel;
        private int execTime;
        private String formattedTimeString;
        private RunLeakageAction runLeakageAction;

        public LeakageToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
            this.project = project;
            this.contentPanel = createContentPanel(toolWindow);
//         this.timeLabel = new JBLabel();
//         this.summaryTable = new JBTable();
//         this.instanceTable = new JBTable();
//         this.instanceTableModel = new DefaultTableModel();
//         this.summaryTableModel = new DefaultTableModel();
            this.execTime = -1;
            this.formattedTimeString = "";
            this.runLeakageAction = new RunLeakageAction(project);

            toolWindow.getComponent().add(contentPanel);
            project.getMessageBus().connect().subscribe(QuickFixActionNotifier.QUICK_FIX_ACTION_TOPIC, (QuickFixActionNotifier) this::updateTableData);

            project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
                @Override
                public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                    clearTableData();
                }

            });
            updateTableData();
        }

        private JPanel createContentPanel(@NotNull ToolWindow toolWindow) {
            JPanel mainPanel, summaryPanel, instancePanel, timePanel, controlsPanel, fileNamePanel;
            GridBagLayout layout;
            GridBagConstraints gbc;
            JComponent toolbar;
            int row;

            mainPanel = new JPanel();
            layout = new GridBagLayout();
            gbc = new GridBagConstraints();
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//         toolbar = createToolBar(mainPanel); // CHANGETOOLBAR
            toolbar = createHelpPanel();

            summaryPanel = createSummaryPanel(new String[]{"Type", "Leakage Count"});
            instancePanel = createInstancePanel(new String[]{"Type", "Line", "Variable", "Cause"});
            fileNamePanel = createFileNamePanel();
            timePanel = createTimePanel();
            controlsPanel = createControlsPanel(toolWindow);

            row = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            GridAdder.addObject(toolbar, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 0);
            gbc.fill = GridBagConstraints.BOTH;
            GridAdder.addObject(summaryPanel, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 0);
            GridAdder.addObject(instancePanel, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 1);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            GridAdder.addObject(fileNamePanel, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 0);
            GridAdder.addObject(timePanel, mainPanel, layout, gbc, 0, row++, 1, 1, 1, 0);
            GridAdder.addObject(controlsPanel, mainPanel, layout, gbc, 0, row, 1, 1, 1, 0);

            mainPanel.setLayout(layout);

            return mainPanel;
        }

        private JPanel createHelpPanel() {
            JPanel panel = new JPanel();
            JBLabel needHelp = new JBLabel("Need help?");
            JBLabel clickHere = createNewLinkLabel("Click here to learn more about data leakage",
                    "https://se4airesearch.github.io/DataLeakage_Website_Fall2023/pages/resources/");


            Icon icon = AllIcons.Ide.External_link_arrow;
//         Icon icon = IconLoader.getIcon("/actions/externalLink.png", getClass());
            JBLabel iconLabel = new JBLabel(icon);

//         panel.add(needHelp);
//         panel.add((Component) icon);
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.add(needHelp, BorderLayout.EAST);
            panel.add(iconLabel, BorderLayout.CENTER);
            panel.add(clickHere, BorderLayout.WEST);

            return panel;
        }

        private JComponent createToolBar(JPanel targetPanel) {
            ActionToolbar toolbar;


            AnAction helpAction = new AnAction("Help"/*, "Show help", AllIcons.General.ContextHelp*/) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    // Open Help site when button clicked
                    openWebpage("https://se4airesearch.github.io/DataLeakage_Website_Fall2023/");
                }
            };

            DefaultActionGroup actionGroup = new DefaultActionGroup(helpAction);
            toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_TITLE, actionGroup, true);
            toolbar.setTargetComponent(targetPanel);

            return toolbar.getComponent();
//         JPanel panel;
//         JButton helpButton;
//
//         help


        }

        @NotNull
        private JPanel createControlsPanel(ToolWindow toolWindow) {
            JPanel controlsPanel = new JPanel();
            JButton runAnalysisButton = new JButton("Run Data Leakage Analysis");
            runAnalysisButton.addActionListener(e -> {
                long startTime = System.currentTimeMillis();

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                formattedTimeString = now.format(formatter);

                DataContext dataContext = DataManager.getInstance().getDataContext(toolWindow.getComponent());
                AnActionEvent actionEvent = AnActionEvent.createFromDataContext("Data Leakage Analysis", null, dataContext);
                runLeakageAction.actionPerformed(actionEvent);


                long endTime = System.currentTimeMillis();
                double executionTimeSeconds = (endTime - startTime) / 1000.0;
                this.execTime = (int) executionTimeSeconds;

                // Check to make sure a python file is open before updating the tables and time label
                String fileName = getOnlyPythonFileName();
                if (fileName != null && runLeakageAction.isCompleted()) {
                    update(fileName);
                }

            });

            controlsPanel.add(runAnalysisButton, BorderLayout.CENTER);
//         controlsPanel.add(refreshTableButton, BorderLayout.WEST);

            return controlsPanel;
        }

        private String getOnlyPythonFileName() {
            VirtualFile file = null;
            String fileName = null;
            FileType fileType = null;

            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

            // Get the currently selected or open file
            VirtualFile[] selectedFiles = fileEditorManager.getSelectedFiles();
            if (selectedFiles.length > 0) {
                file = selectedFiles[0];
                fileName = file.getName();
                fileType = file.getFileType();
                if (!fileType.getName().equals("Python")) {
                    return null;
                }
            }


            return fileName;
        }

        @NotNull
        private JPanel createSummaryPanel(String[] columnNames) {
            JPanel summaryPanel;

            summaryPanel = new JPanel(new BorderLayout());
            summaryTableModel = new DefaultTableModel(columnNames, 3) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Make all cells non-editable
                    return false;
                }
            };
            summaryTable = new JBTable(summaryTableModel);

            summaryTable.setValueAt("Preprocessing Leakage", 0, 0);
            summaryTable.setValueAt("Multi-Test Leakage", 1, 0);
            summaryTable.setValueAt("Overlap Leakage", 2, 0);

            JBScrollPane scrollPane = new JBScrollPane();
            scrollPane.setViewportView(summaryTable);

            scrollPane.setBorder(BorderFactory.createTitledBorder("Leakage Summary"));

            summaryPanel.add(scrollPane, BorderLayout.CENTER);
            summaryPanel.setMinimumSize(new Dimension(0, summaryTable.getRowHeight() * 4 + summaryTable.getTableHeader().getPreferredSize().height));


            return summaryPanel;
        }


        private void updateTimeLabel() {
            String newText = "Showing results from " +
                    this.formattedTimeString +
                    " | Analysis completed in " +
                    execTime
                    + " seconds";

            this.timeLabel.setText(newText);
        }

        private void updateFileNamePanel(String fileName) {
            String newText = "Analysis completed on " + fileName;
            this.fileNameLabel.setText(newText);
        }

        private JPanel createTimePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            String labelString = "";
            JBLabel timeLabel = new JBLabel(labelString, SwingConstants.CENTER);
            this.timeLabel = timeLabel;

            panel.add(timeLabel);

            return panel;
        }

        private JPanel createFileNamePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            String labelString = "";
            this.fileNameLabel = new JBLabel(labelString, SwingConstants.CENTER);
            panel.add(this.fileNameLabel);
            return panel;
        }

        private static JPanel createNewInfoPanel() {
            JPanel panel = new JPanel(new BorderLayout());

            JBLabel multi = createNewLinkLabel("Multi-Test Leakage Info", "https://se4airesearch.github.io/DataLeakage_Website_Fall2023/pages/leakage/multi-test/");
            JBLabel preprop = createNewLinkLabel("Preprocessing Leakage Info", "https://se4airesearch.github.io/DataLeakage_Website_Fall2023/pages/leakage/preprocessing/");
            JBLabel overlap = createNewLinkLabel("Overlap Leakage Info", "https://se4airesearch.github.io/DataLeakage_Website_Fall2023/pages/leakage/overlap/");

            panel.add(multi, BorderLayout.NORTH);
            panel.add(preprop, BorderLayout.CENTER);
            panel.add(overlap, BorderLayout.SOUTH);

            return panel;
        }

        private static JBLabel createNewLinkLabel(String name, String url) {
            JBLabel label = new JBLabel("<html><a href=\"" + url + "\">" + name + "</a><html>");
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openWebpage(url);
                }
            });

            return label;
        }

        private static void openWebpage(String url) {
            try {
                Desktop.getDesktop().browse(URI.create(url));
            } catch (Exception e) {
                return;
            }
        }

        @NotNull
        private JPanel createInstancePanel(String[] columnNames) {
            JPanel instancesPanel = new JPanel(new BorderLayout());

            instanceTableModel = new DefaultTableModel(columnNames, 3) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Make all cells non-editable
                    return false;
                }
            };
            instanceTable = new JBTable(instanceTableModel);

            JBScrollPane scrollPane = new JBScrollPane(instanceTable);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Leakage Instances"));

            instancesPanel.add(scrollPane, BorderLayout.CENTER);


            return instancesPanel;
        }

        private void update(String fileName) {
            updateTableData();
            updateTimeLabel();
            updateFileNamePanel(fileName);
        }

        // Method to resize columns to fit data
        private void packColumns(JBTable table) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                int maxWidth = 0;

                // Determine the maximum width of the column
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer renderer = table.getCellRenderer(row, i);
                    Component comp = table.prepareRenderer(renderer, row, i);
                    maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
                }

                // Set the column width to the maximum width
                column.setPreferredWidth(maxWidth);
            }
        }

        private void packColumnsToHeader(JBTable table) {
            JTableHeader header = table.getTableHeader();
            for (int i = 0; i < table.getColumnCount() - 1; i++) {
                TableColumn col = table.getColumnModel().getColumn(i);
                TableCellRenderer headerRenderer = col.getHeaderRenderer();
                if (headerRenderer == null) {
                    headerRenderer = header.getDefaultRenderer();
                }
                Component headerComp = headerRenderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, i);
                int width = (int) headerComp.getPreferredSize().width;
                col.setPreferredWidth(width - 50);
            }
        }

        private void clearTableData() {
            // Update Instance
            clearTable(instanceTable, instanceTableModel);

            // Refresh table view
            instanceTable.revalidate();
            instanceTable.repaint();
            packColumnsToHeader(instanceTable);

            // Update Summary
            clearTable(summaryTable, summaryTableModel);


            // Refresh table view
            summaryTable.revalidate();
            summaryTable.repaint();

        }

        private void clearTable(JBTable table, DefaultTableModel tableModel) {
            table.removeAll();
            tableModel.setRowCount(0);
        }

        private void updateTableData() {
            // Update Instance
            clearTable(instanceTable, instanceTableModel);

            Object[][] newInstanceData = fetchInstanceData();

            for (Object[] row : newInstanceData) {
                instanceTableModel.addRow(row);
            }

            // Refresh table view
            instanceTable.revalidate();
            instanceTable.repaint();
            packColumnsToHeader(instanceTable);

            // Update Summary
            clearTable(summaryTable, summaryTableModel);

            Object[][] newSummaryData = fetchSummaryData();

            for (Object[] row : newSummaryData) {
                summaryTableModel.addRow(row);
            }

            // Mouse listener for line number navigation
            int lineNumCol = 1;
            MyTableMouseListener myTableMouseListener = new MyTableMouseListener(instanceTable, project, lineNumCol);
            instanceTable.addMouseListener(myTableMouseListener);

            // Refresh table view
            summaryTable.revalidate();
            summaryTable.repaint();
        }

        private Object[][] fetchInstanceData() {
            // for instance it is type, line number, variable name
            ArrayList<String[]> data;
            LeakageInstanceCollector leakageInstanceCollector = new LeakageInstanceCollector();
            List<LeakageInstance> leakageInstances = leakageInstanceCollector.LeakageInstances();
            int row = leakageInstances.size();
            int col = instanceTableModel.getColumnCount();
            HashMap<LeakageCause, String> causeMap = CauseMapFactory.getCauseMap();
            HashMap<LeakageType, String> leakageTypeMap = LeakageTypeMapFactory.getLeakageTypeMap();

            if (row == 0) {
                return new String[1][4];
            }

            data = new ArrayList<>();

//         {"Leakage Type", "Line Number", "Variable Associated", "Cause"}

            int curRow = 0;
            for (LeakageInstance instance : leakageInstances) {
                String[] newDataRow = new String[col];
                var sourceOptional = instance.getLeakageSource();

                newDataRow[0] = leakageTypeMap.get(instance.type()); // Leakage Type
                newDataRow[1] = String.valueOf(instance.lineNumber()); //Line Number
                newDataRow[2] = Utils.stripSuffixFromVariableName(instance.variableName()); // Variable Associated

                newDataRow[3] = causeMap.get(instance.getCause()); // Cause


                data.add(curRow, newDataRow);
                curRow++;
                curRow = addLeakageSourcesIfPresent(instance, newDataRow, data, curRow);
            }

            return data.toArray(String[][]::new); // Convert data arraylist to array
        }

        private static int addLeakageSourcesIfPresent(LeakageInstance instance, String[] newDataRow, ArrayList<String[]> data, int curRow) {
            var sourceOptional = instance.getLeakageSource();
            if (sourceOptional.isPresent()) {
                var source = instance.getLeakageSource().get();
                for (int i = 0; i < source.getLineNumbers().size(); i++) {
                    String[] subDataRow = newDataRow.clone();
                    subDataRow[1] = source.getLineNumbers().get(i).toString();
                    if (data.stream().noneMatch(r -> r[1].equals(subDataRow[1]))) {
                        data.add(curRow, subDataRow);
                        curRow++;
                    }
                }
            }
            return curRow;
        }

        private Object[][] fetchSummaryData() {
            Object[][] data = new String[3][2];
            LeakageInstanceCollector leakageInstanceCollector = new LeakageInstanceCollector();
            List<LeakageInstance> leakageInstances = leakageInstanceCollector.LeakageInstances();
            int preproc = 0, multitest = 0, overlap = 0;

            for (LeakageInstance instance : leakageInstances) {
                switch (instance.type()) {
                    case PreprocessingLeakage:
                        preproc++;
                        break;
                    case MultiTestLeakage:
                        multitest++;
                        break;
                    case OverlapLeakage:
                        overlap++;
                        break;
                }
            }

            data[0][0] = "Pre-Processing";
            data[1][0] = "Multi-Test";
            data[2][0] = "Overlap";

            data[0][1] = String.valueOf(preproc);
            data[1][1] = String.valueOf(multitest);
            data[2][1] = String.valueOf(overlap);

            return data;
        }

        @NotNull
        private JPanel getContentPanel() {
            return contentPanel;
        }


    }
}
