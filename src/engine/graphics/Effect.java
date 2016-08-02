package engine.graphics;

import engine.GameObject;
import processing.core.PGraphics;

/**
 * An effect is drawn onto the screen every frame. Unlike sprites, effects are
 * responsible for any transformations.
 * @author jacobvanthoog
 *
 */
public interface Effect extends GameObject {
	/**
	 * Draw the effect onto the provided PGraphics.
	 * @param g the graphics to draw the sprite on. May or may not have other
	 * things already on it.
	 */
	void draw(PGraphics g);
	
	/**
	 * The z-ordering of the effect. Lower numbers are drawn first; higher
	 * numbers are on top.
	 * @return the z-order of the effect
	 */
	public int getLayer();
}
