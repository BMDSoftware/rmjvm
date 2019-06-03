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
package com.bmdsoftware.monitor.resourcemonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.MonitorInfo;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.bmdsoftware.monitor.resourcemonitor.actions.Action;
import com.bmdsoftware.monitor.resourcemonitor.conf.YmlConfig;
import com.bmdsoftware.monitor.resourcemonitor.jmx.JMXConnection;
import com.bmdsoftware.monitor.resourcemonitor.memory.MemoryChecker;
import com.bmdsoftware.monitor.resourcemonitor.memory.imp.MemoryCheckImp;
import com.bmdsoftware.monitor.resourcemonitor.utils.Consts;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * The main class for the rmjvm
 * 
 * @author Luís A. Bastião Silva <luis.kop@gmail.com>
 */
public class Main {

    private static String PROJECT_NAME = "rmjvm";
    private static String VERSION = "1.0";
    
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private boolean check = false;
    private boolean checkMemory = false;
    private boolean checkCPU = false;
    private boolean export = false;
    private String exportDirectory = "";
    private String hostname = "";
    private Integer port;
    private Map<String, String> dumpFiles = new HashMap<>();
    // Yml Configuration.
    private YmlConfig config = null;


    /**
     * 
     * Constructor with major settings
     * 
     * @param check
     * @param checkMemory
     * @param checkCPU
     * @param export
     * @param exportDirectory
     * @param hostname
     * @param port
     */
    public Main(boolean check, boolean checkMemory, boolean checkCPU, boolean export, String exportDirectory, String hostname, Integer port) {
        this.check = check;
        this.checkMemory = checkMemory;
        this.checkCPU = checkCPU;
        this.export = export;
        this.exportDirectory = exportDirectory;
        this.hostname = hostname;
        this.port = port;
    }

    // Default constructor

    public Main() {

    }


    public static void main(String[] args) {

        logger.info("ResourceMonitor: started");

        // Default configuration file
        String configurationFile = "conf" + File.separator + "rmjvm.yml";

        // Configure options
        Options options = new Options();

        Option help = new Option("h", "help", false, "help shows how to use the rmjvm and what its core funcionality");
        options.addOption(help);

        Option hostOpt = new Option("ho", "host",  false, "set the hostname for JMX of java listen process");
        hostOpt.setArgs(1);
        options.addOption(hostOpt);

        Option portOpt = new Option("p", "port", false, "set the port for JMX of java listen process");
        portOpt.setArgs(1);
        options.addOption(portOpt);

        
        Option checkOpt = new Option("c", "check", false, "check will run all the actions" +
         "and wait until it is requested to stop. Meanwhile it will monitoring the memory and compare ");
        checkOpt.setArgs(0);
        options.addOption(checkOpt);

        Option skipOpt = new Option("s", "skip", false, "skip the cpu or memory (--skip=mem,cpu) ");
        skipOpt.setArgs(1);
        options.addOption(skipOpt);


        Option exportOpt = new Option("e", "export", false, "export format (csv, output) ");
        exportOpt.setArgs(1);
        options.addOption(exportOpt);

        Option exportDirOpt = new Option("ed", "exportdirectory", false, "export directory where will be stored the files.");
        exportDirOpt.setArgs(1);
        options.addOption(exportDirOpt);
        

        // Configure Command Line Parser
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        Main app = new Main();
        
        // Load the configuration file.
        // If any options is passed by the command line, the command line will overwrite
        // the configurations file
        try {
            YmlConfig config = new YmlConfig(new File(configurationFile));
            config.load();
            app.setConfig(config);
            System.out.println("Config: loaded.");
        } catch (FileNotFoundException e1) {
            logger.error("There is a problem locating configuration file " + configurationFile, e1);
            System.err.println("Error: there is a problem locating configuration file" + configurationFile);
            System.exit(Consts.ERROR_WHILE_PARSING);
        }

        // Parse options from command line.
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException e) {
            logger.error("ResourceMonitor: error while parsing the command line options", e);
            System.err.println("Error: error while parsing the command line options" + configurationFile);
            System.exit(Consts.ERROR_WHILE_PARSING);
        }

