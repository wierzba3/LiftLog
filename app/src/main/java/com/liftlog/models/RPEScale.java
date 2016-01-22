package com.liftlog.models;

/**
 * Created by root on 1/21/16.
 */
public enum RPEScale
{
    DEFAULT("Not specified", 0),
    TEN("10 - max effort", 10),
    NINE_PT_FIVE("9.5 - maybe 1 more rep", 9.5),
    NINE("9 - 1 more rep", 9),
    EIGHT_PT_FIVE("8.5 - maybe 2 more reps", 8.5),
    EIGHT("8 - 2 more reps", 8),
    SEVEN("7 - easy", 7),
    SIX("6 - warmup", 6),
    FIVE_PT_FIVE("5 - very easy", 5.5),
    ;

    RPEScale(String label, double value)
    {
        _label = label;
        _value = value;
    }

    private String _label;
    private double _value;

    public double getValue()
    {
        return _value;
    }

    @Override
    public String toString()
    {
        return _label;
    }
}
