package engine;

public interface GameObject {
	/**
	 * Called once after the object is created, and before its first think()
	 * call.
	 * @param time the current time, in millis
	 */
	void start(int time);
	
	/**
	 * Based on the current state of the game, the object should plan what it
	 * is going to do without updating any of its values
	 * @param currentTime the current game time, in milliseconds
	 * @param elapsedTime the elapsed time since the last step, in millis
	 */
	void think(int currentTime, int elapsedTime);
	
	/**
	 * Update the values of the object. At this point, the object shouldn't look
	 * at the values of any other objects, because only some of them will have
	 * updated.
	 */
	void update();
	
	/**
	 * Return true if this object is "dead" and ready to be deleted.
	 * @return if this object should be deleted.
	 */
	boolean readyToDelete();
}