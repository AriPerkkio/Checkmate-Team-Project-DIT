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
    private String boardLayout; // Indicates place of each piece, i.e. "A1 A2 A3 A4 A5". Order is same as in TextureGL pieces'

    // Matrix Initializations
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];

    // Angle of the view
    public float mAngle; // Angle of view
    public static boolean rotated = false; // Also accessed from TextureGL

    // Picture for drawing
    private TextureGL picture = null; // Includes all the textures
    private float[] coordinates; // For matrix coordinates
    private List<float[]> coordinateList = new Vector<>(); // Matrix coordinates for each texture
    private int theme = R.mipmap.defaulttheme; // Chosen theme
    private boolean promotingOn = false;
    private String promotingPlayer = "";

    public OpenGLRenderer(final Context activityContext)
    {
        /** When device is rotated or activity is relaunched the whole class is built again - all the variables reset back to default **/

        Log.d("Renderer", "Constructor");
        mActivityContext = activityContext; // catch the context
        viewInstance = OpenGLView.getInstance(); // Initialize all the instances
        instance = this; // Update instance
        GameController.graphics = this; // Set instance

        if(gameController==null) gameController= GameController.getInstance();

        promotingOn = gameController.getPawnPromoting(); // Update promoting attribute from gameController
        if(gameController.getTurn())
            promotingPlayer = "PlayerOne"; // PlayerOne's turn
        else
            promotingPlayer = "PlayerTwo"; // PlayerTwo's turn
    }

    public static OpenGLRenderer getInstance(){ // Get reference for current instance
        return instance;
    }

    // Called when view created
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        Log.d("Renderer","onSurfaceCreated");

        setupCoordinates();

       // Initialize the drawn picture
        picture = new TextureGL(mActivityContext, // OpenGLView's context
                coordinates, // Starting point coordinates
                theme,  // Picture for the theme package
                gameController.getTextureIdToPiece());

        if(!gameController.getTurn() && rotated){ // Check if board was rotated before
            rotate();
            rotated=!rotated;
        }

        /** ERROR LOG **/ if(promotingOn && promotingPlayer.equals(" ")) Log.e("Renderer", "Promoting on, player empty"); // Promoting is on without player

        if(promotingOn) // Check if pawn promoting was called previously (Before OpenGL Surface was finished last time)
            pawnPromoteOn(promotingPlayer); // Set up promoting window for chosen player

        if(gameController.initialRotate()) // Game started with rotated board
            rotate();
    }

    public void setupCoordinates(){
        coordinateList.clear();

        // Order of coordinateList must match TextureGL's textureCoordinates
        coordinateList.add(allCoordinates.boardCoordinates); // Board

        // Learning tool
        for(int i=0;i<TextureGL.count;i++)
            coordinateList.add(allCoordinates.hideCoordinates);

        boardLayout = gameController.getPieceLayout(); // Get current board layout
        /** ERROR LOG **/  if(boardLayout.split(" ").length!=32) Log.e("Renderer","BoardLayout, Size "+boardLayout.split(" ").length); // Error check

        for(int i=0;i<boardLayout.split(" ").length;i++) // Setup pieces to board or as empty
            coordinateList.add(allCoordinates.coordinateList.get(boardLayout.split(" ")[i])); // Each square is separated by space - check piece order from TextureGL

        // Extra graphics
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Window
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #1
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #2
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #3
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #4

        Log.d("Renderer", "CoordList size "+coordinateList.size()); // See if matches with TextureGL's log output
        coordinates = new float[coordinateList.size()*8]; // Setup coordinate float[] for all the coordinates
        coordinates = setupMatrixCoordinates(coordinateList); // Combine multiple float[]s to one

    }

    // Called for each print
    public void onDrawFrame(GL10 unused) {
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

    public void refresh(){
        float[] coordinates;
        float left; // Order of matrix coordinates (0, 4, 1, 3 - left, right, top, bot)
        float right;
        float top;
        float bot;
        float[] texture;
        float textLeft; // Order of texture coordinates are flipped (4, 0, 3, 1 - left, right, top, bot)
        float textRight;
        float textTop;
        float textBot;

        setupCoordinates(); // Construct latest coordinates for pieces
        Map<Integer, Piece> textureIdToPiece = gameController.getTextureIdToPiece(); // Get latest textureId&Piece pairs
        for(int i=TextureGL.count+1;i<TextureGL.count+33;i++) { // Loop through reserved piece texture ids
            if(textureIdToPiece.get(i) != null) { // Texture with piece
                String player = "PlayerOne";
                if (!textureIdToPiece.get(i).getPlayer().isFirst())
                    player = "PlayerTwo";
                coordinates = allCoordinates.coordinateList.get(textureIdToPiece.get(i).getSquare().getId());
                texture = allCoordinates.promotePieces.get(textureIdToPiece.get(i).getPieceType().toLowerCase() + player);
            }
            else { // Texture without piece - make it empty
                coordinates = allCoordinates.hideCoordinates;
                texture = allCoordinates.hideCoordinates;
            }
            left = coordinates[0]; // Order of matrix coordinates (0, 4, 1, 3 - left, right, top, bot)
            right = coordinates[4];
            top = coordinates[1];
            bot = coordinates[3];

            textLeft = texture[4]; // Order of texture coordinates are flipped (4, 0, 3, 1 - left, right, top, bot)
            textRight = texture[0];
            textTop = texture[3];
            textBot = texture[1];
            picture.changePieceCoordinates(i, left, right, top, bot); // Move piece
            picture.changeTextureCoordinates(i, textLeft, textRight, textTop, textBot); // Setup correct texture
        }
        viewInstance.requestRender(); // Render changes
    }

    // Used in TextureGL
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // For getting touch events from OpenGLView
    public void processTouchEvent(MotionEvent event) {

        float x = event.getX(); // Get horizontal pixel value
        float y = event.getY(); // Get vertical pixel value
        float screenWidth = (float) GameActivity.getscreenwidth(); // Setup screen props
        float screenHeight =  (float) GameActivity.getscreenheight();
        String orientation = (String) GameActivity.getorientation();

        float gameViewEdge = screenHeight * (screenWidth/screenHeight); // Calculate length of one edge using screen ratio
        //Log.d("screenProps", "Height: "+screenHeight+". \nWidth: "+screenWidth+". \nRatio: "+(screenWidth/screenHeight)+".\nGameViewEdge: "+gameViewEdge+"\nOrientation: "+orientation);

        // Convert pixels into OpenGL coordinate system
        float sceneX = (x / gameViewEdge) * 2.0f - 1.0f;
        float sceneY = (y / gameViewEdge) * -2.0f + 1.0f;

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
            if(!gameController.learningTool && // LearningTool disabled
               !((color+" "+shape).equals("green square")) && // Allow highlighting clicked piece
               !((color+" "+shape).equals("red square"))) // Allow highlighting king in check
                square = "hide"; // Change given highlight texture to hidden one

            // Get chosen learningTool texture
            float[] learningToolTexture = allCoordinates.learningToolList.get(color+" "+shape);

            if(square.equals("hide"))
                picture.changePieceCoordinates(i + 1,
                        allCoordinates.hideCoordinates[0], // Left
                        allCoordinates.hideCoordinates[4], // Right
                        allCoordinates.hideCoordinates[1], // Top
                        allCoordinates.hideCoordinates[3]  // Bottom
                );
            else
                // Set square's coordinates
                picture.changePieceCoordinates(i + 1,
                        allCoordinates.coordinateList.get(square)[0], // Left
                        allCoordinates.coordinateList.get(square)[4], // Right
                        allCoordinates.coordinateList.get(square)[1], // Top
                        allCoordinates.coordinateList.get(square)[3]  // Bottom
                );

            // Set shape and color
            picture.changeTextureCoordinates(i + 1,
                    learningToolTexture[0], // Left
                    learningToolTexture[4], // Right
                    learningToolTexture[1], // Top
                    learningToolTexture[3]  // Bottom
            );
        }
    }

    public void promotePawn(int pieceSelect, String textureName) {

        /** ERROR LOG **/ if (allCoordinates.promotePieces.get(textureName) == null) Log.e("Rendr.PromotePawn", "Name: " + textureName);

        float[] texture = allCoordinates.promotePieces.get(textureName);

        picture.changeTextureCoordinates(pieceSelect,
                texture[4],
                texture[0],
                texture[3],
                texture[1]
        );
    }

    public void pawnPromoteOn(String _player){
        promotingOn = true; // Indicates that pawn promoting is turned on
        promotingPlayer = _player; // ...for this player
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
        if(!rotated){
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
        viewInstance.requestRender();
    }

    // Hide pawn promoting window
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
        promotingOn = false;
        promotingPlayer = "";
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