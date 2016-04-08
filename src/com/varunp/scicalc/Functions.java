package com.varunp.scicalc;

/**
 * Wrapper class for all non-standard mathematical functions
 */
public class Functions
{
    public static double factorial(int d) throws Exception
    {
        if(d < 0)
            throw new Exception("Cannot perform factorial operation on a negative number.");

        if(d == 0)
            return 1;

        return d * factorial(d - 1);
    }

    public static double sin(Double d)
    {
        return Math.sin(d);
    }

    public static double cos(Double d)
    {
        return Math.cos(d);
    }

    public static double tan(Double d)
    {
        return Math.tan(d);
    }

    public static double ln(Double d)
    {
        return Math.log(d);
    }

    public static double log(Double d)
    {
        return Math.log10(d);
    }

    public static double pow(Double base, double exp)
    {
        return Math.pow(base, exp);
    }

    public static double sqrt(Double d)
    {
        return Math.sqrt(d);
    }
}