/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.opmodes.holonomic;

import com.deltarobotics9351.deltadrive.extendable.linearopmodes.holonomic.IMUPIDEncoderHolonomicLinearOpMode;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class IMUPIDEncoderHolonomicOpMode extends IMUPIDEncoderHolonomicLinearOpMode {

    @Override
    public final void _runOpMode(){
        _init();

        while(!isStarted() && opModeIsActive()){ _init_loop(); }

        if(!isStarted()) return;

        _run();

        while(opModeIsActive()){ _run_loop(); }

        _stop();
    }

    /**
     * Code to be run ONCE when (init) is pressed
     */
    public void _init(){ }

    /**
     * Code to be run REPETITIVELY until play (>) is pressed
     */
    public void _init_loop(){ }

    /**
     * Code to be run ONCE when play (>) is pressed
     */
    public void _run(){ }

    /**
     * Code to be run REPETITIVELY until stop ([]) is pressed
     */
    public void _run_loop(){ }

    /**
     * Code to be run ONCE when stop ([]) is pressed
     */
    public void _stop(){ }

}