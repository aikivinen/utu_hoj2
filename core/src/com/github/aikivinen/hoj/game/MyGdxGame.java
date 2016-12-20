package com.github.aikivinen.hoj.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame extends ApplicationAdapter {

    long lastMov;

    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_HEIGHT = 8;
    public static final int SQUARE_SIZE = 50;

    final int BOARD_MARGIN_SIDES = 50;
    final int BOARD_MARGIN_TOP_BOTT = 50;

    SpriteBatch batch;
    OrthographicCamera camera;

    final Rectangle[][] board = new Rectangle[BOARD_WIDTH][BOARD_HEIGHT];
    private Texture redSquare;
    private Texture blackSquare;
    private Texture availableMoveSquare;

    private Piece selectedPiece;

    private Fox[] foxes = new Fox[1];


    @Override
    public void create() {
        Fox fox = new Fox();
        foxes[0] = fox;

        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {

                Rectangle rect = new Rectangle();
                rect.setSize(SQUARE_SIZE);
                rect.setX(i * SQUARE_SIZE + BOARD_MARGIN_SIDES);
                rect.setY(j * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT);
                board[i][j] = rect;
            }
        }


        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 640);

        batch = new SpriteBatch();
        redSquare = new Texture("red.png");
        blackSquare = new Texture("black.png");
        availableMoveSquare = new Texture("green.png");


    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Fox myFox = foxes[0];


        // keyboard movement
        if (TimeUtils.nanoTime() - lastMov > 150000000) {

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                myFox.setLocationX(myFox.getLocationX() - 1);
                lastMov = TimeUtils.nanoTime();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                myFox.setLocationX(myFox.getLocationX() + 1);
                lastMov = TimeUtils.nanoTime();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                myFox.setLocationY(myFox.getLocationY() + 1);
                lastMov = TimeUtils.nanoTime();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                myFox.setLocationY(myFox.getLocationY() - 1);
                lastMov = TimeUtils.nanoTime();
            }
        }

        // prevent piece(s) from moving outside of the board
        if (myFox.getLocationX() < 0) {
            myFox.setLocationX(0);
        }
        if (myFox.getLocationX() >= BOARD_WIDTH) {
            myFox.setLocationX(BOARD_WIDTH - 1);
        }
        if (myFox.getLocationY() < 0) {
            myFox.setLocationY(0);
        }
        if (myFox.getLocationY() >= BOARD_HEIGHT) {
            myFox.setLocationY(BOARD_HEIGHT - 1);
        }



        batch.begin();
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                boolean isBlack = (x + y) % 2 == 0;
                Rectangle rect = board[x][y];
                batch.draw(isBlack ? blackSquare : redSquare, rect.x, rect.y, rect.width, rect.height);
            }
        }

        drawPiece(myFox);

        batch.end();


    }

    private void drawPiece(Piece piece) {
        batch.draw(
                piece.getTexture(),
                piece.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES + (SQUARE_SIZE - Piece.PIECE_SIZE) / 2,
                piece.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_SIDES + (SQUARE_SIZE - Piece.PIECE_SIZE) / 2,
                piece.width,
                piece.height);
    }

    @Override
    public void dispose() {

        for (Disposable disposable : new Disposable[]{batch, redSquare, blackSquare}) {
            disposable.dispose();
        }

    }
}
