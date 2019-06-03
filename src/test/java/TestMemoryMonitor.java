
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

import com.bmdsoftware.monitor.resourcemonitor.actions.imp.ProcessRun;
import com.bmdsoftware.monitor.resourcemonitor.jmx.JMXConnection;
import com.bmdsoftware.monitor.resourcemonitor.memory.MemoryMonitor;
import com.bmdsoftware.monitor.resourcemonitor.utils.OSDetector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.MalformedObjectNameException;

public class TestMemoryMonitor {

    // it also could work to check if external process are working
    boolean setupAJavaProces() {
        return true;
    }

    @Test
    void testMemoryConnection() throws IOException, MalformedObjectNameException {
        setupAJavaProces();

        String hostName = "localhost";
        String portNum = "3333";
        JMXConnection con = null;
        try {
            con = new JMXConnection("service:jmx:rmi:///jndi/rmi://" + hostName + ":" + portNum + "/jmxrmi");


            // Create a temporary directory
            //File myTempDir = Files.createTempDir();
            Path tempDirWithPrefix = Files.createTempDirectory("rmjvm-");

            MemoryMonitor monitor = new MemoryMonitor(con);
            monitor.initHeapDump(new File("c:/Users/bastiao/Projects/dicoogle/dicoogle/target/"));
            String report = monitor.reportStrings();
            String hist = monitor.printHistogram();
            System.out.println(hist);
        }
        catch (Exception e){
            return;
        }


    }

    @Test
    void testDiffMemory(){

        //  Snapshot Dump 1 


        //  Snapshot Dump 2

        

    }
}
