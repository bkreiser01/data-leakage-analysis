package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;

public class MyTableMouseListener extends MouseInputAdapter {
   private final JBTable table;
   private final Project project;
   private final int lineNumCol;

   public MyTableMouseListener(JBTable table, Project project, int lineNumCol) {
      this.table = table;
      this.project = project;
      this.lineNumCol = lineNumCol;
//      this.file = project.getProjectFile();
   }

   @Override
   public void mouseClicked(MouseEvent e) {
      int row, column, cellValueInt;

      row = table.rowAtPoint(e.getPoint());
      column = table.columnAtPoint(e.getPoint());
      DefaultTableModel model = (DefaultTableModel) table.getModel();

      // If the selected cell isn't a valid number do nothing
      try {
         String cellValue = (String) model.getValueAt(row, lineNumCol);
         cellValueInt = Integer.parseInt(cellValue);
      } catch (NumberFormatException | ArrayIndexOutOfBoundsException err) {
         return;
      }

      if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
         if (row >= 0 && column >= 0) {
            // TODO check that its actually the right column and that the input is valid before attempting to move
            moveToLine(cellValueInt);
         }
      }
   }

   private void moveToLine(int lineNumber) {
      if (project != null) {
         Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
         int lineCount = editor != null ? editor.getDocument().getLineCount() : -1;
         if (editor != null && lineCount >= lineNumber) {
            editor.getCaretModel().moveToLogicalPosition(editor.offsetToLogicalPosition(editor.getDocument().getLineStartOffset(lineNumber - 1)));
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
         }
      }
   }
}
