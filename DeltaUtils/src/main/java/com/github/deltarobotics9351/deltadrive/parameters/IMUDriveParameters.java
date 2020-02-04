package com.github.deltarobotics9351.deltadrive.parameters;

import com.qualcomm.robotcore.util.Range;

public class IMUDriveParameters {

    public double STRAFING_COUNTERACT_CONSTANT = 0;
    public int ROTATE_MAX_CORRECTION_TIMES = 0;
    public double ROTATE_CORRECTION_POWER = 0;


    public IMUDriveParameters(){ }

    public void secureParameters(){
        ROTATE_MAX_CORRECTION_TIMES = Math.abs(ROTATE_MAX_CORRECTION_TIMES);
        ROTATE_CORRECTION_POWER = Range.clip(Math.abs(ROTATE_CORRECTION_POWER), 0, 1);
        STRAFING_COUNTERACT_CONSTANT = Range.clip(Math.abs(STRAFING_COUNTERACT_CONSTANT), 0, 1);
    }

    public boolean haveBeenDefined(){
        if(ROTATE_MAX_CORRECTION_TIMES == 0 || ROTATE_CORRECTION_POWER == 0){
            return false;
        }
        return true;
    }

}
