package com.github.aikivinen.hoj.game;


import com.badlogic.gdx.graphics.Texture;

public class Fox extends Piece {

    public static final int MAX_MOVE_DIST = 1;

    public Fox() {
        super();
        setTexture(getDefaultTexture());
    }

    public Texture getDefaultTexture() {
        return new Texture("fox.png");
    }

    /**
     * The "Fox" is allowed to move one step at a time to any direction
     */
    @Override
    public boolean isAllowedToMoveTo(int x, int y) {
        if (Math.abs(getLocationX() - x) <= MAX_MOVE_DIST
                && Math.abs(getLocationY() - y) <= MAX_MOVE_DIST) {
            return true;
        }
        return false;
    }


}
