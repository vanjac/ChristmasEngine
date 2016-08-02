package christmas;

import java.util.*;
import engine.*;
import engine.graphics.*;
import processing.core.PGraphics;

public class Lightning implements Effect {
	private Random random;
	private int frameCount;
	
	private long currentSeed;
	
	private final int waitFrames;
	private final int displayFrames;
	
	public Lightning(GameRunner run) {
		random = new Random();
		frameCount = 0;
		waitFrames = run.getResources().getInt("lightningWaitFrames");
		displayFrames = run.getResources().getInt("lightningDisplayFrames");
	}
	
	@Override
	public void start(int time) { }
	
	@Override
	public void think(int currentTime, int elapsedTime) { }
	
	@Override
	public void update() { }
	
	@Override
	public boolean readyToDelete() {
		return false;
	}
	
	@Override
	public void draw(PGraphics g) {
		if(frameCount % waitFrames == 0)
			currentSeed = System.currentTimeMillis();
		
		if(frameCount % waitFrames >= 0
				&& frameCount % waitFrames < displayFrames)
			drawLightning(g);
		
		frameCount++;
	}
	
	private void drawLightning(PGraphics g) {
		random.setSeed(currentSeed);
		
		int direction = random.nextBoolean() ? 1 : -1;
		
		float x = (float)random.nextDouble() * g.width;
		float x1 = x;
		float y = 0;
		float y1 = y;
		g.stroke(255,255,0);
		g.strokeWeight(4);
		while(y < g.height) {
			
			x1 += random.nextDouble() * 64 * direction;
			y1 += random.nextDouble() * 300 + 200;
			
			g.line(x, y, x1, y1);
			x = x1;
			y = y1;
			
			x1 += random.nextDouble() * 64 * -direction;
			y1 += -random.nextDouble() * 64;
			
			g.line(x, y, x1, y1);
			x = x1;
			y = y1;
		}
	}
	
	@Override
	public int getLayer() {
		return -127;
	}
	
}
