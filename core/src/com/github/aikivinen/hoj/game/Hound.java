package com.github.aikivinen.hoj.game;


import com.badlogic.gdx.graphics.Texture;

public class Hound extends  Piece{

    public Hound() {
        super();
        setTexture(new Texture("hound.png"));
    }


    @Override
    public boolean isAllowedToMoveTo(int x, int y) {
        return false;
    }


}
