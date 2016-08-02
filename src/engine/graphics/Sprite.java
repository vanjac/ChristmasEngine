package engine.graphics;

import processing.core.PGraphics;

/**
 * A Sprite can be drawn on the screen, positioned, rotated, scaled, and drawn
 * on the screen.
 * @author jacob
 *
 */
public interface Sprite extends engine.GameObject {
	
	/**
	 * Draw the sprite onto the provided PGraphics. The sprite should be
	 * drawn at (0, 0) and shouldn't have rotation, scaling, or transparency
	 * applied.
	 * @param g the graphics to draw the sprite on. May or may not have other
	 * things already on it.
	 */
	void draw(PGraphics g);
	
	
	public boolean isShown();
	
	/**
	 * The z-ordering of the sprite. Lower numbers are drawn first; higher
	 * numbers are on top.
	 * @return the z-order of the sprite
	 */
	public int getLayer();
	
	public float getX();
	public float getY();
	/**
	 * Get the rotation of the sprite.
	 * @return the rotation, in radians
	 */
	public float getRotation();
	public float getXScale();
	public float getYScale();
	
	/**
	 * The x velocity of the sprite. This is only useful for other sprites, for
	 * physics. It is up to this sprite to update its position based on its
	 * velocity.
	 * @return the x velocity, in units per second.
	 */
	public float getXVelocity();
	public float getYVelocity();
	
	/**
	 * The 3d depth of the sprite, used for parallax effects. Doesn't affect
	 * size.
	 * @return the depth of the sprite. 1 is right at the screen, higher numbers
	 * are farther away.
	 */
	public float getDepth();
	
	/**
	 * The transparency of this sprite.
	 * @return the alpha value of the sprite scaled from 0 (invisible) to 1
	 * (visible).
	 */
	public float getAlpha();
}
