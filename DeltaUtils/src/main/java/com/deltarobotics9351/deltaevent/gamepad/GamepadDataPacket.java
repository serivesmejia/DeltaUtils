/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.gamepad;

import com.deltarobotics9351.deltaevent.gamepad.button.Button;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to contain all current gamepad data.
 */
public class GamepadDataPacket {

    public List<Button> buttonsBeingPressed = new ArrayList<>();
    public List<Button> buttonsReleased = new ArrayList<>();
    public List<Button> buttonsPressed = new ArrayList<>();

    public double left_stick_x = 0;
    public double left_stick_y = 0;

    public double right_stick_x = 0;
    public double right_stick_y = 0;

    public double left_trigger = 0;
    public double right_trigger = 0;

    public Gamepad gamepad;

}
