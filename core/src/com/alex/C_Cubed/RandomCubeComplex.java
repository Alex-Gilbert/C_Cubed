package com.alex.C_Cubed;

/**
 * Created by Alex on 4/18/2015.
 */

import com.badlogic.gdx.utils.Array;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.alg.cycle.PatonCycleBase;
import sun.security.provider.certpath.Vertex;

import java.time.LocalTime;
import java.util.*;

public class RandomCubeComplex
{
    private static Random rand = new Random();
    private static UnderlyingGraph<Integer, String> underlyingGraph = new UnderlyingGraph<Integer, String>(String.class);
    private static ArrayList<Square> squares = new ArrayList<Square>();
    private static ArrayList<Square> AllPossibleSquares = new ArrayList<Square>();

    public static void RunTest(int Vertices, int Edges, int MinSquares, int MaxSquares, int Complexes)
    {
        CubeComplex test;
        for(int i = 0; i < Complexes; i++)
        {
            test = RandomCubeComplex(Vertices, Edges, MaxSquares);
            for(int j = 1; j < 50; j++)
            {
                if(test.Squares.size() >= MinSquares)
                    break;

                CubeComplex cur;
                cur = RandomCubeComplex(Vertices, Edges, MaxSquares);

                if(cur.Squares.size() > test.Squares.size())
                    test = cur;
            }

            if(test.Squares.size() >= MinSquares)
                System.out.println("\nTest Case: " + i + '\n' + test.ToString() + "Special: " + test.Special());
            else
                --i;
        }
    }

    public static CubeComplex RandomCubeComplex(int Vertices, int Edges, int Squares)
    {
        RandomUnderLyingGraph(Vertices, Edges);

        underlyingGraph.removeEdge(Vertices + 5, Vertices + 10);

        for(int i = 1; i <= Vertices; ++i)
        {
            AddAllSquaresFromVertex(i, i, 0, new Square("", "", true, "", true, "", true, "", true));
        }

        RandomSquaresWordHyperbolic(Squares);
        return new CubeComplex(underlyingGraph, squares);
    }

    private static void RandomUnderLyingGraph(int Vertices, int Edges)
    {
        ArrayList<Integer> vertices = new ArrayList<Integer>(underlyingGraph.vertexSet());
        underlyingGraph.removeAllVertices(vertices);

        for(int i = 1; i <= Vertices; ++i)
        {
            underlyingGraph.addVertex(i);
        }

        for(int i = 0; i < Edges; ++i)
        {
            int initialVertex = rand.nextInt(Vertices) + 1;
            int terminalVertex = rand.nextInt(Vertices) + 1;

            String edgeName = "" + (char) ((int) 'a' + (i%26)) + (i/26 != 0 ? i/26 : "");

            underlyingGraph.addEdge(initialVertex, terminalVertex, edgeName);
        }
    }

    private static void RandomSquaresWordHyperbolic(int Squares)
    {
        squares.clear();

        Map<Integer, Pseudograph<String, Integer>> linkGraphs = new HashMap<Integer, Pseudograph<String, Integer>>();
        Map<Integer, Map<String, Map<String, Integer>>> linkFourCycleChecks = new HashMap<Integer, Map<String, Map<String, Integer>>>();

        for(Integer vertex : underlyingGraph.vertexSet())
        {
            Pseudograph<String, Integer> linkGraph = new Pseudograph<String, Integer>(Integer.class);
            for(String e : underlyingGraph.incomingEdgesOf(vertex))
            {
                linkGraph.addVertex(e);
            }
            for(String e : underlyingGraph.outgoingEdgesOf(vertex))
            {
                linkGraph.addVertex(e + "^");
            }

            Map<String, Map<String, Integer>> FourCycleCheck = new HashMap<String, Map<String, Integer>>();
            for(String v1 : linkGraph.vertexSet())
            {
                Map<String, Integer> row = new HashMap<String, Integer>();
                for(String v2 : linkGraph.vertexSet())
                {
                    row.put(v2, 0);
                }
                FourCycleCheck.put(v1, row);
            }

            linkGraphs.put(vertex, linkGraph);
            linkFourCycleChecks.put(vertex, FourCycleCheck);
        }

        while(squares.size() < Squares && AllPossibleSquares.size() > 0)
        {
            Square squareToCheck = AllPossibleSquares.get(rand.nextInt(AllPossibleSquares.size()));

            boolean addSquare = true;
            for(Integer v : underlyingGraph.vertexSet())
            {
                if(!CanAddEdgesToLink(linkGraphs.get(v), linkFourCycleChecks.get(v), squareToCheck))
                {
                    addSquare = false;
                    break;
                }
            }

            if(addSquare)
            {
                for(Integer v : underlyingGraph.vertexSet())
                {
                    AddEdgesToLink(linkGraphs.get(v), linkFourCycleChecks.get(v), squareToCheck);
                }
                squares.add(squareToCheck);
            }
            AllPossibleSquares.remove(squareToCheck);
        }
        AllPossibleSquares.clear();
    }

