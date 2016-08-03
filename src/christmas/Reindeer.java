package christmas;

import java.util.Collection;

import engine.*;
import engine.graphics.*;

public class Reindeer extends ImageSprite {
	private final ChristmasGame game;
	private final float jumpHeight;
	private final float gravity;
	private final float flySpeed;
	
	private float xFly;
	
	private boolean isDead;
	
	public Reindeer(GameRunner run, ChristmasGame gameApplet) {
		super(run.getResources().getImage("reindeer"),
				run.getResources().getInt("reindeerWidth"),
				run.getResources().getInt("reindeerHeight"));
		game = gameApplet;
		ResourceManager resources = run.getResources();
		jumpHeight = resources.getFloat("jumpHeight");
		gravity = resources.getFloat("gravity");
		flySpeed = resources.getFloat("flySpeed");
		layer = 2;
	}
	
	protected void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	protected void jump() {
		yVelocity = -jumpHeight;
	}
	
	protected void fly(float amount) {
		xFly = flySpeed * amount;
	}

	@Override
	public void start(int time) {
		
	}

	@Override
	public void think(int current, int elapsed) {
		float seconds = (float)elapsed / 1000.0f;
		
		x += xVelocity * seconds;
		y += yVelocity * seconds;
		
		x += xFly * seconds;
		
		//gravity
		yVelocity += gravity * seconds;
		
		if(y >= game.getHouses().getTopY() || y <= -game.getCanvasWidth()/2)
			isDead = true;
	}
	
	@Override
	public Collection<GameObject> update() {
		Collection<GameObject> updated = super.update();
		if(isDead) {
			game.die();
		}
		if(updated != null)
			return updated;
		else
			return null;
	}
}
