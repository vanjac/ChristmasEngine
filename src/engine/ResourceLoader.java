package engine;

/**
 * A ResourceLoader adds resources to a ResourceManager.
 * @author jacob
 *
 */
public interface ResourceLoader {
	/**
	 * Add some resources to the given ResourceManager.
	 * @param manager the ResourceManager to add resources to
	 */
	public void loadResources(ResourceManager manager);
}
