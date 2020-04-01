/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.timer;

import com.deltarobotics9351.deltaevent.event.manager.AsyncTimersManager;
import com.qualcomm.robotcore.hardware.HardwareMap;

import static com.deltarobotics9351.AsyncUtil.asyncExecute;

/**
 * Timer class used to run asynchronous events.
 * SDK Operations, such as setting motors to powers, reading sensor values, sending telemetry, etc. are not recommended to be done in this type of timer
 * In that case, use SuperSyncTimer instead, which needs to update()
 */
public class SuperAsyncTimer extends SuperSyncTimer {

    public SuperAsyncTimer(HardwareMap hdwMap){
        super(hdwMap);
        AsyncTimersManager.addAsyncTimer(this);
    }

    /**
     * Destroy this SuperAsyncTimer asynchronously.
     * IT NEEDS TO BE CALLED AT THE END OF YOUR OPMODE
     */
    public final void asyncDestroy(){
        asyncExecute(new Runnable(){
            @Override
            public void run(){
                destroy();
            }
        });
    }

}
