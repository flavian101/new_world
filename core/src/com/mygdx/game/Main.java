package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Terrain.HeightMapTerrain;
import com.mygdx.game.Terrain.Terrain;
import com.mygdx.game.Terrain.TerrainMaterial;
import com.mygdx.game.Terrain.attributes.TerrainFloatAttribute;
import com.mygdx.game.Terrain.attributes.TerrainMaterialAttribute;
import com.mygdx.game.shaders.CustomShaderProvider;
import com.mygdx.game.water.Water;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.nio.ByteBuffer;

public class Main extends ApplicationAdapter {
	protected SceneManager sceneManager;
	protected SceneAsset sceneAsset;
	protected Cubemap environmentCubeMap;
	protected Cubemap diffuseCubeMap;
	protected Cubemap specularCubeMap;
	protected DirectionalLightEx light;
	protected float time;
	protected SceneSkybox skybox;
	protected PerspectiveCamera camera;
	protected FirstPersonCameraController cameraController;
	protected Texture brdfLUT;

	protected Terrain terrain;
	protected Scene terrainScene,waterScene;
	protected Water water;
	@Override
	public void create () {
		sceneManager= new SceneManager(new CustomShaderProvider(), PBRShaderProvider.createDefaultDepth(24));
		camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 1f;
		camera.far = 10000;
		camera.position.set(0, 20f, 4f);
		camera.lookAt(10,10,10);
		camera.up.set(Vector3.Y);
		sceneManager.setCamera(camera);




		cameraController = new FirstPersonCameraController(camera);
		cameraController.setVelocity(500f);

		//Gdx.input.setCursorCatched(true);
		Gdx.input.setInputProcessor(cameraController);

		// setup light
		light = new DirectionalLightEx();
		light.direction.set(1, -3, 1).nor();
		light.color.set(Color.DARK_GRAY);
		sceneManager.environment.add(light);

		// setup quick IBL (image based lighting)
		IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
		environmentCubeMap = iblBuilder.buildEnvMap(1024);
		diffuseCubeMap= iblBuilder.buildIrradianceMap(256);
		specularCubeMap = iblBuilder.buildRadianceMap(10);
		iblBuilder.dispose();

		// This texture is provided by the library, no need to have it in your assets.
		brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

		sceneManager.setAmbientLight(0.5f);
		//sceneManager.setEnvironmentRotation(20);

		sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
		sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubeMap));
		sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubeMap));
		sceneManager.environment.set( new ColorAttribute(ColorAttribute.Fog, 0.9f, 0.9f, 0.9f, 1f));
	//	sceneManager.environment.set(PBRColorAttribute.createDiffuse(Color.YELLOW));
		//sceneManager.environment.set(new DirectionalLight()0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		// setup skybox

		skybox = new SceneSkybox(environmentCubeMap);
		sceneManager.setSkyBox(skybox);
		createTerrain();
		createWater();

	}

	private void createWater() {
		water= new Water(20000,4,new Material(),0,0,1,1);
		waterScene= new Scene(water.getModelInstance());
		sceneManager.addScene(waterScene);


	}

	private void createTerrain(){
			if (terrain != null) {
				terrain.dispose();
				sceneManager.removeScene(terrainScene);
			}

		Pixmap pixmap = new Pixmap(Gdx.files.internal("textures/heightmap.png"));
			terrain= new HeightMapTerrain(pixmap,300);
			terrainScene= new Scene(terrain.getModelInstance());
			sceneManager.addScene(terrainScene);

		}

	@Override
	public void render () {
		float delta= Gdx.graphics.getDeltaTime();
		time+=delta;
		cameraController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
		ScreenUtils.clear(0.9f, 0.9f, 0.9f, 0, true);

		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			Gdx.app.exit();
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
			createTerrain();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F1)) {
			Material mat = terrain.getModelInstance().materials.get(0);
			TerrainMaterial terrainMaterial = ((TerrainMaterialAttribute) mat.get(TerrainMaterialAttribute.TerrainMaterial)).terrainMaterial;
			TerrainFloatAttribute attr = (TerrainFloatAttribute) terrainMaterial.get(TerrainFloatAttribute.minSlope);
			attr.value += 0.01f;
			attr.value= Math.min(attr.value,0.9f);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F2)) {
			Material mat = terrain.getModelInstance().materials.get(0);
			TerrainMaterial terrainMaterial = ((TerrainMaterialAttribute) mat.get(TerrainMaterialAttribute.TerrainMaterial)).terrainMaterial;
			TerrainFloatAttribute attr = (TerrainFloatAttribute) terrainMaterial.get(TerrainFloatAttribute.minSlope);
			attr.value -= 0.01f;
		}

		sceneManager.update(time);
		sceneManager.render();

	}
	
	@Override
	public void dispose () {
		sceneManager.dispose();
		environmentCubeMap.dispose();
		diffuseCubeMap.dispose();
		specularCubeMap.dispose();
		brdfLUT.dispose();
		skybox.dispose();
		sceneAsset.dispose();

	}
}
