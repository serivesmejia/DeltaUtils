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
import com.deltarobotics9351.deltaevent.gamepad.button.Button;

public class IntensiveSuperGamepadEvent extends GamepadEvent {

    @Override
    public final void performEvent(GamepadDataPacket gdp){

        for(Button btt : gdp.buttonsBeingPressed){
            buttonBeingPressed(btt);
        }

        for(Button btt : gdp.buttonsPressed){
            buttonPressed(btt);
        }

        for(Button btt : gdp.buttonsReleased){
            buttonReleased(btt);
        }

    }

    /**
     * Method to be executed ONCE when a button is pressed
     * @param button the pressed button
     */
    public void buttonPressed(Button button){}

    /**
     * Method to be executed ONCE when a button is released
     * @param button the released button
     */
    public void buttonReleased(Button button){}


    /**
     * Method to be executed REPETITIVELY when a button is pressed until it is released
     * @param button the being pressed button
     */
    public void buttonBeingPressed(Button button){}

}
