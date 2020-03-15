package com.deltarobotics9351.deltamath;

/**
 * Class with WPILib FRC Lib math methods and extra custom ones.
 */
public class MathUtil {

    /**
     * Returns value clamped between low and high boundaries.
     *
     * @param value Value to clamp.
     * @param low   The lower boundary to which to clamp value.
     * @param high  The higher boundary to which to clamp value.
     */
    public static int clamp(int value, int low, int high) {
        return Math.max(low, Math.min(value, high));
    }

    /**
     * Returns value clamped between low and high boundaries.
     *
     * @param value Value to clamp.
     * @param low   The lower boundary to which to clamp value.
     * @param high  The higher boundary to which to clamp value.
     */
    public static double clamp(double value, double low, double high) {
        return Math.max(low, Math.min(value, high));
    }

    /**
     * Get the difference (delta) between two angles (in degrees)
     * @param angle1 The angle to be subtracted by the angle2
     * @param angle2 The angle to be subtracted to angle1
     * @return The result of the angles difference, considering the -360 to 360 range.
     */
    public static double deltaDegrees(double angle1, double angle2){
        double deltaAngle = angle1 - angle2;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        return deltaAngle;
    }

}
