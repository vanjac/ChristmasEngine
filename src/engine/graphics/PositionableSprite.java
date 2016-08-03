package engine.graphics;

import java.util.Collection;

import engine.GameObject;

/**
 * A sprite with implementations for many of the values in the Sprite interface.
 * These values are safely updated in the "update" step.
 * Note that PositionableSprite doesn't update its position based on its
 * velocity.
 * @author jacob
 *
 */
public abstract class PositionableSprite implements Sprite {
	/*
	 * 'c' stands for current
	 * For example, 'xScale' is the most up to date x-scale of the sprite, but
	 * 'cXScale' is what the sprite is going to return when asked.
	 * During the update step, the updated values are shifted into the 'current'
	 * values.
	 */
	protected float cX, x, cY, y;
	protected float cRotation, rotation;
	protected float cXScale, xScale, cYScale, yScale;
	protected float cXVelocity, xVelocity, cYVelocity, yVelocity;
	protected float cDepth, depth;
	protected float cAlpha, alpha;
	protected boolean cShown, shown;
	protected int cLayer, layer;
	private boolean cReadyToDelete, readyToDelete;
	
	protected PositionableSprite() {
		x = y = 0;
		rotation = 0;
		xScale = yScale = 1;
		xVelocity = yVelocity = 0;
		depth = 1;
		alpha = 1;
		shown = true;
		layer = 0;
		readyToDelete = false;
		shiftNextValuesToCurrent();
	}
	
	private void shiftNextValuesToCurrent() {
		cX = x;
		cY = y;
		cRotation = rotation;
		cXScale = xScale;
		cYScale = yScale;
		cXVelocity = xVelocity;
		cYVelocity = yVelocity;
		cDepth = depth;
		cAlpha = alpha;
		cShown = shown;
		cLayer = layer;
		cReadyToDelete = readyToDelete;
	}
	
	@Override
	public Collection<GameObject> update() {
		shiftNextValuesToCurrent();
		return null;
	}
	
	protected void delete() {
		readyToDelete = true;
	}
	
	@Override
	public boolean readyToDelete() {
		return cReadyToDelete;
	}

	@Override
	public boolean isShown() {
		return cShown;
	}

	@Override
	public int getLayer() {
		return cLayer;
	}

	@Override
	public float getX() {
		return cX;
	}

	@Override
	public float getY() {
		return cY;
	}

	@Override
	public float getRotation() {
		return cRotation;
	}

	@Override
	public float getXScale() {
		return cXScale;
	}

	@Override
	public float getYScale() {
		return cYScale;
	}

	@Override
	public float getXVelocity() {
		return cXVelocity;
	}

	@Override
	public float getYVelocity() {
		return cYVelocity;
	}

	@Override
	public float getDepth() {
		return cDepth;
	}

	@Override
	public float getAlpha() {
		return cAlpha;
	}

}
