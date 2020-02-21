/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.utils;

public class TelemetryMessage {

    public String caption;
    public Object value;

    public TelemetryMessage(String caption, Object value){
        this.caption = caption;
        this.value = value;
    }

}
