package christmas;

import java.util.Collection;

import engine.*;
import engine.graphics.Effect;
import processing.core.PGraphics;
import processing.core.PImage;

public class Rain implements Effect {
	private PImage[] rain;
	private int rainWidth;
	private int rainHeight;
	
	private byte nextFrame;
	private byte frame;
	
	private float width;
	private float height;
	
	
	public Rain(GameRunner run) {
		ResourceManager resources = run.getResources();
		rain = new PImage[resources.getInt("numRain")];
		for(int i = 0; i < rain.length; i++) {
			rain[i] = resources.getImage("rain" + i);
		}
		rainWidth = rain[0].width;
		rainHeight = rain[0].height;
		nextFrame = frame = 0;
		
		width = run.getApplet().getCanvasWidth();
		height = run.getApplet().getCanvasHeight();
	}
	
	@Override
	public void start(int time) { }
	
	@Override
	public void think(int currentTime, int elapsedTime) {
		nextFrame = (byte)(currentTime/50 % rain.length);
	}
	
	@Override
	public Collection<GameObject> update() {
		frame = nextFrame;
		return null;
	}
	
	@Override
	public boolean readyToDelete() {
		return false;
	}
	
	@Override
	public void draw(PGraphics g) {
		for(int x = 0; x < width; x+= rainWidth) {
			for(int y = 0; y < height; y+= rainHeight) {
				g.image(rain[frame], x, y);
			}
		}
	}
	
	@Override
	public int getLayer() {
		return 127;
	}
	
}
