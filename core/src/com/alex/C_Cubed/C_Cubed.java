package com.alex.C_Cubed;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.zip.CRC32;

public class C_Cubed implements ApplicationListener, InputProcessor, GestureListener
{
    Skin skin;
    Stage ui;
    Table root;
    Table cRoot;
    Table tRoot;
    TextureRegion image2;

    public enum Tools
    {
        Pointer,
        Vertex,
        Edge,
        Square,
        Eraser,
        Identify
    }

    int edgeIdentifier = 1;

    private Color currentColor;

    private OrthographicCamera camera;
    private SpriteBatch sB;
    private ShapeRenderer sR;
    private GestureDetector gestureDetector;

    private Tools currentTool;

    private CubeComplexDisplay complex;
    private TextureRegion vertexImage;
    private TextureRegion[] edgeIdentifiers;

    private Texture vertexID;
    private Texture vertexIU;
    private Texture edgeID;
    private Texture edgeIU;
    private Texture squareID;
    private Texture squareIU;
    private Texture pointerID;
    private Texture pointerIU;
    private Texture eraserI;

    private Texture blackIcon;
    private Texture blueIcon;
    private Texture darkredIcon;
    private Texture greenIcon;
    private Texture lightblueIcon;
    private Texture purpleIcon;
    private Texture redIcon;
    private Texture whiteIcon;
    private Texture yellowIcon;

    private Texture EID1Icon;
    private Texture EID2Icon;
    private Texture EID3Icon;
    private Texture EID4Icon;

    private Label GroupPresentationLabel;
    private Label NPCLabel;
    private Label SpecialLabel;

    private TextField vertTextField;
    private TextField edgeTextField;
    private TextField minSquareTextField;
    private TextField maxSquareTextField;
    private TextField testIterationsTextField;

    private Label workingLabel;

    float velX, velY;
    boolean flinging = false;
    float initialScale = 1;

    boolean draggingVertex = false;
    boolean draggingEdge = false;
    boolean draggingSquare = false;
    int draggingIndex = -1;
    Vector2 prevPosition;

    int[] selectedVerts;
    int[] selectedEdges;

    boolean flipped = false;

