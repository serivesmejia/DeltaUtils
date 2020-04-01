/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.hardware;

import com.deltarobotics9351.deltadrive.utils.Invert;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DeltaHardware {

    public DcMotor wheelFrontLeft = null;
    public DcMotor wheelFrontRight = null;
    public DcMotor wheelBackLeft = null;
    public DcMotor wheelBackRight = null;

    public DcMotor wheelMiddle = null;

    public DcMotor wheelsLeft = null;
    public DcMotor wheelsRight = null;

    DcMotor[] chassisMotorsArray = {null, null, null, null};

    public enum Type { DEFAULT, HOLONOMIC, HDRIVE }

    public Type type = Type.DEFAULT;

    /**
     * Enum specifying the side of the chassis which will be inverted
     * Most of the time, you need to invert the right side.
     */
    public Invert invert = Invert.RIGHT_SIDE;

    public HardwareMap hdwMap = null;

    /**
     * Constructor for the delta hardware holonomic class
     * Do not forget to initialize the motors with initHardware()
     * @param hdwMap The current OpMode hardware map
     * @param invert Enum specifying which side will be inverted (motors), most of the time you need to invert the right side.
     */
    public DeltaHardware(HardwareMap hdwMap, Invert invert){
        this.invert = invert;
        this.hdwMap = hdwMap;
    }


    public void initHardware(DcMotor frontleft, DcMotor frontright, DcMotor backleft, DcMotor backright, boolean brake){
        throw new UnsupportedOperationException("Method initHardware() with four motors is not supported in this DeltaHardware");
    }

    public void initHardware(DcMotor left, DcMotor right, DcMotor hdriveMiddle, boolean brake){
        throw new UnsupportedOperationException("Method initHardware() with three motors is not supported in this DeltaHardware");
    }

    public void initHardware(DcMotor left, DcMotor right, boolean brake){
        throw new UnsupportedOperationException("Method initHardware() with two motors is not supported in this DeltaHardware");
    }

    public void setAllMotorPower(double frontleft, double frontright, double backleft, double backright){
        throw new UnsupportedOperationException("Method setAllMotorPower() with four motors is not supported in this DeltaHardware");
    }

    public void setAllMotorPower(double left, double right, double middle){
        throw new UnsupportedOperationException("Method setAllMotorPower() with three motors is not supported in this DeltaHardware");
    }

    public void setAllMotorPower(double left, double right){
        throw new UnsupportedOperationException("Method setAllMotorPower() with two motors is not supported in this DeltaHardware");
    }

    public void setTargetPositions(int frontleft, int frontright, int backleft, int backright){
        throw new UnsupportedOperationException("Method setTargetPositions() with four motors is not supported in this DeltaHardware");
    }

    public void setTargetPositions(int left, int right, int middle){
        throw new UnsupportedOperationException("Method setTargetPositions() with three motors is not supported in this DeltaHardware");
    }

    public void setTargetPositions(int left, int right){
        throw new UnsupportedOperationException("Method setTargetPositions() with two motors is not supported in this DeltaHardware");
    }

    public void setBrakes(boolean brake){ throw new UnsupportedOperationException("Method setBrakes() is not supported in this DeltaHardware"); }

    public void setRunModes(DcMotor.RunMode runMode){ throw new UnsupportedOperationException("Method setBrakes() is not supported in this DeltaHardware"); }

    public void updateChassisMotorsArray(){ throw new UnsupportedOperationException("Method setBrakes() is not supported in this DeltaHardware"); }

}
