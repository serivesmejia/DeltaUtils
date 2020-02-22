/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltainput.event;

import com.deltarobotics9351.deltainput.gamepad.GamepadDataPacket;
import com.deltarobotics9351.deltainput.gamepad.button.Buttons;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GamepadEvent extends Event {

    public double left_stick_x = 0;
    public double left_stick_y = 0;

    public double right_stick_x = 0;
    public double right_stick_y = 0;

    public double left_trigger = 0;
    public double right_trigger = 0;

    @Override
    public void execute(Object arg1, Object arg2) {
        execute(arg1);
    }

    @Override
    public final void execute(Object arg1){
        if(!(arg1 instanceof GamepadDataPacket)) throw new IllegalArgumentException("Object is not a GamepadDataPacket");
        GamepadDataPacket gdp = (GamepadDataPacket)arg1;

        left_stick_x = gdp.left_stick_x;
        left_stick_y = gdp.left_stick_y;

        right_stick_x = gdp.right_stick_x;
        right_stick_y = gdp.right_stick_y;

        left_trigger = gdp.left_trigger;
        right_trigger = gdp.right_trigger;

        loop(gdp);

        if(!gdp.buttonsBeingPressed.isEmpty()){
            buttonBeingPressed(new Buttons(gdp.buttonsBeingPressed, Buttons.Type.BUTTONS_BEING_PRESSED));
        }

        if(!gdp.buttonsPressed.isEmpty()){
            buttonBeingPressed(new Buttons(gdp.buttonsPressed, Buttons.Type.BUTTONS_PRESSED));
        }

        if(!gdp.buttonsReleased.isEmpty()){
            buttonBeingPressed(new Buttons(gdp.buttonsReleased, Buttons.Type.BUTTONS_RELEASED));
        }
    }

    @Override
    public final void execute(ArrayList<Object> args){
        for(Object obj : args){
            execute(obj);
        }
    }

    @Override
    public final void execute(HashMap<Object, Object> args){
        for(Map.Entry<Object, Object> entry : args.entrySet()){
            execute(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Method to be executed ONCE when at least one button is pressed
     * @param buttons the pressed buttons
     */
    public void buttonPressed(Buttons buttons){}

    /**
     * Method to be executed ONCE when at least one button is released
     * @param buttons the pressed buttons
     */
    public void buttonReleased(Buttons buttons){}


    /**
     * Method to be executed REPETITIVELY when at least one button is pressed until it is released
     * @param buttons the pressed buttons
     */
    public void buttonBeingPressed(Buttons buttons){}

    /**
     * Method to be executed REPETITIVELY every time the SuperGamepad updates.
     * @param gdp the last GamepadDataPacket
     */
    public void loop(GamepadDataPacket gdp){ }

}
