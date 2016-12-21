package com.github.aikivinen.hoj.game;


import com.badlogic.gdx.graphics.Texture;

public abstract class Piece  {

    public static final int PIECE_SIZE = MyGdxGame.SQUARE_SIZE - 10;
    protected Texture texture;

    protected  Texture savedTexture;
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

    public abstract  Texture getDefaultTexture() ;

    /**
     * Move piece to the given coordinates. The method implementation should enforce that the move is valid.
     *
     * @param x
     * @param y
     * @return boolean returns true if movement was successful
     */
    public boolean moveTo(int x, int y) {
        if (isAllowedToMoveTo(x, y)) {
            setLocationX(x);
            setLocationY(y);
            return true;
        }
        return false;
    }

    /**
     * @param x
     * @param y
     * @return true if the Piece is allowed to move to the given coordinates
     */
    public abstract boolean isAllowedToMoveTo(int x, int y);

    public void setSelected(boolean selected) {
        if (selected) {
            setTexture(new Texture("selected.png"));
        }  else {
            setTexture(getDefaultTexture());
        }
    }


}
