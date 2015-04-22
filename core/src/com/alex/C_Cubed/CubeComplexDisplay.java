package com.alex.C_Cubed;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Alex on 9/25/2014.
 */
public class CubeComplexDisplay
{
    final float EdgeThickness = 6f;

    static Color SquareColor = new Color(1,1,0,.98f);
    static Color EdgeColor = new Color(0,0,0,1);

    static Color SquareHighlightColor = new Color(1,1,1,.75f);
    static Color EdgeHighlightColor = new Color(.4f,0,0,1);
    static Color VertexHightlightColor = new Color(1,1,1,1);

    //This positions of all the vertices in our graph
    public Array<Vector2> _vertices;
    Array<Color> _vertexColors;

    public Array<Pair<Integer, Integer>> _edges;
    Array<Triplet<Integer, Boolean, Color>> _edgeIdentifiers;

    public Array<Quartet<Integer, Integer, Integer, Integer>> _squares;

    public int NumOfVertices()
    {
        return _vertices.size;
    }


    public CubeComplexDisplay()
    {
        _vertices = new Array<Vector2>();
        _vertexColors = new Array<Color>();

        _edges = new Array<Pair<Integer, Integer>>();
        _edgeIdentifiers = new Array<Triplet<Integer, Boolean, Color>>();
        _squares = new Array<Quartet<Integer, Integer, Integer, Integer>>();
    }

    public CubeComplexDisplay(Array<Vector2> Vertices, Array<Pair<Integer, Integer>> Edges, Array<Quartet<Integer,Integer,Integer,Integer>> Squares)
    {
        _vertices = new Array<Vector2>();
        _vertexColors = new Array<Color>();

        _edges = new Array<Pair<Integer, Integer>>();
        _edgeIdentifiers = new Array<Triplet<Integer, Boolean, Color>>();

        for(Vector2 v : Vertices)
        {
            addVertex(v, new Color(0,0,0,1));
        }

        for(Pair<Integer, Integer> e : Edges)
        {
            addEdge(e);
        }

        _squares = Squares;
    }

    public void addVertex(Vector2 vertex, Color vertexColor)
    {
        _vertices.add(vertex);
        _vertexColors.add(vertexColor);
    }

    public void removeVertex(int index)
    {
        //First we remove all the edges associated with this vertex
        for(int i = 0; i < _edges.size; ++i)
        {
            if(_edges.get(i).contains(index))
            {
               removeEdge(i);
                --i;
            }
        }

        //Since removing a vertex causes the indices of all the following vertices to decrement by one
        //we must decrement the edges assigned to those vertices as well
        for(int i = 0; i < _edges.size; ++i)
        {
            if(_edges.get(i).getValue0() > index)
            {
                _edges.set(i, new Pair<Integer, Integer>(_edges.get(i).getValue0() - 1, _edges.get(i).getValue1()));
            }
            if(_edges.get(i).getValue1() > index)
            {
                _edges.set(i, new Pair<Integer, Integer>(_edges.get(i).getValue0(), _edges.get(i).getValue1() - 1));
            }
        }

        _vertices.removeIndex(index);
        _vertexColors.removeIndex(index);
    }

