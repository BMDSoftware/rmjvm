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
package com.bmdsoftware.monitor.resourcemonitor.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bmdsoftware.monitor.resourcemonitor.actions.Action;

import com.bmdsoftware.monitor.resourcemonitor.actions.imp.ProcessRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * Load the configurations for the rmjvm
 * 
 * @author Luís A. Bastião Silva <luis.kop@gmail.com>
 */
public class YmlConfig {
    /**
     *
     */

    private static final String ACTIONS2 = "actions";

    /**
     *
     */

    private static final String APPLICATION = "application";

    private static Logger logger = LoggerFactory.getLogger(YmlConfig.class);

    // Link for JMX application to connect/monitoring
    private String uriJmxService;

    // List of actions to execute, that will allow to increase or reduce the memory
    private List<Action> actions = new ArrayList<>();

    // Link for the tracer to send information according with the standard.
    private String tracerUri;

    // Directory of the application to store dumps
    private String applicationDirectory;

    private File configurationFile;

    public YmlConfig(File f) throws FileNotFoundException {
        this.configurationFile = f;
        this.load(f);
    }

    public YmlConfig(String uriJmxService, List<Action> actions, String tracerUri) {
        this.uriJmxService = uriJmxService;
        this.actions = actions;
        this.tracerUri = tracerUri;
    }

    public void load() throws FileNotFoundException {
        this.load(this.configurationFile);
    }

    /**
     * Load the configurations file
     * 
     * @param f
     * @throws FileNotFoundException
     */
    public void load(File f) throws FileNotFoundException {

        if (!f.exists()) {
            throw new FileNotFoundException(f.getAbsolutePath());
        }
        Yaml yaml = new Yaml();

        InputStream inputStream = new FileInputStream(f);
        Map<String, Object> obj = yaml.load(inputStream);

        logger.debug("Loading configurations.. ");

        Map<String, Object> objActions = (Map) obj.get(APPLICATION);
        this.setUriJmxService((String)objActions.get("uri"));
        this.setApplicationDirectory((String)objActions.get("directory"));

        // Get actions
        List<Object> objApplication = (List) obj.get(ACTIONS2);
        for (Object k: objApplication){
            Map<String, Object> objAction = (Map<String, Object> )k ;
            String name = (String) objAction.get("name");
            String cmd = (String)objAction.get("cmd");
            Integer timeout = (Integer) objAction.get("timeout");
            List<String> monitor = (List)objAction.get("monitor");
            Integer executions = (Integer) objAction.get("executions");
            ProcessRun action = new ProcessRun(name, cmd, timeout, monitor, executions);
            this.actions.add(action);

        }
        logger.debug("Loading configurations complete. ");
   
    }

    private Action getAction(Map<String, String> action){

        return null;

    }
    
    public String getUriJmxService() {
        return this.uriJmxService;
    }

    public void setUriJmxService(String uriJmxService) {
        this.uriJmxService = uriJmxService;
    }

    public List<Action> getActions() {
        return this.actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getTracerUri() {
        return this.tracerUri;
    }

    public void setTracerUri(String tracerUri) {
        this.tracerUri = tracerUri;
    }

    public YmlConfig uriJmxService(String uriJmxService) {
        this.uriJmxService = uriJmxService;
        return this;
    }

    public YmlConfig actions(List<Action> actions) {
        this.actions = actions;
        return this;
    }

    public YmlConfig tracerUri(String tracerUri) {
        this.tracerUri = tracerUri;
        return this;
    }

    public String getApplicationDirectory() {
        return applicationDirectory;
    }

    public void setApplicationDirectory(String applicationDirectory) {
        this.applicationDirectory = applicationDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof YmlConfig)) {
            return false;
        }
        YmlConfig ymlConfig = (YmlConfig) o;
        return Objects.equals(uriJmxService, ymlConfig.uriJmxService) && Objects.equals(actions, ymlConfig.actions) && Objects.equals(tracerUri, ymlConfig.tracerUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uriJmxService, actions, tracerUri);
    }
    
    @Override
    public String toString() {
        return "{" +
            " uriJmxService='" + getUriJmxService() + "'" +
            ", actions='" + getActions() + "'" +
            ", tracerUri='" + getTracerUri() + "'" +
            "}";
    }

}
