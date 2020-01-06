package com.github.deltarobotics9351.deltadrive.hardware;

import com.github.deltarobotics9351.deltadrive.utils.ChassisType;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DeltaHardware {

	public DcMotor wheelFrontLeft = null;
    public DcMotor wheelFrontRight = null;
    public DcMotor wheelBackLeft = null;
    public DcMotor wheelBackRight = null;

    public DcMotor wheelHDriveMiddle = null;

    public ChassisType chassisType = null;

    public HardwareMap hdwMap = null;

    //Constructor
    public DeltaHardware(HardwareMap hdwMap, DcMotor frontleft, DcMotor frontright, DcMotor backleft, DcMotor backright, ChassisType chassisType){
        this.chassisType = chassisType;
        if(chassisType != ChassisType.hdrive) {
            this.hdwMap = hdwMap;
            initHardware(frontleft, frontright, backleft, backright);
        }else{
            return;
        }
    }

    public DeltaHardware(HardwareMap hdwMap, DcMotor frontleft, DcMotor frontright, DcMotor backleft, DcMotor backright, DcMotor hdrivemiddle, ChassisType chassisType){
        this.chassisType = chassisType;
        if(chassisType == ChassisType.hdrive) {
            this.hdwMap = hdwMap;
            initHardwareHDrive(frontleft, frontright, backleft, backright, hdrivemiddle);
        }else{
            return;
        }
    }

    public void initHardware(DcMotor frontleft, DcMotor frontright, DcMotor backleft, DcMotor backright){

		wheelFrontLeft = frontleft;
		wheelFrontRight = frontright;
		wheelBackLeft = backleft;
		wheelBackRight = backright;
        wheelFrontLeft.setDirection(DcMotor.Direction.FORWARD);
        wheelFrontRight.setDirection(DcMotor.Direction.REVERSE);
        wheelBackLeft.setDirection(DcMotor.Direction.REVERSE);
        wheelBackRight.setDirection(DcMotor.Direction.REVERSE);

        wheelFrontRight.setPower(0);
        wheelBackRight.setPower(0);
        wheelFrontLeft.setPower(0);
        wheelBackLeft.setPower(0);

		wheelFrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheelBackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheelFrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheelBackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		
        wheelFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void initHardwareHDrive(DcMotor frontleft, DcMotor frontright, DcMotor backleft, DcMotor backright, DcMotor hdrivemiddle){

        wheelFrontLeft = frontleft;
        wheelFrontRight = frontright;
        wheelBackLeft = backleft;
        wheelBackRight = backright;
        wheelHDriveMiddle = hdrivemiddle;

        wheelFrontLeft.setDirection(DcMotor.Direction.FORWARD);
        wheelFrontLeft.setDirection(DcMotor.Direction.FORWARD);
        wheelFrontLeft.setDirection(DcMotor.Direction.FORWARD);
        wheelFrontLeft.setDirection(DcMotor.Direction.FORWARD);
        wheelHDriveMiddle.setDirection(DcMotor.Direction.FORWARD);

        wheelFrontRight.setPower(0);
        wheelBackRight.setPower(0);
        wheelFrontLeft.setPower(0);
        wheelBackLeft.setPower(0);
        wheelHDriveMiddle.setPower(0);

        wheelFrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheelBackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheelFrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheelBackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheelHDriveMiddle.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        wheelFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelHDriveMiddle.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

}
