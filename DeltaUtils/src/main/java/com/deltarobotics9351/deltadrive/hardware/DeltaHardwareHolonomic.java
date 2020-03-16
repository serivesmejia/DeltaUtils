/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.hardware;

import com.deltarobotics9351.deltadrive.utils.Invert;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DeltaHardwareHolonomic extends DeltaHardware{

    /**
     * Constructor for the delta hardware mecanum class
     * Do not forget to initialize the motors with initHardware()
     * @param hdwMap The current OpMode hardware map
     * @param invert Enum specifying which side will be inverted (motors), most of the time you need to invert the right side.
     */
    public DeltaHardwareHolonomic(HardwareMap hdwMap, Invert invert){
        super(hdwMap, invert);
        type = Type.HOLONOMIC;
    }

    /**
     * Initialize motors.
     * @param frontleft The front left motor of the chassis.
     * @param frontright The front right motor of the chassis.
     * @param backleft The back left motor of the chassis.
     * @param backright The back right motor of the chassis.
     * @param brake brake the motors when their power is 0
     */
    public final void initHardware(DcMotor frontleft, DcMotor frontright, DcMotor backleft, DcMotor backright, boolean brake){

		wheelFrontLeft = frontleft;
		wheelFrontRight = frontright;
		wheelBackLeft = backleft;
		wheelBackRight = backright;

        wheelFrontLeft.setDirection(DcMotor.Direction.FORWARD); //all motors need to be ALWAYS FORWARD for the drive classes to work correctly
        wheelFrontRight.setDirection(DcMotor.Direction.FORWARD);
        wheelBackLeft.setDirection(DcMotor.Direction.FORWARD);
        wheelBackRight.setDirection(DcMotor.Direction.FORWARD);

        wheelFrontRight.setPower(0); //just in case.
        wheelBackRight.setPower(0);
        wheelFrontLeft.setPower(0);
        wheelBackLeft.setPower(0);

        updateChassisMotorsArray();

        setBrakes(brake);

        setRunModes(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public final void updateChassisMotorsArray(){
        chassisMotorsArray[0] = wheelFrontLeft;
        chassisMotorsArray[1] = wheelFrontRight;
        chassisMotorsArray[2] = wheelBackLeft;
        chassisMotorsArray[3] = wheelBackRight;
    }

    @Override
    public final void setAllMotorPower(double frontleft, double frontright, double backleft, double backright){
        switch(invert) {
            case RIGHT_SIDE:
                wheelFrontLeft.setPower(frontleft);
                wheelFrontRight.setPower(-frontright);
                wheelBackLeft.setPower(backleft);
                wheelBackRight.setPower(-backright);
                break;
            case LEFT_SIDE:
                wheelFrontLeft.setPower(-frontleft);
                wheelFrontRight.setPower(frontright);
                wheelBackLeft.setPower(-backleft);
                wheelBackRight.setPower(backright);
                break;
            case BOTH_SIDES:
                wheelFrontLeft.setPower(-frontleft);
                wheelFrontRight.setPower(-frontright);
                wheelBackLeft.setPower(-backleft);
                wheelBackRight.setPower(-backright);
                break;
            case RIGHT_SIDE_MIDDLE:
                wheelFrontLeft.setPower(frontleft);
                wheelFrontRight.setPower(-frontright);
                wheelBackLeft.setPower(backleft);
                wheelBackRight.setPower(-backright);
                break;
            case LEFT_SIDE_MIDDLE:
                wheelFrontLeft.setPower(-frontleft);
                wheelFrontRight.setPower(frontright);
                wheelBackLeft.setPower(-backleft);
                wheelBackRight.setPower(backright);
                break;
            case BOTH_SIDES_MIDDLE:
                wheelFrontLeft.setPower(-frontleft);
                wheelFrontRight.setPower(-frontright);
                wheelBackLeft.setPower(-backleft);
                wheelBackRight.setPower(-backright);
                break;
            default:
                wheelFrontLeft.setPower(frontleft);
                wheelFrontRight.setPower(frontright);
                wheelBackLeft.setPower(backleft);
                wheelBackRight.setPower(backright);
                break;
        }
    }


    @Override
    public final void setTargetPositions(int frontleft, int frontright, int backleft, int backright){
        switch(invert) {
            case RIGHT_SIDE:
                wheelFrontLeft.setTargetPosition(frontleft);
                wheelFrontRight.setTargetPosition(-frontright);
                wheelBackLeft.setTargetPosition(backleft);
                wheelBackRight.setTargetPosition(-backright);
                break;
            case LEFT_SIDE:
                wheelFrontLeft.setTargetPosition(-frontleft);
                wheelFrontRight.setTargetPosition(frontright);
                wheelBackLeft.setTargetPosition(-backleft);
                wheelBackRight.setTargetPosition(backright);
                break;
            case BOTH_SIDES:
                wheelFrontLeft.setTargetPosition(-frontleft);
                wheelFrontRight.setTargetPosition(-frontright);
                wheelBackLeft.setTargetPosition(-backleft);
                wheelBackRight.setTargetPosition(-backright);
                break;
            case RIGHT_SIDE_MIDDLE:
                wheelFrontLeft.setTargetPosition(frontleft);
                wheelFrontRight.setTargetPosition(-frontright);
                wheelBackLeft.setTargetPosition(backleft);
                wheelBackRight.setTargetPosition(-backright);
                break;
            case LEFT_SIDE_MIDDLE:
                wheelFrontLeft.setTargetPosition(-frontleft);
                wheelFrontRight.setTargetPosition(frontright);
                wheelBackLeft.setTargetPosition(-backleft);
                wheelBackRight.setTargetPosition(backright);
                break;
            case BOTH_SIDES_MIDDLE:
                wheelFrontLeft.setTargetPosition(-frontleft);
                wheelFrontRight.setTargetPosition(-frontright);
                wheelBackLeft.setTargetPosition(-backleft);
                wheelBackRight.setTargetPosition(-backright);
                break;
            default:
                wheelFrontLeft.setTargetPosition(frontleft);
                wheelFrontRight.setTargetPosition(frontright);
                wheelBackLeft.setTargetPosition(backleft);
                wheelBackRight.setTargetPosition(backright);
                break;
        }
    }
    public final void setBrakes(boolean brake){
        updateChassisMotorsArray();

        for(DcMotor motor : chassisMotorsArray){
            if(brake && motor != null){
                motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }else if(motor != null){
                motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            }
        }
    }

    public final void setRunModes(DcMotor.RunMode runMode){
        updateChassisMotorsArray();

        for(DcMotor motor : chassisMotorsArray){
            if(motor != null){
                motor.setMode(runMode);
            }
        }
    }

}