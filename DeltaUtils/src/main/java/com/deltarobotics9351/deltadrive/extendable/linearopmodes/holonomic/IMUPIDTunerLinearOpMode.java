/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.holonomic;

import com.deltarobotics9351.LibraryData;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.deltamath.geometry.Twist2d;
import com.deltarobotics9351.pid.PIDCoefficients;

/**
 * LinearOpMode class to tune the IMU PID Coefficients.
 * You need to override the setup() methods and define the motor variables (starting with "wheel") in there.
 * You also need to add the @Autonomous annotation for this to show up in the driver station list.
 */
public class IMUPIDTunerLinearOpMode extends IMUPIDHolonomicLinearOpMode {

    static double P = 0.01;
    static double I = 0.000001;
    static double D = 0.000001;

    @Override
    public final void _runOpMode() {

        this.RESET_ROBOT_HEADING = false;
        this.ROBOT_INITIAL_HEADING = new Rot2d();

        int selected = 0;

        while(!isStarted()){

            if(gamepad1.x){
                P = 0;
                I = 0;
                D = 0;
            }

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

                telemetry.addData("->P", String.format ("%.10f", P));
                telemetry.addData("I", String.format ("%.10f", I));
                telemetry.addData("D", String.format ("%.10f", D) +
                        "\n\nTo change selection, (A) or (B)\n" +
                        "\nTo change selected value, DPAD UP or DPAD DOWN\n" +
                        "To reset values back to 0, (X)" +
                        "\n\nDeltaUtils v" + LibraryData.VERSION);
            }else if(selected == 1){

                if (gamepad1.dpad_up) {
                    I += 0.0001;
                } else if (gamepad1.dpad_down) {
                    I -= 0.0001;
                }

                telemetry.addData("P", String.format ("%.10f", P));
                telemetry.addData("->I", String.format ("%.10f", I));
                telemetry.addData("D", String.format ("%.10f", D) +
                        "\n\nTo change selection, (A) or (B)\n" +
                        "\nTo change selected value, DPAD UP or DPAD DOWN\n" +
                        "To reset values back to 0, (X)" +
                        "\n\nDeltaUtils v" + LibraryData.VERSION);

            }else if(selected == 2){

                if (gamepad1.dpad_up) {
                    D += 0.0001;
                } else if (gamepad1.dpad_down) {
                    D -= 0.0001;
                }

                telemetry.addData("P", String.format ("%.10f", P));
                telemetry.addData("I", String.format ("%.10f", I));
                telemetry.addData("->D", String.format ("%.10f", D) +
                        "\n\nTo change selection, (A) or (B)\n" +
                        "\nTo change selected value, DPAD_UP or DPAD DOWN\n" +
                        "To reset values back to 0, (X)" +
                        "\n\nDeltaUtils v" + LibraryData.VERSION);
            }

            telemetry.update();
            sleep(40);
        }

        if(!isStarted()) return;

        setRotatePID(new PIDCoefficients(P, I, D));

        Twist2d twist = rotate(Rot2d.fromDegrees(90), 0.7, 5);

        sleep(3000);

        telemetry.addData("Final Robot Angle", this.getRobotAngle());
        telemetry.addData("IMU Reported Twist", twist);
        telemetry.addData("Expected Robot Angle", "90");
        telemetry.update();

        while(opModeIsActive());

    }

}
