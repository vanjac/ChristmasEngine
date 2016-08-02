package christmas;

import engine.*;
import engine.graphics.*;
import shiffman.box2d.*;

public class MainGameInitializer{
	
	public void initialize(ChristmasGame game, GameRunner runner,
			SpriteGroup group, Box2DProcessing box2d) {
		ResourceManager resource = runner.getResources();
		float screenWidth = game.getCanvasWidth();
		float screenHeight = game.getCanvasHeight();
		
		addReindeer(game, runner, group, box2d, screenWidth, screenHeight);
		
		BackgroundImage background =
				new BackgroundImage(runner, game, resource.getImage("sky"));
		runner.addObject(background);
		group.addEffect(background);
		
		Rain rain = new Rain(runner);
		runner.addObject(rain);
		group.addEffect(rain);
		
		Lightning lightning = new Lightning(runner);
		runner.addObject(lightning);
		group.addEffect(lightning);
		
		HouseGroup houses = new HouseGroup(runner, screenWidth, screenHeight);
		runner.addObject(houses);
		group.addSprite(houses);
		game.setHouses(houses);
		
		initBox2d(game, runner, box2d);
	}
	
	
	private void addReindeer(ChristmasGame game, GameRunner runner,
			SpriteGroup group, Box2DProcessing box2d,
			float screenWidth, float screenHeight) {
		ResourceManager resource = runner.getResources();
		int numReindeer = resource.getInt("numReindeer");
		int followDistance = resource.getInt("reindeerFollowDistance");
		
		Reindeer mainReindeer = new Reindeer(runner, game);
		mainReindeer.setPosition(
				screenWidth * resource.getFloat("reindeerStartXRatio"),
				100);
		runner.addObject(mainReindeer);
		group.addSprite(mainReindeer);
		
		Follower mainFollow =
				new ReindeerFollower(runner, mainReindeer, 0);
		runner.addObject(mainFollow);
		group.addSprite(mainFollow);
		
		Sprite lastReindeer = mainReindeer;
		for(int i = 0; i < numReindeer/2 - 1; i++) {
			Follower follow =
					new ReindeerFollower(runner, lastReindeer, followDistance);
			runner.addObject(follow);
			group.addSprite(follow);
			
			Follower follow2 =
					new ReindeerFollower(runner, follow, 0);
			runner.addObject(follow2);
			group.addSprite(follow2);
			
			lastReindeer = follow;
		}
		
		int sleighFollowDistance = resource.getInt("sleighFollowDistance");
		SleighFollower sleigh =
				new SleighFollower(runner, group, box2d, game,
						lastReindeer, sleighFollowDistance);
		runner.addObject(sleigh);
		group.addSprite(sleigh);
		
		game.setMainReindeer(mainReindeer);
		game.setSleigh(sleigh);
	}
	
	
	private void initBox2d(ChristmasGame game, GameRunner runner, 
			Box2DProcessing box2d) {
		ResourceManager resource = runner.getResources();
		
		box2d.createWorld();
		box2d.setGravity(0, -resource.getFloat("box2dGravity"));
		
		box2d.world.setContactListener(
				new Chimney.CollisionHandler(runner, box2d, game));
	}

}
