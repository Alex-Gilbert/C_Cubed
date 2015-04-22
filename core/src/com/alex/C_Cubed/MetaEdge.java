package com.alex.C_Cubed;

/**
 * Created by Alex on 11/7/2014.
 */
public class MetaEdge
{
    String name;
    boolean direction;

    public MetaEdge(String Name, boolean Direction)
    {
        name = Name;
        direction = Direction;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String value){name = value;}

    public boolean getDirection()
    {
        return direction;
    }
    public void setDirection(boolean value){direction = value;}

    public boolean equals(MetaEdge me)
    {
        return (name.equals(getName()) && (direction == me.getDirection()));
    }
}
