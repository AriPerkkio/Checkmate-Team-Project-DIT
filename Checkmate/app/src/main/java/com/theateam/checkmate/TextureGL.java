package com.theateam.checkmate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.Vector;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Texture/Pictures to be drawn.
 * See class diagram for detailed info
 *
 */
public class TextureGL {
    public static final int count = 50; // Used in multiple classes. Number of highlight textures

    private final Context mActivityContext;
    private FloatBuffer mCubeTextureCoordinates;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private final int mTextureCoordinateDataSize = 2;
    private int mTextureDataHandle;
    private final int shaderProgram;
    public FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private short drawOrder[];
    private final int COORDS_PER_VERTEX = 2;
    private float coordinates[];
    private final int vertexStride = COORDS_PER_VERTEX * 4; //Bytes per vertex
    private float color[] = {1.0f, 1.0f, 1.0f, 1.0f}; // Initial color


    private final String vertexShaderCode =
            "attribute vec2 a_TexCoordinate;" +
                    "varying vec2 v_TexCoordinate;" +
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "gl_Position = uMVPMatrix * vPosition;" +
                    "v_TexCoordinate = a_TexCoordinate;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +
                    "void main() {" +
                    "gl_FragColor = (vColor * texture2D(u_Texture, v_TexCoordinate));" +
                    "}";

    private Coordinates allCoordinates = new Coordinates();
    private List<float[]> coordinateList = new Vector<>();
    private float[] TextureCoordinateData;

    // Constructor (Parameters with current coordinates for all the pictures, resourceId to pick
    // correct theme. )
    public TextureGL(Context _context, float[] _coordinates, int _resourceId) {
        mActivityContext = _context;
        coordinates = _coordinates;
        drawOrder = new short[(coordinates.length/8)*6];

        // Fill drawOrder for each texture
        int last = 0;
        for (int i = 0; i < (coordinates.length / 8); i++) {
            drawOrder[(i * 6)] = (short) (last);
            drawOrder[(i * 6) + 1] = (short) (last + 1);
            drawOrder[(i * 6) + 2] = (short) (last + 2);
            drawOrder[(i * 6) + 3] = (short) (last);
            drawOrder[(i * 6) + 4] = (short) (last + 2);
            drawOrder[(i * 6) + 5] = (short) (last + 3);
            last = last + 4;
        }

        // Order of coordinateList must match OpenGLRenderer's coordinateList
        // Current IDs: 0 reserved for board
        //              1-27 Square Highlights
        //              28-44 Player One pieces
        //              39-55 Player Two pieces
        coordinateList.add(allCoordinates.boardTexture); // Board
        // Learning tool
        for(int i=0;i<count;i++)
            coordinateList.add(allCoordinates.learningTool_circle_green); // Learning tool #1-27
        // Player One
        coordinateList.add(allCoordinates.pawnPlayerOne); // Player one pawn #1
        coordinateList.add(allCoordinates.pawnPlayerOne); // Player one pawn #2
        coordinateList.add(allCoordinates.pawnPlayerOne); // Player one pawn #3
        coordinateList.add(allCoordinates.pawnPlayerOne); // Player one pawn #4
        coordinateList.add(allCoordinates.pawnPlayerOne); // Player one pawn #5
        coordinateList.add(allCoordinates.pawnPlayerOne); // Player one pawn #6
        coordinateList.add(allCoordinates.pawnPlayerOne); // Player one pawn #7
        coordinateList.add(allCoordinates.pawnPlayerOne); // Player one pawn #8
        coordinateList.add(allCoordinates.rookPlayerOne); // Rook #1 Player One
        coordinateList.add(allCoordinates.knightPlayerOne); // Knight #1 Player One
        coordinateList.add(allCoordinates.bishopPlayerOne); // Bishop #1 Player One
        coordinateList.add(allCoordinates.kingPlayerOne); // King Player One
        coordinateList.add(allCoordinates.queenPlayerOne); // Queen Player One
        coordinateList.add(allCoordinates.bishopPlayerOne); // Bishop #2 Player One
        coordinateList.add(allCoordinates.knightPlayerOne); // Knight #2 Player One
        coordinateList.add(allCoordinates.rookPlayerOne); // Rook #2 Player One
        // Player Two
        coordinateList.add(allCoordinates.pawnPlayerTwo); // Player two pawn #1
        coordinateList.add(allCoordinates.pawnPlayerTwo); // Player two pawn #2
        coordinateList.add(allCoordinates.pawnPlayerTwo); // Player two pawn #3
        coordinateList.add(allCoordinates.pawnPlayerTwo); // Player two pawn #4
        coordinateList.add(allCoordinates.pawnPlayerTwo); // Player two pawn #5
        coordinateList.add(allCoordinates.pawnPlayerTwo); // Player two pawn #6
        coordinateList.add(allCoordinates.pawnPlayerTwo); // Player two pawn #7
        coordinateList.add(allCoordinates.pawnPlayerTwo); // Player two pawn #8
        coordinateList.add(allCoordinates.rookPlayerTwo); // Rook #1 Player two
        coordinateList.add(allCoordinates.knightPlayerTwo); // Knight #1 Player two
        coordinateList.add(allCoordinates.bishopPlayerTwo); // Bishop #1 Player two
        coordinateList.add(allCoordinates.kingPlayerTwo); // King Player two
        coordinateList.add(allCoordinates.queenPlayerTwo); // Queen Player two
        coordinateList.add(allCoordinates.bishopPlayerTwo); // Bishop #2 Player two
        coordinateList.add(allCoordinates.knightPlayerTwo); // Knight #2 Player two
        coordinateList.add(allCoordinates.rookPlayerTwo); // Rook #2 Player two
        // Extra graphics
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Window
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #1
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #2
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #3
        coordinateList.add(allCoordinates.hideCoordinates); // Promote pawn, Piece #4

        Log.d("Textr. CoLst size:", ""+coordinateList.size());
        // Here add texture coordinates for each piece, board, square etc.
        TextureCoordinateData = setupTextureCoordinates(coordinateList); // We are using .png files which have y-axis inverted, so y coordinates 1-Y

        // Fill buffers
        ByteBuffer bb = ByteBuffer.allocateDirect(coordinates.length * 4).order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer().put(coordinates);
        vertexBuffer.position(0);

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(TextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(TextureCoordinateData).position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(coordinates.length * 2).order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer().put(drawOrder); // Note: Overflow here? -> Check initialize loop
        drawListBuffer.position(0);

        int vertexShader = OpenGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = OpenGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glBindAttribLocation(shaderProgram, 0, "a_TexCoordinate");
        GLES20.glLinkProgram(shaderProgram);
        mTextureDataHandle = setResource(mActivityContext, _resourceId);
    }

    // Use to set/change theme
    public int setResource(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);


            // Set transparent background
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glEnable(GLES20.GL_BLEND);


            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }

