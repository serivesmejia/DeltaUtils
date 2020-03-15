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

import com.deltarobotics9351.deltaevent.gamepad.GamepadDataPacket;
import com.deltarobotics9351.deltaevent.gamepad.button.Buttons;

public class SuperGamepadEvent extends GamepadEvent {

    @Override
    public final void performEvent(GamepadDataPacket gdp){

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

    /**
     * Method to be executed ONCE when at least one button is pressed
     * @param buttons the pressed buttons
     */
    public void buttonsPressed(Buttons buttons){}

    /**
     * Method to be executed ONCE when at least one button is released
     * @param buttons the released buttons
     */
    public void buttonsReleased(Buttons buttons){}


    /**
     * Method to be executed REPETITIVELY when at least one button is pressed until it is released
     * @param buttons the being pressed buttons
     */
    public void buttonsBeingPressed(Buttons buttons){}

}