        // Check the options and call the correct options.

        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            System.out.println(PROJECT_NAME + " " + VERSION);
            formatter.printHelp(PROJECT_NAME, options, true);
        } else if (cmd.hasOption("version")) {
            System.out.println("version: " + VERSION);
        }
        else if (cmd.hasOption("check")) {
            System.out.println("Checking ..");
            app.setCheck(true);
        }
        else if (cmd.hasOption("host")) {
            System.out.println("Host: ");
            String hostname = cmd.getOptionValue("host");
            app.setHostname(hostname);
        }
        else if (cmd.hasOption("port")) {
            System.out.println("Port: ");
            String port = cmd.getOptionValue("port");
            Integer portInt = Integer.parseInt(port);
            app.setPort(portInt);
        }
        else if (cmd.hasOption("skip")) {
            // Get what are going skip analysis. 
            String [] skipOpts = cmd.getOptionValues("skip");
            for (int i = 0; i<i++; i++)
                System.out.println("Skip will be: "+skipOpts[i] );
        }
        else if (cmd.hasOption("export")) {
            logger.debug("Export mode: enabled.");
            app.setExport(true);
        }
        else if (cmd.hasOption("exportdirectory")) {
            String exportdirectory = cmd.getOptionValue("exportdirectory");
            app.setExportDirectory(exportdirectory);
            System.out.println("Export Directory: "+ exportdirectory);
        }

        if (app.isCheck()){
            System.out.println("Tracing actions to monitor Resources (e.g. CPU/Mem)");
            System.out.println("JMX URI to trace: "+ app.getConfig().getUriJmxService());
            app.check();
        }

    }

    /**
     * Checking the memory dumps.
     * All the options has been already loading. 
     */
    public void check(){

        // Get actions 
        List<Action> actions = this.config.getActions();

        // Execute the actions 
        if (actions!=null) {
            for (Action a : actions) {
                logger.debug("Action " + a.toString());

                for (int i = 0; i<a.getExecutions(); i++){
                    // Execute
                    a.execute();

                    // Wait a timeout before monitor
                    if (a.getTimeout()>0) {
                        try {

                            Thread.sleep(a.getTimeout());
                        } catch (InterruptedException e) {
                            logger.error("Problem while waiting for timeout", e);
                        }
                    }

                    JMXConnection connection = new JMXConnection(config.getUriJmxService());
                    // Monitor
                    List<String> monitor = a.getMonitor();
                    if (monitor.contains("cpu")){
                      // TODO: to be implemented.
                    }
                    if (monitor.contains("mem")){

                        MemoryChecker check = new MemoryCheckImp(connection,config.getApplicationDirectory());
                        try {
                            check.monitor();
                        } catch (IOException e) {
                            logger.error("Error while monitoring memory ", e);
                        }

                    }


                }

            }
        }
        else
        {
            System.out.println("Warning: no actions available to execute. Please configure it first.");
        }
        // Execute the actions and dumps the results 

    }
    /**
     * 
     */
    public void monitorCPU(){

    }

    /**
     * 
     */
    public void monitorMem(){

    }

    public void getFinalReport(){

    }

    public static void getMetrics(){


        String hostName = "localhost";
        Integer portNum = 3333;

        JMXServiceURL u = null;
        try {
            u = new JMXServiceURL(
                    "service:jmx:rmi:///jndi/rmi://" + hostName + ":" + portNum +  "/jmxrmi");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            JMXConnector c = JMXConnectorFactory.connect(u);

//create object instances that will be used to get memory and operating system Mbean objects exposed by JMX; create variables for cpu time and system time before
            Object memoryMbean = null;
            Object osMbean = null;
            long cpuBefore = 0;
            long tempMemory = 0;
            int samplesCount = 10;
            CompositeData cd = null;
            JMXConnector jmxc = c;

            cpuBefore = 0;

// call the garbage collector before the test using the Memory Mbean
            jmxc.getMBeanServerConnection().invoke(new ObjectName("java.lang:type=Memory"), "gc", null, null);

//create a loop to get values every second (optional)
            for (int i = 0; i < samplesCount; i++) {

//get an instance of the HeapMemoryUsage Mbean
                memoryMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
                cd = (CompositeData) memoryMbean;
//get an instance of the OperatingSystem Mbean
                osMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=OperatingSystem"),"ProcessCpuTime");
                System.out.println("Used memory: " + " " + cd.get("used") + " Used cpu: " + osMbean); //print memory usage
                tempMemory = tempMemory + Long.parseLong(cd.get("used").toString());


                memoryMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "NonHeapMemoryUsage");

                cd = (CompositeData) memoryMbean;
                System.out.println("Used NON HEAP memory: " + " " + cd.get("used") + " Used cpu: " + osMbean); //print memory usage



                Thread.sleep(1000); //delay for one second
            }

//get system time and cpu time from last poll
            long cpuAfter = Long.parseLong(osMbean.toString());



            long cpuDiff = cpuAfter - cpuBefore; //find cpu time between our first and last jmx poll
            System.out.println("Cpu diff in milli seconds: " + cpuDiff / 1000000); //print cpu time in miliseconds
            System.out.println("average memory usage is: " + tempMemory / samplesCount);//print average memory usage

            Set<ObjectInstance> objectInstances = c.getMBeanServerConnection().queryMBeans(null, null);


            objectInstances.stream().forEach(instance -> System.out.println(instance));
            MBeanInfo info = c.getMBeanServerConnection().getMBeanInfo(new ObjectName("java.lang:type=Memory"));

            for (MBeanAttributeInfo element : info.getAttributes())
            {
                Object value;
                if (element.isReadable())
                {
                    try
                    {
                        value = c.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), element.getName());
                        System.out.println(element.getName());
                        System.out.println(value.toString());
                    }
                    catch (Exception e)
                    {
                    }
                }
                else
                {
                }
            }
