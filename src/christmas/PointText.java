package christmas;

import processing.core.PGraphics;
import engine.graphics.*;

public class PointText extends PositionableSprite {
	private int startTime;
	private final int pointValue;
	private final float endY;
	//per milli
	private final float moveSpeed;
	
	public PointText(int points, float x, float y, float screenHeight) {
		this.x = x;
		this.y = y;
		pointValue = points;
		layer = 128;
		endY = screenHeight * .6f;
		moveSpeed = (y - endY) / 300;
	}
	
	@Override
	public void draw(PGraphics g) {
		if(pointValue > 0)
			g.fill(255,255,0);
		else
			g.fill(255, 0, 0);
		g.textSize(24);
		g.textAlign(PGraphics.CENTER, PGraphics.CENTER);
		g.text(pointValue, 0, 0);
		
		//reset state
		g.fill(255,255,255);
	}
	
	@Override
	public void start(int time) {
		startTime = time;
	}
	
	@Override
	public void think(int currentTime, int elapsedTime) {
		if(y > endY)
			y -= moveSpeed * elapsedTime;
		if(currentTime - startTime > 1000)
			delete();
	}
	
}