    public void removeEdge(int index)
    {
        if(_edgeIdentifiers.get(index).getValue0() == 0)
        {
            for(int i = 0; i < _squares.size; ++i)
            {
                if(_squares.get(i).contains(index))
                {
                    removeSquare(i);
                    --i;
                }
            }

            for(int i = 0; i < _squares.size; ++i)
            {
                if(_squares.get(i).getValue0() > index)
                {
                    _squares.set(i, new Quartet<Integer, Integer, Integer, Integer>(_squares.get(i).getValue0() - 1, _squares.get(i).getValue1(), _squares.get(i).getValue2(), _squares.get(i).getValue3()));
                }
                if(_squares.get(i).getValue1() > index)
                {
                    _squares.set(i, new Quartet<Integer, Integer, Integer, Integer>(_squares.get(i).getValue0(), _squares.get(i).getValue1() - 1, _squares.get(i).getValue2(), _squares.get(i).getValue3()));
                }
                if(_squares.get(i).getValue2() > index)
                {
                    _squares.set(i, new Quartet<Integer, Integer, Integer, Integer>(_squares.get(i).getValue0(), _squares.get(i).getValue1(), _squares.get(i).getValue2() - 1, _squares.get(i).getValue3()));
                }
                if(_squares.get(i).getValue3() > index)
                {
                    _squares.set(i, new Quartet<Integer, Integer, Integer, Integer>(_squares.get(i).getValue0(), _squares.get(i).getValue1(), _squares.get(i).getValue2(), _squares.get(i).getValue3() - 1));
                }

            }

            _edges.removeIndex(index);
            _edgeIdentifiers.removeIndex(index);
        }
        else
            _edgeIdentifiers.set(index, new Triplet<Integer, Boolean, Color>(0, false, Color.LIGHT_GRAY));
    }

    public void removeSquare(int index)
    {
        _squares.removeIndex(index);
    }

    public void moveVertex(Vector2 deltaPosition, int vertexIndex)
    {
        if(vertexIndex > -1 && vertexIndex < _vertices.size)
            _vertices.set(vertexIndex, _vertices.get(vertexIndex).add(deltaPosition));
    }

    public void moveEdge(Vector2 deltaPosition, int edgeIndex)
    {
        if(edgeIndex > -1 && edgeIndex < _edges.size)
        {
            moveVertex(deltaPosition, _edges.get(edgeIndex).getValue0());
            moveVertex(deltaPosition, _edges.get(edgeIndex).getValue1());
        }
    }

    public void moveSquare(Vector2 deltaPosition, int squareIndex)
    {
        if(squareIndex > -1 && squareIndex < _squares.size)
        {
            moveEdge(deltaPosition, _squares.get(squareIndex).getValue0());
            moveEdge(deltaPosition, _squares.get(squareIndex).getValue2());
        }
    }

    public void identifyEdge(int edgeIndex, Color color, int identifierIndex)
    {
        _edgeIdentifiers.set(edgeIndex, new Triplet<Integer, Boolean, Color>
                (identifierIndex + 1, _edgeIdentifiers.get(edgeIndex).getValue1(), color));
    }

    public void flipEdgeIdentifier(int edgeIndex)
    {
        _edgeIdentifiers.set(edgeIndex, new Triplet<Integer, Boolean, Color>(
                _edgeIdentifiers.get(edgeIndex).getValue0(),
                !_edgeIdentifiers.get(edgeIndex).getValue1(),
                _edgeIdentifiers.get(edgeIndex).getValue2()
        ));
    }

    public void identifyVertex(int vertexIndex, Color color)
    {
        _vertexColors.set(vertexIndex, color);
    }

    public void addEdge(Pair<Integer, Integer> edge)
    {
        _edges.add(new Pair<Integer, Integer>(edge.getValue0(), edge.getValue1()));
        _edgeIdentifiers.add(new Triplet<Integer, Boolean, Color>(0, false, Color.LIGHT_GRAY));
    }

    public void addSquare(Quartet<Integer, Integer, Integer, Integer> square)
    {
        _squares.add(square);
    }

    public Vector2 edgeVector(int edgeIndex)
    {
        return new Vector2(
                _vertices.get(_edges.get(edgeIndex).getValue1()).x - _vertices.get(_edges.get(edgeIndex).getValue0()).x,
                _vertices.get(_edges.get(edgeIndex).getValue1()).y - _vertices.get(_edges.get(edgeIndex).getValue0()).y);
    }

    public Vector2 edgeMidpoint(int edgeIndex)
    {
        Vector2 toReturn = (_vertices.get(_edges.get(edgeIndex).getValue0()).add(_vertices.get(_edges.get(edgeIndex).getValue1())));
        toReturn.x *= .5f;
        toReturn.y *= .5f;
        return toReturn;
    }

