/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltometry;

import com.deltarobotics9351.deltometry.parameters.OdometerParameters;

public class Odometer {

    String deviceName;
    OdometerParameters parameters;

    public Odometer(String deviceName, OdometerParameters parameters){
        this.deviceName = deviceName;
        this.parameters = parameters;
    }

    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

}
