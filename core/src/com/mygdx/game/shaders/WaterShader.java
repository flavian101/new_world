package com.mygdx.game.shaders;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.water.WaterShaderProgram;


public class WaterShader extends WaterShaderProgram {
    private final static String VERTEX_FILE =  GET_VERTEX();
    private final static String FRAGMENT_FILE = GET_FRAGMENT();


    private int location_modelMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_reflection;
    private int location_refraction;


    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {

        bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");
        location_modelMatrix = getUniformLocation("modelMatrix");
        location_reflection = getUniformLocation("reflectionTexture");
        location_refraction = getUniformLocation("refractionTexture");
    }

    public void connectTextureUnits(){
        super.loadInt(location_reflection, 0);
        super.loadInt(location_refraction, 1);

    }

    private static String GET_FRAGMENT() {
        return Gdx.files.internal("shaders/waterFragment.glsl").readString();
    }
        private static String GET_VERTEX() {
            return Gdx.files.internal("shaders/waterVertex.glsl").readString();
        }

}