    public Vector2 edgeMidpoint(Pair<Integer, Integer> edge)
    {
        Vector2 v1 = _vertices.get(edge.getValue0());
        Vector2 v2 = _vertices.get(edge.getValue1());

        return new Vector2((v1.x + v2.x) * .5f, (v1.y + v2.y) * .5f);
    }

    public void Draw(SpriteBatch sB, ShapeRenderer sR, TextureRegion vertexTexture, TextureRegion[] identifierTextures,
                     int VertexToHighlight, int EdgeToHighlight, int SquareToHighlight)
    {
        //sB.begin();
        for(int i = 0; i < _squares.size; ++i)
        {
            drawSquare(sR, _squares.get(i), i == SquareToHighlight ? SquareHighlightColor : SquareColor);
        }

        for(int i = 0; i < _edges.size; ++i)
        {
            if(_edgeIdentifiers.get(i).getValue0() == 0)
                drawEdge(sR, _edges.get(i), i == EdgeToHighlight ? EdgeHighlightColor : EdgeColor);
            else
                drawEdge(sR, sB, _edges.get(i), identifierTextures[_edgeIdentifiers.get(i).getValue0() - 1],
                        _edgeIdentifiers.get(i).getValue1(), _edgeIdentifiers.get(i).getValue2(),
                        i == EdgeToHighlight ? EdgeHighlightColor : EdgeColor);
        }

        for (int i = 0; i < _vertices.size; ++i)
        {
            drawVertex(sB, vertexTexture, _vertices.get(i), i == VertexToHighlight ? VertexHightlightColor : _vertexColors.get(i));
        }
        //sB.end();
    }

    private Quartet<Vector2, Vector2, Vector2, Vector2> thickLineCorners(Vector2 startPoint, Vector2 endPoint, float thickness)
    {
        Vector2 topLeft = new Vector2(startPoint.x,startPoint.y);
        Vector2 bottomLeft = new Vector2(startPoint.x, startPoint.y);
        Vector2 topRight = new Vector2(endPoint.x, endPoint.y);
        Vector2 bottomRight = new Vector2(endPoint.x, endPoint.y);

        Vector2 delta = new Vector2(startPoint.y - endPoint.y, endPoint.x - startPoint.x);
        delta = delta.nor();
        delta = delta.scl(thickness);

        topLeft = topLeft.add(delta.scl(-1));
        bottomLeft = bottomLeft.add(delta.scl(-1));

        topRight = topRight.add(delta);
        bottomRight = bottomRight.add(delta.scl(-1));

        return new Quartet<Vector2, Vector2, Vector2, Vector2>(topLeft, topRight, bottomLeft, bottomRight);
    }

    private void drawThickLine(ShapeRenderer sR, Color drawColor, Vector2 startPoint, Vector2 endPoint)
    {
        Quartet<Vector2, Vector2, Vector2, Vector2> corners = thickLineCorners(startPoint, endPoint, EdgeThickness);

        sR.begin(ShapeRenderer.ShapeType.Filled);
        sR.setColor(drawColor);

        sR.triangle(corners.getValue0().x, corners.getValue0().y,
                corners.getValue1().x, corners.getValue1().y, corners.getValue2().x, corners.getValue2().y);

        sR.triangle(corners.getValue2().x, corners.getValue2().y,
                corners.getValue1().x, corners.getValue1().y, corners.getValue3().x, corners.getValue3().y);

        sR.end();
    }

    private void drawVertex(SpriteBatch sB, TextureRegion vertexTexture, Vector2 vertex, Color drawColor)
    {
        sB.setColor(drawColor);
        sB.begin();
        sB.draw(vertexTexture, vertex.x - vertexTexture.getRegionWidth()*.5f, vertex.y - vertexTexture.getRegionHeight()*.5f,
                vertexTexture.getRegionWidth()*.5f, vertexTexture.getRegionHeight()*.5f,
                vertexTexture.getRegionWidth(), vertexTexture.getRegionHeight(),
                .5f, .5f, 0);
        sB.end();
    }

