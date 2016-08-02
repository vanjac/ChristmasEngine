package engine;

import java.util.*;
import java.nio.file.*;
import processing.core.*;
import engine.sound.Sound;


/**
 * A ResourceManager keeps track of game resources. A resource can be a game
 * asset, or a value used in the game.
 * 
 * ResourceManager uses a PApplet to load image and sound files.
 * 
 * Types of resources:
 * - Flag (boolean)
 * - Number (double)
 * - String (String)
 * - Image (PImage)
 * - Sound (SoundFile)
 * @author jacob
 *
 */
public class ResourceManager {
	PApplet a;
	
	private final Path resourceDirectory;
	
	Map<String, Boolean> flags;
	Map<String, Double> numbers;
	Map<String, String> strings;
	Map<String, PImage> images;
	Map<String, Path> sounds;
	
	public ResourceManager(PApplet applet, Path resourceDir) {
		a = applet;
		flags = new HashMap<>();
		numbers = new HashMap<>();
		strings = new HashMap<>();
		images = new HashMap<>();
		sounds = new HashMap<>();
		resourceDirectory = resourceDir.toAbsolutePath();
	}
	
	public boolean hasResource(String name) {
		return flags.containsKey(name)
				|| numbers.containsKey(name)
				|| strings.containsKey(name)
				|| images.containsKey(name)
				|| sounds.containsKey(name);
	}
	
	public void addFlag(String name, boolean value) {
		flags.put(name, value);
	}
	
	public boolean getFlag(String name) {
		if(!flags.containsKey(name))
			throw new ResourceNotFoundError(name, "flag");
		return flags.get(name);
	}
	
	public void addNumber(String name, double value) {
		numbers.put(name, value);
	}
	
	public double getDouble(String name) {
		if(!numbers.containsKey(name))
			throw new ResourceNotFoundError(name, "number");
		return numbers.get(name);
	}
	
	public float getFloat(String name) {
		return (float)getDouble(name);
	}
	
	public int getInt(String name) {
		return (int)getDouble(name);
	}
	
	public long getLong(String name) {
		return (long)getDouble(name);
	}
	
	public short getShort(String name) {
		return (short)getDouble(name);
	}
	
	public byte getByte(String name) {
		return (byte)getDouble(name);
	}
	
	public void addString(String name, String value) {
		strings.put(name, value);
	}
	
	public String getString(String name) {
		if(!strings.containsKey(name))
			throw new ResourceNotFoundError(name, "string");
		return strings.get(name);
	}
	
	public void addImage(String name, String path) {
		PImage image = a.loadImage(resourceDirectory.resolve(path).toString());
		if(image == null)
			return;
		images.put(name, image);
	}
	
	public PImage getImage(String name) {
		if(!images.containsKey(name))
			throw new ResourceNotFoundError(name, "image");
		return images.get(name);
	}
	
	public void addSound(String name, String path) {
		Path p = resourceDirectory.resolve(path);
		if(!p.toFile().exists())
			return;
		sounds.put(name, p);
	}
	
	public Sound getSound(String name, GameRunner runner) {
		if(!sounds.containsKey(name))
			throw new ResourceNotFoundError(name, "sound");
		return new Sound(runner, sounds.get(name));
	}
}
