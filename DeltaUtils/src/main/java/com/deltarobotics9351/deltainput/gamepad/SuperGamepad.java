/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */
package com.deltarobotics9351.deltainput.gamepad;

import com.deltarobotics9351.deltainput.gamepad.button.Button;
import com.deltarobotics9351.deltainput.event.Event;
import com.deltarobotics9351.deltainput.event.GamepadEvent;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Improved gamepad class in which you can use "GamepadEvent", needs to update() inside your OpMode loop.
 */
public class SuperGamepad {

    private Gamepad gamepad;

    private ArrayList<GamepadEvent> events = new ArrayList<GamepadEvent>();

    private ArrayList<Button> pressedButtons = new ArrayList<Button>();

    private HashMap<Button, Integer> ticksPressedButtons = new HashMap<Button, Integer>();

    /**
     * Constructor for SuperGamepad
     * @param gamepad
     */
    public SuperGamepad(Gamepad gamepad){
        this.gamepad = gamepad;

        for(Button btt : Button.values()){
            ticksPressedButtons.put(btt, -1);
        }
    }

    /**
     * Set a new gamepad to this SuperGamepad
     * @param gamepad the Gamepad to set to this SuperGamepad (From the FTC SDK)
     */
    public void setGamepad(Gamepad gamepad){ this.gamepad = gamepad; }

    /**
     * Register an event
     * @param event the GamepadEvent to register
     */
    public SuperGamepad registerEvent(GamepadEvent event){
        events.add(event);
        return this;
    }

    /**
     * Unregister all the events
     */
    public void unregisterEvents(){
        events.clear();
    }

    /**
     * Update the pressed buttons and execute all the events.
     * This method should be placed at the end or at the start of your loop in your OpMode
     */
    public void update() {

        GamepadDataPacket gdp = new GamepadDataPacket();

        pressedButtons.clear();

        updatePressedButtons();

        gdp.left_stick_x = gamepad.left_stick_x;
        gdp.left_stick_y = gamepad.left_stick_y;

        gdp.right_stick_x = gamepad.right_stick_x;
        gdp.right_stick_y = gamepad.right_stick_y;

        gdp.left_trigger = gamepad.left_trigger;
        gdp.right_trigger = gamepad.right_trigger;

        gdp.gamepad = gamepad;

        for(Button btt : pressedButtons){
            int ticks = ticksPressedButtons.get(btt);

            ticksPressedButtons.remove(btt);
            ticks++;
            ticksPressedButtons.put(btt, ticks);

            gdp.buttonsBeingPressed.add(btt);

            if(ticks == 0){
                gdp.buttonsPressed.add(btt);
            }
        }

        for (Map.Entry<Button, Integer> entry : ticksPressedButtons.entrySet()) {
            Button btt = entry.getKey();
            int ticks = entry.getValue();

            boolean continueThisIteration = true;

            if(ticks < 0){ continueThisIteration = false; }

            if(continueThisIteration){
                if(!buttonIsPressed(btt)){
                    ticksPressedButtons.remove(btt);
                    ticksPressedButtons.put(btt, -1);

                    gdp.buttonsReleased.add(btt);
                }
            }
        }

        updateAllEvents(gdp);

    }

    private void updateAllEvents(GamepadDataPacket gdp){
        for(Event evt : events){
            if(!(evt instanceof GamepadEvent)) throw new IllegalArgumentException("Event is not a GamepadEvent");

            evt.execute(gdp);
        }
    }

    private void updatePressedButtons(){

        if(gamepad.a) pressedButtons.add(Button.A);

        if(gamepad.b) pressedButtons.add(Button.B);

        if(gamepad.x) pressedButtons.add(Button.X);

        if(gamepad.y) pressedButtons.add(Button.Y);

        if(gamepad.dpad_up) pressedButtons.add(Button.DPAD_UP);

        if(gamepad.dpad_down) pressedButtons.add(Button.DPAD_DOWN);

        if(gamepad.dpad_left) pressedButtons.add(Button.DPAD_LEFT);

        if(gamepad.dpad_right) pressedButtons.add(Button.DPAD_RIGHT);

        if(gamepad.right_bumper) pressedButtons.add(Button.RIGHT_BUMPER);

        if(gamepad.left_bumper) pressedButtons.add(Button.LEFT_BUMPER);

        if(gamepad.left_stick_button) pressedButtons.add(Button.LEFT_STICK_BUTTON);

        if(gamepad.right_stick_button) pressedButtons.add(Button.RIGHT_STICK_BUTTON);

        if(gamepad.right_trigger > 0.1) pressedButtons.add(Button.RIGHT_TRIGGER);

        if(gamepad.left_trigger > 0.1) pressedButtons.add(Button.LEFT_TRIGGER);

    }

    private boolean buttonIsPressed(Button btt){
        return pressedButtons.contains(btt);
    }

}
