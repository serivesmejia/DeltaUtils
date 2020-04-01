/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.event.gamepad;

import com.deltarobotics9351.deltaevent.event.Event;
import com.deltarobotics9351.deltaevent.gamepad.GamepadDataPacket;
import com.deltarobotics9351.deltaevent.gamepad.button.Button;
import com.deltarobotics9351.deltaevent.gamepad.button.Buttons;

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

    public final Button A = Button.A;
    public final Button B = Button.B;
    public final Button X = Button.X;
    public final Button Y = Button.Y;

    public final Button DPAD_UP = Button.DPAD_UP;
    public final Button DPAD_DOWN = Button.DPAD_DOWN;
    public final Button DPAD_LEFT = Button.DPAD_LEFT;
    public final Button DPAD_RIGHT = Button.DPAD_RIGHT;

    public final Button LEFT_BUMPER = Button.LEFT_BUMPER;
    public final Button RIGHT_BUMPER = Button.RIGHT_BUMPER;

    public final Button LEFT_TRIGGER = Button.LEFT_TRIGGER;
    public final Button RIGHT_TRIGGER = Button.RIGHT_TRIGGER;

    public final Button LEFT_STICK_BUTTON = Button.LEFT_STICK_BUTTON;
    public final Button RIGHT_STICK_BUTTON = Button.RIGHT_STICK_BUTTON;

    public final Buttons.Type BUTTONS_BEING_PRESSED = Buttons.Type.BUTTONS_BEING_PRESSED;
    public final Buttons.Type BUTTONS_PRESSED = Buttons.Type.BUTTONS_PRESSED;
    public final Buttons.Type BUTTONS_RELEASED = Buttons.Type.BUTTONS_RELEASED;

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

        performEvent(gdp);
    }

    public void performEvent(GamepadDataPacket gdp){ }

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
     * Method to be executed REPETITIVELY every time the SuperGamepad updates.
     * @param gdp the last GamepadDataPacket
     */
    public void loop(GamepadDataPacket gdp){ }

}
