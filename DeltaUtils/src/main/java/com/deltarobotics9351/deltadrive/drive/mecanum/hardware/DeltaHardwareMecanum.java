package com.deltarobotics9351.deltadrive.drive.mecanum.hardware;

import com.deltarobotics9351.deltadrive.utils.Invert;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DeltaHardwareMecanum {

	public DcMotor wheelFrontLeft = null;
    public DcMotor wheelFrontRight = null;
    public DcMotor wheelBackLeft = null;
    public DcMotor wheelBackRight = null;

    /**
     * Enum specifying the side which will be inverted
     * Most of the time, you need to invert the right side.
     */
    public Invert invert = null;

    public HardwareMap hdwMap = null;

    /**
     * Constructor for the delta hardware mecanum class
     * Do not forget to initialize the motors with initHardware()
     * @param hdwMap The current OpMode hardware map
     * @param invert Enum specifying which side will be inverted (motors), most of the time you need to invert the right side.
     */
    public DeltaHardwareMecanum(HardwareMap hdwMap, Invert invert){
        this.invert = invert;
        this.hdwMap = hdwMap;
    }

    /**
     * Initialize motors.
     * @param frontleft The front left motor of the chassis.
     * @param frontright The front right motor of the chassis.
     * @param backleft The back left motor of the chassis.
     * @param backright The back right motor of the chassis.
     * @param brake brake the motors when their power is 0
     */
    public void initHardware(DcMotor frontleft, DcMotor frontright, DcMotor backleft, DcMotor backright, boolean brake){

		wheelFrontLeft = frontleft;
		wheelFrontRight = frontright;
		wheelBackLeft = backleft;
		wheelBackRight = backright;

        wheelFrontLeft.setDirection(DcMotor.Direction.FORWARD);
        wheelFrontRight.setDirection(DcMotor.Direction.FORWARD);
        wheelBackLeft.setDirection(DcMotor.Direction.FORWARD);
        wheelBackRight.setDirection(DcMotor.Direction.FORWARD);

        wheelFrontRight.setPower(0);
        wheelBackRight.setPower(0);
        wheelFrontLeft.setPower(0);
        wheelBackLeft.setPower(0);

        if(brake) {

            wheelFrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            wheelBackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            wheelFrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            wheelBackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        }else{

            wheelFrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            wheelBackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            wheelFrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            wheelBackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        }

        wheelFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheelBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

}
