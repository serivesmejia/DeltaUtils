/*
 * Copyright (c) 2020 FTC Delta Robotics #9351 - Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.serivesmejia.deltautils.deltaevent.gamepad

import com.github.serivesmejia.deltautils.deltaevent.Super
import com.github.serivesmejia.deltautils.deltaevent.event.Event
import com.github.serivesmejia.deltautils.deltaevent.event.gamepad.SuperGamepadEvent
import com.github.serivesmejia.deltautils.deltaevent.gamepad.button.Button
import com.qualcomm.robotcore.hardware.Gamepad
import kotlin.collections.ArrayList

class SuperGamepad (private var gamepad: Gamepad) : Super {

    override var events: ArrayList<Event> = ArrayList<Event>()

    private val pressedButtons = ArrayList<Button>()

    private val ticksPressedButtons = ArrayList<ButtonTicks>()

    init {
        for (btt in Button.values()) {
            val bt = ButtonTicks()
            bt.button = btt
            ticksPressedButtons.add(bt)
        }
    }

    /**
     * Set a new gamepad to this SuperGamepad
     * @param gamepad the Gamepad to set to this SuperGamepad (From the FTC SDK)
     */
    fun setGamepad(gamepad: Gamepad) {
        this.gamepad = gamepad
    }

    /**
     * Register an event
     * @param event the SuperGamepadEvent to register
     * @return itself
     */
    override fun registerEvent(event: Event): SuperGamepad {
        events.add(event);
        return this
    }

    /**
     * Registers an event, the parameter is ignored
     * @param event the Event to register
     * @param parameter parameter ignored
     * @return itself
     */
    override fun registerEvent(event: Event, parameter: Int): SuperGamepad {
        registerEvent(event)
        return this
    }

    /**
     * Registers an event, the parameter is ignored
     * @param event the Event to register
     * @param parameter parameter ignored
     * @return itself
     */
    override fun registerEvent(event: Event, parameter: Double): SuperGamepad {
        registerEvent(event)
        return this
    }

    override fun registerEvent(event: Event, parameterBoolean1: Boolean): SuperGamepad {
        registerEvent(event)
        return this
    }

    override fun registerEvent(event: Event, parameterInt1: Int, parameterBoolean1: Boolean): SuperGamepad {
        registerEvent(event)
        return this
    }

    override fun registerEvent(event: Event, parameterDouble1: Double, parameterBoolean1: Boolean): SuperGamepad {
        registerEvent(event)
        return this
    }

    /**
     * Unregister all the events
     */
    override fun unregisterEvents() {
        events.clear()
    }

    /**
     * Update the pressed buttons and execute all the events.
     * This method should be placed at the end or at the start of your repeat in your OpMode
     */
    override fun update() {

        val gdp = GamepadDataPacket()

        pressedButtons.clear()
        updatePressedButtons()

        gdp.left_stick_x = gamepad!!.left_stick_x.toDouble()
        gdp.left_stick_y = gamepad!!.left_stick_y.toDouble()
        gdp.right_stick_x = gamepad!!.right_stick_x.toDouble()
        gdp.right_stick_y = gamepad!!.right_stick_y.toDouble()
        gdp.left_trigger = gamepad!!.left_trigger.toDouble()
        gdp.right_trigger = gamepad!!.right_trigger.toDouble()
        gdp.gamepad = gamepad

        for (btt in pressedButtons) {

            val bt = getElementFromTicksPressedButtons(btt)
            var ticks = bt!!.ticks

            ticks++
            bt.ticks++

            gdp.buttonsBeingPressed[btt] = bt.ticks

            if (ticks == 1) {
                gdp.buttonsPressed[btt] = bt.ticks
            }

        }

        for (bt in ticksPressedButtons) {

            if (bt.ticks <= 0) continue

            if (!buttonIsPressed(bt.button)) {
                bt.ticks = 0
                gdp.buttonsReleased[bt.button] = bt.ticks
            }

        }

        updateAllEvents(gdp)
    }

    private fun updateAllEvents(gdp: GamepadDataPacket) {
        for (evt in events) {
            require(evt is SuperGamepadEvent) { "Event is not a SuperGamepadEvent" }
            evt.execute(gdp)
        }
    }

    private fun updatePressedButtons() {

        if (gamepad!!.a) pressedButtons.add(Button.A)
        if (gamepad!!.b) pressedButtons.add(Button.B)
        if (gamepad!!.x) pressedButtons.add(Button.X)
        if (gamepad!!.y) pressedButtons.add(Button.Y)
        if (gamepad!!.dpad_up) pressedButtons.add(Button.DPAD_UP)
        if (gamepad!!.dpad_down) pressedButtons.add(Button.DPAD_DOWN)
        if (gamepad!!.dpad_left) pressedButtons.add(Button.DPAD_LEFT)
        if (gamepad!!.dpad_right) pressedButtons.add(Button.DPAD_RIGHT)
        if (gamepad!!.right_bumper) pressedButtons.add(Button.RIGHT_BUMPER)
        if (gamepad!!.left_bumper) pressedButtons.add(Button.LEFT_BUMPER)
        if (gamepad!!.left_stick_button) pressedButtons.add(Button.LEFT_STICK_BUTTON)
        if (gamepad!!.right_stick_button) pressedButtons.add(Button.RIGHT_STICK_BUTTON)
        if (gamepad!!.right_trigger > 0.1) pressedButtons.add(Button.RIGHT_TRIGGER)
        if (gamepad!!.left_trigger > 0.1) pressedButtons.add(Button.LEFT_TRIGGER)
    }

    private fun buttonIsPressed(btt: Button): Boolean {
        return pressedButtons.contains(btt)
    }

    private fun getElementFromTicksPressedButtons(element: Button): ButtonTicks? {
        for (bt in ticksPressedButtons) {
            if (bt.button == element) return bt
        }
        return null
    }

    private class ButtonTicks {
        var ticks = 0
        var button = Button.NONE
    }

}