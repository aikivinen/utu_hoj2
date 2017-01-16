package com.github.aikivinen.hoj.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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


    public enum Type {NONE, FOX, HOUNDS}

    // fox starts the game
    private Type currentTurn_ = Type.FOX;

    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_HEIGHT = 8;
    public static final int SQUARE_SIZE = 50;

    final int BOARD_MARGIN_SIDES = 50;
    final int BOARD_MARGIN_TOP_BOTT = 50;

    private int screenHeight_;
    private int screenWidth_;


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

    private Type type_;
    private String typeText_;
    private BitmapFont typeFont_;
    private static final String YOUR_TURN_TEXT = "It's your turn!";
    private static final String WAITING_OTHER_TEXT = "Waiting for the opponet.";
    private static final String YOU_WIN_TEXT = "You win!";
    private static final String YOU_LOSE_TEXT = "You lose!";
    private String messageText_;
    private BitmapFont messageFont_;


    public MyGdxGame(int screenHeight, int screenWidth, Type type) throws RemoteException {
        screenHeight_ = screenHeight;
        screenWidth_ = screenWidth;
        
        type_ = type;
        typeText_ = "You are playing as the ";
        if (type_ == Type.FOX) {
            typeText_+= "Fox";
        } else if (type_ == Type.HOUNDS) {
            typeText_ += "Hounds";
        }
        
        messageText_ = "";
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

        typeFont_ = new BitmapFont();
        typeFont_.getData().setScale(1.5f);
        typeFont_.setColor(0.0f, 0.0f, 0.0f, 1.0f);

        messageFont_ = new BitmapFont();
        messageFont_.getData().setScale(4.0f);
        messageFont_.setColor(0.0f, 0.0f, 0.0f, 1.0f);

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

        int typeTextY = BOARD_MARGIN_TOP_BOTT + SQUARE_SIZE;
        if (type_ == Type.HOUNDS) {
            typeTextY = BOARD_HEIGHT * SQUARE_SIZE;
        }
        
        typeFont_.draw(
            batch_,
            typeText_,
            BOARD_WIDTH * SQUARE_SIZE + 2 * BOARD_MARGIN_SIDES,
            typeTextY
        );
        
        if (currentTurn_ != Type.NONE) {
            messageText_ =
                (currentTurn_ == type_) ? YOUR_TURN_TEXT : WAITING_OTHER_TEXT;
        }
        
        messageFont_.draw(
            batch_,
            messageText_,
            BOARD_MARGIN_SIDES,
            BOARD_HEIGHT * SQUARE_SIZE + 3.5f * BOARD_MARGIN_SIDES
        );
    
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
        if (currentTurn_ == type_) {
            Gdx.app.log(this.getClass().getSimpleName(), "in touchdown");

            Piece prevSelection = selectedPiece_;
            selectPiece(null);

            // normalize screenY
            screenY = screenHeight_ - screenY;

            // process pieces
            for (Piece p : (currentTurn_ == Type.HOUNDS) ? hounds_ : foxes_) {
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
                        if (currentTurn_ == Type.FOX && prevSelection instanceof Fox
                                || currentTurn_ == Type.HOUNDS
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
        
        selectPiece(null);
        return false;
    }

    private void flipTurn() {
        if (!isGameEnded()) {
            if (currentTurn_ == Type.FOX) {
                currentTurn_ = Type.HOUNDS;
            } else if (currentTurn_ == Type.HOUNDS) {
                currentTurn_ = Type.FOX;
            }
        }
    }
    
    private boolean isGameEnded() {
        Type winner = Type.NONE;
        boolean gameEnd = false;
        
        for (Piece p : foxes_) {
            if (p.getLocationY() == BOARD_HEIGHT - 1) {
                winner = Type.FOX;
                gameEnd = true;
                break;
            }
        }
        
        if (winner == Type.NONE) {
            if (checkHoundsWinCondition()) {
                winner = Type.HOUNDS;
                gameEnd = true;
            }
        }

        
        if (gameEnd) {
            currentTurn_ = Type.NONE;
            
            if (winner.equals(type_)) {
                messageText_ = YOU_WIN_TEXT;
            } else {
                messageText_ = YOU_LOSE_TEXT;
            }
        }
        
        return gameEnd;
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
        
        boolean moveResult = selection.moveTo(move.getToX(), move.getToY(), pieces);
        
        flipTurn();
        
        return selection != null && moveResult;
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

    /**
     * Checks whether the hounds have won the game. The hounds have won if the fox (or any of the foxes) can't move anywhere
     * @return true if the hounds have won the game
     */
    private boolean checkHoundsWinCondition() {
        for (Fox fox : foxes_) {
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    if  (fox.isAllowedToMoveTo(j,i, Arrays.asList(concat(foxes_,hounds_)))) {
                        return false;
                    };
                }
            }
        }
        return true;
    }
}
