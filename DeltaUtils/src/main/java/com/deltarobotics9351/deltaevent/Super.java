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
     * Register an event with an int parameter
     * @param event the Event to register
     */
    Super registerEvent(Event event, int parameterInt1);

    /**
     * Register an event with a double parameter
     * @param event the Event to register
     */
    Super registerEvent(Event event, double parameterDouble1);

    /**
     * Register an event with a boolean parameter
     * @param event The event to register
     * @return itself
     */
    Super registerEvent(Event event, boolean parameterBoolean1);

    /**
     * Register an event with int & boolean parameters
     * @param event the Event to register
     */
    Super registerEvent(Event event, int parameterInt1, boolean parameterBoolean1);

    /**
     * Register an event with double & boolean parameters
     * @param event the Event to register
     */
    Super registerEvent(Event event, double parameterDouble1, boolean parameterBoolean1);

    /**
     * Unregister all the events
     */
    void unregisterEvents();

    /**
     * Update the pressed buttons and execute all the events.
     * This method should be placed at the end or at the start of your repeat in your OpMode
     */
    void update();

}
