/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent;

import com.deltarobotics9351.deltaevent.event.Event;

import java.util.ArrayList;

public interface Super {


    ArrayList<Event> events = new ArrayList<>();

    /**
     * Register an event
     * @param event the Event to register
     */
    Super registerEvent(Event event);

    /**
     * Unregister all the events
     */
    void unregisterEvents();

    /**
     * Update the pressed buttons and execute all the events.
     * This method should be placed at the end or at the start of your loop in your OpMode
     */
    void update();

}
