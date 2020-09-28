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

package com.github.serivesmejia.deltautils.deltaevent

import com.github.serivesmejia.deltautils.deltaevent.event.Event

interface Super {

    var events : ArrayList<Event>

    /**
     * Register an event
     * @param event the Event to register
     */
    fun registerEvent(event: Event): Super

    /**
     * Register an event with an int parameter
     * @param event the Event to register
     */
    fun registerEvent(event: Event, parameterInt1: Int): Super

    /**
     * Register an event with a double parameter
     * @param event the Event to register
     */
    fun registerEvent(event: Event, parameterDouble1: Double): Super

    /**
     * Register an event with a boolean parameter
     * @param event The event to register
     * @return itself
     */
    fun registerEvent(event: Event, parameterBoolean1: Boolean): Super

    /**
     * Register an event with int & boolean parameters
     * @param event the Event to register
     */
    fun registerEvent(event: Event, parameterInt1: Int, parameterBoolean1: Boolean): Super

    /**
     * Register an event with double & boolean parameters
     * @param event the Event to register
     */
    fun registerEvent(event: Event, parameterDouble1: Double, parameterBoolean1: Boolean): Super

    /**
     * Unregister all the events
     */
    fun unregisterEvents()

    /**
     * Update the pressed buttons and execute all the events.
     * This method should be placed at the end or at the start of your repeat in your OpMode
     */
    fun update()

}