/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.hardware;

import com.deltarobotics9351.deltadrive.utils.Invert;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DeltaHardwareHDrive extends DeltaHardware{

    /**
     * Constructor for the delta hardware mecanum class
     * Do not forget to initialize the motors with initHardware()
     * @param hdwMap The current OpMode hardware map
     * @param invert Enum specifying which side will be inverted (motors), most of the time you need to invert the right side.
     */
    public DeltaHardwareHDrive(HardwareMap hdwMap, Invert invert){
        super(hdwMap, invert);
        type = Type.HDRIVE;
    }

    /**
     * Initialize motors.
     * @param left The left side motor of the chassis.
     * @param right The right side motor of the chassis.
     * @param middle The middle wheel motor of the chassis
     * @param brake brake the motors when their power is 0
     */
    public final void initHardware(DcMotor left, DcMotor right, DcMotor middle, boolean brake){

        wheelsLeft = left;
        wheelsRight = right;
        wheelMiddle = middle;

        wheelsLeft.setDirection(DcMotor.Direction.FORWARD); //all motors need to be ALWAYS FORWARD for the drive classes to work correctly
        wheelsRight.setDirection(DcMotor.Direction.FORWARD);
        wheelMiddle.setDirection(DcMotor.Direction.FORWARD);

        setAllMotorPower(0, 0, 0);

        updateChassisMotorsArray();

        setBrakes(true);

        setRunModes(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public final void updateChassisMotorsArray(){
        chassisMotorsArray[0] = wheelsLeft;
        chassisMotorsArray[1] = wheelsRight;
        chassisMotorsArray[2] = wheelMiddle;
    }

    @Override
    public final void setAllMotorPower(double left, double right, double middle){
        switch(invert) {
            case RIGHT_SIDE:
                wheelsLeft.setPower(left);
                wheelsRight.setPower(-right);
                wheelMiddle.setPower(middle);
                break;
            case LEFT_SIDE:
                wheelsLeft.setPower(-left);
                wheelsRight.setPower(right);
                wheelMiddle.setPower(middle);
                break;
            case BOTH_SIDES:
                wheelsLeft.setPower(-left);
                wheelsRight.setPower(-right);
                wheelMiddle.setPower(middle);
                break;
            case MIDDLE:
                wheelsLeft.setPower(left);
                wheelsRight.setPower(right);
                wheelMiddle.setPower(-middle);
                break;
            case RIGHT_SIDE_MIDDLE:
                wheelsLeft.setPower(left);
                wheelsRight.setPower(-right);
                wheelMiddle.setPower(-middle);
                break;
            case LEFT_SIDE_MIDDLE:
                wheelsLeft.setPower(-left);
                wheelsRight.setPower(right);
                wheelMiddle.setPower(-middle);
                break;
            case BOTH_SIDES_MIDDLE:
                wheelsLeft.setPower(-left);
                wheelsRight.setPower(-right);
                wheelMiddle.setPower(-middle);
                break;
            default:
                wheelsLeft.setPower(left);
                wheelsRight.setPower(right);
                wheelMiddle.setPower(middle);
                break;
        }
    }

    @Override
    public final void setAllMotorPower(double frontleft, double frontright, double backleft, double backright){

        double left = (frontleft + backleft) / 2;
        double right = (frontleft + backleft) / 2;
        double middle = 0;

        switch(invert) {
            case RIGHT_SIDE:
                wheelsLeft.setPower(left);
                wheelsRight.setPower(-right);
                wheelMiddle.setPower(middle);
                break;
            case LEFT_SIDE:
                wheelsLeft.setPower(-left);
                wheelsRight.setPower(right);
                wheelMiddle.setPower(middle);
                break;
            case BOTH_SIDES:
                wheelsLeft.setPower(-left);
                wheelsRight.setPower(-right);
                wheelMiddle.setPower(middle);
                break;
            case MIDDLE:
                wheelsLeft.setPower(left);
                wheelsRight.setPower(right);
                wheelMiddle.setPower(-middle);
                break;
            case RIGHT_SIDE_MIDDLE:
                wheelsLeft.setPower(left);
                wheelsRight.setPower(-right);
                wheelMiddle.setPower(-middle);
                break;
            case LEFT_SIDE_MIDDLE:
                wheelsLeft.setPower(-left);
                wheelsRight.setPower(right);
                wheelMiddle.setPower(-middle);
                break;
            case BOTH_SIDES_MIDDLE:
                wheelsLeft.setPower(-left);
                wheelsRight.setPower(-right);
                wheelMiddle.setPower(-middle);
                break;
            default:
                wheelsLeft.setPower(left);
                wheelsRight.setPower(right);
                wheelMiddle.setPower(middle);
                break;
        }
    }

    @Override
    public final void setTargetPositions(int left, int right, int middle){
        switch(invert) {
            case RIGHT_SIDE:
                wheelsLeft.setTargetPosition(left);
                wheelsRight.setTargetPosition(-right);
                wheelMiddle.setTargetPosition(middle);
                break;
            case LEFT_SIDE:
                wheelsLeft.setTargetPosition(-left);
                wheelsRight.setTargetPosition(right);
                wheelMiddle.setTargetPosition(middle);
                break;
            case BOTH_SIDES:
                wheelsLeft.setTargetPosition(-left);
                wheelsRight.setTargetPosition(-right);
                wheelMiddle.setTargetPosition(middle);
                break;
            case MIDDLE:
                wheelsLeft.setTargetPosition(left);
                wheelsRight.setTargetPosition(right);
                wheelMiddle.setTargetPosition(-middle);
                break;
            case RIGHT_SIDE_MIDDLE:
                wheelsLeft.setTargetPosition(left);
                wheelsRight.setTargetPosition(-right);
                wheelMiddle.setTargetPosition(-middle);
                break;
            case LEFT_SIDE_MIDDLE:
                wheelsLeft.setTargetPosition(-left);
                wheelsRight.setTargetPosition(right);
                wheelMiddle.setTargetPosition(-middle);
                break;
            case BOTH_SIDES_MIDDLE:
                wheelsLeft.setTargetPosition(-left);
                wheelsRight.setTargetPosition(-right);
                wheelMiddle.setTargetPosition(-middle);
                break;
            default:
                wheelsLeft.setTargetPosition(left);
                wheelsRight.setTargetPosition(right);
                wheelMiddle.setTargetPosition(middle);
                break;
        }
    }

    @Override
    public final void setTargetPositions(int frontleft, int frontright, int backleft, int backright){

        int left = Math.round((frontleft + backleft) / 2);
        int right = Math.round((frontright + backright) / 2);
        int middle = 0;

        switch(invert) {
            case RIGHT_SIDE:
                wheelsLeft.setTargetPosition(left);
                wheelsRight.setTargetPosition(-right);
                wheelMiddle.setTargetPosition(middle);
                break;
            case LEFT_SIDE:
                wheelsLeft.setTargetPosition(-left);
                wheelsRight.setTargetPosition(right);
                wheelMiddle.setTargetPosition(middle);
                break;
            case BOTH_SIDES:
                wheelsLeft.setTargetPosition(-left);
                wheelsRight.setTargetPosition(-right);
                wheelMiddle.setTargetPosition(middle);
                break;
            case MIDDLE:
                wheelsLeft.setTargetPosition(left);
                wheelsRight.setTargetPosition(right);
                wheelMiddle.setTargetPosition(-middle);
                break;
            case RIGHT_SIDE_MIDDLE:
                wheelsLeft.setTargetPosition(left);
                wheelsRight.setTargetPosition(-right);
                wheelMiddle.setTargetPosition(-middle);
                break;
            case LEFT_SIDE_MIDDLE:
                wheelsLeft.setTargetPosition(-left);
                wheelsRight.setTargetPosition(right);
                wheelMiddle.setTargetPosition(-middle);
                break;
            case BOTH_SIDES_MIDDLE:
                wheelsLeft.setTargetPosition(-left);
                wheelsRight.setTargetPosition(-right);
                wheelMiddle.setTargetPosition(-middle);
                break;
            default:
                wheelsLeft.setTargetPosition(left);
                wheelsRight.setTargetPosition(right);
                wheelMiddle.setTargetPosition(middle);
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
