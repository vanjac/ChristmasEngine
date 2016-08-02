package engine;

/**
 * Keeps track of game resources, and allows many operations of the main loop to
 * be placed in a single step.
 * The main loop is split into "think" and "update" steps, among others. During
 * the "think" step, objects assess their environment and decide what they will
 * do next. During the "update" step, objects update their attributes.
 * @author jacob
 *
 */
public interface GameRunner {
	
	/**
	 * Get the PApplet running this game.
	 * @return the PApplet for the game
	 */
	public GameApplet getApplet();
	
	/**
	 * Get the resource manager for this game.
	 * @return a ResourceManager with all of the game's resources
	 */
	public ResourceManager getResources();
	
	/**
	 * Get the current game world time.
	 * @return the game time, in milliseconds
	 */
	public int getTime();
	
	/**
	 * Start the game simulation.
	 */
	public void start();
	
	/**
	 * Get the speed of the simulation.
	 * @return the speed of time.
	 */
	public float getSpeed();
	
	/**
	 * Set the speed of the simulation.
	 * @param speed the speed of time. 1 is normal speed, 2 is twice as fast
	 */
	public void setSpeed(float speed);
	
	/**
	 * Add a GameObject to the World. This will not add a Sprite to any
	 * SpriteGroups.
	 * @param o the object to add
	 */
	public void addObject(GameObject o);
	
	/**
	 * Remove a GameObject from the World.
	 * @param o the object to remove
	 */
	public void removeObject(GameObject o);
	
	/**
	 * Checks if the GameRunner has been updating this object.
	 * @param o the object to check for
	 * @return true if the object has been added and not removed, and if the
	 * object has been setup.
	 */
	public boolean hasObject(GameObject o);
	
	
	/**
	 * Get the number of objects being updated in the GameRunner.
	 * @return the number of objects in this GameRunner
	 */
	public int numObjects();
	
	/**
	 * Remove all objects from the GameRunner immediately
	 */
	public void clearAll();
	
	
	/**
	 * Execute a single step of the game loop.
	 * The operations that occur, in order:
	 * 	- delete: Any objects marked as "readyToDelete," as well as objects
	 * given in removeObject() calls, are deleted
	 *  - add: Any objects given in addObject() calls are added
	 *  - think: All objects' "think" methods are executed
	 *  - update: All objects' "update" methods are executed
	 */
	public void gameLoop();
}
