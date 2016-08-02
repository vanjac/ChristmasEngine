package engine;

import java.util.*;

/**
 * Keeps track of game resources, and allows many operations of the main loop to
 * be placed in a single step.
 * The main loop is split into "think" and "update" steps, among others. During
 * the "think" step, objects assess their environment and decide what they will
 * do next. During the "update" step, objects update their attributes.
 * @author jacob
 *
 */
public class GenericGameRunner implements GameRunner {
	private final GameApplet applet;
	private final ResourceManager resourceManager;
	
	private final Set<GameObject> objects;
	private final Set<GameObject> objectsToAdd;
	private final Set<GameObject> objectsToRemove;
	private final Set<GameObject> objectsToStart;
	private long lastSystemTime;
	private int currentGameTime;
	private float speed;
	
	public GenericGameRunner(GameApplet applet, ResourceManager resources) {
		objects = new HashSet<>();
		objectsToAdd = new HashSet<>();
		objectsToRemove = new HashSet<>();
		objectsToStart = new HashSet<>();
		resourceManager = resources;
		this.applet = applet;
		speed = 1;
	}
	
	@Override
	public GameApplet getApplet() {
		return applet;
	}
	
	@Override
	public ResourceManager getResources() {
		return resourceManager;
	}
	
	@Override
	public int getTime() {
		return currentGameTime;
	}
	
	@Override
	public void start() {
		lastSystemTime = System.currentTimeMillis();
		currentGameTime = 0;
	}
	
	@Override
	public float getSpeed() {
		return speed;
	}
	
	@Override
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	@Override
	public void addObject(GameObject o) {
		if(o == null)
			return;
		if(objects.contains(o))
			return;
		if(objectsToAdd.contains(o))
			return;
		if(objectsToRemove.contains(o)) {
			objectsToRemove.remove(o);
			return;
		}
		objectsToAdd.add(o);
	}
	
	@Override
	public void removeObject(GameObject o) {
		if(o == null)
			return;
		if(objectsToRemove.contains(o))
			return;
		if(objectsToAdd.contains(o)) {
			objectsToAdd.remove(o);
			return;
		}
		if(!objects.contains(o))
			return;
		objectsToRemove.add(o);
	}
	
	@Override
	public boolean hasObject(GameObject o) {
		return objects.contains(o);
	}
	
	@Override
	public int numObjects() {
		return objects.size();
	}
	
	@Override
	public void clearAll() {
		objectsToAdd.clear();
		objectsToRemove.clear();
		objectsToStart.clear();
		objects.clear();
	}
	
	
	@Override
	public void gameLoop() {
		int time = getTime();
		long systemTime = System.currentTimeMillis();
		int elapsedTime = (int)((systemTime - lastSystemTime) * speed);
		
		//delete
		//find objects that are ready to delete
		for(GameObject o : objects) {
			if(o.readyToDelete())
				removeObject(o);
		}
		for(GameObject o : objectsToAdd) {
			if(o.readyToDelete())
				removeObject(o);
		}
		//remove objects
		objects.removeAll(objectsToRemove);
		objectsToRemove.clear();
		
		//add
		//add new objects
		for(GameObject o : objectsToAdd) {
			objects.add(o);
			objectsToStart.add(o);
		}
		objectsToAdd.clear();
		for(GameObject o : objectsToStart) {
			o.start(time);
		}
		objectsToStart.clear();
		
		//think
		for(GameObject o : objects) {
			o.think(time, elapsedTime);
		}
		
		//update
		for(GameObject o : objects) {
			o.update();
		}
		
		currentGameTime += elapsedTime;
		lastSystemTime = systemTime;
	}
}
