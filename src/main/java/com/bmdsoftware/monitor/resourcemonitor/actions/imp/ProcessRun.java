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
package com.bmdsoftware.monitor.resourcemonitor.actions.imp;

import com.bmdsoftware.monitor.resourcemonitor.Main;
import com.bmdsoftware.monitor.resourcemonitor.actions.Action;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.buildobjects.process.StreamConsumer;
import org.buildobjects.process.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ProcessRun: run the process/execute an action
 *
 * @author Luís A. Bastião Silva <luis.kop@gmail.com>
 */
public class ProcessRun implements Action {
    private static Logger logger = LoggerFactory.getLogger(Main.class);


    private String name = null;
    private String cmd = null;
    private Integer timeout = null;
    private List<String> monitor = null;
    private Integer executions = null;

    /**
     * Execute the process
     */
    public void execute( ) {

        try {
            logger.debug("Executing the following cmd: " + cmd);
            System.out.println("Executing: " + cmd);
            String[] args = cmd.split(" ");
            List<String> strs = new ArrayList<String>();

            for (int i = 1; i < args.length; i++) {
                strs.add(args[i]);
            }
            String output = ProcBuilder.run(args[0], strs.toArray(new String[strs.size()]));
        }
        catch (Exception e){
            logger.error("There is a problem executing action "+ cmd, e);
        }
        //System.out.println(output);
    }

    /**
     * Execute the process with a specific timeout
     */
    public void execute(int timeout) {
        logger.debug("Executing "+ cmd + ", with timeout " + timeout);
        ProcBuilder builder = new ProcBuilder(cmd);
        try{
            ProcResult result = builder.withTimeoutMillis(timeout)
                    .withArg("-version")
                    .withOutputConsumer(new StreamConsumer() {
                        public void consume(InputStream stream) throws IOException {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                            if (reader.readLine()!=null)
                                System.out.printf(reader.readLine());
                        }
                    })
                    .run();
            if (result.getExitValue()!=0){
                logger.error("There is a problem while running the process");
            }

        } catch (TimeoutException ex) {
            logger.error("There is a problem while running the process", ex);

        }

    }


    public ProcessRun() {
    }

    public ProcessRun(String name, String cmd, Integer timeout, List<String> monitor, Integer executions) {
        this.name = name;
        this.cmd = cmd;
        this.timeout = timeout;
        this.monitor = monitor;
        this.executions = executions;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public List<String> getMonitor() {
        return this.monitor;
    }

    public void setMonitor(List<String> monitor) {
        this.monitor = monitor;
    }

    public Integer getExecutions() {
        return this.executions;
    }

    public void setExecutions(Integer executions) {
        this.executions = executions;
    }

    public ProcessRun name(String name) {
        this.name = name;
        return this;
    }

    public ProcessRun cmd(String cmd) {
        this.cmd = cmd;
        return this;
    }

    public ProcessRun timeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    public ProcessRun monitor(List<String> monitor) {
        this.monitor = monitor;
        return this;
    }

    public ProcessRun executions(Integer executions) {
        this.executions = executions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ProcessRun)) {
            return false;
        }
        ProcessRun processRun = (ProcessRun) o;
        return Objects.equals(name, processRun.name) && Objects.equals(cmd, processRun.cmd) && Objects.equals(timeout, processRun.timeout) && Objects.equals(monitor, processRun.monitor) && Objects.equals(executions, processRun.executions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cmd, timeout, monitor, executions);
    }

    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", cmd='" + getCmd() + "'" +
            ", timeout='" + getTimeout() + "'" +
            ", monitor='" + getMonitor() + "'" +
            ", executions='" + getExecutions() + "'" +
            "}";
    }



}