    private void drawEdge(ShapeRenderer sR, Pair<Integer, Integer> edge, Color drawColor)
    {
        //drawThickLine(sR, drawColor, _vertices.get(edge.getValue0()), _vertices.get(edge.getValue1()));
        sR.setColor(drawColor);
        sR.begin(ShapeRenderer.ShapeType.Filled);
        sR.rectLine(_vertices.get(edge.getValue0()), _vertices.get(edge.getValue1()), EdgeThickness);
        sR.end();
    }

    private void drawEdge(ShapeRenderer sR, SpriteBatch sB, Pair<Integer, Integer> edge,
                          TextureRegion identifierTexture, boolean flipIdentifier, Color identifierColor,
                          Color drawColor)
    {
        sR.setColor(drawColor);
        sR.begin(ShapeRenderer.ShapeType.Filled);
        sR.rectLine(_vertices.get(edge.getValue0()), _vertices.get(edge.getValue1()), EdgeThickness);
        sR.end();

        Vector2 midpoint = this.edgeMidpoint(edge);

        float rotation = MathUtils.atan2(_vertices.get(edge.getValue1()).y - _vertices.get(edge.getValue0()).y,
                _vertices.get(edge.getValue1()).x - _vertices.get(edge.getValue0()).x);




        sB.setColor(identifierColor);
        sB.begin();
        sB.draw(identifierTexture, midpoint.x - identifierTexture.getRegionWidth()*.5f, midpoint.y - identifierTexture.getRegionHeight()*.5f,
                identifierTexture.getRegionWidth()*.5f, identifierTexture.getRegionHeight()*.5f,
                identifierTexture.getRegionWidth(), identifierTexture.getRegionHeight(),
                .25f, .25f, flipIdentifier ? 180 + MathUtils.radiansToDegrees * rotation : MathUtils.radiansToDegrees * rotation);
        sB.end();
    }

    private void drawSquare(ShapeRenderer sR, Quartet<Integer, Integer, Integer, Integer> square, Color drawColor)
    {
        Vector2 v1 = _vertices.get(_edges.get(square.getValue0()).getValue0());
        Vector2 v2 = _vertices.get(_edges.get(square.getValue0()).getValue1());
        Vector2 v3 = _vertices.get(_edges.get(square.getValue2()).getValue0());
        Vector2 v4 = _vertices.get(_edges.get(square.getValue2()).getValue1());

        sR.begin(ShapeRenderer.ShapeType.Filled);

        sR.setColor(drawColor);

        sR.triangle(v1.x, v1.y,
                v2.x, v2.y, v4.x,v4.y);

        sR.triangle(v2.x, v2.y,
                v3.x, v3.y, v4.x, v4.y);

        sR.triangle(v1.x, v1.y,
                v2.x, v2.y, v3.x,v3.y);

        sR.triangle(v1.x, v1.y,
                v3.x, v3.y, v4.x, v4.y);

        sR.end();
    }

    public int HoveringVertex(Vector2 mousePosition)
    {
        for(int i = 0; i < _vertices.size; ++i)
        {
            Rectangle vertexRect = new Rectangle(_vertices.get(i).x - 8, _vertices.get(i).y - 8, 16, 16);

            if(vertexRect.contains(mousePosition.x, mousePosition.y))
                return i;
        }
        return -1;
    }

    public int HoveringEdge(Vector2 mousePosition)
    {
        for (int i = 0; i < _edges.size; ++i)
        {
            Quartet<Vector2, Vector2, Vector2, Vector2> corners =
                    thickLineCorners(_vertices.get(_edges.get(i).getValue0()),
                            _vertices.get(_edges.get(i).getValue1()), EdgeThickness);

            if(PointInSqaure(mousePosition, corners.getValue0(), corners.getValue1(), corners.getValue3(), corners.getValue2()))
                return i;
        }
        return -1;
    }

