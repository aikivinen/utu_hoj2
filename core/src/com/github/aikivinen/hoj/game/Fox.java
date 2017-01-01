package com.github.aikivinen.hoj.game;


import com.badlogic.gdx.graphics.Texture;

import java.io.Serializable;
import java.util.List;

public class Fox extends Piece implements Serializable {

    public static final int MAX_MOVE_DIST = 1;

    public Fox() {
        super();
        setTexture(getDefaultTexture());
    }

    /**
     * The "Fox" is allowed to move one step at a time to any diagonal direction
     */
    @Override
    public boolean isAllowedToMoveTo(int x, int y, List<Piece> pieces) {
        if (super.isAllowedToMoveTo(x, y, pieces)
                && Math.abs(getLocationX() - x) == MAX_MOVE_DIST
                && Math.abs(getLocationY() - y) == MAX_MOVE_DIST) {
            return true;
        }
        return false;
    }

    public Texture getDefaultTexture() {
        return new Texture("fox.png");
    }


}
