/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.event.gamepad;

import com.deltarobotics9351.deltaevent.gamepad.GamepadDataPacket;
import com.deltarobotics9351.deltaevent.gamepad.button.Button;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.Map;

public class IntensiveSuperGamepadEvent extends GamepadEvent {

    private GamepadDataPacket gdp;

    @Override
    public final void performEvent(GamepadDataPacket gdp){

        this.gdp = gdp;

        for(Map.Entry<Button, Integer> entry : gdp.buttonsBeingPressed.entrySet()){
            buttonBeingPressed(entry.getKey(), entry.getValue());
        }

        for(Map.Entry<Button, Integer> entry : gdp.buttonsPressed.entrySet()){
            buttonPressed(entry.getKey(), entry.getValue());
        }

        for(Map.Entry<Button, Integer> entry : gdp.buttonsReleased.entrySet()){
            buttonReleased(entry.getKey(), entry.getValue());
        }

    }

    /**
     * Method to be executed ONCE when a button is pressed
     * @param button the pressed button
     */
    public void buttonPressed(Button button, int ticks){}

    /**
     * Method to be executed ONCE when a button is released
     * @param button the released button
     */
    public void buttonReleased(Button button, int ticks){}


    /**
     * Method to be executed REPETITIVELY when a button is pressed until it is released
     * @param button the being pressed button
     */
    public void buttonBeingPressed(Button button, int ticks){}

    /**
     * Method to run REPETITIVELY every time the SuperGamepad is updated
     * @param gdp the last GamepadDataPacket
     */
    public void loop(GamepadDataPacket gdp){}

}
