/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.event;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Event {

    public abstract void execute(Object arg1, Object arg2);

    public abstract void execute(Object arg1);

    public abstract void execute(ArrayList<Object> args);

    public abstract void execute(HashMap<Object, Object> args);

}
