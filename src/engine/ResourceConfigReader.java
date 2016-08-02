package engine;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * A ResourceLoader that reads resources from a config file (specified in
 * RESOURCE_CONFIG_FILE_PATH).
 * The config file has a resource per line. Empty lines or lines that begin with
 * a '#' are ignored. For all other lines, the first character specifies the
 * type of resource. This can be:
 * 	- b: Flag (boolean)
 *  - n: Number (double)
 *  - t: Text (String)
 *  - i: Image (path to an image file)
 *  - a: Audio (path to a sound file)
 * After this character should be some whitespace. Following that is the key
 * name and value of the property, separated by whitespace. The key name
 * shouldn't contain whitespace, but the value can.
 * @author jacob
 *
 */
public class ResourceConfigReader implements ResourceLoader {
	
	private final Path config;
	
	private int errors;
	
	public ResourceConfigReader(Path configFile) {
		config = configFile;
	}
	
	@Override
	public void loadResources(ResourceManager manager) {
		System.out.println("Reading resource config file at "
				+ config.toAbsolutePath().toString());
		List<String> strings;
		try {
			strings = Files.readAllLines(config);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		errors = 0;
		for(String s : strings) {
			interpretLine(s, manager);
		}
		
		System.out.println("Done reading config. "
				+ errors + " errors occurred.");
	}
	
	//true if success
	private void interpretLine(String line, ResourceManager manager) {
		String s = line.trim();
		if(s.isEmpty())
			return;
		s = simplifyWhitespace(s);
		
		char resourceType;
		String key;
		String value;
		
		try {
			resourceType = s.charAt(0);
			resourceType = Character.toLowerCase(resourceType);
			if(resourceType == '#') //the line is a comment, skip it
				return;
			s = s.substring(s.indexOf(' ') + 1);
			key = s.substring(0, s.indexOf(' '));
			value = s.substring(s.indexOf(' ') + 1);
		} catch (StringIndexOutOfBoundsException e) {
			System.err.println("Error parsing line: " + line);
			errors++;
			return;
		}
		
		switch(resourceType) {
		case 'b': //boolean
			boolean flag = stringToBoolean(value);
			logKeyValue("Boolean", key, flag);
			manager.addFlag(key, flag);
			break;
		case 'n': //number (double)
			double number = Double.parseDouble(value);
			logKeyValue("Number", key, number);
			manager.addNumber(key, number);
			break;
		case 't': //text (string)
			logKeyValue("String", key, value);
			value = value.replace("\\n", "\n");
			manager.addString(key, value);
			break;
		case 'i': //image (path to image)
			logKeyValue("Image", key, value);
			manager.addImage(key, value);
			break;
		case 'a': //sound
			logKeyValue("Audio", key, value);
			manager.addSound(key, value);
			break;
		default:
			errors++;
			System.err.println("Error parsing line: " + line);
		}
	}
	
	/**
	 * Replace large groups of whitespace with single spaces.
	 * @param s the string to read
	 * @return the string with simplified whitespace
	 */
	private String simplifyWhitespace(String s) {
		s = s.trim();
		StringBuilder newString = new StringBuilder();
		
		boolean lastCharWasWhitespace = false;
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(Character.isWhitespace(c)) {
				if(!lastCharWasWhitespace) {
					newString.append(' ');
					lastCharWasWhitespace = true;
				}
			} else {
				newString.append(c);
				lastCharWasWhitespace = false;
			}
		}
		
		return newString.toString();
	}
	
	
	private boolean stringToBoolean(String s) {
		s = s.trim().toLowerCase();
		if(s.isEmpty())
			return false;
		if(s.equals("0"))
			return false;
		if(s.equals("false"))
			return false;
		if(s.equals("no"))
			return false;
		if(s.equals("off"))
			return false;
		if(s.equals("disabled"))
			return false;
		return true;
	}
	
	private void logKeyValue(String type, String key, Object value) {
		System.out.println(String.format("%8s", type)
				+ " " + key + ": " + value);
	}
}
