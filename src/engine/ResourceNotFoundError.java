package engine;

/**
 * An error used by the ResourceManager. Thrown if a requested resource was not
 * found.
 * @author jacobvanthoog
 *
 */
public class ResourceNotFoundError extends Error {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1043853227040543389L;
	private final String resourceName;
	private final String resourceType;
	
	/**
	 * Construct a ResourceNotFoundError.
	 * @param resource the name of the resource that was not found
	 * @param type a string describing the type of resource. For example,
	 * "number"
	 */
	public ResourceNotFoundError(String resource, String type) {
		resourceName = resource;
		resourceType = type;
	}
	
	@Override
	public String getLocalizedMessage() {
		return "The " + resourceType + " resource \"" + resourceName +
				"\" was not found.";
	}
}
