package com.alex.C_Cubed;
/**
 * Created by Alex on 11/7/2014.
 */
import com.badlogic.gdx.utils.Array;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.alg.cycle.PatonCycleBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CubeComplex
{
    UnderlyingGraph<Integer, String> G;
    ArrayList<Square> Squares;

    public CubeComplex(UnderlyingGraph<Integer, String> graph, ArrayList<Square> squares)
    {
        G = graph;
        Squares = squares;
    }

    public String PrintGroupPresentation()
    {
        KruskalMinimumSpanningTree spanningTree = new KruskalMinimumSpanningTree(G);
        Set<String> edgesInSTree = spanningTree.getMinimumSpanningTreeEdgeSet();

        String groupPresentation = "<";

        int numberOfGenerators = 0;

        //Concat Generators
        for(String s : G.edgeSet())
        {
            if(!edgesInSTree.contains(s))
            {
                ++numberOfGenerators;
                groupPresentation += s + ",";
            }
        }

        if(numberOfGenerators == 0)
            return "<|>";



        groupPresentation = groupPresentation.substring(0, groupPresentation.length()-1);

        if(Squares.size() == 0)
            return groupPresentation.concat(">");

        //Seperator
        groupPresentation += "|";

        //Relations
        for(Square s : Squares)
        {
            String relation = "";
            if(!edgesInSTree.contains(s.getEdge1()))
            {
                relation += s.getEdge1();
                if(!s.getDirection1())
                    relation += "^";
            }
            if(!edgesInSTree.contains(s.getEdge2()))
            {
                relation += s.getEdge2();
                if(!s.getDirection2())
                    relation += "^";
            }
            if(!edgesInSTree.contains(s.getEdge3()))
            {
                relation += s.getEdge3();
                if(!s.getDirection3())
                    relation += "^";
            }
            if(!edgesInSTree.contains(s.getEdge4()))
            {
                relation += s.getEdge4();
                if(!s.getDirection4())
                    relation += "^";
            }
            if(!relation.equals(""))
                relation += ", ";
            groupPresentation = groupPresentation.concat(relation);
        }


        groupPresentation = groupPresentation.substring(0, groupPresentation.length() - 2);
        groupPresentation += ">";

        System.out.println(groupPresentation);

        return groupPresentation;
    }

    public boolean NPC()
    {
        boolean npc = true;
        int i = 0;
        for(Integer v : G.vertexSet())
        {
            Pseudograph<String, Integer> linkGraph = new Pseudograph<String, Integer>(Integer.class);
            for(String e : G.incomingEdgesOf(v))
            {
                linkGraph.addVertex(e);
            }
            for(String e : G.outgoingEdgesOf(v))
            {
                linkGraph.addVertex(e + "^");
            }

            for(Square s : Squares)
            {
                if(linkGraph.vertexSet().contains(s.getEdge1()))
                {
                    if(s.getDirection1())
                    {
                        if(s.getDirection2())
                        {
                            //Does this add a double edge?
                            if(linkGraph.containsEdge(s.getEdge1(), s.getEdge2() + "^"))
                                npc = false;
                            linkGraph.addEdge(s.getEdge1(), s.getEdge2() + "^", i++);
                        }
                        else
                        {
                            if(linkGraph.containsEdge(s.getEdge1(), s.getEdge2()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge1(), s.getEdge2(), i++);
                        }
                    }
                    else
                    {
                        if(s.getDirection4())
                        {
                            if(linkGraph.containsEdge(s.getEdge1(), s.getEdge4()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge1(), s.getEdge4(), i++);
                        }
                        else
                        {
                            if(linkGraph.containsEdge(s.getEdge1(), s.getEdge4() + "^"))
                                npc = false;
                            linkGraph.addEdge(s.getEdge1(), s.getEdge4() + "^", i++);
                        }
                    }
                }
                if(linkGraph.vertexSet().contains(s.getEdge1() + "^"))
                {
                    if(s.getDirection1())
                    {
                        if(s.getDirection4())
                        {
                            if(linkGraph.containsEdge(s.getEdge1() + "^", s.getEdge4()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge1() + "^", s.getEdge4(), i++);
                        }
                        else
                        {
                            if(linkGraph.containsEdge(s.getEdge1() + "^", s.getEdge4() + "^"))
                                npc = false;
                            linkGraph.addEdge(s.getEdge1() + "^", s.getEdge4() + "^", i++);
                        }
                    }
                    else
                    {
                        if(s.getDirection2())
                        {
                            if(linkGraph.containsEdge(s.getEdge1() + "^", s.getEdge2()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge1() + "^", s.getEdge2() + "^", i++);
                        }
                        else
                        {
                            if(linkGraph.containsEdge(s.getEdge1() + "^", s.getEdge2()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge1() + "^", s.getEdge2(), i++);
                        }
                    }
                }
                if(linkGraph.vertexSet().contains(s.getEdge3()))
                {
                    if(s.getDirection3())
                    {
                        if(s.getDirection4())
                        {
                            if(linkGraph.containsEdge(s.getEdge3(), s.getEdge4() + "^"))
                                npc = false;
                            linkGraph.addEdge(s.getEdge3(), s.getEdge4() + "^", i++);
                        }
                        else
                        {
                            if(linkGraph.containsEdge(s.getEdge3(), s.getEdge4()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge3(), s.getEdge4(), i++);
                        }
                    }
                    else
                    {
                        if(s.getDirection2())
                        {
                            if(linkGraph.containsEdge(s.getEdge3(), s.getEdge2()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge3(), s.getEdge2(), i++);
                        }
                        else
                        {
                            if(linkGraph.containsEdge(s.getEdge3(), s.getEdge2() + "^"))
                                npc = false;
                            linkGraph.addEdge(s.getEdge3(), s.getEdge2() + "^", i++);
                        }
                    }
                }
                if(linkGraph.vertexSet().contains(s.getEdge3() + "^"))
                {
                    if(s.getDirection3())
                    {
                        if(s.getDirection2())
                        {
                            if(linkGraph.containsEdge(s.getEdge3() + "^", s.getEdge2()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge3() + "^", s.getEdge2(), i++);
                        }
                        else
                        {
                            if(linkGraph.containsEdge(s.getEdge3() + "^", s.getEdge2() + "^"))
                                npc = false;
                            linkGraph.addEdge(s.getEdge3() + "^", s.getEdge2() + "^", i++);
                        }
                    }
                    else
                    {
                        if(s.getDirection4())
                        {
                            if(linkGraph.containsEdge(s.getEdge3() + "^", s.getEdge4() + "^"))
                                npc = false;
                            linkGraph.addEdge(s.getEdge3() + "^", s.getEdge4() + "^", i++);
                        }
                        else
                        {
                            if(linkGraph.containsEdge(s.getEdge3() + "^", s.getEdge4()))
                                npc = false;
                            linkGraph.addEdge(s.getEdge3() + "^", s.getEdge4(), i++);
                        }
                    }
                }
            }
            if(npc)
                npc = graphTriangleFree(linkGraph);
            if(npc)
                npc = graphSquareFree(linkGraph);

            System.out.println(linkGraph.toString());
        }

        return npc;
    }

    private boolean graphTriangleFree(Pseudograph<String, Integer> linkGraph)
    {
        NeighborIndex neighborIndex = new NeighborIndex(linkGraph);

        for(String s : linkGraph.vertexSet())
        {
            List<String> sNei = neighborIndex.neighborListOf(s);
            for(String t : sNei)
            {
                List<String> vNei = neighborIndex.neighborListOf(t);
                for(String v2 : vNei)
                {
                    if(sNei.contains(v2))
                        return false;
                }
            }
        }
        return true;
    }

    private boolean graphSquareFree(Pseudograph<String, Integer> linkGraph)
    {
        NeighborIndex neighborIndex = new NeighborIndex(linkGraph);

        for(String s : linkGraph.vertexSet())
        {
            List<String> sNei = neighborIndex.neighborListOf(s);
            for(String t : sNei)
            {
                List<String> tNei = neighborIndex.neighborListOf(t);
                tNei.remove(s);

                for(String vertex : tNei)
                {
                    List<String> vNei = neighborIndex.neighborListOf(vertex);
                    vNei.remove(t);
                    if(vNei.contains(s))
                        return false;
                }
            }

        }
        return true;
    }

    public boolean Special()
    {
        ArrayList<ArrayList<String>> hyperplanes = new ArrayList<ArrayList<String>>();

        //We first create two classes for each edge in our cube complex
        for(String s:G.edgeSet())
        {
            ArrayList<String> toAdd = new ArrayList<String>();
            toAdd.add(s);
            ArrayList<String> toAddReversed = new ArrayList<String>();
            toAddReversed.add(s + "^");

            hyperplanes.add(toAdd);
            hyperplanes.add(toAddReversed);
        }

        //for each square combine 4 hyperplane classes
        for(Square s:Squares)
        {
            //making sure to reverse the edges 3 and 4
            String edge1 = s.getEdge1() + (s.getDirection1() ? "" : "^");
            String edge2 = s.getEdge2() + (s.getDirection2() ? "" : "^");
            String edge3 = s.getEdge3() + (s.getDirection3() ? "^" : "");
            String edge4 = s.getEdge4() + (s.getDirection4() ? "^" : "");

            //combining the classes edge1 and edge3
            int class1 = WhatClass(hyperplanes, edge1);
            int class2 = WhatClass(hyperplanes, edge3);
            if(class1 != class2)
                CombineClasses(hyperplanes, new int[] {class1, class2});

            //Then the reverse of edge1 and edge 3
            class1 = WhatClass(hyperplanes, flip(edge1));
            class2 = WhatClass(hyperplanes, flip(edge3));
            if(class1 != class2)
                CombineClasses(hyperplanes, new int[] {class1, class2});

            //combining the classes edge2 and edge4
            class1 = WhatClass(hyperplanes, edge2);
            class2 = WhatClass(hyperplanes, edge4);
            if(class1 != class2)
                CombineClasses(hyperplanes, new int[] {class1, class2});

            //Then the reverse of edge2 and edge 4
            class1 = WhatClass(hyperplanes, flip(edge2));
            class2 = WhatClass(hyperplanes, flip(edge4));
            if(class1 != class2)
                CombineClasses(hyperplanes, new int[] {class1, class2});
        }

        //Checking two sidedness as well as removing half of the hyperplane classes
        for(String s:G.edgeSet())
        {
            int class1 = WhatClass(hyperplanes, s);
            int class2 = WhatClass(hyperplanes, flip(s));

            //if these appear in the same hyperplane class this complex has failed to be two sided and is not special :(
            if(class1 == class2)
                return false;

            //removing a hyperplane class.
            if(class1 != -1 && class2 != -1)
            {
                hyperplanes.remove(class2);
            }
        }

        // Checking self intersection
        // The idea is to go through each square and see if it's first edge and second edge belong to the same hyperplane class
        for(Square s:Squares)
        {
            String edge1 = s.getEdge1();
            String edge2 = s.getEdge2();

            int class1 = WhatClass(hyperplanes, edge1);
            if(class1 == -1)
                class1 = WhatClass(hyperplanes, flip(edge1));

            int class2 = WhatClass(hyperplanes, edge2);
            if(class2 == -1)
                class2 = WhatClass(hyperplanes, flip(edge2));

            //after finding the class for edge one and edge two if they are the same, then we know our hyperplane intersects
            //in which case our cube complex is not special
            if(class1 == class2)
                return false;
        }

        // Checking osculation
        // We go through each hyperplane. if a vertex shows up twice in the same 'coordinate' then our hyperplanes osculate
        // in which case we are not special
        for(ArrayList<String> hp : hyperplanes)
        {
            ArrayList<Integer> initialVertices = new ArrayList<Integer>();
            ArrayList<Integer> terminalVertices = new ArrayList<Integer>();

            for(String s : hp)
            {
                int initV = 0;
                int termV = 0;
                if(s.endsWith("^"))
                {
                    initV = G.getEdgeTarget(flip(s));
                    termV = G.getEdgeSource(flip(s));
                }
                else
                {
                    initV = G.getEdgeSource(s);
                    termV = G.getEdgeTarget(s);
                }

                if(!initialVertices.contains(initV))
                    initialVertices.add(initV);
                else
                    return false;

                if(!terminalVertices.contains(termV))
                    terminalVertices.add(termV);
                else
                    return false;
            }
        }

        // Checking Inter-osculation
        // We go through each square take that pair of intersecting
        for(Square s:Squares)
        {
            String edge1 = s.getEdge1();
            String edge2 = s.getEdge2();

            int class1 = WhatClass(hyperplanes, edge1);
            if(class1 == -1)
                class1 = WhatClass(hyperplanes, flip(edge1));

            int class2 = WhatClass(hyperplanes, edge2);
            if(class2 == -1)
                class2 = WhatClass(hyperplanes, flip(edge2));

            //after finding the class for edge one and edge two we know these are intersecting hyper planes
            ArrayList<String> hp1 = hyperplanes.get(class1);
            ArrayList<String> hp2 = hyperplanes.get(class2);
            ArrayList<Integer> hp1Verts = new ArrayList<Integer>();
            ArrayList<Integer> hp2Verts = new ArrayList<Integer>();

            for(String e : hp1)
            {
                if(e.endsWith("^"))
                {
                    hp1Verts.add(G.getEdgeTarget(flip(e)));
                    hp1Verts.add(G.getEdgeSource(flip(e)));
                }
                else
                {
                    hp1Verts.add(G.getEdgeSource(e));
                    hp1Verts.add(G.getEdgeTarget(e));
                }
            }

            for(String e : hp2)
            {
                if(e.endsWith("^"))
                {
                    hp2Verts.add(G.getEdgeTarget(flip(e)));
                    hp2Verts.add(G.getEdgeSource(flip(e)));
                }
                else
                {
                    hp2Verts.add(G.getEdgeSource(e));
                    hp2Verts.add(G.getEdgeTarget(e));
                }
            }

            ArrayList<Integer> intersection = new ArrayList<Integer>(hp1Verts);
            intersection.retainAll(hp2Verts);
            intersection.removeAll(VerticesInSquare(s));

            while(intersection.size() > 0)
            {
                ArrayList<Square> potentialSquares = SquaresThatContainVertex(intersection.get(0));
                boolean foundOne = false;
                for(Square ps : potentialSquares)
                {
                    edge1 = ps.getEdge1();
                    int c1 = WhatClass(hyperplanes, edge1);
                    if(c1 == -1)
                        c1 = WhatClass(hyperplanes, flip(edge1));

                    edge2 = ps.getEdge2();
                    int c2 = WhatClass(hyperplanes, edge2);
                    if(c2 == -1)
                        c2 = WhatClass(hyperplanes, flip(edge2));

                    if((c1 == class1 && c2 == class2) || (c1 == class2 && c2 == class1))
                    {
                        intersection.removeAll(VerticesInSquare(ps));
                        foundOne = true;
                        break;
                    }
                }
                if(!foundOne)
                    return false;
            }
        }

        return true;
    }

    private String flip(String s)
    {
        if(!s.endsWith("^"))
            return s + "^";
        else
            return s.substring(0, s.length()-1);
    }

    private ArrayList<Square> SquaresThatContainVertex(Integer Vertex)
    {
        ArrayList<Square> toReturn = new ArrayList<Square>();

        for(Square s : Squares)
        {
            if(G.getEdgeSource(s.getEdge1()).equals(Vertex) || G.getEdgeTarget(s.getEdge1()).equals(Vertex) ||
                    G.getEdgeSource(s.getEdge2()).equals(Vertex) || G.getEdgeTarget(s.getEdge2()).equals(Vertex) ||
                    G.getEdgeSource(s.getEdge3()).equals(Vertex) || G.getEdgeTarget(s.getEdge3()).equals(Vertex) ||
                    G.getEdgeSource(s.getEdge4()).equals(Vertex) || G.getEdgeTarget(s.getEdge4()).equals(Vertex))
            {
                toReturn.add(s);
            }
        }

        return toReturn;
    }

    private ArrayList<Integer> VerticesInSquare(Square square)
    {
        ArrayList<Integer> toReturn = new ArrayList<Integer>();

        toReturn.add(G.getEdgeSource(square.getEdge1()));

        if(!toReturn.contains(G.getEdgeTarget(square.getEdge1())))
            toReturn.add(G.getEdgeTarget(square.getEdge1()));

        if(!toReturn.contains(G.getEdgeSource(square.getEdge3())))
            toReturn.add(G.getEdgeSource(square.getEdge3()));

        if(!toReturn.contains(G.getEdgeTarget(square.getEdge3())))
            toReturn.add(G.getEdgeTarget(square.getEdge3()));

        return toReturn;
    }

    private void CombineClasses(ArrayList<ArrayList<String>> Classes, int[] toCombine)
    {
        ArrayList<String> CombinedArray = new ArrayList<String>();
        ArrayList<ArrayList<String>> toRemove = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < toCombine.length; i++)
        {
            for(String j : Classes.get(toCombine[i]))
            {
                CombinedArray.add(j);
            }
            toRemove.add(Classes.get(toCombine[i]));
        }
        Classes.removeAll(toRemove);
        Classes.add(CombinedArray);
    }

    private int WhatClass(ArrayList<ArrayList<String>> Classes, String s)
    {
        for(int j = 0; j < Classes.size(); j++)
        {
            if(Classes.get(j).contains(s))
                return j;
        }
        return -1;
    }

    public String ToString()
    {
        String toReturn = "";
        toReturn += G.toString() + '\n';
        for(Square s : Squares)
        {
            toReturn += "[" + s.getEdge1() + (s.getDirection1() ? "" : "^") + ", " +
                              s.getEdge2() + (s.getDirection2() ? "" : "^") + ", " +
                              s.getEdge3() + (s.getDirection3() ? "" : "^") + ", " +
                              s.getEdge4() + (s.getDirection4() ? "" : "^") + "]\n";
        }
        return toReturn;
    }
}

