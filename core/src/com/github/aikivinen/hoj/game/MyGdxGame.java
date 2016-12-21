package com.github.aikivinen.hoj.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import java.util.Arrays;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {

    public enum Turn {FOX, HOUNDS}

    // fox starts the game
    private Turn currentTurn = Turn.FOX;

    public static final int SCREEN_HEIGHT = 640;

    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_HEIGHT = 8;
    public static final int SQUARE_SIZE = 50;

    final int BOARD_MARGIN_SIDES = 50;
    final int BOARD_MARGIN_TOP_BOTT = 50;

    private SpriteBatch batch;
    private OrthographicCamera camera;

    final Rectangle[][] board = new Rectangle[BOARD_WIDTH][BOARD_HEIGHT];
    private Texture redSquare;
    private Texture blackSquare;
//    private Texture availableMoveSquare;

    private Piece selectedPiece;

    private Fox[] foxes = new Fox[1];

    private Hound[] hounds = new Hound[4];


    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.input.setInputProcessor(this);

        foxes[0] = new Fox();

        for (int i = 0; i < hounds.length; i++) {
            Hound hound = new Hound();
            hound.setLocationY(BOARD_HEIGHT - 1);
            hound.setLocationX(2 * i + 1);
            hounds[i] = hound;
        }

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
        //      availableMoveSquare = new Texture("green.png");


    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                boolean isBlack = (x + y) % 2 == 0;
                Rectangle rect = board[x][y];
                batch.draw(isBlack ? blackSquare : redSquare, rect.x, rect.y, rect.width, rect.height);
            }
        }

        for (Piece p : concat(hounds, foxes)) {
            drawPiece(p);
        }

        batch.end();
    }

    private void drawPiece(Piece piece) {
        batch.draw(
                piece.getTexture(),
                piece.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES + (SQUARE_SIZE - Piece.PIECE_SIZE) / 2,
                piece.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_SIDES + (SQUARE_SIZE - Piece.PIECE_SIZE) / 2,
                Piece.PIECE_SIZE,
                Piece.PIECE_SIZE);
    }

    @Override
    public void dispose() {

        for (Disposable disposable : new Disposable[]{batch, redSquare, blackSquare}) {
            disposable.dispose();
        }

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log(this.getClass().getSimpleName(), "in touchdown");

        Piece prevSelection = selectedPiece;
        selectPiece(null);

        // normalize screenY
        screenY = SCREEN_HEIGHT - screenY;

        // process pieces
        for (Piece p : concat(hounds, foxes)) {
            if ((p.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT <= screenY
                    && p.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT + Piece.PIECE_SIZE >= screenY)
                    && (p.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES <= screenX
                    && p.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES + Piece.PIECE_SIZE >= screenX)) {

                selectPiece(p);
            } else {
                if (prevSelection != null) {
                    selectPiece(null);
                    if (currentTurn == Turn.FOX && prevSelection instanceof Fox
                            || currentTurn == Turn.HOUNDS && prevSelection instanceof Hound) {


                        int locX = (screenX - BOARD_MARGIN_SIDES) / SQUARE_SIZE;
                        int locY = (screenY - BOARD_MARGIN_TOP_BOTT) / SQUARE_SIZE;
                        List<Piece> pieces = Arrays.asList(concat(foxes, hounds));

                        if (prevSelection.moveTo(locX, locY, pieces)) {
                            flipTurn();
                        }
                        prevSelection = null;
                    }
                }
            }
        }

        return true;
    }

    private void flipTurn() {
        if (currentTurn == Turn.FOX) {
            currentTurn = Turn.HOUNDS;
        } else if (currentTurn == Turn.HOUNDS) {
            currentTurn = Turn.FOX;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void selectPiece(Piece piece) {
        if (piece == null) {
            if (selectedPiece != null) {
                selectedPiece.setSelected(false);
                selectedPiece = null;
            }
        } else {
            piece.setSelected(true);
            selectedPiece = piece;
        }
    }


    public Piece[] concat(Piece[] a, Piece[] b) {
        int aLen = a.length;
        int bLen = b.length;
        Piece[] c = new Piece[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
