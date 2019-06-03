package com.bmdsoftware.monitor.resourcemonitor.cpu.imp;

import com.bmdsoftware.monitor.resourcemonitor.Main;
import com.bmdsoftware.monitor.resourcemonitor.cpu.CPUMonitor;
import com.bmdsoftware.monitor.resourcemonitor.jmx.JMXConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

public class CPUMonitorCheck implements CPUMonitor {

    private static Logger logger = LoggerFactory.getLogger(CPUMonitorCheck.class);


    private JMXConnection jmxConnection;
    private Long cpuCycle;


    public CPUMonitorCheck(JMXConnection jmxConnection) {
        this.jmxConnection = jmxConnection;
    }

    @Override
    public void monitor() throws IOException {


        JMXServiceURL url = this.jmxConnection.getJmxService();
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        Object osMbean = null;

        //get an instance of the OperatingSystem Mbean
        try {
            osMbean = jmxc.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=OperatingSystem"),"ProcessCpuTime");
            long cpuAfter = Long.parseLong(osMbean.toString());

            cpuCycle = cpuAfter;


        } catch (MBeanException e) {
            logger.error("Error while fetching MBean ", e);
        } catch (AttributeNotFoundException e) {
            logger.error("Error while fetching MBean attribute ", e);
        } catch (InstanceNotFoundException e) {
            logger.error("Error while fetching MBean instance ", e);
        } catch (ReflectionException e) {
            logger.error("Error while fetching MBean - reflection issue ", e);
        } catch (MalformedObjectNameException e) {
            logger.error("Error while fetching MBean - malformed object name ", e);
        }
        jmxc.close();
    }

    public JMXConnection getJmxConnection() {
        return jmxConnection;
    }

    public void setJmxConnection(JMXConnection jmxConnection) {
        this.jmxConnection = jmxConnection;
    }

    public Long getCpuCycle() {
        return cpuCycle;
    }

    public void setCpuCycle(Long cpuCycle) {
        this.cpuCycle = cpuCycle;
    }
}