    public void Draw(float[] mvpMatrix)
    {
        GLES20.glUseProgram(shaderProgram);
        mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        mColorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        mTextureUniformHandle = GLES20.glGetAttribLocation(shaderProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "a_TexCoordinate");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, mCubeTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        //Get Handle to Shape's Transformation Matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");

        //Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        //Draw the picture
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        //Disable Vertex Array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void changePieceCoordinates(int pieceSelect, float left, float right, float top, float bottom){

        if(OpenGLRenderer.rotated) {
            coordinates[    8 * pieceSelect] = right;
            coordinates[1 + 8 * pieceSelect] = bottom;
            coordinates[2 + 8 * pieceSelect] = right;
            coordinates[3 + 8 * pieceSelect] = top;
            coordinates[4 + 8 * pieceSelect] = left;
            coordinates[5 + 8 * pieceSelect] = top;
            coordinates[6 + 8 * pieceSelect] = left;
            coordinates[7 + 8 * pieceSelect] = bottom;
        }
        else
        {
            coordinates[8 * pieceSelect] = left;
            coordinates[1 + 8 * pieceSelect] = top;
            coordinates[2 + 8 * pieceSelect] = left;
            coordinates[3 + 8 * pieceSelect] = bottom;
            coordinates[4 + 8 * pieceSelect] = right;
            coordinates[5 + 8 * pieceSelect] = bottom;
            coordinates[6 + 8 * pieceSelect] = right;
            coordinates[7 + 8 * pieceSelect] = top;
        }
        // Fill new coordinates to buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(coordinates.length* 4).order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer().put(coordinates);
        vertexBuffer.position(0);

    }

    public void changeTextureCoordinates(int textureSelect, float left, float right, float top, float bottom){
        TextureCoordinateData[    8 * textureSelect] = right;
        TextureCoordinateData[    8 * textureSelect] = right;
        TextureCoordinateData[1 + 8 * textureSelect] = bottom;
        TextureCoordinateData[2 + 8 * textureSelect] = right;
        TextureCoordinateData[3 + 8 * textureSelect] = top;
        TextureCoordinateData[4 + 8 * textureSelect] = left;
        TextureCoordinateData[5 + 8 * textureSelect] = top;
        TextureCoordinateData[6 + 8 * textureSelect] = left;
        TextureCoordinateData[7 + 8 * textureSelect] = bottom;

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(TextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(TextureCoordinateData).position(0);
    }

    public void rotatePieces(){

        // Loop starts from player one pieces coordinate ids
        for(int i=count+1;i<count+33;i++) {
            // Get initial values
            float left  = coordinates[    8 * i];
            float right = coordinates[4 + 8 * i];
            float top   = coordinates[7 + 8 * i];
            float bot   = coordinates[5 + 8 * i];

            // Update rotation
            coordinates[    8 * i] = right;
            coordinates[1 + 8 * i] = bot;
            coordinates[2 + 8 * i] = right;
            coordinates[3 + 8 * i] = top;
            coordinates[4 + 8 * i] = left;
            coordinates[5 + 8 * i] = top;
            coordinates[6 + 8 * i] = left;
            coordinates[7 + 8 * i] = bot;
        }

        // Fill new coordinates to buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(coordinates.length* 4).order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer().put(coordinates);
        vertexBuffer.position(0);
    }

    // Combine multiple float[] together
    public float[] setupTextureCoordinates(List<float[]> coordinateList){
        float[] returnArray = new float[coordinateList.size()*8];

        for(int i=0;i<coordinateList.size();i++) { // All the float[]
            for(int x=0;x<8;x++)
                returnArray[i * 8 + x] = coordinateList.get(i)[x];
        }
    return returnArray;
    }
}










