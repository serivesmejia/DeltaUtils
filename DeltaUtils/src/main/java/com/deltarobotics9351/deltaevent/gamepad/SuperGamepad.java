/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */
package com.deltarobotics9351.deltaevent.gamepad;

import com.deltarobotics9351.deltaevent.Super;
import com.deltarobotics9351.deltaevent.event.gamepad.GamepadEvent;
import com.deltarobotics9351.deltaevent.event.gamepad.SuperGamepadEvent;
import com.deltarobotics9351.deltaevent.gamepad.button.Button;
import com.deltarobotics9351.deltaevent.event.Event;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;

/**
 * Improved gamepad class in which you can use "SuperGamepadEvent", needs to update() inside your OpMode repeat.
 */
public class SuperGamepad implements Super {

    private Gamepad gamepad;

    private ArrayList<Button> pressedButtons = new ArrayList<Button>();

    private ArrayList<ButtonTicks> ticksPressedButtons = new ArrayList<ButtonTicks>();

    /**
     * Constructor for SuperGamepad
     * @param gamepad
     */
    public SuperGamepad(Gamepad gamepad){
        this.gamepad = gamepad;

        for(Button btt : Button.values()){
            ButtonTicks bt = new ButtonTicks();
            bt.button = btt;
            ticksPressedButtons.add(bt);
        }
    }

    /**
     * Set a new gamepad to this SuperGamepad
     * @param gamepad the Gamepad to set to this SuperGamepad (From the FTC SDK)
     */
    public void setGamepad(Gamepad gamepad){ this.gamepad = gamepad; }

    /**
     * Register an event
     * @param event the SuperGamepadEvent to register
     * @return itself
     */
    @Override
    public SuperGamepad registerEvent(Event event){
        registerEvent(event);
        return this;
    }

    /**
     * Registers an event, the parameter is ignored
     * @param event the Event to register
     * @param parameter parameter ignored
     * @return itself
     */
    @Override
    public SuperGamepad registerEvent(Event event, int parameter) {
        registerEvent(event);
        return this;
    }

    /**
     * Registers an event, the parameter is ignored
     * @param event the Event to register
     * @param parameter parameter ignored
     * @return itself
     */
    @Override
    public SuperGamepad registerEvent(Event event, double parameter) {
        registerEvent(event);
        return this;
    }

    @Override
    public SuperGamepad registerEvent(Event event, boolean parameterBoolean1) {
        registerEvent(event);
        return this;
    }

    @Override
    public SuperGamepad registerEvent(Event event, int parameterInt1, boolean parameterBoolean1) {
        registerEvent(event);
        return this;
    }

    @Override
    public SuperGamepad registerEvent(Event event, double parameterDouble1, boolean parameterBoolean1) {
        registerEvent(event);
        return this;
    }

    /**
     * Unregister all the events
     */
    @Override
    public void unregisterEvents(){
        events.clear();
    }

    /**
     * Update the pressed buttons and execute all the events.
     * This method should be placed at the end or at the start of your repeat in your OpMode
     */
    @Override
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
            ButtonTicks bt = getElementFromTicksPressedButtons(btt);

            int ticks = bt.ticks;

            ticks++;

            bt.ticks++;

            gdp.buttonsBeingPressed.put(btt, bt.ticks);

            if(ticks == 1){
                gdp.buttonsPressed.put(btt, bt.ticks);
            }
        }

        for (ButtonTicks bt : ticksPressedButtons) {

            if(bt.ticks <= 0){ continue; }

            if(!buttonIsPressed(bt.button)){
                bt.ticks = 0;
                gdp.buttonsReleased.put(bt.button, bt.ticks);
            }
        }

        updateAllEvents(gdp);

    }

    private void updateAllEvents(GamepadDataPacket gdp){
        for(Event evt : events){
            if(!(evt instanceof SuperGamepadEvent)) throw new IllegalArgumentException("Event is not a SuperGamepadEvent");
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

    private ButtonTicks getElementFromTicksPressedButtons(Button element){
        for(ButtonTicks bt : ticksPressedButtons){
            if(bt.button == element) return bt;
        }
        return null;
    }

    private class ButtonTicks{

        public int ticks = 0;
        public Button button = Button.NONE;

        @Override
        public boolean equals(Object other){
            if(other instanceof ButtonTicks) return false;

            ButtonTicks bt = (ButtonTicks)other;

            if(bt.button == bt.button) return true;

            return false;
        }

    }

}
