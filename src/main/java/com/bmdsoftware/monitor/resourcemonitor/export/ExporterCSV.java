/**
 * Copyright (c) 2019, BMD software
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the <organization> nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.bmdsoftware.monitor.resourcemonitor.export;

import com.bmdsoftware.monitor.resourcemonitor.cpu.CPUMonitor;
import com.bmdsoftware.monitor.resourcemonitor.memory.MemoryChecker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.gridkit.jvmtool.heapdump.HeapHistogram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/**
 * Export Memory Summary to CSV format
 */
public class ExporterCSV {


    private String exportFile;

    private String exportHeapFile;

    private boolean exportSummaryOpened = false;
    private boolean exportHeapSummaryOpened = false;

    private CSVPrinter csvPrinterSummary = null;
    private CSVPrinter csvPrinterHeapSummary = null;

    public ExporterCSV(String exportFile, String exportHeapFile ) {
        this.exportFile = exportFile;
        this.exportHeapFile = exportHeapFile;
    }


    public void exportSummary() throws IOException {
        // [ActionName, ExecutionIteration, Timestamp, UsedMemory, CPUTime]
        String[] HEADERS = { "ActionName", "ExecutionIteration", "Timestamp", "UsedMemory", "CPUTime"};
        if (!exportSummaryOpened) {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(exportFile));
            csvPrinterSummary = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader(HEADERS));
        }
    }


    public void exportSummaryEntry(EntrySummary entry) throws IOException {
        // Pop last actions
        csvPrinterSummary.printRecord(entry.getName(), entry.getIteration(), entry.getTimestamp(),
                entry.getTotalMemory(), entry.getCpuTime());
        csvPrinterSummary.flush();

    }


    public void exportHeapSummary() throws IOException {
        // [ActionName, ExecutionIteration, NameOfObject, ByteOrderBySize, Size, Count]

        String[] HEADERS = { "ActionName", "ExecutionIteration", "NameOfObject", "ByteOrderBySize", "Size", "Count"};
        if (!exportHeapSummaryOpened) {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(exportHeapFile));
            csvPrinterHeapSummary = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader(HEADERS));
        }
    }

    public void exportHeapSummaryEntries(EntrySummary entry) throws IOException {

        for(HeapHistogram.ClassRecord r: entry.getRecords()){
            exportHeapSummaryEntry(entry, r);
        }
        csvPrinterHeapSummary.flush();

    }

    public void exportHeapSummaryEntry(EntrySummary entry, HeapHistogram.ClassRecord record) throws IOException {
        csvPrinterHeapSummary.printRecord(entry.getName(), entry.getIteration(), record.getClassName(),
                record.getInstanceCount(), record.getInstanceCount());
    }



    public String getExportFile() {
        return exportFile;
    }

    public void setExportFile(String exportFile) {
        this.exportFile = exportFile;
    }

}
