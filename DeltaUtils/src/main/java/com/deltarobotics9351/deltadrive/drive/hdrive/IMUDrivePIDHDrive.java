/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.hdrive;

import com.deltarobotics9351.deltadrive.drive.ExtendableIMUDrivePID;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHDrive;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class IMUDrivePIDHDrive extends ExtendableIMUDrivePID {

    public IMUDrivePIDHDrive(DeltaHardwareHDrive deltaHardware, Telemetry telemetry){
        super(deltaHardware, telemetry);
        setAllowedDeltaHardwareType(DeltaHardware.Type.HDRIVE);
    }

}
