package com.github.aikivinen.hoj.game;


import com.badlogic.gdx.graphics.Texture;

public class Hound extends Piece {

    public Hound() {
        super();
        setTexture(getDefaultTexture());
    }


    @Override
    public boolean isAllowedToMoveTo(int x, int y) {
        return false;
    }

    @Override
    public Texture getDefaultTexture() {
        return new Texture("hound.png");
    }

}
