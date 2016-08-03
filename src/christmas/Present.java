package christmas;

import engine.*;
import engine.graphics.*;
import engine.sound.*;
import shiffman.box2d.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class Present extends ImageSprite {
	private final ChristmasGame game;
	private final Box2DProcessing box2d;
	private final ResourceManager resource;
	private final float screenHeight;
	
	private Body body;
	private final Sound fallSound;
	private final int fallPoints;
	
	public Present(GameRunner runner, Box2DProcessing box2d,
			ChristmasGame game, float x, float y) {
		super();
		this.box2d = box2d;
		this.game = game;
		resource = runner.getResources();
		
		screenHeight = runner.getApplet().getCanvasHeight();
		this.x = x;
		this.y = y;
		
		String imageName = "present" +
				(int)(Math.random() * resource.getShort("numPresents"));
		super.setImage(resource.getImage(imageName));
		super.setScale(resource.getFloat("presentScale"));
		
		fallSound = resource.getSound("presentFallSound", runner);
		fallSound.setVolume(resource.getFloat("presentFallVolume"));
		fallSound.deleteWhenFinished();
		fallPoints = -resource.getInt("fallPoints");
		
		layer = 4;
	}
	
	@Override
	public void start(int time) {
		makeBody(resource);
		
		float xVel = resource.getFloat("presentXVel");
		float yVel = resource.getFloat("presentYVel");
		body.setLinearVelocity(new Vec2(xVel, yVel));
	}

	@Override
	public void think(int currentTime, int elapsedTime) {
		Vec2 position = box2d.getBodyPixelCoord(body);
		x = position.x;
		y = position.y;
		rotation = -body.getAngle();
		
		if(y >= screenHeight) {
			game.addPoints(fallPoints, x, y);
			box2d.destroyBody(body);
			fallSound.restart();
			delete();
		}
		if(x < -getWidth()) {
			box2d.destroyBody(body);
			delete();
		}
	}
	
	public void presentDelete() {
		box2d.destroyBody(body);
		delete();
	}
	
	//add the body for this present to the box2d world
	private void makeBody(ResourceManager resource) {
		float width = super.getWidth();
		float height = super.getHeight();
		float centerX = x;
		float centerY = y;
		float box2dWidth = box2d.scalarPixelsToWorld(width/2);
		float box2dHeight = box2d.scalarPixelsToWorld(height/2);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(box2dWidth, box2dHeight);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		//physics properties
		fixtureDef.density = resource.getFloat("presentDensity");
		fixtureDef.friction = resource.getFloat("presentFriction");
		fixtureDef.restitution = resource.getFloat("presentRestitution");
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(box2d.coordPixelsToWorld(centerX, centerY));
		bodyDef.setLinearDamping(0);
		
		
		body = box2d.createBody(bodyDef);
		Fixture f = body.createFixture(fixtureDef);
		f.setUserData(this);
	}

}
