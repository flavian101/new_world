package com.mygdx.game.Terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.mygdx.game.Terrain.attributes.TerrainFloatAttribute;
import com.mygdx.game.Terrain.attributes.TerrainMaterialAttribute;
import com.mygdx.game.Terrain.attributes.TerrainTextureAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

public class HeightMapTerrain extends Terrain{
    private final HeightField field;

    public HeightMapTerrain(Pixmap data, float magnitude) {
        this.size= 2000;
        this.width= data.getWidth();
        this.heightMagnitude= magnitude;

        field= new HeightField(true,data,true, VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal|VertexAttributes.Usage.TextureCoordinates);
        data.dispose();
        field.corner00.set(0f,0f,0f);
        field.corner10.set(size,0f,0f);
        field.corner01.set(0f,0f,size);
        field.corner11.set(size,0f, size);
        field.magnitude.set(0f,magnitude,0f);
        field.update();


        ModelBuilder mb= new ModelBuilder();
        mb.begin();
        mb.part("terrain", field.mesh, GL20.GL_TRIANGLES,new Material());
        modelInstance = new ModelInstance(mb.end());

        //setting the material attribute before model creation was resulting in issues
        Material material= modelInstance.materials.get(0);


        TerrainTextureAttribute baseAttribute=TerrainTextureAttribute.createDiffuseBase(getMipMap("textures/brown_mud_02_diff_4k.jpg"));//mashy area
        TerrainTextureAttribute terrainSlopeTexture=TerrainTextureAttribute.createDiffuseSlope(getMipMap("textures/rocks_ground_05_diff_4k.jpg"));//cliff area
        TerrainTextureAttribute terrainHeightTexture=TerrainTextureAttribute.createDiffuseHeight(getMipMap("textures/aerial_rocks_01_diff_4k.jpg"));//top height area

        baseAttribute.scaleU= 3f;
        baseAttribute.scaleV= 3f;

        TerrainFloatAttribute slope = TerrainFloatAttribute.createMinSlope(0.85f);


        TerrainMaterial terrainMaterial= new TerrainMaterial();
        terrainMaterial.set(baseAttribute);
        terrainMaterial.set(terrainSlopeTexture);
        terrainMaterial.set(terrainHeightTexture);
        terrainMaterial.set(slope);


        material.set(TerrainMaterialAttribute.createTerrainMaterialAttribute(terrainMaterial));

    }
    private Texture getMipMap(String filePath){
        Texture texture= new Texture(Gdx.files.internal(filePath),true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        return  texture;


    }

    @Override
    public void dispose() {
        field.dispose();

    }
}
