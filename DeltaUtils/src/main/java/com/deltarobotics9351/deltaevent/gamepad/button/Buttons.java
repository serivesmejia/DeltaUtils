/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.gamepad.button;

import java.util.ArrayList;
import java.util.List;

public class Buttons {

    private List<Button> buttons = new ArrayList<>();
    private Type type;

    public enum Type{
        BUTTONS_PRESSED, BUTTONS_RELEASED, BUTTONS_BEING_PRESSED
    }

    public Buttons(List<Button> buttons, Type type){
        this.buttons = buttons;
        this.type = type;
    }

    public boolean is(Button btt){
        return buttons.contains(btt);
    }

    public Type type(){
        return type;
    }

}
