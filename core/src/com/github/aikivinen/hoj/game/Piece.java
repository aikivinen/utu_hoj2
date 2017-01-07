package com.github.aikivinen.hoj.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.io.Serializable;
import java.util.List;

public abstract class Piece implements Serializable{

    public static final int PIECE_SIZE = MyGdxGame.SQUARE_SIZE - 10;
    protected Texture texture;

    protected Texture savedTexture;
    private int locationX;
    private int locationY;


    public Piece() {

    }


    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public abstract Texture getDefaultTexture();

    /**
     * Move piece to the given coordinates. The method implementation should
     * enforce that the move is valid.
     *
     * @param x
     * @param y
     * @return boolean returns true if movement was successful
     */
    public boolean moveTo(int x, int y, List<Piece> pieces) {
        if (isAllowedToMoveTo(x, y, pieces)) {
            Gdx.app.log(this.toString(), String.format("Moving to: %s %s", x, y));
            setLocationX(x);
            setLocationY(y);
            return true;
        } else {
            Gdx.app.log(this.toString(), String.format("Illegal move: %s %s", x, y));
        }
        return false;
    }

    /**
     * @param x
     * @param y
     * @return true if the Piece is allowed to move to the given coordinates
     */
    public boolean isAllowedToMoveTo(int x, int y, List<Piece> pieces) {
        for (Piece p : pieces) {
            if (p.getLocationY() - y == 0 && p.getLocationX() - x == 0) {
                return false;
            }
        }
        return true;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setTexture(new Texture("selected.png"));
        } else {
            setTexture(getDefaultTexture());
        }
    }

    @Override
    public String toString() {
        return "Piece{" +
                "locationX=" + locationX +
                ", locationY=" + locationY +
                '}';
    }
}
