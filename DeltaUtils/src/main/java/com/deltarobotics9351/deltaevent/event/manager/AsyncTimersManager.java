/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.event.manager;

import com.deltarobotics9351.deltaevent.timer.SuperAsyncTimer;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import java.util.ArrayList;

import static com.deltarobotics9351.AsyncUtil.asyncExecute;

public class AsyncTimersManager {

    static Thread asyncTimersManager;

    static ArrayList<SuperAsyncTimer> asyncTimers = new ArrayList<SuperAsyncTimer>();

    static boolean alreadyInitialized = false;

    @OpModeRegistrar //annotation for the FTC SDK to execute this method every time the robot initializes
    public static void initialize(OpModeManager manager){
        removeAsyncTimers();

        if(!asyncTimersManager.isAlive()) asyncTimersManager.start();
    }

    public static void removeAsyncTimers(){
        for(final SuperAsyncTimer timer : asyncTimers){
            asyncExecute(new Runnable() {
                @Override
                public void run() {
                    timer.destroy();
                }
            });

            asyncTimers.remove(timer);
        }
    }

    public static void addAsyncTimer(SuperAsyncTimer timer){
        if(!asyncTimers.contains(timer)){
            asyncTimers.add(timer);
        }
    }

    public static void removeAsyncTimer(final SuperAsyncTimer timer){
        if(asyncTimers.contains(timer)){

            asyncExecute(new Runnable() {
                @Override
                public void run() {
                    timer.destroy();
                }
            });
            asyncTimers.remove(timer);

        }
    }

    private class AsyncTimersManagerRunnable implements Runnable {

        @Override
        public void run() {
            while(!Thread.interrupted()) {

                SuperAsyncTimer[] safeAsyncTimers = (SuperAsyncTimer[]) asyncTimers.toArray();

                for (final SuperAsyncTimer asyncTimer : safeAsyncTimers) {
                    asyncExecute(new Runnable(){
                        @Override
                        public void run(){
                            asyncTimer.update();
                        }
                    });
                }
            }
        }
    }

}
