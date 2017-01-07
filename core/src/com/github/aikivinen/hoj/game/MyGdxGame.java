package com.github.aikivinen.hoj.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.github.aikivinen.hoj.game.rmi.GameService;
import com.github.aikivinen.hoj.game.rmi.Move;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class MyGdxGame
        implements ApplicationListener, InputProcessor, GameService, Serializable {


    public enum Turn {FOX, HOUNDS}

    // fox starts the game
    private Turn currentTurn_ = Turn.FOX;

    public static final int SCREEN_HEIGHT = 640;

    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_HEIGHT = 8;
    public static final int SQUARE_SIZE = 50;

    final int BOARD_MARGIN_SIDES = 50;
    final int BOARD_MARGIN_TOP_BOTT = 50;

    private SpriteBatch batch_;
    private OrthographicCamera camera_;

    final Rectangle[][] board_ = new Rectangle[BOARD_WIDTH][BOARD_HEIGHT];
    private Texture redSquare_;
    private Texture blackSquare_;
//    private Texture availableMoveSquare;

    private Piece selectedPiece_;

    private Fox[] foxes_ = new Fox[1];

    private Hound[] hounds_ = new Hound[4];

    private GameService remote_;



    public MyGdxGame() throws RemoteException{

    }

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.input.setInputProcessor(this);

        foxes_[0] = new Fox();

        for (int i = 0; i < hounds_.length; i++) {
            Hound hound = new Hound();
            hound.setLocationY(BOARD_HEIGHT - 1);
            hound.setLocationX(2 * i + 1);
            hounds_[i] = hound;
        }

        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {

                Rectangle rect = new Rectangle();
                rect.setSize(SQUARE_SIZE);
                rect.setX(i * SQUARE_SIZE + BOARD_MARGIN_SIDES);
                rect.setY(j * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT);
                board_[i][j] = rect;
            }
        }


        camera_ = new OrthographicCamera();
        camera_.setToOrtho(false, 800, 640);

        batch_ = new SpriteBatch();
        redSquare_ = new Texture("red.png");
        blackSquare_ = new Texture("black.png");
        //      availableMoveSquare = new Texture("green.png");


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch_.begin();
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                boolean isBlack = (x + y) % 2 == 0;
                Rectangle rect = board_[x][y];
                batch_.draw(
                    isBlack ? blackSquare_
                    : redSquare_, rect.x, rect.y, rect.width, rect.height
                );
            }
        }

        for (Piece p : concat(hounds_, foxes_)) {
            drawPiece(p);
        }

        batch_.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    private void drawPiece(Piece piece) {
        batch_.draw(
            piece.getTexture(),
            piece.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES
                                        + (SQUARE_SIZE - Piece.PIECE_SIZE) / 2,
            piece.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_SIDES
                                        + (SQUARE_SIZE - Piece.PIECE_SIZE) / 2,
            Piece.PIECE_SIZE,
            Piece.PIECE_SIZE
        );
    }

    @Override
    public void dispose() {

        for (Disposable disposable :
                        new Disposable[]{batch_, redSquare_, blackSquare_}) {
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

        Piece prevSelection = selectedPiece_;
        selectPiece(null);

        // normalize screenY
        screenY = SCREEN_HEIGHT - screenY;

        // process pieces
        for (Piece p : concat(hounds_, foxes_)) {
            boolean insideVerticalEdges = (
                p.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT <= screenY
                && p.getLocationY() * SQUARE_SIZE + BOARD_MARGIN_TOP_BOTT
                                                    + Piece.PIECE_SIZE >= screenY
            );
            boolean insideHorizontalEdges = (
                p.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES <= screenX
                && p.getLocationX() * SQUARE_SIZE + BOARD_MARGIN_SIDES
                                                    + Piece.PIECE_SIZE >= screenX
            );
            
            if (insideVerticalEdges && insideHorizontalEdges) {
                // If touchdown is inside this piece.
                selectPiece(p);
            } else {
                if (prevSelection != null) {
                    // Clear any selected piece.
                    selectPiece(null);
                    if (currentTurn_ == Turn.FOX && prevSelection instanceof Fox
                            || currentTurn_ == Turn.HOUNDS
                            && prevSelection instanceof Hound) {

                        int fromX = prevSelection.getLocationX();
                        int fromY = prevSelection.getLocationY();

                        int locX = (screenX - BOARD_MARGIN_SIDES) / SQUARE_SIZE;
                        int locY = (screenY - BOARD_MARGIN_TOP_BOTT) / SQUARE_SIZE;
                        List<Piece> pieces = Arrays.asList(concat(foxes_, hounds_));

                        if (prevSelection.moveTo(locX, locY, pieces)) {
                            try {
                                remote_.movePiece(
                                    new Move(fromX, fromY, locX, locY)
                                );
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
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
        if (currentTurn_ == Turn.FOX) {
            currentTurn_ = Turn.HOUNDS;
        } else if (currentTurn_ == Turn.HOUNDS) {
            currentTurn_ = Turn.FOX;
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
            if (selectedPiece_ != null) {
                selectedPiece_.setSelected(false);
                selectedPiece_ = null;
            }
        } else {
            piece.setSelected(true);
            selectedPiece_ = piece;
        }
    }

    @Override
    public boolean movePiece(Move move) throws RemoteException {
        System.out.println("movePiece" + " " + move);
        for (int i = 0; i < foxes_.length; i++) {
            System.out.println(foxes_[i]);
        }
        for (int i = 0; i < hounds_.length; i++) {
            System.out.println(hounds_[i]);
        }
        
        List<Piece> pieces = getPieces();
        System.out.println(Arrays.toString(pieces.toArray()));

        Piece selection = null;
        for (Piece p : pieces) {
            if (p.getLocationX() == move.getFromX()
                    && p.getLocationY() == move.getFromY()) {
                selection = p;
            }
        }
        
        flipTurn();
        
        return selection != null
                    && selection.moveTo(move.getToX(), move.getToY(), pieces);
    }


    public Piece[] concat(Piece[] a, Piece[] b) {
        int aLen = a.length;
        int bLen = b.length;
        Piece[] c = new Piece[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public GameService getRemote() {
        return remote_;
    }

    public void setRemote(GameService remote) {
       System.out.println("setRemote");
        this.remote_ = remote;
    }

    @Override
    public void setRemoteService(GameService remote) throws RemoteException {
        setRemote(remote);
    }

    @Override
    public List<Piece> getPieces() throws RemoteException {
        return  Arrays.asList(concat(foxes_, hounds_));
    }
}