    @Override
    public void create ()
    {
        //RandomCubeComplex.RunTest(8, 20, 3, 6, 1000);

        //set UI
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        ui = new Stage();

        //Set Input
        InputMultiplexer input = new InputMultiplexer();
        gestureDetector = new GestureDetector(20,.5f, 1, .15f, this);
        input.addProcessor(ui);
        input.addProcessor(gestureDetector);
        input.addProcessor(this);
        Gdx.input.setInputProcessor(input);

        root = new Table();
        root.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui.addActor(root);
        //root.debug();

        cRoot = new Table();
        cRoot.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui.addActor(cRoot);

        tRoot = new Table();
        tRoot.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui.addActor(tRoot);

        //Create The Pointer Icon
        pointerID = new Texture(Gdx.files.internal("Icons/PointerIconDown.png"));
        pointerIU = new Texture(Gdx.files.internal("Icons/PointerIconUp.png"));
        ImageButtonStyle PBstyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        PBstyle.imageUp = new TextureRegionDrawable(new TextureRegion(pointerIU));
        PBstyle.imageDown = new TextureRegionDrawable(new TextureRegion(pointerID));

        ImageButton pointerButton = new ImageButton(PBstyle);
        pointerButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchTool(Tools.Pointer);
            }
        });

        //Create The Eraser Icon
        eraserI = new Texture(Gdx.files.internal("Icons/EraserIcon.png"));
        ImageButtonStyle ErBstyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        ErBstyle.imageUp = new TextureRegionDrawable(new TextureRegion(eraserI));
        ErBstyle.imageDown = new TextureRegionDrawable(new TextureRegion(eraserI));

        ImageButton eraserButton = new ImageButton(ErBstyle);
        eraserButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchTool(Tools.Eraser);
            }
        });

        //Create The Vertex Icon
        vertexID = new Texture(Gdx.files.internal("Icons/VertexIconDown.png"));
        vertexIU = new Texture(Gdx.files.internal("Icons/VertexIconUp.png"));
        ImageButtonStyle VBstyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        VBstyle.imageUp = new TextureRegionDrawable(new TextureRegion(vertexIU));
        VBstyle.imageDown = new TextureRegionDrawable(new TextureRegion(vertexID));
        ImageButton vertexButton = new ImageButton(VBstyle);
        vertexButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchTool(Tools.Vertex);
            }
        });

        //Create The Edge Icon
        edgeID = new Texture(Gdx.files.internal("Icons/EdgeIconDown.png"));
        edgeIU = new Texture(Gdx.files.internal("Icons/EdgeIconUp.png"));
        ImageButtonStyle EBstyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        EBstyle.imageUp = new TextureRegionDrawable(new TextureRegion(edgeIU));
        EBstyle.imageDown = new TextureRegionDrawable(new TextureRegion(edgeID));
        ImageButton edgeButton = new ImageButton(EBstyle);
        edgeButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchTool(Tools.Edge);
            }
        });

        //Create The Square Icon
        squareID = new Texture(Gdx.files.internal("Icons/SquareIconDown.png"));
        squareIU = new Texture(Gdx.files.internal("Icons/SquareIconUp.png"));
        ImageButtonStyle SBstyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        SBstyle.imageUp = new TextureRegionDrawable(new TextureRegion(squareIU));
        SBstyle.imageDown = new TextureRegionDrawable(new TextureRegion(squareID));
        ImageButton squareButton = new ImageButton(SBstyle);
        squareButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchTool(Tools.Square);
            }
        });

        //Create The IdentifierButtons Icon
        EID1Icon = new Texture(Gdx.files.internal("Icons/ID1.png"));
        ImageButtonStyle id1style = new ImageButtonStyle(skin.get(ButtonStyle.class));
        id1style.imageUp = new TextureRegionDrawable(new TextureRegion(EID1Icon));
        ImageButton id1Button = new ImageButton(id1style);
        id1Button.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                edgeIdentifier = 0;
            }
        });

        EID2Icon = new Texture(Gdx.files.internal("Icons/ID2.png"));
        ImageButtonStyle id2style = new ImageButtonStyle(skin.get(ButtonStyle.class));
        id2style.imageUp = new TextureRegionDrawable(new TextureRegion(EID2Icon));
        ImageButton id2Button = new ImageButton(id2style);
        id2Button.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                edgeIdentifier = 1;
            }
        });

        EID3Icon = new Texture(Gdx.files.internal("Icons/ID3.png"));
        ImageButtonStyle id3style = new ImageButtonStyle(skin.get(ButtonStyle.class));
        id3style.imageUp = new TextureRegionDrawable(new TextureRegion(EID3Icon));
        ImageButton id3Button = new ImageButton(id3style);
        id3Button.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                edgeIdentifier = 2;
            }
        });

        EID4Icon = new Texture(Gdx.files.internal("Icons/ID4.png"));
        ImageButtonStyle id4style = new ImageButtonStyle(skin.get(ButtonStyle.class));
        id4style.imageUp = new TextureRegionDrawable(new TextureRegion(EID4Icon));
        ImageButton id4Button = new ImageButton(id4style);
        id4Button.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                edgeIdentifier = 3;
            }
        });

        //Create The color Icons
        blackIcon = new Texture(Gdx.files.internal("Icons/Black.png"));
        ImageButtonStyle blackStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        blackStyle.imageUp = new TextureRegionDrawable(new TextureRegion(blackIcon));
        ImageButton blackButton = new ImageButton(blackStyle);
        blackButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.BLACK);
            }
        });

        blueIcon = new Texture(Gdx.files.internal("Icons/Blue.png"));
        ImageButtonStyle blueStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        blueStyle.imageUp = new TextureRegionDrawable(new TextureRegion(blueIcon));
        ImageButton blueButton = new ImageButton(blueStyle);
        blueButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.BLUE);
            }
        });

        darkredIcon = new Texture(Gdx.files.internal("Icons/DarkRed.png"));
        ImageButtonStyle darkredStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        darkredStyle.imageUp = new TextureRegionDrawable(new TextureRegion(darkredIcon));
        ImageButton darkredButton = new ImageButton(darkredStyle);
        darkredButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.MAROON);
            }
        });

        greenIcon = new Texture(Gdx.files.internal("Icons/Green.png"));
        ImageButtonStyle greenStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        greenStyle.imageUp = new TextureRegionDrawable(new TextureRegion(greenIcon));
        ImageButton greenButton = new ImageButton(greenStyle);
        greenButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.GREEN);
            }
        });

        lightblueIcon = new Texture(Gdx.files.internal("Icons/LightBlue.png"));
        ImageButtonStyle lightblueStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        lightblueStyle.imageUp = new TextureRegionDrawable(new TextureRegion(lightblueIcon));
        ImageButton lightblueButton = new ImageButton(lightblueStyle);
        lightblueButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.CYAN);
            }
        });

        purpleIcon = new Texture(Gdx.files.internal("Icons/Purple.png"));
        ImageButtonStyle purpleStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        purpleStyle.imageUp = new TextureRegionDrawable(new TextureRegion(purpleIcon));
        ImageButton purpleButton = new ImageButton(purpleStyle);
        purpleButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.PURPLE);
            }
        });

        redIcon = new Texture(Gdx.files.internal("Icons/Red.png"));
        ImageButtonStyle redStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        redStyle.imageUp = new TextureRegionDrawable(new TextureRegion(redIcon));
        ImageButton redButton = new ImageButton(redStyle);
        redButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.RED);
            }
        });

        whiteIcon = new Texture(Gdx.files.internal("Icons/White.png"));
        ImageButtonStyle whiteStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        whiteStyle.imageUp = new TextureRegionDrawable(new TextureRegion(whiteIcon));
        ImageButton whiteButton = new ImageButton(whiteStyle);
        whiteButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.WHITE);
            }
        });

        yellowIcon = new Texture(Gdx.files.internal("Icons/Yellow.png"));
        ImageButtonStyle yellowStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
        yellowStyle.imageUp = new TextureRegionDrawable(new TextureRegion(yellowIcon));
        ImageButton yellowButton = new ImageButton(yellowStyle);
        yellowButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                switchColor(Color.YELLOW);
            }
        });

        //cRoot Actors
        final TextButton button = new TextButton("Calculate", skin, "default");
        button.setWidth(100f);
        button.setHeight(100f);
        button.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                CubeComplex screenComplex = complex.ToCubeComplex();
                GroupPresentationLabel.setText("Group Presentation: " + screenComplex.PrintGroupPresentation());
                NPCLabel.setText("NPC: " + screenComplex.NPC());
                SpecialLabel.setText("Spcl: " + screenComplex.Special());
            }
        });

        GroupPresentationLabel = new Label("Group Presentation: ", skin);
        GroupPresentationLabel.setFontScale(1,1);
        GroupPresentationLabel.setColor(Color.BLACK);
        NPCLabel = new Label("NPC: ", skin);
        NPCLabel.setFontScale(1,1);
        NPCLabel.setColor(Color.BLACK);
        SpecialLabel = new Label("Special: ", skin);
        SpecialLabel.setFontScale(1,1);
        SpecialLabel.setColor(Color.BLACK);

        //tRootActors
        vertTextField = new TextField("", skin);
        vertTextField.setMessageText("Vertices");

        edgeTextField = new TextField("", skin);
        edgeTextField.setMessageText("Edges");

        minSquareTextField = new TextField("", skin);
        minSquareTextField.setMessageText("MinSquares");

        maxSquareTextField = new TextField("", skin);
        maxSquareTextField.setMessageText("MaxSquares");

        testIterationsTextField = new TextField("", skin);
        testIterationsTextField.setMessageText("TestIterations");

        workingLabel = new Label("Waiting Test", skin);
        workingLabel.setColor(Color.WHITE);

        final TextButton testButton = new TextButton("RunTest", skin, "default");
        testButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                workingLabel.setColor(Color.RED);
                workingLabel.setText("Working");
                if(RandomCubeComplex.RunTest(Integer.parseInt(vertTextField.getText()),
                        Integer.parseInt(edgeTextField.getText()), Integer.parseInt(minSquareTextField.getText()),
                        Integer.parseInt(maxSquareTextField.getText()), Integer.parseInt(testIterationsTextField.getText())))
                {
                    workingLabel.setColor(Color.WHITE);
                    workingLabel.setText("Test Finished");
                }
                else
                {
                    workingLabel.setColor(Color.RED);
                    workingLabel.setText("There was an error");
                }
            }
        });

        //Make The tables
        root.left().bottom();
        root.add(pointerButton);
        root.add(eraserButton);
        root.add(vertexButton);
        root.add(edgeButton);
        root.add(squareButton);
        root.row();
        root.add(id1Button);
        root.add(id2Button);
        root.add(id3Button);
        root.add(id4Button);
        root.row();
        root.add(whiteButton);
        root.add(blackButton);
        root.add(lightblueButton);
        root.add(blueButton);
        root.add(purpleButton);
        root.add(redButton);
        root.add(darkredButton);
        root.add(greenButton);
        root.add(yellowButton);

        cRoot.top().left();
        cRoot.add(button).width(200).height(64);
        cRoot.row();
        cRoot.add(GroupPresentationLabel);
        cRoot.row();
        cRoot.add(NPCLabel);
        cRoot.row();
        cRoot.add(SpecialLabel);

        tRoot.top().right();
        tRoot.add(vertTextField);
        tRoot.add(edgeTextField);
        tRoot.add(minSquareTextField);
        tRoot.add(maxSquareTextField);
        tRoot.add(testIterationsTextField);
        tRoot.row();
        tRoot.add(testButton);
        tRoot.add(workingLabel);

        //Set data members
        sB = new SpriteBatch();
        sR = new ShapeRenderer();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(w, h);
        camera.position.set(0, 0, 0);
        camera.update();

        vertexImage = new TextureRegion(new Texture(Gdx.files.internal("Images/Vertex.png")));
        edgeIdentifiers = new TextureRegion[4];
        edgeIdentifiers[0] = new TextureRegion(new Texture(Gdx.files.internal("Images/EID1.png")));
        edgeIdentifiers[1] = new TextureRegion(new Texture(Gdx.files.internal("Images/EID2.png")));
        edgeIdentifiers[2] = new TextureRegion(new Texture(Gdx.files.internal("Images/EID3.png")));
        edgeIdentifiers[3] = new TextureRegion(new Texture(Gdx.files.internal("Images/EID4.png")));

        prevPosition = new Vector2(0,0);

        selectedVerts = new int[]{-1,-1};
        selectedEdges = new int[]{-1,-1,-1,-1};

        switchTool(Tools.Pointer);

        complex = new CubeComplexDisplay();
    }

    @Override
    public void dispose () {
        ui.dispose();
        skin.dispose();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(.8f, .8f, .75f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateFling();
        camera.update();
        sB.setProjectionMatrix(camera.combined);
        sR.setProjectionMatrix(camera.combined);

        int edgeToHighlight = -1;

        for(int i = 3; i > -1; --i)
        {
            if(selectedEdges[i] != -1)
            {
                edgeToHighlight = selectedEdges[i];
                break;
            }
        }

        complex.Draw(sB, sR, vertexImage, edgeIdentifiers, -1, edgeToHighlight, -1);

        ui.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        ui.draw();
    }

    private void switchTool(Tools tool)
    {
        currentTool = tool;

        for(int i = 0; i < selectedVerts.length; ++i)
            selectedVerts[i] = -1;

        for(int i = 0; i < selectedEdges.length; ++i)
            selectedEdges[i] = -1;
    }

    private void switchColor(Color color)
    {
        currentColor = color;

        switchTool(Tools.Identify);
    }

    @Override
    public void resize (int width, int height)
    {
        ui.getViewport().update(width, height, true);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public boolean touchDown (float x, float y, int pointer, int button) {
        flinging = false;
        initialScale = camera.zoom;
        return false;
    }

    @Override
    public boolean tap (float x, float y, int count, int button) {

        return false;
    }

    @Override
    public boolean longPress (float x, float y) {
        Vector3 unprojected = camera.unproject(new Vector3(x,y,0));
        Vector2 tapPos = new Vector2(unprojected.x, unprojected.y);
        int tapEdge = complex.HoveringEdge(tapPos);

        if(tapEdge > -1 && !flipped)
        {
            complex.flipEdgeIdentifier(tapEdge);
            flipped = true;
            Gdx.app.log("I flipped!", "Why?");
        }

        return false;
    }

    @Override
    public boolean fling (float velocityX, float velocityY, int button) {
        if(draggingIndex == -1)
        {
            flinging = true;
            velX = camera.zoom * velocityX * 0.5f;
            velY = camera.zoom * velocityY * 0.5f;
        }
        return false;
    }

    @Override
    public boolean pan (float x, float y, float deltaX, float deltaY) {
        if(draggingIndex == -1)
            camera.position.add(-deltaX * camera.zoom, deltaY * camera.zoom, 0);
        return false;
    }

    @Override
    public boolean zoom (float originalDistance, float currentDistance) {
        float ratio = originalDistance / currentDistance;
        camera.zoom = initialScale * ratio;
        System.out.println(camera.zoom);
        return false;
    }

    @Override
    public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
        return false;
    }

    public void updateFling() {
        if (flinging) {
            velX *= 0.98f;
            velY *= 0.98f;
            camera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
            if (Math.abs(velX) < 0.04f) velX = 0;
            if (Math.abs(velY) < 0.04f) velY = 0;
        }
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        Vector3 unprojected = camera.unproject(new Vector3(screenX,screenY,0));
        Vector2 tapPos = new Vector2(unprojected.x, unprojected.y);

        int VhoveringIndex = complex.HoveringVertex(tapPos);
        int EhoveringIndex = complex.HoveringEdge(tapPos);
        int ShoveringIndex = complex.HoveringSquare(tapPos);

        switch(currentTool)
        {
            case Pointer:


            if(VhoveringIndex > -1)
            {
                draggingVertex = true;
                draggingIndex = VhoveringIndex;
                prevPosition = tapPos;
            }
            else if(EhoveringIndex > -1)
            {
                draggingEdge = true;
                draggingIndex = EhoveringIndex;
                prevPosition = tapPos;
            }
            else if(ShoveringIndex > -1)
            {
                draggingSquare = true;
                draggingIndex = ShoveringIndex;
                prevPosition = tapPos;
            }
                break;
            case Eraser:
                if(VhoveringIndex > -1)
                {
                    complex.removeVertex(VhoveringIndex);
                }
                else if(EhoveringIndex > -1)
                {
                    complex.removeEdge(EhoveringIndex);
                }
                else if(ShoveringIndex > -1)
                {
                    complex.removeSquare((ShoveringIndex));
                }
                break;
            case Vertex:
                complex.addVertex(tapPos, new Color(0,0,0,1));
                break;
            case Edge:
                int vertexTapped = complex.HoveringVertex(tapPos);
                if(vertexTapped > -1)
                {
                    for(int i = 0; i < selectedVerts.length; ++i)
                    {
                        if(selectedVerts[i] == -1)
                        {
                            selectedVerts[i] = vertexTapped;
                            if(i == 1)
                            {
                                complex.addEdge(new Pair<Integer, Integer>(selectedVerts[0], selectedVerts[1]));
                                switchTool(Tools.Edge);
                            }
                            break;
                        }
                    }
                }
                break;
            case Square:
                int edgeTapped = complex.HoveringEdge(tapPos);
                if(edgeTapped > -1)
                {
                    for(int i = 0; i < selectedEdges.length; ++i)
                    {
                        if(selectedEdges[i] == -1)
                        {
                            selectedEdges[i] = edgeTapped;
                            Gdx.app.log("Added an Edge", "Edge: " + edgeTapped);
                            if(i == 3)
                            {
                                complex.addSquare(new Quartet<Integer, Integer, Integer, Integer>(selectedEdges[0],selectedEdges[1],selectedEdges[2],selectedEdges[3]));
                                switchTool(Tools.Square);
                                Gdx.app.log("Completed Adding a Square", "Hurray!");
                            }
                            break;
                        }
                    }
                }
                break;
            case Identify:
                int tapVertex = complex.HoveringVertex(tapPos);
                int tapEdge = complex.HoveringEdge(tapPos);
                if(tapVertex>-1)
                {
                    complex.identifyVertex(tapVertex, currentColor);
                }
                else if(tapEdge>-1)
                {
                    complex.identifyEdge(tapEdge, currentColor, edgeIdentifier);
                }
                break;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        draggingVertex = false;
        draggingEdge = false;
        draggingSquare = false;
        draggingIndex = -1;
        flipped = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        if(draggingIndex > -1)
        {
            Vector3 unprojected = camera.unproject(new Vector3(screenX, screenY, 0));
            Vector2 tapPos = new Vector2(unprojected.x, unprojected.y);

            tapPos.add(-prevPosition.x, -prevPosition.y);
            if(draggingVertex)
            {
                complex.moveVertex(tapPos, draggingIndex);
            }
            else if(draggingEdge)
            {
                complex.moveEdge(tapPos, draggingIndex);
            }
            else if(draggingSquare)
            {
                complex.moveSquare(tapPos, draggingIndex);
            }
            prevPosition.add(tapPos);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
}