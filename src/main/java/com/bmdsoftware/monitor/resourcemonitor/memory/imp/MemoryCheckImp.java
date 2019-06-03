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

package com.bmdsoftware.monitor.resourcemonitor.memory.imp;

import com.bmdsoftware.monitor.resourcemonitor.jmx.JMXConnection;
import com.bmdsoftware.monitor.resourcemonitor.memory.MemoryAnalysisUtil;
import com.bmdsoftware.monitor.resourcemonitor.memory.MemoryChecker;
import com.sun.management.HotSpotDiagnosticMXBean;
import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMX;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bmdsoftware.monitor.resourcemonitor.memory.MemoryAnalysisUtil.printHistogram;
import static com.bmdsoftware.monitor.resourcemonitor.memory.MemoryAnalysisUtil.reportStrings;


/**
 * Memory Checker Implementation
 */
public class MemoryCheckImp implements MemoryChecker {

    private JMXConnection jmxConnection;
    private String nameHeapFile;

    private Long totalMemory;


    private Heap heap;


    public MemoryCheckImp(JMXConnection jmxConnection, String nameHeapFile) {
        this.jmxConnection = jmxConnection;
        this.nameHeapFile = nameHeapFile;
    }



    public void monitor() throws IOException {
        JMXServiceURL url = this.jmxConnection.getJmxService();
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        ObjectName mbeanName = null;
        try {
            mbeanName = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        HotSpotDiagnosticMXBean bean = JMX.newMBeanProxy(mbsc, mbeanName, HotSpotDiagnosticMXBean.class, true);

        String fileName = "heap_dump_" + new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date()) + ".hprof";
        boolean onlyLiveObjects = false;
        bean.dumpHeap(fileName, onlyLiveObjects);

        heap = HeapFactory.createFastHeap(new File(nameHeapFile + fileName));
        MemoryAnalysisUtil.heap = heap;


        printHistogram();
        // get the memory composite information


        //create object instances that will be used to get memory and operating system Mbean objects exposed by JMX; create variables for cpu time and system time before
        Object memoryMbean = null;
        Object osMbean = null;
        long cpuBefore = 0;
        long tempMemory = 0;
        CompositeData cd = null;

        cpuBefore = Long.parseLong("100");

        int sampleCount = 10;

    // call the garbage collector before the test using the Memory Mbean
        try {
            jmxc.getMBeanServerConnection().invoke(new ObjectName("java.lang:type=Memory"), "gc", null, null);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

//create a loop to get values every second (optional)
        for (int i = 0; i < sampleCount; i++) {

            //get an instance of the HeapMemoryUsage Mbean
            try {
                memoryMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
            } catch (MBeanException e) {
                e.printStackTrace();
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
            } catch (InstanceNotFoundException e) {
                e.printStackTrace();
            } catch (ReflectionException e) {
                e.printStackTrace();
            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
            }
            cd = (CompositeData) memoryMbean;

            //get an instance of the OperatingSystem Mbean
            try {
                osMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=OperatingSystem"),"ProcessCpuTime");
            } catch (MBeanException e) {
                e.printStackTrace();
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
            } catch (InstanceNotFoundException e) {
                e.printStackTrace();
            } catch (ReflectionException e) {
                e.printStackTrace();
            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
            }

            //System.out.println("Used memory: " + " " + cd.get("used") + " Used cpu: " + osMbean); //print memory usage
            tempMemory = tempMemory + Long.parseLong(cd.get("used").toString());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //get system time and cpu time from last poll
        long cpuAfter = Long.parseLong(osMbean.toString());

        long cpuDiff = cpuAfter - cpuBefore; //find cpu time between our first and last jmx poll
        System.out.println("Cpu diff in milli seconds: " + cpuDiff / 1000000); //print cpu time in miliseconds
        System.out.println("average memory usage is: " + tempMemory / sampleCount);//print average memory usage
    }


        public JMXConnection getJmxConnection() {
        return jmxConnection;
    }

    public void setJmxConnection(JMXConnection jmxConnection) {
        this.jmxConnection = jmxConnection;
    }

    public String getNameHeapFile() {
        return nameHeapFile;
    }

    public void setNameHeapFile(String nameHeapFile) {
        this.nameHeapFile = nameHeapFile;
    }



    public Heap getHeap() {
        return heap;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemoryCheckImp that = (MemoryCheckImp) o;

        if (jmxConnection != null ? !jmxConnection.equals(that.jmxConnection) : that.jmxConnection != null)
            return false;
        return nameHeapFile != null ? nameHeapFile.equals(that.nameHeapFile) : that.nameHeapFile == null;

    }

    @Override
    public int hashCode() {
        int result = jmxConnection != null ? jmxConnection.hashCode() : 0;
        result = 31 * result + (nameHeapFile != null ? nameHeapFile.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MemoryCheckImp{" +
                "jmxConnection=" + jmxConnection +
                ", nameHeapFile='" + nameHeapFile + '\'' +
                '}';
    }
}
