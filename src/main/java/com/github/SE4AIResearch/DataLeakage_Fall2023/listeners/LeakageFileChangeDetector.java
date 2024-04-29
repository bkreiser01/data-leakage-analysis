package com.github.SE4AIResearch.DataLeakage_Fall2023.listeners;

import com.github.SE4AIResearch.DataLeakage_Fall2023.parsers.LeakageInstanceCollector;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.listeners.Utils.aCSVFileWasChanged;

public  class LeakageFileChangeDetector implements BulkFileListener {
  //  private final List<LeakageDetector> leakageDetectors;
    private LeakageInstanceCollector leakageInstanceCollector;


    public LeakageFileChangeDetector() {
        leakageInstanceCollector = new LeakageInstanceCollector();
    }


    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
//        for (VFileEvent event : events) {
//            var editor = getEditorForFileChanged(event);
//            if (editor != null) {
//              //  if (debug ) {
//                 //   if (debug) {
//                        dataLeakageIndicator.clearAllDataLeakageWarnings(editor);
//                   // }
//
//                    var instances = leakageAnalysisParser.LeakageInstances();
//
//                    if (!instances.isEmpty()) {
//                        for (var instance : instances) {
//                            dataLeakageIndicator.renderDataLeakageWarning(editor, instance.lineNumber(), instance.type());
//                        }
//
//                    } else {
//                        dataLeakageIndicator.clearAllDataLeakageWarnings(editor);
//
//                    }
//                //}
//            }
//        }

    }

    private boolean MultiTestLeakageCSVFileWasChanged(VFileEvent event) {
        return aCSVFileWasChanged(event) && event.getPath().endsWith("MultiUseTestLeak.csv");
    }

    private boolean OverlapLeakageCSVFileWasChanged(VFileEvent event) {
        return aCSVFileWasChanged(event) && event.getPath().endsWith("FinalOverlapLeak.csv");
    }


}
