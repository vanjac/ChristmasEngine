package christmas;

import engine.*;
import engine.graphics.*;
import java.util.*;

public class Follower extends ImageSprite {
	private final Sprite other;
	private final Queue<TimedPosition> positions;
	private final int delay;
	private final float followDistance;
	private int startTime;
	
	private class TimedPosition {
		final float x, y;
		final int time;
		TimedPosition(float x, float y, int time) {
			this.x = x;
			this.y = y;
			this.time = time;
		}
	}
	
	
	public Follower(GameRunner run, String image, Sprite otherReindeer,
			float followDistance) {
		super(run.getResources().getImage(image),
				run.getResources().getInt(image + "Width"),
				run.getResources().getInt(image + "Height"));
		ResourceManager resources = run.getResources();
		delay = resources.getInt("reindeerFollowDelay");
		this.followDistance = followDistance;
		other = otherReindeer;
		positions = new ArrayDeque<>();
	}
	
	protected void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void start(int time) {
		startTime = time;
		x = other.getX() - followDistance;
		y = other.getY();
	}

	@Override
	public void think(int current, int elapsed) {
		positions.add(new TimedPosition(other.getX(), other.getY(),
				current));
		if(current - startTime < delay) {
			x = other.getX() - followDistance;
			y = other.getY();
		} else if(current - positions.peek().time > delay) {
			TimedPosition position = null;
			while (!positions.isEmpty() &&
					current - positions.peek().time > delay) {
				position = positions.remove();
			}
			if(position == null) {
				return;
			}
			x = position.x - followDistance;
			y = position.y;
		}
	}
}
