package engine.graphics;

import processing.core.*;

/**
 * A sprite with an image.
 * @author jacob
 *
 */
public abstract class ImageSprite extends PositionableSprite {
	private PImage image;
	private float width;
	private float height;
	private boolean scaled;
	
	protected ImageSprite(PImage image, float width, float height) {
		setImage(image, width, height);
	}
	
	protected ImageSprite(PImage image) {
		setImage(image);
	}
	
	protected ImageSprite(PImage image, float scale) {
		setImage(image, scale);
	}
	
	protected ImageSprite() {
		image = null;
		width = 0;
		height = 0;
	}
	
	protected PImage getImage() {
		return image;
	}
	
	protected void setImage(PImage image, float width, float height) {
		this.image = image;
		setDimensions(width, height);
	}
	
	protected void setImage(PImage image) {
		this.image = image;
		if(image == null) {
			width = 0;
			height = 0;
		} else {
			width = image.width;
			height = image.height;
			scaled = false;
		}
	}
	
	protected void setImage(PImage image, float scale) {
		this.image = image;
		setScale(scale);
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	protected void setDimensions(float width, float height) {
		this.width = width;
		this.height = height;
		if(width != image.width || height != image.height)
			scaled = true;
	}
	
	protected void setScale(float scale) {
		width = image.width * scale;
		height = image.height * scale;
		scaled = scale != 1;
	}

	@Override
	public void draw(PGraphics g) {
		if(image == null)
			return;
		g.imageMode(PGraphics.CENTER);
		if(scaled)
			g.image(image, 0, 0, width, height);
		else
			g.image(image, 0, 0);
	}
}
