/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.holonomic;

import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Turning using the IMU sensor integrated in the Expansion Hub and a PID loop, which slows the motors speed the more closer the robot is to the target.
 */
public class IMUDrivePIDHolonomic extends com.deltarobotics9351.deltadrive.drive.holonomic.ExtendableIMUDrivePID {


    /**
     * Constructor for the IMU PID Mecanum drive class
     * (Do not forget to call initIMU() before the OpMode starts!)
     *
     * @param hdw       The initialized hardware containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    public IMUDrivePIDHolonomic(DeltaHardwareHolonomic hdw, Telemetry telemetry) {
        super(hdw, telemetry);
        setAllowedDeltaHardwareType(DeltaHardware.Type.HOLONOMIC);
    }

}