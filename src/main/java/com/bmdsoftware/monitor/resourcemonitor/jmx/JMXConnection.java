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
package com.bmdsoftware.monitor.resourcemonitor.jmx;

import java.net.MalformedURLException;
import java.util.Objects;

import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection to JMX
 */
public class JMXConnection {
    private static Logger logger = LoggerFactory.getLogger(JMXConnection.class);

    private String jmxUri = null;
    private JMXServiceURL jmxService;

    public JMXConnection(String jmxUri) {
        this.jmxUri = jmxUri;
        JMXServiceURL u = null;
        try {
            u = new JMXServiceURL(
                    jmxUri);
        } catch (MalformedURLException e) {
            logger.error("Error: problem with JMX connection", e);
        }
        jmxService = u ;
    }


    public String getJmxUri() {
        return this.jmxUri;
    }

    public void setJmxUri(String jmxUri) {
        this.jmxUri = jmxUri;
    }

    public JMXServiceURL getJmxService() {
        return this.jmxService;
    }

    public void setJmxService(JMXServiceURL jmxService) {
        this.jmxService = jmxService;
    }

    public JMXConnection jmxUri(String jmxUri) {
        this.jmxUri = jmxUri;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof JMXConnection)) {
            return false;
        }
        JMXConnection connection = (JMXConnection) o;
        return Objects.equals(jmxUri, connection.jmxUri) && Objects.equals(jmxService, connection.jmxService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jmxUri, jmxService);
    }

    @Override
    public String toString() {
        return "{" +
            " jmxUri='" + getJmxUri() + "'" +
            ", jmxService='" + getJmxService() + "'" +
            "}";
    }



}
