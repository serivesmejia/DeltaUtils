/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltainput.event;

import com.deltarobotics9351.deltainput.gamepad.GamepadDataPacket;
import com.deltarobotics9351.deltainput.gamepad.button.Button;
import com.deltarobotics9351.deltainput.gamepad.button.Buttons;

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


    public Button A = Button.A;
    public Button B = Button.B;
    public Button X = Button.X;
    public Button Y = Button.Y;

    public Button DPAD_UP = Button.DPAD_UP;
    public Button DPAD_DOWN = Button.DPAD_DOWN;
    public Button DPAD_LEFT = Button.DPAD_LEFT;
    public Button DPAD_RIGHT = Button.DPAD_RIGHT;

    public Button LEFT_BUMPER = Button.LEFT_BUMPER;
    public Button RIGHT_BUMPER = Button.RIGHT_BUMPER;

    public Button LEFT_TRIGGER = Button.LEFT_TRIGGER;
    public Button RIGHT_TRIGGER = Button.RIGHT_TRIGGER;

    public Button LEFT_STICK_BUTTON = Button.LEFT_STICK_BUTTON;
    public Button RIGHT_STICK_BUTTON = Button.RIGHT_STICK_BUTTON;

    public Buttons.Type BUTTONS_BEING_PRESSED = Buttons.Type.BUTTONS_BEING_PRESSED;
    public Buttons.Type BUTTONS_PRESSED = Buttons.Type.BUTTONS_PRESSED;
    public Buttons.Type BUTTONS_RELEASED = Buttons.Type.BUTTONS_RELEASED;

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
            buttonsBeingPressed(new Buttons(gdp.buttonsBeingPressed, BUTTONS_BEING_PRESSED));
        }

        if(!gdp.buttonsPressed.isEmpty()){
            buttonsPressed(new Buttons(gdp.buttonsPressed, BUTTONS_PRESSED));
        }

        if(!gdp.buttonsReleased.isEmpty()){
            buttonsReleased(new Buttons(gdp.buttonsReleased, BUTTONS_RELEASED));
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
    public void buttonsPressed(Buttons buttons){}

    /**
     * Method to be executed ONCE when at least one button is released
     * @param buttons the pressed buttons
     */
    public void buttonsReleased(Buttons buttons){}


    /**
     * Method to be executed REPETITIVELY when at least one button is pressed until it is released
     * @param buttons the pressed buttons
     */
    public void buttonsBeingPressed(Buttons buttons){}

    /**
     * Method to be executed REPETITIVELY every time the SuperGamepad updates.
     * @param gdp the last GamepadDataPacket
     */
    public void loop(GamepadDataPacket gdp){ }

}