    public int HoveringSquare(Vector2 mousePosition)
    {
        for(int i=0; i < _squares.size; ++i)
        {
            Vector2 v1 = _vertices.get(_edges.get(_squares.get(i).getValue0()).getValue0());
            Vector2 v2 = _vertices.get(_edges.get(_squares.get(i).getValue0()).getValue1());
            Vector2 v3 = _vertices.get(_edges.get(_squares.get(i).getValue2()).getValue0());
            Vector2 v4 = _vertices.get(_edges.get(_squares.get(i).getValue2()).getValue1());

            if(PointInSqaure(mousePosition, v1, v2, v3, v4))
                return i;
        }
        return -1;
    }

    private boolean SameSide(Vector2 p1, Vector2 p2, Vector2 A, Vector2 B)
    {
        // Convert points to Vector3 for use the Cross product, which is Vector3-only
        Vector3 BMinusA = new Vector3(B.x - A.x, B.y - A.y, 0);
        Vector3 BMinusA2 = new Vector3(B.x - A.x, B.y - A.y, 0);
        Vector3 p1MinusA = new Vector3(p1.x - A.x, p1.y - A.y, 0);
        Vector3 p2MinusA = new Vector3(p2.x - A.x, p2.y - A.y, 0);

        Vector3 cp1 = BMinusA.crs(p1MinusA);
        Vector3 cp2 = BMinusA2.crs(p2MinusA);
        return cp1.dot(cp2) >= 0;
    }

