package com.alex.C_Cubed;

import org.javatuples.Pair;

/**
 * Created by Alex on 11/7/2014.
 */
public class Square
{
    String Name;

    MetaEdge[] edges = new MetaEdge[4];

    public Square(String name, String Edge1, Boolean Direction1,String Edge2, Boolean Direction2,
                  String Edge3, Boolean Direction3,String Edge4, Boolean Direction4)
    {
        Name = name;

        edges[0] = new MetaEdge(Edge1, Direction1);
        edges[1] = new MetaEdge(Edge2, Direction2);
        edges[2] = new MetaEdge(Edge3, Direction3);
        edges[3] = new MetaEdge(Edge4, Direction4);
    }

    public String getName()
    {
        return Name;
    }
    public void setName(String value)
    {
        Name = value;
    }

    public MetaEdge getMetaEdge1()
    {
        return edges[0];
    }

    public MetaEdge getMetaEdge2()
    {
        return edges[1];
    }

    public MetaEdge getMetaEdge3()
    {
        return edges[2];
    }

    public MetaEdge getMetaEdge4()
    {
        return edges[3];
    }

    public String getEdge1()
    {
        return edges[0].getName();
    }

    public String getEdge2()
    {
        return edges[1].getName();
    }

    public String getEdge3()
    {
        return edges[2].getName();
    }

    public String getEdge4()
    {
        return edges[3].getName();
    }

    public boolean getDirection1()
    {
        return edges[0].getDirection();
    }

    public boolean getDirection2()
    {
        return edges[1].getDirection();
    }

    public boolean getDirection3()
    {
        return edges[2].getDirection();
    }

    public boolean getDirection4()
    {
        return edges[3].getDirection();
    }

    public String getEdge(int Edge)
    {
        return edges[Edge].getName();
    }

    public boolean getDirection(int Edge)
    {
        return edges[Edge].getDirection();
    }

    public void setEdge(int Edge, String value)
    {
        edges[Edge].setName(value);
    }

    public void setDirection(int Edge, boolean value)
    {
        edges[Edge].setDirection(value);
    }

    public Square copy()
    {
        return new Square(getName(), getEdge1(), getDirection1(), getEdge2(), getDirection2(), getEdge3(), getDirection3(), getEdge4(), getDirection4());
    }
}
