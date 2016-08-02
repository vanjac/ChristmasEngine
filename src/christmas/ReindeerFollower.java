package christmas;

import engine.GameRunner;
import engine.graphics.Sprite;

public class ReindeerFollower extends Follower {
	public ReindeerFollower(GameRunner run, Sprite otherReindeer,
			float followDistance) {
		super(run, "reindeer", otherReindeer, followDistance);
		layer = 2;
	}
}
