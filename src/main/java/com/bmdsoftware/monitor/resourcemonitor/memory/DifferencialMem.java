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

import java.util.Map;

/**
 * Class that allows to differencial two memory dumps 
 */
public class DifferencialMem {

    // default increase ram 
    private Long INCREASE_MEM = (long) (20 * 1024 * 1024); // 20MB;

    private MemoryDump dump1;
    private MemoryDump dump2;

    private Long diffSizeAlarmTrigger = INCREASE_MEM;

    public DifferencialMem(MemoryDump dump1, MemoryDump dump2) {
        this.dump1 = dump1;
        this.dump2 = dump2;
    }

    public Long getDiffSizeAlarmTrigger() {
        return diffSizeAlarmTrigger;
    }

    public void setDiffSizeAlarmTrigger(Long diffSizeAlarmTrigger) {
        this.diffSizeAlarmTrigger = diffSizeAlarmTrigger;
    }

    /**
     * Check if there are significative changes between dump1 and dump2
     * @return
     */
    public boolean isThereSignificativeIncrease() {
        return false;
    }
    /**
     * Get the objects that has the most significative increase 
     * @return
     */
    public Map<Object, Long> getIncreasedObjects(){
        return null;     
    }


    /**
     * Get the objects that was released between two memory dumps 
     * @return
     */
    public Map<Object, Long> getReleasedObjects(){
        return null;
    }

    



}