    public CubeComplex ToCubeComplex()
    {
        Array<Array<Integer>> VertexClasses = new Array<Array<Integer>>();
        Array<Array<Integer>> EdgeClasses = new Array<Array<Integer>>();
        
        //Put Vertices in 'Classes' initially based solely on Color
        for(int i = 0; i < _vertexColors.size; i++)
        {
            boolean addNewClass = true;

            for(int j = 0; j < VertexClasses.size; j++)
            {
                Color vertexColor = _vertexColors.get(i);
                Color classColor = _vertexColors.get(VertexClasses.get(j).get(0));
                if(!sameColor(vertexColor, Color.BLACK) && sameColor(vertexColor, classColor))
                {
                    VertexClasses.get(j).add(i);
                    addNewClass = false;
                    break;
                }
            }
            if(addNewClass)
            {
                Array<Integer> newArray = new Array<Integer>();
                newArray.add(i);
                VertexClasses.add(newArray);
            }
        }
        
        //Do The Same for Edges
        for(int i = 0; i < _edgeIdentifiers.size; i++)
        {
            boolean addNewClass = true;
            for(int j = 0; j < EdgeClasses.size; ++j)
            {
                Triplet<Integer, Boolean, Color> edgeIdentifier = _edgeIdentifiers.get(i);
                Triplet<Integer, Boolean, Color> classIdentifier = _edgeIdentifiers.get(EdgeClasses.get(j).get(0));
                if(edgeIdentifier.getValue0() != 0 && sameIdentifier(edgeIdentifier, classIdentifier))
                {
                    EdgeClasses.get(j).add(i);
                    addNewClass = false;
                    break;
                }
            }
            if(addNewClass)
            {
                Array<Integer> newArray = new Array<Integer>();
                newArray.add(i);
                EdgeClasses.add(newArray);
            }
        }

        Array<Integer> VertClassesToCombine = new Array<Integer>();
        for(int i = 0; i < EdgeClasses.size; i++)
        {
            //first the initial vertex of an edge class
            for(int j = 0; j < EdgeClasses.get(i).size; j++)
            {
                int edge = EdgeClasses.get(i).get(j);
                int initVertex = _edgeIdentifiers.get(edge).getValue1() ? _edges.get(edge).getValue1() : _edges.get(edge).getValue0();
                int vertClass = WhatClass(VertexClasses, initVertex);
                if(!VertClassesToCombine.contains(vertClass, true))
                    VertClassesToCombine.add(vertClass);
            }

            CombineClasses(VertexClasses, VertClassesToCombine);
            VertClassesToCombine.clear();

            //then the terminal vertex of an edge class
            for(int j = 0; j < EdgeClasses.get(i).size; j++)
            {
                int edge = EdgeClasses.get(i).get(j);
                int initVertex = _edgeIdentifiers.get(edge).getValue1() ? _edges.get(edge).getValue0() : _edges.get(edge).getValue1();
                int vertClass = WhatClass(VertexClasses, initVertex);
                if(!VertClassesToCombine.contains(vertClass, true))
                    VertClassesToCombine.add(vertClass);
            }

            CombineClasses(VertexClasses, VertClassesToCombine);
            VertClassesToCombine.clear();
        }

        UnderlyingGraph<Integer, String> underlyingGraph = new UnderlyingGraph<Integer, String>(String.class);
        for(int i = 0; i < VertexClasses.size; ++i)
        {
            underlyingGraph.addVertex(i);
        }
        for(int i = 0; i < EdgeClasses.size; ++i)
        {
            int edge = EdgeClasses.get(i).first();
            int iV = WhatClass(VertexClasses, _edges.get(edge).getValue0());
            int tV = WhatClass(VertexClasses, _edges.get(edge).getValue1());
            String edgeName = "" + (char) ((int) 'a' + i);
            if(!_edgeIdentifiers.get(edge).getValue1())
            {
                underlyingGraph.addEdge(iV, tV, edgeName);
            }
            else
            {
                underlyingGraph.addEdge(tV, iV, edgeName);
            }
        }


        ArrayList<Square> squares = new ArrayList<Square>();
        boolean triedToReverse = false;
        for(int i = 0; i < _squares.size; ++i)
        {
            String squareName = "s" + i;
            String[] edgeNames = new String[]{"", "", "", ""};
            boolean[] orientation = new boolean[]{true, true, true, true};
            int edge1 = _squares.get(i).getValue0();
            int anchorVertex = _edges.get(edge1).getValue0();
            edgeNames[0] += (char)('a' + WhatClass(EdgeClasses, edge1));
            orientation[0] =  !_edgeIdentifiers.get(edge1).getValue1();
            int v2 = _edges.get(edge1).getValue1();
            int edge3 =  _squares.get(i).getValue2();

            if(_edges.get(_squares.get(i).getValue1()).getValue0() == v2)
            {
                int edge2 =  _squares.get(i).getValue1();
                edgeNames[1] += (char)('a' + WhatClass(EdgeClasses, edge2));
                orientation[1] = !_edgeIdentifiers.get(edge2).getValue1();
                int v3 = _edges.get(edge2).getValue1();
                int edge4 = _squares.get(i).getValue3();

                if(_edges.get(edge3).getValue0() == v3)
                {
                    edgeNames[2] += (char)('a' + WhatClass(EdgeClasses, edge3));
                    orientation[2] = !_edgeIdentifiers.get(edge3).getValue1();
                    int v4 = _edges.get(edge3).getValue1();

                    if(_edges.get(edge4).getValue0() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = !_edgeIdentifiers.get(edge4).getValue1();
                    }
                    else if(_edges.get(edge4).getValue1() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = _edgeIdentifiers.get(edge4).getValue1();
                    }
                }
                else if(_edges.get(edge3).getValue1() == v3)
                {
                    edgeNames[2] += (char)('a' + WhatClass(EdgeClasses, edge3));
                    orientation[2] = _edgeIdentifiers.get(edge3).getValue1();
                    int v4 = _edges.get(edge3).getValue0();

                    if(_edges.get(edge4).getValue0() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = !_edgeIdentifiers.get(edge4).getValue1();
                    }
                    else if(_edges.get(edge4).getValue1() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = _edgeIdentifiers.get(edge4).getValue1();
                    }
                }
            }
            else if(_edges.get(_squares.get(i).getValue1()).getValue1() == v2)
            {
                int edge2 =  _squares.get(i).getValue1();
                edgeNames[1] += (char)('a' + WhatClass(EdgeClasses, edge2));
                orientation[1] = _edgeIdentifiers.get(edge2).getValue1();
                int v3 = _edges.get(edge2).getValue0();
                int edge4 = _squares.get(i).getValue3();

                if(_edges.get(edge3).getValue0() == v3)
                {
                    edgeNames[2] += (char)('a' + WhatClass(EdgeClasses, edge3));
                    orientation[2] = !_edgeIdentifiers.get(edge3).getValue1();
                    int v4 = _edges.get(edge3).getValue1();

                    if(_edges.get(edge4).getValue0() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = !_edgeIdentifiers.get(edge4).getValue1();
                    }
                    else if(_edges.get(edge4).getValue1() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = _edgeIdentifiers.get(edge4).getValue1();
                    }
                }
                else if(_edges.get(edge3).getValue1() == v3)
                {
                    edgeNames[2] += (char)('a' + WhatClass(EdgeClasses, edge3));
                    orientation[2] = _edgeIdentifiers.get(edge3).getValue1();
                    int v4 = _edges.get(edge3).getValue0();

                    if(_edges.get(edge4).getValue0() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = !_edgeIdentifiers.get(edge4).getValue1();
                    }
                    else if(_edges.get(edge4).getValue1() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = _edgeIdentifiers.get(edge4).getValue1();
                    }
                }
            }
            else if(_edges.get(_squares.get(i).getValue3()).getValue0() == v2)
            {
                int edge2 =  _squares.get(i).getValue3();
                edgeNames[1] += (char)('a' + WhatClass(EdgeClasses, edge2));
                orientation[1] = !_edgeIdentifiers.get(edge2).getValue1();
                int v3 = _edges.get(edge2).getValue0();
                int edge4 = _squares.get(i).getValue1();

                if(_edges.get(edge3).getValue0() == v3)
                {
                    edgeNames[2] += (char)('a' + WhatClass(EdgeClasses, edge3));
                    orientation[2] = !_edgeIdentifiers.get(edge3).getValue1();
                    int v4 = _edges.get(edge3).getValue1();

                    if(_edges.get(edge4).getValue0() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = !_edgeIdentifiers.get(edge4).getValue1();
                    }
                    else if(_edges.get(edge4).getValue1() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = _edgeIdentifiers.get(edge4).getValue1();
                    }
                }
                else if(_edges.get(edge3).getValue1() == v3)
                {
                    edgeNames[2] += (char)('a' + WhatClass(EdgeClasses, edge3));
                    orientation[2] = _edgeIdentifiers.get(edge3).getValue1();
                    int v4 = _edges.get(edge3).getValue0();

                    if(_edges.get(edge4).getValue0() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = !_edgeIdentifiers.get(edge4).getValue1();
                    }
                    else if(_edges.get(edge4).getValue1() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = _edgeIdentifiers.get(edge4).getValue1();
                    }
                }
            }
            else if(_edges.get(_squares.get(i).getValue3()).getValue1() == v2)
            {
                int edge2 =  _squares.get(i).getValue3();
                edgeNames[1] += (char)('a' + WhatClass(EdgeClasses, edge2));
                orientation[1] = _edgeIdentifiers.get(edge2).getValue1();
                int v3 = _edges.get(edge2).getValue0();
                int edge4 = _squares.get(i).getValue1();

                if(_edges.get(edge3).getValue0() == v3)
                {
                    edgeNames[2] += (char)('a' + WhatClass(EdgeClasses, edge3));
                    orientation[2] = !_edgeIdentifiers.get(edge3).getValue1();
                    int v4 = _edges.get(edge3).getValue1();

                    if(_edges.get(edge4).getValue0() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = !_edgeIdentifiers.get(edge4).getValue1();
                    }
                    else if(_edges.get(edge4).getValue1() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = _edgeIdentifiers.get(edge4).getValue1();
                    }
                }
                else if(_edges.get(edge3).getValue1() == v3)
                {
                    edgeNames[2] += (char)('a' + WhatClass(EdgeClasses, edge3));
                    orientation[2] = _edgeIdentifiers.get(edge3).getValue1();
                    int v4 = _edges.get(edge3).getValue0();

                    if(_edges.get(edge4).getValue0() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = !_edgeIdentifiers.get(edge4).getValue1();
                    }
                    else if(_edges.get(edge4).getValue1() == v4)
                    {
                        edgeNames[3] += (char)('a' + WhatClass(EdgeClasses, edge4));
                        orientation[3] = _edgeIdentifiers.get(edge4).getValue1();
                    }
                }
            }

            if(edgeNames[0].equals("") || edgeNames[1].equals("") || edgeNames[2].equals("") || edgeNames[3].equals(""))
            {
                if(!triedToReverse)
                {
                    //This is to fix an assumption I made about the orientation of the square
                    Quartet<Integer, Integer, Integer, Integer> curS = _squares.get(i);
                    _squares.set(i, new Quartet<Integer, Integer, Integer, Integer>(curS.getValue0(), curS.getValue3(), curS.getValue2(), curS.getValue1()));
                    --i;
                    triedToReverse = true;
                }
                else
                {
                    Quartet<Integer, Integer, Integer, Integer> curS = _squares.get(i);
                    Color errorColor = Color.MAGENTA;

                    identifyEdge(curS.getValue0(), errorColor, 2);
                    identifyEdge(curS.getValue1(), errorColor, 2);
                    identifyEdge(curS.getValue2(), errorColor, 2);
                    identifyEdge(curS.getValue3(), errorColor, 2);
                    triedToReverse = false;
                }
            }
            else
            {
                triedToReverse = false;
                squares.add(new Square(squareName, edgeNames[0], orientation[0],
                        edgeNames[1], orientation[1], edgeNames[2], orientation[2], edgeNames[3], orientation[3]));
            }
        }
        return new CubeComplex(underlyingGraph, squares);
    }

