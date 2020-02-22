/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.mecanum;

import com.deltarobotics9351.LibraryData;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.pid.PIDConstants;

/**
 * LinearOpMode class to tune the IMU PID Coefficients.
 * You need to override the setup() methods and define the motor variables (starting with "wheel") in there.
 * You also need to add the @Autonomous annotation for this opmode to show up in the driver station.
 */
public class IMUPIDTunerLinearOpMode extends IMUPIDMecanumLinearOpMode { //extendemos una clase que ya contiene todos los metodos de encoders y IMU para optimizar el codigo y el tiempo

    @Override
    public final void _runOpMode() {

        double P = 0.01;
        double I = 0.000001;
        double D = 0.000001;

        int selected = 0;

        while(!isStarted()){

            if(gamepad1.a) selected++;

            if(gamepad1.b) selected--;

            if(selected < 0) selected = 2;
            if(selected > 2) selected = 0;

            if(selected == 0) {
                if (gamepad1.dpad_up) {
                    P += 0.0001;
                } else if (gamepad1.dpad_down) {
                    P -= 0.0001;
                }

                telemetry.addData("->P", P);
                telemetry.addData("I", I);
                telemetry.addData("D", D + "\n\nDeltaUtils v" + LibraryData.VERSION);
                telemetry.update();

                sleep(60);

            }else if(selected == 1){

                if (gamepad1.dpad_up) {
                    P += 0.0001;
                } else if (gamepad1.dpad_down) {
                    P -= 0.0001;
                }

                telemetry.addData("P", P);
                telemetry.addData("->I", I);
                telemetry.addData("D", D + "\n\nDeltaUtils v" + LibraryData.VERSION);
                telemetry.update();

                sleep(60);

            }else if(selected == 2){

                if (gamepad1.dpad_up) {
                    P += 0.0001;
                } else if (gamepad1.dpad_down) {
                    P -= 0.0001;
                }

                telemetry.addData("P", P);
                telemetry.addData("I", I);
                telemetry.addData("->D", D + "\n\nDeltaUtils v" + LibraryData.VERSION);
                telemetry.update();

                sleep(60);

            }
        }

        if(!isStarted()) return;

        setPID(new PIDConstants(P, I, D));

        rotate(Rot2d.fromDegrees(90), 0.7, 5);

        sleep(1000);

        telemetry.addData("Final Robot Angle", this.getRobotAngle());
        telemetry.update();

        while(opModeIsActive());

    }

}
