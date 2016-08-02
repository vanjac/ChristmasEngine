package christmas;

import java.util.*;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import shiffman.box2d.Box2DProcessing;
import engine.GameRunner;
import engine.ResourceManager;
import engine.graphics.ImageSprite;
import engine.sound.Sound;

public class Chimney extends ImageSprite {
	private final ChristmasGame game;
	private final Box2DProcessing box2d;
	private Body body;
	private boolean move;
	
	public static class CollisionHandler implements ContactListener {
		ChristmasGame game;
		private final int points;
		private final List<Fixture> alreadyCollided;
		private final Box2DProcessing box2d;
		private long lastCollisionSoundTime;
		private final Sound collisionSound;
		
		public CollisionHandler(GameRunner runner, Box2DProcessing box2d,
				ChristmasGame game) {
			this.game = game;
			this.box2d = box2d;
			ResourceManager resource = runner.getResources();
			points = resource.getInt("presentPoints");
			alreadyCollided = new ArrayList<>();
			collisionSound = resource.getSound("collisionSound", runner);
			collisionSound.setVolume(resource.getFloat("collisionVolume"));
			lastCollisionSoundTime = -1;
		}
		
		@Override
		public void beginContact(Contact c) {
			Fixture fixtureA = c.getFixtureA();
			Object objectA = fixtureA.getUserData();
			Fixture fixtureB = c.getFixtureB();
			Object objectB = fixtureB.getUserData();
			
			
			//the bottom of chimneys have their user data set to the main game
			//applet
			if(objectA == game) {
				if(!alreadyCollided.contains(fixtureB)) {
					Vec2 position = box2d.coordWorldToPixels(
							fixtureB.getBody().getPosition());
					game.addPoints(points, position.x, position.y);
					alreadyCollided.add(fixtureB);
					Present p = (Present)fixtureB.getUserData();
					p.presentDelete();
				}
				
			}
			
			if(objectB == game) {
				if(!alreadyCollided.contains(fixtureA)) {
					Vec2 position = box2d.coordWorldToPixels(
							fixtureA.getBody().getPosition());
					game.addPoints(points, position.x, position.y);
					alreadyCollided.add(fixtureA);
					Present p = (Present)fixtureA.getUserData();
					p.presentDelete();
				}
			}
			
			if((!alreadyCollided.contains(fixtureB))
					&& (!alreadyCollided.contains(fixtureA))) {
				if(System.currentTimeMillis() - lastCollisionSoundTime > 250) {
					lastCollisionSoundTime = System.currentTimeMillis();
					collisionSound.restart();
				}
			}
				
		}
		@Override
		public void endContact(Contact arg0) { }
		@Override
		public void postSolve(Contact arg0, ContactImpulse arg1) { }
		@Override
		public void preSolve(Contact arg0, Manifold arg1) { }
	}
	
	
	public Chimney(GameRunner runner, Box2DProcessing box2d,
			ChristmasGame game) {
		super(runner.getResources().getImage("chimney"),
				runner.getResources().getFloat("chimneyScale"));
		ResourceManager resource = runner.getResources();
		this.box2d = box2d;
		this.game = game;
		
		x = game.getCanvasWidth() + getWidth();
		
		y = game.getHouses().getTopY()
				- getHeight() / 2 + resource.getFloat("chimneyYOffset");
		
		makeBody(resource);
		float xSpeed =
				box2d.scalarPixelsToWorld(-resource.getFloat("houseMoveSpeed"))
				- box2d.scalarPixelsToWorld(0);
		body.setLinearVelocity(new Vec2(xSpeed, 0));
		
		move = true;
		
		//above presents
		layer = 6;
	}
	
	@Override
	public void start(int time) { }
	
	@Override
	public void think(int currentTime, int elapsedTime) {
		if(move) {
			Vec2 position = box2d.getBodyPixelCoord(body);
			x = position.x;
			y = position.y;
			rotation = -body.getAngle();
		}
		
		if(x < -getWidth()) {
			box2d.destroyBody(body);
			delete();
		}
	}
	
	private void makeBody(ResourceManager resource) {
		float width = super.getWidth();
		float height = super.getHeight();
		float centerX = x;
		float centerY = y;
		float box2dWidth = box2d.scalarPixelsToWorld(width/2);
		float box2dHeight = box2d.scalarPixelsToWorld(height/2);
		
		PolygonShape shape1 = new PolygonShape();
		//width height center angle
		shape1.setAsBox(1, box2dHeight,
				new Vec2(-box2dWidth, 0), 0);
		
		PolygonShape shape2 = new PolygonShape();
		shape2.setAsBox(1, box2dHeight,
				new Vec2(box2dWidth, 0), 0);
		
		PolygonShape bottomShape = new PolygonShape();
		bottomShape.setAsBox(box2dWidth, 1,
				new Vec2(0, -box2dHeight), 0);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KINEMATIC;
		
		bodyDef.position.set(box2d.coordPixelsToWorld(centerX, centerY));
		bodyDef.setGravityScale(0);
		bodyDef.setLinearDamping(0);
		
		body = box2d.createBody(bodyDef);
		body.createFixture(shape1, 1);
		body.createFixture(shape2, 1);
		Fixture bottomFixture = body.createFixture(bottomShape, 1);
		
		bottomFixture.setUserData(game);
	}
	
	public void stopMoving() {
		move = false;
		box2d.destroyBody(body);
	}
}
