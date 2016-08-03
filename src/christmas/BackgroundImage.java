package christmas;

import java.util.Collection;

import engine.*;
import engine.graphics.Effect;
import processing.core.*;

public class BackgroundImage implements Effect {
	private ChristmasGame game;
	private final ResourceManager resource;
	private boolean narwhal;
	private PImage image;
	
	private float width;
	private float height;
	
	public BackgroundImage(GameRunner runner, ChristmasGame game, PImage img) {
		image = img;
		resource = runner.getResources();
		this.game = game;
		narwhal = false;
		
		width = runner.getApplet().getCanvasWidth();
		height = runner.getApplet().getCanvasHeight();
	}
	
	@Override
	public void start(int time) { }
	
	@Override
	public void think(int currentTime, int elapsedTime) {
		if(game.narwhalMode && !narwhal) {
			narwhal = true;
			image = resource.getImage("narwhal");
		}
	}
	
	@Override
	public Collection<GameObject> update() { return null; }
	
	@Override
	public boolean readyToDelete() {
		return false;
	}
	
	@Override
	public void draw(PGraphics g) {
		g.image(image, 0, 0, width, height);
	}
	
	@Override
	public int getLayer() {
		return -128;
	}
	
}
