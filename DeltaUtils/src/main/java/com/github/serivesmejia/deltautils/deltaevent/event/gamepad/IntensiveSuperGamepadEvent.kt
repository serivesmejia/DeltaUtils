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

package com.github.serivesmejia.deltautils.deltaevent.event.gamepad

import com.github.serivesmejia.deltautils.deltaevent.gamepad.GamepadDataPacket
import com.github.serivesmejia.deltautils.deltaevent.gamepad.button.Button

class IntensiveSuperGamepadEvent : GamepadEvent(){

    private var gdp: GamepadDataPacket = GamepadDataPacket()

    override fun performEvent(gdp: GamepadDataPacket) {
        this.gdp = gdp
        for ((key, value) in gdp.buttonsBeingPressed) {
            buttonBeingPressed(key, value)
        }
        for ((key, value) in gdp.buttonsPressed) {
            buttonPressed(key, value)
        }
        for ((key, value) in gdp.buttonsReleased) {
            buttonReleased(key, value)
        }
    }

    /**
     * Method to be executed ONCE when a button is pressed
     * @param button the pressed button
     */
    open fun buttonPressed(button: Button, ticks: Int) {}

    /**
     * Method to be executed ONCE when a button is released
     * @param button the released button
     */
    open fun buttonReleased(button: Button, ticks: Int) {}


    /**
     * Method to be executed REPETITIVELY when a button is pressed until it is released
     * @param button the being pressed button
     */
    open fun buttonBeingPressed(button: Button, ticks: Int) {}


}