package engine;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.nio.file.*;

import engine.graphics.*;
import processing.core.*;

/**
 * The main game applet. Keeps track of the game runner, the main sprite group,
 * and other game state. Games should extend this.
 * @author jacobvanthoog
 *
 */
public abstract class GameApplet extends PApplet {
	public static final int ENGINE_VERSION_MAJOR = 0;
	public static final int ENGINE_VERSION_MINOR = 0;
	public static final int ENGINE_VERSION_REVISION = 0;
	private static final String STARTUP_MESSAGE =
			"..:: ChristmasEngine ::..\nDesigned by Jacob van't Hoog";
	
	private static int xRes;
	private static int yRes;
	private static boolean fullScreen;
	private static String renderer;
	private static int smooth;
	private static boolean startInDevMode = false;
	
	
	protected SpriteGroup group;
	protected GameRunner runner;
	//the global runner isn't reset when the game resets
	protected GameRunner globalRunner;
	
	protected boolean devMode = false;
	
	protected float canvasWidth;
	protected float canvasHeight;
	
	
	/**
	 * Start the game, with some certain parameters that can only be set once.
	 * @param args command line arguments to interpret. Any unrecognized
	 * arguments will be ignored.
	 * @param appletClass the GameApplet class to start
	 * @param _xRes the x resolution of the window
	 * @param _yRes the y resolution of the window
	 * @param _fullScreen whether to run in fullscreen mode. This will override
	 * the given resolution.
	 * @param _renderer the renderer class to use. Can be one of the ones
	 * defined in PConstants (P2D or P3D, but not PDF), or can be null or an
	 * empty string to use the default, software renderer.
	 * @param _smooth how much anti-aliasing to draw with. The meaning of this
	 * can differ among render implementations. 0 turns off smoothing, and
	 * negative numbers will choose the default setting. Other accepted values
	 * are 2 or 3 for the default software renderer, or 2, 4, or 8 for the P2D
	 * or P3D renderers. Smooth levels 4 and 8 may not be compatible with all
	 * hardware.
	 */
	public static void startApplet(String[] args,
			Class<? extends GameApplet> appletClass) {
		System.out.println(STARTUP_MESSAGE);
		System.out.println("v" + ENGINE_VERSION_MAJOR
				+ "." + ENGINE_VERSION_MINOR
				+ "." + ENGINE_VERSION_REVISION
				+ "\n\n");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		xRes = (int)screenSize.getWidth();
		yRes = (int)screenSize.getHeight();
		fullScreen = true;
		renderer = PApplet.P2D;
		smooth = 0;
		
		for(int i = 1; i < args.length; i++) {
			String s = args[i].trim().toLowerCase();
			String value = "";
			if(i + 1 < args.length)
				value = args[i+1].trim();
			
			if(s.equals("-dev")) {
				startInDevMode = true;
			} else if(s.equals("-xres")) {
				xRes = Integer.parseInt(value);
				i += 1;
			} else if(s.equals("-yres")) {
				yRes = Integer.parseInt(value);
				i += 1;
			} else if(s.equals("-window")) {
				fullScreen = false;
			} else if(s.equals("-fullscreen")) {
				fullScreen = true;
			} else if(s.equals("-smooth")) {
				smooth = Integer.parseInt(value);
				i += 1;
			} else if(s.equals("-software")) {
				renderer = null;
			} else if(s.equals("-gl")) {
				renderer = value;
				i += 1;
			} else {
				System.err.println("WARNING: Unknown argument " + s);
			}
		}
		
		PApplet.main(appletClass.getName());
	}
	
	
	/**
	 * Reset some of the style states of the given PGraphics to their default
	 * values.
	 * @param g the PGraphics to reset
	 */
	public static void resetStyle(PGraphics g) {
		g.colorMode(RGB, 255);
		g.fill(g.color(255));
		g.stroke(g.color(0));
		g.noTint();
		g.strokeWeight(1);
		g.strokeCap(ROUND);
		g.strokeJoin(MITER);
		g.imageMode(CORNER);
		g.rectMode(CORNER);
		g.ellipseMode(CENTER);
		g.shapeMode(CORNER);
		g.textAlign(LEFT, BASELINE);
		g.textureMode(IMAGE);
		g.textureWrap(CLAMP);
		g.noClip();
	}
	
	
	@Override
	public final void settings() {
		System.out.println("Setting up applet...");
		
		if(renderer != null && !renderer.isEmpty())
			size(xRes, yRes, renderer);
		else
			size(xRes, yRes);
			
		if(fullScreen)
			fullScreen();
		if(smooth == 0)
			noSmooth();
		else if(smooth > 0)
			smooth(smooth);
	}
	
