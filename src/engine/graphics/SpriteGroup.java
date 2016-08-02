package engine.graphics;

import java.util.*;
import engine.*;
import processing.core.PGraphics;

/**
 * A SpriteGroup contains multiple sprites or effects combined on a single layer
 * that can be manipulated like any other sprite.
 * 
 * Note: SpriteGroup implementations should not have any alpha value other than
 * 1.
 * @author jacob
 *
 */
public abstract class SpriteGroup extends PositionableSprite {
	private List<Effect> effects;
	
	private Set<Effect> effectsToAdd;
	private Set<Effect> effectsToRemove;
	
	private Map<Sprite, Effect> spriteEffects;
	
	private final GameRunner runner;
	
	private class SpriteEffect implements Effect {
		Sprite s;
		
		SpriteEffect(Sprite sprite) {
			s = sprite;
		}
		
		@Override
		public void start(int time) { }
		
		@Override
		public void think(int currentTime, int elapsedTime) { }
		
		@Override
		public void update() { }
		
		@Override
		public boolean readyToDelete() {
			return s.readyToDelete();
		}
		
		@Override
		public void draw(PGraphics g) {
			if(!s.isShown())
				return;
			if(s.getAlpha() <= 0)
				return;
			g.pushMatrix();
			float depth = s.getDepth();
			g.translate(s.getX() / depth, s.getY() / depth);
			g.rotate(s.getRotation());
			g.scale(s.getXScale(), s.getYScale());
			//TODO: alpha isn't working
			g.tint(255, s.getAlpha() * 255);
			s.draw(g);
			g.tint(255, 255);
			g.popMatrix();
		}
		
		@Override
		public int getLayer() {
			return s.getLayer();
		}
		
	}
	
	public SpriteGroup(GameRunner run) {
		super();
		runner = run;
		effects = new ArrayList<>();
		effectsToAdd = new HashSet<>();
		effectsToRemove = new HashSet<>();
		spriteEffects = new HashMap<>();
	}
	
	@Override
	public void think(int current, int elapsed) {
		for(Effect e : effects) {
			if(e.readyToDelete())
				removeEffect(e);
		}
		for(Effect e : effectsToAdd) {
			if(e.readyToDelete())
				removeEffect(e);
		}
	}
	
	@Override
	public void update() {
		super.update();
		effects.addAll(effectsToAdd);
		effects.removeAll(effectsToRemove);
		effectsToAdd.clear();
		effectsToRemove.clear();
	}
	
	public void addEffect(Effect e) {
		if(e == null)
			return;
		if(effects.contains(e))
			return;
		if(effectsToAdd.contains(e))
			return;
		if(effectsToRemove.contains(e)) {
			effectsToRemove.remove(e);
			return;
		}
		effectsToAdd.add(e);
	}
	
	public void addSprite(Sprite s) {
		Effect e = new SpriteEffect(s);
		addEffect(e);
		spriteEffects.put(s, e);
	}
	
	public void removeEffect(Effect e) {
		if(e == null)
			return;
		if(spriteEffects.containsValue(e))
			spriteEffects.remove(((SpriteEffect)e).s);
		if(effectsToRemove.contains(e))
			return;
		if(effectsToAdd.contains(e)) {
			effectsToAdd.remove(e);
			return;
		}
		if(!effects.contains(e))
			return;
		effectsToRemove.add(e);
	}
	
	public void removeSprite(Sprite s) {
		Effect e = spriteEffects.get(s);
		removeEffect(e);
	}
	
	public int numEffects() {
		return effects.size();
	}
	
	public void clearAll() {
		effects.clear();
		effectsToAdd.clear();
		effectsToRemove.clear();
		spriteEffects.clear();
	}

	@Override
	public void draw(PGraphics g) {
		//sort by layer -- lower layers are first in the list
		Collections.sort(effects, new Comparator<Effect>() {
			@Override
			public int compare(Effect e1, Effect e2) {
				return e1.getLayer() - e2.getLayer();
			}
		});
		
		for(Effect e : effects) {
			//if the effect or sprite has not been initialized, continue
			if(e instanceof SpriteEffect) {
				SpriteEffect sprite = (SpriteEffect)e;
				if(!runner.hasObject(sprite.s))
					continue;
			} else {
				if(!runner.hasObject(e))
					continue;
			}
			//draw the effect
			g.pushStyle();
			e.draw(g);
			g.popStyle();
		}
	}

}