/*
            HotSpotDiagnosticMXBean bean =
                    ManagementFactory.newPlatformMXBeanProxy(server,
                            HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean.class);
*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }


    }


    public Main(boolean check, boolean checkMemory, boolean checkCPU, boolean export, String exportDirectory) {
        this.check = check;
        this.checkMemory = checkMemory;
        this.checkCPU = checkCPU;
        this.export = export;
        this.exportDirectory = exportDirectory;
    }

    public boolean isCheck() {
        return this.check;
    }

    public boolean getCheck() {
        return this.check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheckMemory() {
        return this.checkMemory;
    }

    public boolean getCheckMemory() {
        return this.checkMemory;
    }

    public void setCheckMemory(boolean checkMemory) {
        this.checkMemory = checkMemory;
    }

    public boolean isCheckCPU() {
        return this.checkCPU;
    }

    public boolean getCheckCPU() {
        return this.checkCPU;
    }

    public void setCheckCPU(boolean checkCPU) {
        this.checkCPU = checkCPU;
    }

    public boolean isExport() {
        return this.export;
    }

    public boolean getExport() {
        return this.export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }

    public String getExportDirectory() {
        return this.exportDirectory;
    }

    public void setExportDirectory(String exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    public Main checkMemory(boolean checkMemory) {
        this.checkMemory = checkMemory;
        return this;
    }

    public Main checkCPU(boolean checkCPU) {
        this.checkCPU = checkCPU;
        return this;
    }

    public Main export(boolean export) {
        this.export = export;
        return this;
    }

    public Main exportDirectory(String exportDirectory) {
        this.exportDirectory = exportDirectory;
        return this;
    }


    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Main hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public Main port(Integer port) {
        this.port = port;
        return this;
    }


    public YmlConfig getConfig() {
        return config;
    }

    public void setConfig(YmlConfig config) {
        this.config = config;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Main)) {
            return false;
        }
        Main main = (Main) o;
        return check == main.check && checkMemory == main.checkMemory && checkCPU == main.checkCPU && export == main.export && Objects.equals(exportDirectory, main.exportDirectory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(check, checkMemory, checkCPU, export, exportDirectory);
    }

    @Override
    public String toString() {
        return "{" +
            " check='" + isCheck() + "'" +
            ", checkMemory='" + isCheckMemory() + "'" +
            ", checkCPU='" + isCheckCPU() + "'" +
            ", export='" + isExport() + "'" +
            ", exportDirectory='" + getExportDirectory() + "'" +
            "}";
    }



}
