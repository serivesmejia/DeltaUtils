/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.RobotCoreLynxUsbDevice;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.ui.UILocation;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.deltarobotics9351.AsyncUtil.asyncExecute;

public class DeltaAppUtil {

    public static final void restartAppCausedByError(final HardwareMap hardwareMap, String globalErrorMessage, String toast){
        final CountDownLatch lastDitchEffortFailsafeDone = new CountDownLatch(1);
        asyncExecute(new Runnable(){ //send failSafe command to all modules
            public void run(){
                for(RobotCoreLynxUsbDevice dev : hardwareMap.getAll(RobotCoreLynxUsbDevice.class)){
                    dev.lockNetworkLockAcquisitions();
                    dev.failSafe();
                }
                lastDitchEffortFailsafeDone.countDown();
            }
        });

        try {
            if(lastDitchEffortFailsafeDone.await(250, TimeUnit.MILLISECONDS)){ //wait for failSafe command to be sent, with a timeout of 250 ms
                RobotLog.e("DeltaUtils - Successfully sent failsafe commands to all Lynx modules before the app restarts");
            }else{
                RobotLog.e("DeltaUtils - Timed out to send failsafe commands to all Lynx modules before the app restarts");
            }
        } catch (InterruptedException e) { } //ignore the exception

        RobotLog.setGlobalErrorMsg(globalErrorMessage); //show error messages
        RobotLog.e(globalErrorMessage);
        AppUtil.getInstance().showToast(UILocation.BOTH, toast);

        threadStackTracesDump(); //show all stacktraces, for debugging purposes.

        try {
            Thread.sleep(3000); //wait a bit for the messages to be seen
        } catch (InterruptedException e) { } //ignore the exception again

        AppUtil.getInstance().restartApp(-1); //use the FTC SDK's app util class to restart the app
    }

    public static final void threadStackTracesDump(){

        RobotLog.e("DeltaUtils - Thread dump start");

        for(Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()){
            RobotLog.logStackTrace(entry.getKey(), entry.getValue());
        }

        RobotLog.e("DeltaUtils - Thread dump end");

    }

}
