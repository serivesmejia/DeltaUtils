package com.github.deltarobotics9351.pid;

public class PIDControl {

        private double P;                     // factor for "proportional" control
        private double I;                     // factor for "integral" control
        private double D;                     // factor for "derivative" control
        private double input;                 // sensor input for pid controller
        private double maximumOutput = 1.0;    // |maximum output|
        private double minimumOutput = -1.0;    // |minimum output|
        private double maximumInput = 0.0;    // maximum input - limit setpoint to this
        private double minimumInput = 0.0;    // minimum input - limit setpoint to this
        private boolean continuous = false;    // do the endpoints wrap around? eg. Absolute encoder
        private boolean enabled = false;      // is the pid controller enabled
        private double prevError = 0.0;       // the prior sensor input (used to compute velocity)
        private double totalError = 0.0;      // the sum of the errors for use in the integral calc
        private double tolerance = 0.05;      // the percentage error that is considered on target
        private double setpoint = 0.0;
        private double error = 0.0;
        private double result = 0.0;

        /**
         * Allocate a PID object with the given constants for P, I, D
         *
         * @param pid Object containing the constants
         */
        public PIDControl(PIDConstants pid) {
            P = pid.P;
            I = pid.I;
            D = pid.D;
        }

        /**
         * Read the input, calculate the output accordingly, and write to the output.
         * This should only be called by the PIDTask
         * and is created during initialization.
         */
        private void doCalc() {
            int sign = 1;

            // If enabled then proceed into controller calculations
            if (enabled) {
                // Calculate the error signal
                error = setpoint - input;

                // If continuous is set to true allow wrap around
                if (continuous) {
                    if (Math.abs(error) > (maximumInput - minimumInput) / 2) {
                        if (error > 0)
                            error = error - maximumInput + minimumInput;
                        else
                            error = error + maximumInput - minimumInput;
                    }
                }

                // Integrate the errors as long as the upcoming integrator does
                // not exceed the minimum and maximum output thresholds.

                if ((Math.abs(totalError + error) * I < maximumOutput) &&
                        (Math.abs(totalError + error) * I > minimumOutput))
                    totalError += error;

                // Perform the primary PID calculation
                result = P * error + I * totalError + D * (error - prevError);

                // Set the current error to the previous error for the next cycle.
                prevError = error;

                if (result < 0) sign = -1;    // Record sign of result.

                // Make sure the final result is within bounds. If we constrain the result, we make
                // sure the sign of the constrained result matches the original result sign.
                if (Math.abs(result) > maximumOutput)
                    result = maximumOutput * sign;
                else if (Math.abs(result) < minimumOutput)
                    result = minimumOutput * sign;
            }
        }

        /**
         * Set the PID Controller gain parameters.
         * Set the proportional, integral, and differential coefficients.
         *
         * @param pid Object containing the constants
         */
        public void setPID(PIDConstants pid) {
            P = pid.P;
            I = pid.I;
            D = pid.D;
        }

        /**
         * Get the Proportional coefficient
         *
         * @return proportional coefficient
         */
        public double getP() {
            return P;
        }

        /**
         * Get the Integral coefficient
         *
         * @return integral coefficient
         */
        public double getI() {
            return I;
        }

        /**
         * Get the Differential coefficient
         *
         * @return differential coefficient
         */
        public double getD() {
            return D;
        }

        /**
         * Return the current PID result for the last input set with setInput().
         * This is always centered on zero and constrained the the max and min outs
         *
         * @return the latest calculated output
         */
        public double doPID() {
            doCalc();
            return result;
        }

        /**
         * Return the current PID result for the specified input.
         *
         * @param input The input value to be used to calculate the PID result.
         *              This is always centered on zero and constrained the the max and min outs
         * @return the latest calculated output
         */
        public double performPID(double input) {
            defineInput(input);
            return doPID();
        }

        /**
         * Set the PID controller to consider the input to be continuous,
         * Rather then using the max and min in as constraints, it considers them to
         * be the same point and automatically calculates the shortest route to
         * the setpoint.
         *
         * @param continuous Set to true turns on continuous, false turns off continuous
         */
        public void defineContinuous(boolean continuous) {
            continuous = continuous;
        }

        /**
         * Define the PID controller to consider the input to be continuous,
         * Rather then using the max and min in as constraints, it considers them to
         * be the same point and automatically calculates the shortest route to
         * the setpoint.
         */
        public void defineContinuous() {
            this.defineContinuous(true);
        }

        /**
         * Sets the maximum and minimum values expected from the input.
         *
         * @param minimumInput the minimum value expected from the input, always positive
         * @param maximumInput the maximum value expected from the output, always positive
         */
        public void defineInputRange(double minimumInput, double maximumInput) {
            minimumInput = Math.abs(minimumInput);
            maximumInput = Math.abs(maximumInput);
            defineSetpoint(setpoint);
        }

        /**
         * Defines the minimum and maximum values to write.
         *
         * @param minimumOutput the minimum value to write to the output, always positive
         * @param maximumOutput the maximum value to write to the output, always positive
         */
        public void defineOutputRange(double minimumOutput, double maximumOutput) {
            minimumOutput = Math.abs(minimumOutput);
            maximumOutput = Math.abs(maximumOutput);
        }

        /**
         * Define the setpoint for the PIDControl
         *
         * @param setpoint the desired setpoint
         */
        public void defineSetpoint(double setpoint) {
            int sign = 1;

            if (maximumInput > minimumInput) {
                if (setpoint < 0) sign = -1;

                if (Math.abs(setpoint) > maximumInput)
                    setpoint = maximumInput * sign;
                else if (Math.abs(setpoint) < minimumInput)
                    setpoint = minimumInput * sign;
                else
                    setpoint = setpoint;
            } else
                setpoint = setpoint;
        }

        /**
         * Returns the current setpoint of the PIDControl
         *
         * @return the current setpoint
         */
        public double getSetpoint() {
            return setpoint;
        }

        /**
         * Retruns the current difference of the input from the setpoint
         *
         * @return the current error
         */
        public synchronized double getError() {
            return error;
        }

        /**
         * Set the percentage error which is considered tolerable for use with
         * OnTarget. (Input of 15.0 = 15 percent)
         *
         * @param percent error which is tolerable
         */
        public void setTolerance(double percent) {
            tolerance = percent;
        }

        /**
         * Return true if the error is within the percentage of the total input range,
         * determined by setTolerance. This assumes that the maximum and minimum input
         * were set using setInputRange.
         *
         * @return true if the error is less than the tolerance
         */
        public boolean onTarget() {
            return (Math.abs(error) < Math.abs(tolerance / 100.0 * (maximumInput - minimumInput)));
        }

        /**
         * Begin running the PIDControl
         */
        public void enable() {
            enabled = true;
        }

        /**
         * Stop running the PIDControl.
         */
        public void disable() {
            enabled = false;
        }

        /**
         * Reset the previous error,, the integral term, and disable the controller.
         */
        public void reset() {
            disable();
            prevError = 0;
            totalError = 0;
            result = 0;
        }

        /**
         * Defines the input value to be used by the next call to performPID().
         *
         * @param input Input value to the PID calculation.
         */
        public void defineInput(double input) {
            int sign = 1;

            if (maximumInput > minimumInput) {
                if (input < 0) sign = -1;

                if (Math.abs(input) > maximumInput)
                    input = maximumInput * sign;
                else if (Math.abs(input) < minimumInput)
                    input = minimumInput * sign;
                else
                    input = input;
            } else
                input = input;
        }
}
