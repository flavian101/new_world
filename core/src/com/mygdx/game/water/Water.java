package com.mygdx.game.water;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Water {
   // protected int widht;
    protected int size;
    Mesh mesh;
    protected ModelInstance modelInstance;

    public Water(final float width, final float height, final Material material,
                 final float u1, final float v1, final float u2, final float v2) {
        this.size = size;




        ModelBuilder mb= new ModelBuilder();
        mb.begin();
       MeshPartBuilder b_mesh= mb.part("water", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked| VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
                new Material());
        b_mesh.setColor(Color.ROYAL);
        b_mesh.setUVRange(u1, v1, u2, v2);
        b_mesh.rect(0-width, height, 0-width,
                     0-width, height, width,
                     width, height, width,
                     width, height, 0-width,
                    0, -1, 0);


        modelInstance=new ModelInstance(mb.end());
//
//        b_mesh.rect(-(width*0.5f), -(height*0.5f), 0,
//                (width*0.5f), -(height*0.5f), 0,
//                (width*0.5f), (height*0.5f), 0,
//                -(width*0.5f), (height*0.5f), 0,
//                0, 0, -1);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }
}