	@Override
	public void setup() {
		System.out.println("Setting up game...");
		if(startInDevMode)
			devMode(true);
		canvasWidth = 1;
		canvasHeight = 1;
		
		// draw "..." loading symbol
		background(0);
		float dotSize = 12;
		ellipse(width/2 - 32, height/2, dotSize, dotSize);
		ellipse(width/2, height/2, dotSize, dotSize);
		ellipse(width/2 + 32, height/2, dotSize, dotSize);
	}
	
	@Override
	public void draw() {
		scale((float)width / canvasWidth, (float)height / canvasHeight);
	}
	
	protected void setCanvas(float width, float height) {
		canvasWidth = width;
		canvasHeight = height;
	}
	
	public float getCanvasWidth() {
		return canvasWidth;
	}
	
	public float getCanvasHeight() {
		return canvasHeight;
	}
	
	/**
	 * Initialize some things that will be needed for as long as the game is
	 * running. This includes loading resources, and creating the main
	 * GameRunner and SpriteGroup.
	 * @param resources the path to the game resource directory
	 * @param config the path to the game configuration file
	 */
	protected void initializeGlobal(Path resources, Path config) {
		System.out.println("Initializing global resources...");
		ResourceManager resource = new ResourceManager(this, resources);
		ResourceLoader loader = new ResourceConfigReader(config);
		loader.loadResources(resource);
		runner = new GenericGameRunner(this, resource);
		
		group = new SpriteGroup(runner) {
			@Override
			public void start(int time) { }
		};
		
		//the real game runner will be reset with each game reset, but things
		//like music should continue through this. The globalRunner won't be
		//reset when the game is.
		globalRunner = new GenericGameRunner(this, resource);
		globalRunner.start();
	}
	
	/**
	 * Initialize a new instance of the game. Restart the game if it is already
	 * running. Start the main GameRunner.
	 */
	protected void initializeGame() {
		System.out.println("Initializing game...");
		runner.clearAll();
		group.clearAll();
		runner.setSpeed(1);
		runner.addObject(group);
		
		runner.start();
	}
	
	/**
	 * Enable or disable dev mode.
	 * @param enabled whether to enable dev mode.
	 */
	protected void devMode(boolean enabled) {
		devMode = enabled;
		if(enabled)
			System.out.println("Dev mode enabled.");
	}
	
	/**
	 * Should be called once per frame. Go through one loop of the game.
	 */
	protected void gameLoop() {
		runner.gameLoop();
		globalRunner.gameLoop();
		group.draw(g);
		
		if(devMode) {
			textSize(12);
			textAlign(LEFT);
			text((int)frameRate + " fps\n"
					+ globalRunner.numObjects() + " global objects\n"
					+ runner.numObjects() + " objects\n"
					+ group.numEffects() + " effects\n"
					+ Runtime.getRuntime().totalMemory() / (1024*1024) + " MB"
					, 0, 10);
		}
	}
	
	/**
	 * Load the current PFont, at the current font size, so it is ready to be
	 * used when needed.
	 */
	protected void prepareFont() {
		text(" ", 0, 0);
	}
}