    private boolean sameColor(Color A, Color B)
    {
        return (A.r == B.r && A.g == B.g && A.b == B.b);
    }

    private boolean sameIdentifier(Triplet<Integer, Boolean, Color> A, Triplet<Integer, Boolean, Color> B)
    {
        return (A.getValue0() == B.getValue0() && sameColor(A.getValue2(), B.getValue2()));
    }

    private void CombineClasses(Array<Array<Integer>> Classes, Array<Integer> ToCombine)
    {
        Array<Integer> CombinedArray = new Array<Integer>();
        Array<Array<Integer>> toRemove = new Array<Array<Integer>>();
        for(int i = 0; i < ToCombine.size; i++)
        {
            for(Integer j : Classes.get(ToCombine.get(i)))
            {
                CombinedArray.add(j);
            }
            toRemove.add(Classes.get(ToCombine.get(i)));
        }
        Classes.removeAll(toRemove, true);
        Classes.add(CombinedArray);
    }

    private int WhatClass(Array<Array<Integer>> Classes, int i)
    {
        for(int j = 0; j < Classes.size; j++)
        {
            if(Classes.get(j).contains(i, true))
                return j;
        }
        return -1;
    }

    /**
     * Return true if the point p is in the triangle ABC
     */
    private boolean PointInTriangle(Vector2 p, Vector2 A, Vector2 B, Vector2 C)
    {
        return SameSide(p, A, B, C) && SameSide(p, B, A, C) && SameSide(p, C, A, B);
    }

    private boolean PointInSqaure(Vector2 p, Vector2 A, Vector2 B, Vector2 C, Vector2 D)
    {
        return PointInTriangle(p, A, B, D) || PointInTriangle(p, D, B, C);
    }
}
