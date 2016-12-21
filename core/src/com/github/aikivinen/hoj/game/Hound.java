package com.github.aikivinen.hoj.game;


import com.badlogic.gdx.graphics.Texture;

import java.util.List;

public class Hound extends Piece {

    private static final int MAX_MOVE_DIST = 1;

    public Hound() {
        super();
        setTexture(getDefaultTexture());
    }


    /**
     * The "Hound" is allowed to move one step at a time to any diagonal direction
     */
    @Override
    public boolean isAllowedToMoveTo(int x, int y, List<Piece> pieces) {
        if (super.isAllowedToMoveTo(x, y, pieces)
                && Math.abs(getLocationX() - x) == MAX_MOVE_DIST
                && Math.abs(getLocationY() - y) == MAX_MOVE_DIST
                && getLocationY() > y) {
            return true;
        }
        return false;
    }

    @Override
    public Texture getDefaultTexture() {
        return new Texture("hound.png");
    }

}
