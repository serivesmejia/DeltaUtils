/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.mecanum;

import com.deltarobotics9351.deltadrive.drive.mecanum.hardware.DeltaHardwareMecanum;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Class to control
 */
public class TimeDriveMecanum {

    DeltaHardwareMecanum hdw;
    Telemetry telemetry;

    private ElapsedTime runtime = new ElapsedTime();

    /**
     * Constructor for the time drive class
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry The current OpMode telemetry to show info related tnto the moveme
     */
    public TimeDriveMecanum(DeltaHardwareMecanum hdw, Telemetry telemetry){
        this.hdw = hdw;
        this.telemetry = telemetry;
    }

    //se define el power de todos los motores y el tiempo en el que avanzaran a este power
    //la string es simplemente para mostrarla en la driver station con un mensaje telemetry.
    //(el tiempo es en segundos)
    private void setAllWheelPower(double frontleft, double frontright, double backleft, double backright, double time, String movementDescription){

        runtime.reset();

        switch(hdw.invert) {
            case RIGHT_SIDE:
                hdw.wheelFrontLeft.setPower(frontleft);
                hdw.wheelFrontRight.setPower(-frontright);
                hdw.wheelBackLeft.setPower(backleft);
                hdw.wheelBackRight.setPower(-backright);
                break;
            case LEFT_SIDE:
                hdw.wheelFrontLeft.setPower(-frontleft);
                hdw.wheelFrontRight.setPower(frontright);
                hdw.wheelBackLeft.setPower(-backleft);
                hdw.wheelBackRight.setPower(backright);
                break;
            case BOTH_SIDES:
                hdw.wheelFrontLeft.setPower(-frontleft);
                hdw.wheelFrontRight.setPower(-frontright);
                hdw.wheelBackLeft.setPower(-backleft);
                hdw.wheelBackRight.setPower(-backright);
                break;
            case NO_INVERT:
                hdw.wheelFrontLeft.setPower(frontleft);
                hdw.wheelFrontRight.setPower(frontright);
                hdw.wheelBackLeft.setPower(backleft);
                hdw.wheelBackRight.setPower(backright);
                break;
        }

        while(runtime.seconds() <= time){
            telemetry.addData("[Movement]", movementDescription);
            telemetry.addData("[frontleft]", frontleft);
            telemetry.addData("[frontright]", frontright);
            telemetry.addData("[backleft]", backleft);
            telemetry.addData("[backright]", backright);
            telemetry.addData("[Time]", time);
            telemetry.update();
        }

        hdw.wheelFrontLeft.setPower(0);
        hdw.wheelFrontRight.setPower(0);
        hdw.wheelBackLeft.setPower(0);
        hdw.wheelBackRight.setPower(0);

        telemetry.addData("[frontleft]", 0);
        telemetry.addData("[frontright]", 0);
        telemetry.addData("[backleft]", 0);
        telemetry.addData("[backright]", 0);
        telemetry.update();
    }

    //basado en esta imagen: https://i.imgur.com/R82YOwT.png
    //el movementDescription es simplemente para mostrarlo en un mensaje telemetry (driver station)

    //hacia adelante
    public void forward(double power, double timeSecs) {
        power = Math.abs(power);
        setAllWheelPower(power, power, power, power, timeSecs, "forward");
    }

    //hacia atras
    public void backwards(double power, double timeSecs) {
        power = Math.abs(power);
        setAllWheelPower(-power, -power, -power, -power, timeSecs, "backwards");
    }

    //deslizarse a la izquierda
    public void strafeRight(double power, double timeSecs) {
        power = Math.abs(power);
        setAllWheelPower(power, -power, -power, power, timeSecs, "strafeLeft");
    }

    //deslizarse a la izquierda
    public void strafeLeft(double power, double timeSecs) {
        power = Math.abs(power);
        setAllWheelPower(-power, power, power, -power, timeSecs, "strafeRight");
    }

    //girar a la derecha
    public void turnRight(double power, double timeSecs) {
        power = Math.abs(power);
        setAllWheelPower(power, -power, power, -power, timeSecs, "turnRight");
    }

    //girar a la izquierda
    public void turnLeft(double power, double timeSecs) {
        power = Math.abs(power);
        setAllWheelPower(-power, power, -power, power, timeSecs, "turnLeft");
    }

    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