    //Checks if adding a square to our complex will cause our complex to not have a word hyperbolic fundamental group
    private static boolean CanAddEdgesToLink(Pseudograph<String, Integer> linkGraph, Map<String, Map<String, Integer>> fourCycleCheck, Square s)
    {
        Array<Pair<String, String>> addedEdges = new Array<Pair<String, String>>();

        if(linkGraph.vertexSet().contains(s.getEdge1()))
        {
            if(s.getDirection1())
            {
                if(s.getDirection2())
                {
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1(), s.getEdge2() + "^");
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
                else
                {
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1(), s.getEdge2());
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
            }
            else
            {
                if(s.getDirection4())
                {
                    //linkGraph.addEdge(s.getEdge1(), s.getEdge4(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1(), s.getEdge4());
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge1(), s.getEdge4() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1(), s.getEdge4() + "^");
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
            }
        }
        if(linkGraph.vertexSet().contains(s.getEdge1() + "^"))
        {
            if(s.getDirection1())
            {
                if(s.getDirection4())
                {
                    //linkGraph.addEdge(s.getEdge1() + "^", s.getEdge4(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1() + "^", s.getEdge4());
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge1() + "^", s.getEdge4() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1() + "^", s.getEdge4() + "^");
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
            }
            else
            {
                if(s.getDirection2())
                {
                    //linkGraph.addEdge(s.getEdge1() + "^", s.getEdge2() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1() + "^", s.getEdge2() + "^");
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge1() + "^", s.getEdge2(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1() + "^", s.getEdge2());
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
            }
        }
        if(linkGraph.vertexSet().contains(s.getEdge3()))
        {
            if(s.getDirection3())
            {
                if(s.getDirection4())
                {
                    //linkGraph.addEdge(s.getEdge3(), s.getEdge4() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3(), s.getEdge4() + "^");
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge3(), s.getEdge4(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3(), s.getEdge4());
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
            }
            else
            {
                if(s.getDirection2())
                {
                    //linkGraph.addEdge(s.getEdge3(), s.getEdge2(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3(), s.getEdge2());
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge3(), s.getEdge2() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3(), s.getEdge2() + "^");
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
            }
        }
        if(linkGraph.vertexSet().contains(s.getEdge3() + "^"))
        {
            if(s.getDirection3())
            {
                if(s.getDirection2())
                {
                    //linkGraph.addEdge(s.getEdge3() + "^", s.getEdge2(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3() + "^", s.getEdge2());
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge3() + "^", s.getEdge2() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3() + "^", s.getEdge2() + "^");
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
            }
            else
            {
                if(s.getDirection4())
                {
                    //linkGraph.addEdge(s.getEdge3() + "^", s.getEdge4() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3() + "^", s.getEdge4() + "^");
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge3() + "^", s.getEdge4(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3() + "^", s.getEdge4());
                    if(CanAddEdge(linkGraph, fourCycleCheck, edge))
                    {
                        AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                        addedEdges.add(edge);
                    }
                    else
                    {
                        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
                        return false;
                    }
                }
            }
        }

        RemoveEdgesFromLink(addedEdges, linkGraph, fourCycleCheck);
        return true;
    }

    //Check if adding a single edge will break word hyperbolicity
    private static boolean CanAddEdge(Pseudograph<String, Integer> linkGraph, Map<String, Map<String, Integer>> fourCycleCheck, Pair<String, String> edge)
    {
        if(!linkGraph.containsVertex(edge.getValue0()) || !linkGraph.containsVertex(edge.getValue1()))
            return false;

        //makes a double edge
        if(linkGraph.containsEdge(edge.getValue0(), edge.getValue1()) || linkGraph.containsEdge(edge.getValue1(), edge.getValue0()))
            return false;

        NeighborIndex neighborIndex = new NeighborIndex(linkGraph);
        List<String> v0Nei = neighborIndex.neighborListOf(edge.getValue0());
        List<String> v1Nei = neighborIndex.neighborListOf(edge.getValue1());

        //creates a triangle
        ArrayList<String> intersectionCheck = new ArrayList<String>(v0Nei);
        intersectionCheck.removeAll(v1Nei);
        if(intersectionCheck.size() != v0Nei.size())
            return false;

        //creates a square
        for(String s : v0Nei)
        {
            if(fourCycleCheck.get(s).get(edge.getValue1()) > 0 ||fourCycleCheck.get(edge.getValue1()).get(s) > 0)
                return false;
        }

        for(String s : v1Nei)
        {
            if(fourCycleCheck.get(s).get(edge.getValue0()) > 0 ||fourCycleCheck.get(edge.getValue0()).get(s) > 0)
                return false;
        }

        return true;
    }

    private static void AddEdgeToLink(Pseudograph<String, Integer> linkGraph, Map<String, Map<String, Integer>> fourCycleCheck, Pair<String, String> edge)
    {
        NeighborIndex neighborIndex = new NeighborIndex(linkGraph);
        List<String> v0Nei = neighborIndex.neighborListOf(edge.getValue0());
        List<String> v1Nei = neighborIndex.neighborListOf(edge.getValue1());

        for(String s : v0Nei)
        {
            fourCycleCheck.get(s).put(edge.getValue1(), fourCycleCheck.get(s).get(edge.getValue1()) + 1);
            fourCycleCheck.get(edge.getValue1()).put(s, fourCycleCheck.get(edge.getValue1()).get(s) + 1);
        }

        for(String s : v1Nei)
        {
            fourCycleCheck.get(s).put(edge.getValue0(), fourCycleCheck.get(s).get(edge.getValue0()) + 1);
            fourCycleCheck.get(edge.getValue0()).put(s, fourCycleCheck.get(edge.getValue0()).get(s) + 1);
        }

        linkGraph.addEdge(edge.getValue0(), edge.getValue1(), linkGraph.edgeSet().size());
    }

    private static void RemoveEdgesFromLink(Array<Pair<String, String>> EdgesToRemove, Pseudograph<String, Integer> linkGraph, Map<String, Map<String, Integer>> fourCycleCheck)
    {
        NeighborIndex neighborIndex = new NeighborIndex(linkGraph);
        for(Pair<String, String> edge : EdgesToRemove)
        {
            linkGraph.removeEdge(edge.getValue0(), edge.getValue1());

            List<String> v0Nei = neighborIndex.neighborListOf(edge.getValue0());
            List<String> v1Nei = neighborIndex.neighborListOf(edge.getValue1());

            for(String s : v0Nei)
            {
                fourCycleCheck.get(s).put(edge.getValue1(), fourCycleCheck.get(s).get(edge.getValue1()) - 1);
                fourCycleCheck.get(edge.getValue1()).put(s, fourCycleCheck.get(edge.getValue1()).get(s) - 1);
            }
            for(String s : v1Nei)
            {
                fourCycleCheck.get(s).put(edge.getValue0(), fourCycleCheck.get(s).get(edge.getValue0()) - 1);
                fourCycleCheck.get(edge.getValue0()).put(s, fourCycleCheck.get(edge.getValue0()).get(s) - 1);
            }
        }
    }

    //Will add edges in the link graph
    private static void AddEdgesToLink(Pseudograph<String, Integer> linkGraph, Map<String, Map<String, Integer>> fourCycleCheck, Square s)
    {
        if(linkGraph.vertexSet().contains(s.getEdge1()))
        {
            if(s.getDirection1())
            {
                if(s.getDirection2())
                {
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1(), s.getEdge2() + "^");
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
                else
                {
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1(), s.getEdge2());
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
            }
            else
            {
                if(s.getDirection4())
                {
                    //linkGraph.addEdge(s.getEdge1(), s.getEdge4(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1(), s.getEdge4());
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge1(), s.getEdge4() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1(), s.getEdge4() + "^");
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
            }
        }
        if(linkGraph.vertexSet().contains(s.getEdge1() + "^"))
        {
            if(s.getDirection1())
            {
                if(s.getDirection4())
                {
                    //linkGraph.addEdge(s.getEdge1() + "^", s.getEdge4(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1() + "^", s.getEdge4());
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge1() + "^", s.getEdge4() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1() + "^", s.getEdge4() + "^");
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
            }
            else
            {
                if(s.getDirection2())
                {
                    //linkGraph.addEdge(s.getEdge1() + "^", s.getEdge2() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1() + "^", s.getEdge2() + "^");
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge1() + "^", s.getEdge2(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge1() + "^", s.getEdge2());
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
            }
        }
        if(linkGraph.vertexSet().contains(s.getEdge3()))
        {
            if(s.getDirection3())
            {
                if(s.getDirection4())
                {
                    //linkGraph.addEdge(s.getEdge3(), s.getEdge4() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3(), s.getEdge4() + "^");
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge3(), s.getEdge4(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3(), s.getEdge4());
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
            }
            else
            {
                if(s.getDirection2())
                {
                    //linkGraph.addEdge(s.getEdge3(), s.getEdge2(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3(), s.getEdge2());
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge3(), s.getEdge2() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3(), s.getEdge2() + "^");
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
            }
        }
        if(linkGraph.vertexSet().contains(s.getEdge3() + "^"))
        {
            if(s.getDirection3())
            {
                if(s.getDirection2())
                {
                    //linkGraph.addEdge(s.getEdge3() + "^", s.getEdge2(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3() + "^", s.getEdge2());
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge3() + "^", s.getEdge2() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3() + "^", s.getEdge2() + "^");
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
            }
            else
            {
                if(s.getDirection4())
                {
                    //linkGraph.addEdge(s.getEdge3() + "^", s.getEdge4() + "^", i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3() + "^", s.getEdge4() + "^");
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
                else
                {
                    //linkGraph.addEdge(s.getEdge3() + "^", s.getEdge4(), i++);
                    Pair<String, String> edge = new Pair<String, String>(s.getEdge3() + "^", s.getEdge4());
                    AddEdgeToLink(linkGraph, fourCycleCheck, edge);
                }
            }
        }
    }

    private static void AddAllSquaresFromVertex(Integer InitialVertex, Integer curVertex, int curPathLength, Square curSquare)
    {
        if(curPathLength == 4)
        {
            if(curVertex == InitialVertex)
            {
                curSquare.setName("s" + AllPossibleSquares.size());
                AllPossibleSquares.add(curSquare.copy());
            }
            return;
        }

        for(String s : underlyingGraph.outgoingEdgesOf(curVertex))
        {
            if(curPathLength == 0 || (!curSquare.getEdge(curPathLength - 1).equals(s) && (curPathLength != 3 || !curSquare.getEdge(0).equals(s))))
            {
                Integer nextVertex = underlyingGraph.getEdgeTarget(s);

                if(nextVertex < InitialVertex)
                    return;

                if(!nextVertex.equals(curVertex))
                {
                    curSquare.setEdge(curPathLength, s);
                    curSquare.setDirection(curPathLength, true);
                    AddAllSquaresFromVertex(InitialVertex, nextVertex, curPathLength + 1, curSquare);
                }
                else
                {
                    curSquare.setEdge(curPathLength, s);
                    curSquare.setDirection(curPathLength, true);
                    AddAllSquaresFromVertex(InitialVertex, nextVertex, curPathLength + 1, curSquare);
                    curSquare.setEdge(curPathLength, s);
                    curSquare.setDirection(curPathLength, false);
                    AddAllSquaresFromVertex(InitialVertex, nextVertex, curPathLength + 1, curSquare);
                }
            }
        }

        for(String s : underlyingGraph.incomingEdgesOf(curVertex))
        {
            if(curPathLength == 0 || (!curSquare.getEdge(curPathLength - 1).equals(s) && (curPathLength != 3 || !curSquare.getEdge(0).equals(s))))
            {
                Integer nextVertex = underlyingGraph.getEdgeSource(s);

                if(nextVertex < InitialVertex)
                    return;

                if(!nextVertex.equals(curVertex))
                {
                    curSquare.setEdge(curPathLength, s);
                    curSquare.setDirection(curPathLength, false);
                    AddAllSquaresFromVertex(InitialVertex, nextVertex, curPathLength + 1, curSquare);
                }
            }
        }
    }
}
