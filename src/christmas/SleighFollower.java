package christmas;

import engine.*;
import engine.graphics.*;
import engine.sound.Sound;
import shiffman.box2d.*;

public class SleighFollower extends Follower {
	private final GameRunner run;
	private final SpriteGroup group;
	private final Box2DProcessing box2d;
	private final ChristmasGame game;
	private final Sound launchSound;
	private int launchWaitTime;
	private int lastLaunchTime;
	
	public SleighFollower(GameRunner run, SpriteGroup group,
			Box2DProcessing box2d, ChristmasGame game, Sprite otherReindeer,
			float followDistance) {
		super(run, "sleigh", otherReindeer, followDistance);
		
		ResourceManager resource = run.getResources();
		
		launchSound = resource.getSound("presentLaunch", run);
		launchSound.setVolume(resource.getFloat("presentLaunchVolume"));
		launchWaitTime = resource.getInt("presentLaunchWaitTime");
		
		lastLaunchTime = -1;
		
		this.run = run;
		this.group = group;
		this.box2d = box2d;
		this.game = game;
		
		layer = 5;
	}
	
	protected void launchPresent() {
		Present p = new Present(run, box2d, game, x, y);
		if(run.getTime() - lastLaunchTime > launchWaitTime) {
			run.addObject(p);
			group.addSprite(p);
			launchSound.restart();
			lastLaunchTime = run.getTime();
		}
	}
}
