package christmas;

import java.util.Random;

import engine.*;
import engine.graphics.*;
import processing.core.*;

public class HouseGroup extends SpriteGroup {
	private final Random random;
	private final GameRunner runner;
	
	private final float screenWidth;
	private final float houseWidth;
	private final float moveSpeed;
	private final float houseY;
	private final float topY;
	
	private final PImage[] houseImages;
	
	private boolean isMoving;
	private float nextHousePosition;
	
	private class House extends ImageSprite {
		public House(float x, float y) {
			super(houseImages[random.nextInt(houseImages.length)]);
			this.x = x;
			this.y = y;
			layer = 1;
		}

		@Override
		public void start(int time) { }

		@Override
		public void think(int currentTime, int elapsedTime) {
			if(x + getSpriteGroupX() < -houseWidth) {
				delete();
			}
		}
	}
	
	public HouseGroup(GameRunner runner,
			float screenWidth, float screenHeight) {
		super(runner);
		this.runner = runner;
		this.screenWidth = screenWidth;
		
		x = 0;
		y = 0;
		
		random = new Random();
		
		ResourceManager resource = runner.getResources();
		
		short numHouseImages = resource.getShort("numHouses");
		houseImages = new PImage[numHouseImages];
		for(int i = 0; i < numHouseImages; i++) {
			houseImages[i] = resource.getImage("house" + i);
		}
		
		houseWidth = houseImages[0].width - 1;
		
		houseY = screenHeight - houseImages[0].height / 2;
		topY = screenHeight - houseImages[0].height;
		
		moveSpeed = -resource.getFloat("houseMoveSpeed");
		isMoving = true;
		
		layer = 7;
	}
	
	@Override
	public void start(int time) {
		nextHousePosition = 0;
		addMoreHouses();
	}
	
	@Override
	public void think(int current, int elapsed) {
		super.think(current, elapsed);
		
		addMoreHouses();
		if(isMoving)
			x += moveSpeed * ((float)elapsed / 1000.0);
		
	}
	
	private void addMoreHouses() {
		if(nextHousePosition >= screenWidth - x + houseWidth)
			return;
		for(; nextHousePosition < screenWidth - x + houseWidth;
				nextHousePosition += houseWidth) {
			House house = new House(nextHousePosition, houseY);
			addSprite(house);
			runner.addObject(house);
		}
	}
	
	private float getSpriteGroupX() {
		return getX();
	}
	
	public float getTopY() {
		return topY;
	}
	
	public void stopMoving() {
		isMoving = false;
	}
}
