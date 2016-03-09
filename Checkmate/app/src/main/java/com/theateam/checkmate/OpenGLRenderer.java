package com.theateam.checkmate;



import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Class to control graphic rendering
 * See class diagram for detailed info
 *
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer{

    private final Context mActivityContext;
    private Coordinates allCoordinates = new Coordinates(); // For texture and matrix coordinates
    static OpenGLView viewInstance = OpenGLView.getInstance(); // Used for renderRequests
    static OpenGLRenderer instance; // For the current instance
    static GameController gameController = GameController.getInstance(); // Used for passing clicked square

    // Matrix Initializations
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];

    // Angle of the view
    public float mAngle; // Angle of view
    public static boolean rotated = false; // Also accessed from TextureGL

    // Picture for drawing
    private TextureGL picture; // Includes all the textures
    private float[] coordinates; // For matrix coordinates
    private List<float[]> coordinateList = new Vector<>(); // Matrix coordinates for each texture

    public OpenGLRenderer(final Context activityContext)
    {
        Log.d("Renderer", "Constructor");
        mActivityContext = activityContext; // catch the context
        viewInstance = OpenGLView.getInstance(); // Initialize all the instances
        instance = this;
        GameController.graphics = this;

        if(gameController==null){ // Error checking
            Log.e("Renderer", "GameController reinitializing");
            gameController= GameController.getInstance();
        }
    }

    public static OpenGLRenderer getInstance(){ // Get reference for current instance
        return instance;
    }

    // Called when view created
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        Log.d("Renderer","onSurfaceCreated");

        // TODO: This kind of initializing may not be used once Game Logic works.
        // TODO: Game logic should call each piece to set up into its starting point.
        // TODO: Only board is drawn by calling printBoard()

        // Order of coordinateList must match TextureGL's textureCoordinates
        // Current IDs: 0 reserved for board
        //              1-27 Square Highlights
        //              28-44 Player One pieces
        //              39-55 Player Two pieces
        coordinateList.add(allCoordinates.boardCoordinates); // Board
        // Learning tool
        for(int i=0;i<TextureGL.count;i++)
            coordinateList.add(allCoordinates.hideCoordinates);  // Learning Tool #1-27
        // Player one
        coordinateList.add(allCoordinates.coordinateList.get("A2")); // Pawn #1 Player One
        coordinateList.add(allCoordinates.coordinateList.get("B2")); // Pawn #2 Player One
        coordinateList.add(allCoordinates.coordinateList.get("C2")); // Pawn #3 Player One
        coordinateList.add(allCoordinates.coordinateList.get("D2")); // Pawn #4 Player One
        coordinateList.add(allCoordinates.coordinateList.get("E2")); // Pawn #5 Player One
        coordinateList.add(allCoordinates.coordinateList.get("F2")); // Pawn #6 Player One
        coordinateList.add(allCoordinates.coordinateList.get("G2")); // Pawn #7 Player One
        coordinateList.add(allCoordinates.coordinateList.get("H2")); // Pawn #8 Player One
        coordinateList.add(allCoordinates.coordinateList.get("A1")); // Rook #1 Player One
        coordinateList.add(allCoordinates.coordinateList.get("B1")); // Knight #1 Player One
        coordinateList.add(allCoordinates.coordinateList.get("C1")); // Bishop #1 Player One
        coordinateList.add(allCoordinates.coordinateList.get("D1")); // King Player One
        coordinateList.add(allCoordinates.coordinateList.get("E1")); // Queen Player One
        coordinateList.add(allCoordinates.coordinateList.get("F1")); // Bishop #2 Player One
        coordinateList.add(allCoordinates.coordinateList.get("G1")); // Knight #2 Player One
        coordinateList.add(allCoordinates.coordinateList.get("H1")); // Rook #2 Player One
        // Player two
        coordinateList.add(allCoordinates.coordinateList.get("A7")); // Pawn #1 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("B7")); // Pawn #2 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("C7")); // Pawn #3 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("D7")); // Pawn #4 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("E7")); // Pawn #5 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("F7")); // Pawn #6 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("G7")); // Pawn #7 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("H7")); // Pawn #8 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("A8")); // Rook #1 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("B8")); // Knight #1 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("C8")); // Bishop #1 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("D8")); // King Player Two
        coordinateList.add(allCoordinates.coordinateList.get("E8")); // Queen Player Two
        coordinateList.add(allCoordinates.coordinateList.get("F8")); // Bishop #2 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("G8")); // Knight #2 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("H8")); // Rook #2 Player Two
        // Extra graphics
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Window
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #1
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #2
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #3
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #4

        Log.d("Renrdr. CoordList size:", ""+coordinateList.size()); // See if matches with TextureGL's log output
        coordinates = new float[coordinateList.size()*8];
        coordinates = setupMatrixCoordinates(coordinateList); // Combine multiple float[]s to one

       // Initialize the drawn picture
       picture = new TextureGL(mActivityContext, // OpenGLView's context
               coordinates, // Starting point coordinates
               R.mipmap.wooden); // Picture for the theme package
    }

    // Called for each print
    public void onDrawFrame(GL10 unused)
    {
        // Clear everything before draw - otherwise rotating looks strange
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // Set eyes to selected place, xyz
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Sets the view projection
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        // Rotate the view by mAngle as degrees
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);
        // Apply the rotation
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
        // Draw the picture using matrix values as set above
        picture.Draw(mMVPMatrix);
    }

    // Used in TextureGL
    public static int loadShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // For getting touch events from OpenGLView
    public void processTouchEvent(MotionEvent event) {

        float x = event.getX(); // Get horizontal pixel value
        float y = event.getY(); // Get vertical pixel value
        float screenWidth = 1080; // Setup screen props
        float screenHeight =  1920*0.5625f; // 9/16 = 0.5625

        // Convert pixels into OpenGL coordinate system
        float sceneX = (x / screenWidth) * 2.0f - 1.0f;
        float sceneY = (y / screenHeight) * -2.0f + 1.0f;

        // Convert coordinates into chessboard's square
        String clickedSquare = coordinatesToSquare(sceneX, sceneY);
        Log.d("Clicked Square: ", clickedSquare);

        // Pass info to gameController
        gameController.selectSquare(clickedSquare);
    }


    // Called i.e. when rotating screen landscape - portrait
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        Log.d("Renderer","onSurfaceChanged");
        // Set window attributes again since screen props changed
        GLES20.glViewport(0, 0, width, height); // Reset screen dimensions
        float ratio = (float) width / height; // Calculate screen ratio
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7); // Pass changed values
    }

    // Combine multiple float[] together
    public float[] setupMatrixCoordinates(List<float[]> coordinateList){
        float[] returnArray = new float[coordinateList.size()*8]; // Size multiplied by 2D corner count

        for(int i=0;i<coordinateList.size();i++) { // All the float[]
            for(int x=0;x<8;x++) // All the coordinates
                returnArray[i * 8 + x] = coordinateList.get(i)[x];
        }
        return returnArray;
    }

    // Moves specific piece from source to target
    public void movePiece(int pieceSelect, String targetSquare, String sourceSquare){

        // TODO: Knights movement may require different direction

        // Get float[] coordinates for the given String square
        float[] target = allCoordinates.coordinateList.get(targetSquare);
        float[] source = allCoordinates.coordinateList.get(sourceSquare);
        float left = source[0];
        float right = source[4];
        float top = source[1];
        float bot = source[3];

        int movementCount = 250; // Adjusting this determines speed
        for(int i=0;i<movementCount;i++){ // Loop in small steps to create animation
            left  += ((target[0]-source[0])/movementCount);
            right += ((target[4]-source[4])/movementCount);
            top   += ((target[1]-source[1])/movementCount);
            bot   += ((target[3]-source[3])/movementCount);
            picture.changePieceCoordinates(pieceSelect, left, right, top, bot);
            SystemClock.sleep(1); // Sleep 1ms
            viewInstance.requestRender(); // Render picture after each movement
                                          // Careful with this one. Too fast rendering throws runtime errors
        }
    }

    // Makes pieces shrink until they disappear
    public void eliminatePiece(int pieceSelect, String _square){

        // Get coordinates for given square
        float[] square = allCoordinates.coordinateList.get(_square);
        float left = square[0];
        float right = square[4];
        float top = square[1];
        float bot = square[3];

        int fadingCount = 250; // Fading speed is determined by this
        for(int i=0;i<fadingCount/2;i++){
            left  -= ((square[0]-square[4])/fadingCount);
            right -= ((square[4]-square[0])/fadingCount);
            top   -= ((square[1]-square[3])/fadingCount);
            bot   -= ((square[3]-square[1])/fadingCount);
            picture.changePieceCoordinates(pieceSelect, left, right, top, bot);
            SystemClock.sleep(5); // Sleep 5ms
            viewInstance.requestRender(); // Render picture after each movement
            // Careful with this one. Too fast rendering throws runtime errors
        }
        // After making it disappear set it to 0,0,0,0 coordinates
        picture.changePieceCoordinates(pieceSelect, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    // Rotate board and pieces. Refresh the view
    public void rotate(){

        int rotateCount = 180; // Rotation speed and angle is determined by this
        for(int i=0;i<rotateCount;i++){
            mAngle ++; // Increase current angle
            SystemClock.sleep(5); // Sleep 5ms
            viewInstance.requestRender();
        }
        rotated = !rotated; // Invert status of rotation
        picture.rotatePieces(); // Rotate all the pieces 180 degrees
        viewInstance.requestRender();
    }

    // Highlight specific squares with chosen shape+color
    // List<String[]> list consists of Square and shape combinations
    // I.e. {"A3", "circle", "blue"}
    public void highlight(List<String[]> list){

        for(int i=0; i<list.size();i++) {
            String square = list.get(i)[0];
            String shape = list.get(i)[1];
            String color = list.get(i)[2];
            // Get chosen learningTool texture
            float[] learningToolTexture = allCoordinates.learningToolList.get(color+" "+shape);

            if(square.equals("hide")){
                picture.changePieceCoordinates(i + 1,
                        allCoordinates.hideCoordinates[0], // Left
                        allCoordinates.hideCoordinates[4], // Right
                        allCoordinates.hideCoordinates[1], // Top
                        allCoordinates.hideCoordinates[3]  // Bottom
                );
            }
            else {
                // Set square's coordinates
                picture.changePieceCoordinates(i + 1,
                        allCoordinates.coordinateList.get(square)[0], // Left
                        allCoordinates.coordinateList.get(square)[4], // Right
                        allCoordinates.coordinateList.get(square)[1], // Top
                        allCoordinates.coordinateList.get(square)[3]  // Bottom
                );
            }

            // Set shape and color
            picture.changeTextureCoordinates(i + 1,
                    learningToolTexture[0], // Left
                    learningToolTexture[4], // Right
                    learningToolTexture[1], // Top
                    learningToolTexture[3]  // Bottom
            );

        }
    }

    public void promotePawn(int pieceSelect, String textureName){

        if(allCoordinates.promotePieces.get(textureName) == null)
            Log.e("Rendr.PromotePawn", "Name: "+textureName);

        float[] texture = allCoordinates.promotePieces.get(textureName);

        picture.changeTextureCoordinates(pieceSelect,
                texture[4],
                texture[0],
                texture[3],
                texture[1]
        );
    }

    public void pawnPromoteOn(String _player){
                                            // Board+Highlights+all the pieces
        int windowId = TextureGL.count+1+32;
        int pieceId = windowId+1;

        picture.changeTextureCoordinates(windowId,
                allCoordinates.learningTool_square_grey[0],
                allCoordinates.learningTool_square_grey[4],
                allCoordinates.learningTool_square_grey[1],
                allCoordinates.learningTool_square_grey[3]
        );
        picture.changePieceCoordinates(windowId,
                allCoordinates.promotePawnWindow[4],
                allCoordinates.promotePawnWindow[0],
                allCoordinates.promotePawnWindow[3],
                allCoordinates.promotePawnWindow[1]
        );
        if(_player.equals("PlayerOne")){
            picture.changeTextureCoordinates(pieceId,
                    allCoordinates.promotePieces.get("queen"+_player)[0],
                    allCoordinates.promotePieces.get("queen"+_player)[4],
                    allCoordinates.promotePieces.get("queen"+_player)[1],
                    allCoordinates.promotePieces.get("queen"+_player)[3]
            );
            picture.changeTextureCoordinates(pieceId+1,
                    allCoordinates.promotePieces.get("rook"+_player)[0],
                    allCoordinates.promotePieces.get("rook"+_player)[4],
                    allCoordinates.promotePieces.get("rook"+_player)[1],
                    allCoordinates.promotePieces.get("rook"+_player)[3]
            );
            picture.changeTextureCoordinates(pieceId+2,
                    allCoordinates.promotePieces.get("bishop"+_player)[0],
                    allCoordinates.promotePieces.get("bishop"+_player)[4],
                    allCoordinates.promotePieces.get("bishop"+_player)[1],
                    allCoordinates.promotePieces.get("bishop"+_player)[3]
            );
            picture.changeTextureCoordinates(pieceId+3,
                    allCoordinates.promotePieces.get("knight"+_player)[0],
                    allCoordinates.promotePieces.get("knight"+_player)[4],
                    allCoordinates.promotePieces.get("knight"+_player)[1],
                    allCoordinates.promotePieces.get("knight"+_player)[3]
            );
        }
        else
        {
            picture.changeTextureCoordinates(pieceId,
                    allCoordinates.promotePieces.get("knight"+_player)[0],
                    allCoordinates.promotePieces.get("knight"+_player)[4],
                    allCoordinates.promotePieces.get("knight"+_player)[1],
                    allCoordinates.promotePieces.get("knight"+_player)[3]
            );
            picture.changeTextureCoordinates(pieceId+1,
                    allCoordinates.promotePieces.get("bishop"+_player)[0],
                    allCoordinates.promotePieces.get("bishop"+_player)[4],
                    allCoordinates.promotePieces.get("bishop"+_player)[1],
                    allCoordinates.promotePieces.get("bishop"+_player)[3]
            );
            picture.changeTextureCoordinates(pieceId+2,
                    allCoordinates.promotePieces.get("rook"+_player)[0],
                    allCoordinates.promotePieces.get("rook"+_player)[4],
                    allCoordinates.promotePieces.get("rook"+_player)[1],
                    allCoordinates.promotePieces.get("rook"+_player)[3]
            );
            picture.changeTextureCoordinates(pieceId+3,
                    allCoordinates.promotePieces.get("queen"+_player)[0],
                    allCoordinates.promotePieces.get("queen"+_player)[4],
                    allCoordinates.promotePieces.get("queen"+_player)[1],
                    allCoordinates.promotePieces.get("queen"+_player)[3]
            );
        }

        picture.changePieceCoordinates(pieceId,
                allCoordinates.promotePawnOne[4],
                allCoordinates.promotePawnOne[0],
                allCoordinates.promotePawnOne[3],
                allCoordinates.promotePawnOne[1]
        );
        picture.changePieceCoordinates(pieceId+1,
                allCoordinates.promotePawnTwo[4],
                allCoordinates.promotePawnTwo[0],
                allCoordinates.promotePawnTwo[3],
                allCoordinates.promotePawnTwo[1]
        );
        picture.changePieceCoordinates(pieceId+2,
                allCoordinates.promotePawnThree[4],
                allCoordinates.promotePawnThree[0],
                allCoordinates.promotePawnThree[3],
                allCoordinates.promotePawnThree[1]
        );
        picture.changePieceCoordinates(pieceId+3,
                allCoordinates.promotePawnFour[4],
                allCoordinates.promotePawnFour[0],
                allCoordinates.promotePawnFour[3],
                allCoordinates.promotePawnFour[1]
        );

    }

    public void pawnPromoteOff(){

        int windowId = coordinateList.size()-5;

        for(int i=0;i<5;i++) {

            picture.changeTextureCoordinates(windowId+i,
                    allCoordinates.hideCoordinates[0],
                    allCoordinates.hideCoordinates[4],
                    allCoordinates.hideCoordinates[1],
                    allCoordinates.hideCoordinates[3]
            );

            picture.changePieceCoordinates(windowId+i,
                    allCoordinates.hideCoordinates[0],
                    allCoordinates.hideCoordinates[4],
                    allCoordinates.hideCoordinates[1],
                    allCoordinates.hideCoordinates[3]
            );
        }
    }

    // Convert coordinates to String square
    public String coordinatesToSquare(float x, float y){
        String returnText = ""; // Consist of Column (A-H) and row (1-8)

        // Original squares
        String rowCharacters = "ABCDEFGH";
        String columnNumbers = "12345678";

        // Squares for rotated board
        if(rotated) {
            rowCharacters = "HGFEDCBA";
            columnNumbers = "87654321";
        }
        // Find column
        // Check if x is between left and right
        if(x>allCoordinates.coordinateList.get("A1")[0] && x<allCoordinates.coordinateList.get("A1")[4])
            returnText += rowCharacters.charAt(0);
        if(x>allCoordinates.coordinateList.get("B1")[0] && x<allCoordinates.coordinateList.get("B1")[4])
            returnText += rowCharacters.charAt(1);
        if(x>allCoordinates.coordinateList.get("C1")[0] && x<allCoordinates.coordinateList.get("C1")[4])
            returnText += rowCharacters.charAt(2);
        if(x>allCoordinates.coordinateList.get("D1")[0] && x<allCoordinates.coordinateList.get("D1")[4])
            returnText += rowCharacters.charAt(3);
        if(x>allCoordinates.coordinateList.get("E1")[0] && x<allCoordinates.coordinateList.get("E1")[4])
            returnText += rowCharacters.charAt(4);
        if(x>allCoordinates.coordinateList.get("F1")[0] && x<allCoordinates.coordinateList.get("F1")[4])
            returnText += rowCharacters.charAt(5);
        if(x>allCoordinates.coordinateList.get("G1")[0] && x<allCoordinates.coordinateList.get("G1")[4])
            returnText += rowCharacters.charAt(6);
        if(x>allCoordinates.coordinateList.get("H1")[0] && x<allCoordinates.coordinateList.get("H1")[4])
            returnText += rowCharacters.charAt(7);

        // Find row
        // Check if y is between bottom and top
            //                                    BOT <   y <  TOP
        if(allCoordinates.coordinateList.get("A1")[3] < y && y < allCoordinates.coordinateList.get("A1")[1])
            returnText += columnNumbers.charAt(0);
        if(y>allCoordinates.coordinateList.get("A2")[3] && y<allCoordinates.coordinateList.get("A2")[1])
            returnText += columnNumbers.charAt(1);
        if(y>allCoordinates.coordinateList.get("A3")[3] && y<allCoordinates.coordinateList.get("A3")[1])
            returnText += columnNumbers.charAt(2);
        if(y>allCoordinates.coordinateList.get("A4")[3] && y<allCoordinates.coordinateList.get("A4")[1])
            returnText += columnNumbers.charAt(3);
        if(y>allCoordinates.coordinateList.get("A5")[3] && y<allCoordinates.coordinateList.get("A5")[1])
            returnText += columnNumbers.charAt(4);
        if(y>allCoordinates.coordinateList.get("A6")[3] && y<allCoordinates.coordinateList.get("A6")[1])
            returnText += columnNumbers.charAt(5);
        if(y>allCoordinates.coordinateList.get("A7")[3] && y<allCoordinates.coordinateList.get("A7")[1])
            returnText += columnNumbers.charAt(6);
        if(y>allCoordinates.coordinateList.get("A8")[3] && y<allCoordinates.coordinateList.get("A8")[1])
            returnText += columnNumbers.charAt(7);

        // Check if click was out of the board
        if(returnText.length()!=2) // This includes especially cases like "A", "1" etc. Also "A1234567" if it still occurs
            returnText = "OutOfBoard"; // Receiver should 1st check if "OutOfBoard"

        return returnText;
    }
}