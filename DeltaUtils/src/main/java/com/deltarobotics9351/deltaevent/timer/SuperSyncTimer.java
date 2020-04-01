/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.timer;

import com.deltarobotics9351.DeltaAppUtil;
import com.deltarobotics9351.deltaevent.Super;
import com.deltarobotics9351.deltaevent.event.Event;
import com.deltarobotics9351.deltaevent.event.timer.TimerEvent;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.deltarobotics9351.AsyncUtil.asyncExecute;

/**
 * Timer class used to run synchrnous events.
 * SDK Operations, such as setting motors to powers, reading sensor values, sending telemetry, etc. are safe in this type of timer
 * This timer will be needed to update() repetitively in your OpMode code.
 * See SuperAsyncTimer if you can't update() repetitively in your OpMode
 */
public class SuperSyncTimer implements Super {

    private HashMap<TimerEvent, TimerDataPacket> eventsTime = new HashMap<>();

    public boolean destroying = false;

    private boolean finishedDestroying = false;

    public static final long msStuckInDestroy = 1000;

    private HardwareMap hardwareMap;

    public SuperSyncTimer(HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
    }

    @Override
    public SuperSyncTimer registerEvent(Event event) {
        if(!(event instanceof TimerEvent)){ throw new IllegalArgumentException("Event is not TimerEvent"); }

        events.add(event);
        eventsTime.put((TimerEvent) event, new TimerDataPacket(0, 0, false));

        return this;
    }

    @Override
    public SuperSyncTimer registerEvent(Event event, boolean repeat) {
        if(!(event instanceof TimerEvent)){ throw new IllegalArgumentException("Event is not TimerEvent"); }

        events.add(event);
        eventsTime.put((TimerEvent) event, new TimerDataPacket(0, 0, repeat));

        return this;
    }

    @Override
    public SuperSyncTimer registerEvent(Event event, int timeSeconds) {
        if(!(event instanceof TimerEvent)){ throw new IllegalArgumentException("Event is not TimerEvent"); }

        events.add(event);
        eventsTime.put((TimerEvent) event, new TimerDataPacket(timeSeconds * 1000, 0, false));

        return this;
    }

    @Override
    public SuperSyncTimer registerEvent(Event event, double timeSeconds) {
        if(!(event instanceof TimerEvent)){ throw new IllegalArgumentException("Event is not TimerEvent"); }

        events.add(event);
        eventsTime.put((TimerEvent) event, new TimerDataPacket((long)timeSeconds * 1000, 0, false));

        return this;
    }

    @Override
    public SuperSyncTimer registerEvent(Event event, int timeSeconds, boolean repeat) {
        if(!(event instanceof TimerEvent)){ throw new IllegalArgumentException("Event is not TimerEvent"); }

        events.add(event);
        eventsTime.put((TimerEvent) event, new TimerDataPacket((long)timeSeconds * 1000, 0, repeat));

        return this;
    }

    @Override
    public SuperSyncTimer registerEvent(Event event, double timeSeconds, boolean repeat) {
        if(!(event instanceof TimerEvent)){ throw new IllegalArgumentException("Event is not TimerEvent"); }

        events.add(event);
        eventsTime.put((TimerEvent) event, new TimerDataPacket((long)timeSeconds * 1000, 0, repeat));

        return this;
    }

    @Override
    public void unregisterEvents() {
        eventsTime.clear();
    }

    /**
     * Update method, which is not needed to call manually because it is called in another thread.
     */
    @Override
    public void update() {

        ArrayList<TimerEvent> evtToRemove = new ArrayList<>();

        for(Map.Entry<TimerEvent, TimerDataPacket> entry : eventsTime.entrySet()){

            TimerEvent evt = entry.getKey();
            TimerDataPacket evtTimeDataPacket = entry.getValue();

            if(evtTimeDataPacket.msLastSystemTime == 0 ){
                evt.startEvent();
                evtTimeDataPacket.msStartSystemTime = System.currentTimeMillis();
                evtTimeDataPacket.msLastSystemTime = System.currentTimeMillis();
            }

            if(evt.isCancelled()){
                evtToRemove.add(evt);
                continue;
            }

            evt.loopEvent(new TimerDataPacket(evtTimeDataPacket));

            if(evtTimeDataPacket.msEventTime < 1){
                evt.timeoutEvent();
                if(!evtTimeDataPacket.repeat){
                    evtToRemove.add(evt);
                } else {
                    evtTimeDataPacket.msStartSystemTime = 0;
                    evtTimeDataPacket.msElapsedTime = 0;
                    evtTimeDataPacket.msLastSystemTime = 0;
                }
            }else{
                long msElapsed = System.currentTimeMillis() - evtTimeDataPacket.msStartSystemTime;

                evtTimeDataPacket.msElapsedTime = msElapsed;
                evtTimeDataPacket.msLastSystemTime = System.currentTimeMillis();

                if(evtTimeDataPacket.msEventTime >= evtTimeDataPacket.msElapsedTime){
                    evt.timeoutEvent();
                    if(!evtTimeDataPacket.repeat){
                        evtToRemove.add(evt);
                    } else {
                        evtTimeDataPacket.msStartSystemTime = 0;
                        evtTimeDataPacket.msElapsedTime = 0;
                        evtTimeDataPacket.msLastSystemTime = 0;
                    }
                }
            }

            eventsTime.remove(evt);
            eventsTime.put(evt, evtTimeDataPacket);
        }

        for(TimerEvent evt : evtToRemove){
            eventsTime.remove(evt);
        }

    }

    /**
     * Destroy this SuperSyncTimer synchronously.
     * IT NEEDS TO BE CALLED AT THE END OF YOUR OPMODE
     */
    public final void destroy() {
        destroying = true;

        asyncExecute(new Runnable(){
            @Override
            public void run() {
                long msStartDestroying = System.currentTimeMillis();
                long msMaxTimeDestroying = msStartDestroying + msStuckInDestroy;

                while(!Thread.interrupted() && System.currentTimeMillis() < msMaxTimeDestroying);

                if(!finishedDestroying){
                    DeltaAppUtil.restartAppCausedByError(hardwareMap, "User SuperTimer stuck in destroy(). Restarting robot controller app.", "SuperTimer stuck in destroy(). Restarting robot controller app.");
                }

            }
        });

        cancelAllEvents();

    }


    public final void cancelAllEvents(){

        Event[] safeEvents = (Event[])events.toArray();

        for(Event evt : safeEvents){
            TimerEvent e = (TimerEvent)evt;
            e.cancelEvent();
            e.cancel();
        }

        if(destroying) finishedDestroying = true;

    }

    public final boolean hasFinishedDestroying(){ return finishedDestroying; }

}
