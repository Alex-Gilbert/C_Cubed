package com.alex.C_Cubed;

/**
 * Created by Alex on 11/7/2014.
 */

import org.jgrapht.*;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;

public class UnderlyingGraph<V, E>
        extends AbstractBaseGraph<V, E>
        implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = 321321654987987645L;



    /**
     * Creates a new directed graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public UnderlyingGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<V, E>(edgeClass));
    }

    /**
     * Creates a new directed graph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public UnderlyingGraph(EdgeFactory<V, E> ef)
    {
        super(ef, true, true);
    }
}
