/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.holonomic;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.deltarobotics9351.deltadrive.drive.ExtendableIMUDrive;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;

/**
 * Class to use the IMU sensor integrated in the Rev Expansion Hub to make precise turns with mecanum wheels
*/
public class IMUDriveHolonomic extends ExtendableIMUDrive {

    /**
     * Constructor for the IMU drive Mecanum class
     * (Do not forget to call initIMU() before the OpMode starts!)
     *
     * @param hdw       The initialized DeltaHardwareHolonomic containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    public IMUDriveHolonomic(DeltaHardwareHolonomic hdw, Telemetry telemetry) {
        super(hdw, telemetry);
        setAllowedDeltaHardwareType(DeltaHardware.Type.HOLONOMIC);
    }

}