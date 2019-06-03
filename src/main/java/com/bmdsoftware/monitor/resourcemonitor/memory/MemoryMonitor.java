/**
 * Copyright (c) 2019, BMD software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
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
package com.bmdsoftware.monitor.resourcemonitor.memory;

import java.io.File;
import java.util.Objects;

import com.bmdsoftware.monitor.resourcemonitor.jmx.JMXConnection;

import static org.gridkit.jvmtool.heapdump.HeapWalker.valueOf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bmdsoftware.monitor.resourcemonitor.memory.TextTable;
import com.sun.management.HotSpotDiagnosticMXBean;
import org.gridkit.jvmtool.heapdump.HeapClusterAnalyzer;
import org.gridkit.jvmtool.heapdump.HeapClusterAnalyzer.ClusterDetails;
import org.gridkit.jvmtool.heapdump.HeapHistogram;
import org.gridkit.jvmtool.heapdump.HeapHistogram.ClassRecord;
import org.gridkit.jvmtool.heapdump.StringCollector;

import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;
import org.netbeans.lib.profiler.heap.Instance;
import org.netbeans.lib.profiler.heap.JavaClass;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.netbeans.lib.profiler.heap.HeapFactory;

/**
 * MemoryMonitor: monitoring the memory
 */
public class MemoryMonitor {

    private JMXConnection jmx;
    public Heap heap;

    public MemoryMonitor() {
    }

    public MemoryMonitor(JMXConnection jmx) {
        this.jmx = jmx;
    }

    /**
     * 
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    public void initHeapDump(File dumpFileDir) throws IOException, MalformedObjectNameException {
        //heap = HeapFactory.createFastHeap(dumpFile);
        JMXConnector jmxc = JMXConnectorFactory.connect(jmx.getJmxService(), null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        ObjectName mbeanName = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
        HotSpotDiagnosticMXBean bean = JMX.newMBeanProxy(mbsc, mbeanName, HotSpotDiagnosticMXBean.class, true);

        String fileName = "heap_dump_" + new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date()) + ".hprof";
        boolean onlyLiveObjects = false;
        bean.dumpHeap(fileName, onlyLiveObjects);
        File dumpFile = new File(dumpFileDir.getAbsolutePath()+File.separator+fileName);
        heap = HeapFactory.createFastHeap(dumpFile);
        //heap = HeapFactory.createFastHeap(dumpFile);
        dumpFile.deleteOnExit();
        
    }

    /** Reports retained size of string object in dump */
    public  String reportStrings() {
        StringCollector collector = new StringCollector();
        collector.collect(heap);
        //System.out.println(collector);
        return collector.toString();
    }


  /**
     * Create "jmap -histo" like class histogram from dump
     */
    public String printHistogram() {
        StringCollector collector = new StringCollector();
        HeapHistogram histo = new HeapHistogram();
        collector.collect(heap, histo);
        List<ClassRecord> ht = new ArrayList<ClassRecord>(histo.getHisto());
        ht.add(collector.asClassRecord());
        Collections.sort(ht, HeapHistogram.BY_SIZE);
        TextTable tt = new TextTable();
        int n = 0;
        for(ClassRecord cr: ht.subList(0, 500)) {
            tt.addRow("" + (++n), " " + cr.getTotalSize(), " " + cr.getInstanceCount(), " " + cr.getClassName());

        }
        //System.out.println(tt.formatTextTableUnbordered(1000));
        return tt.formatTextTableUnbordered(1000);
    }


    public JMXConnection getJmx() {
        return this.jmx;
    }

    public void setJmx(JMXConnection jmx) {
        this.jmx = jmx;
    }

    public MemoryMonitor jmx(JMXConnection jmx) {
        this.jmx = jmx;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MemoryMonitor)) {
            return false;
        }
        MemoryMonitor memoryMonitor = (MemoryMonitor) o;
        return Objects.equals(jmx, memoryMonitor.jmx);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jmx);
    }

    @Override
    public String toString() {
        return "{" +
            " jmx='" + getJmx() + "'" +
            "}";
    }

}