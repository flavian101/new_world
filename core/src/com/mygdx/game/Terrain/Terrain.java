package com.mygdx.game.Terrain;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Disposable;

public abstract class Terrain implements Disposable {
    protected float heightMagnitude;//scale factor for the read values in the height map
    protected  int size;
    protected  int width;
    protected ModelInstance modelInstance;


    public ModelInstance getModelInstance() {
        return modelInstance;
    }
}
