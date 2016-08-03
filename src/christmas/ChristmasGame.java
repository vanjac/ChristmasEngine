package christmas;

import java.nio.file.*;
import java.util.*;
import processing.core.*;
import engine.*;
import engine.sound.Sound;
import shiffman.box2d.*;

public class ChristmasGame extends GameApplet {
	private static Path RESOURCE_DIRECTORY;
	private static final String CONFIG_FILE_NAME = "resource.txt";
	private static Path CONFIG_FILE;

	enum GameScreen {
		START, GAME, DEAD
	}

	// the current game state
	GameScreen currentScreen = GameScreen.START;
	int gameFrame = -1;
	boolean gameSetup = false;
	boolean gameInitialized = false;
	PImage splashScreen;
	PImage deadScreen;
	int speedRoundPoints;
	boolean speedRound = false;
	int narwhalModePoints;
	boolean narwhalMode = false;
	int lastTime;

	int points;
	int deadMessage = -1;

	Box2DProcessing box2d;

	boolean leftPressed;
	boolean rightPressed;

	Sound mainMusic;
	Sound santaSound;
	float santaSoundMinDelay;
	float santaSoundMaxDelay;
	int nextSantaSoundTime;
	Sound rainSound;
	Sound pointSound;

	Reindeer mainReindeer;
	SleighFollower sleigh;
	HouseGroup houses;

	int minChimneyWait;
	int maxChimneyWait;
	int nextChimneyTime;
	List<Chimney> chimneys;

	boolean jumpWasPressed;

	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("Please specify the resource directory.");
		}
		RESOURCE_DIRECTORY = Paths.get(args[0]);
		CONFIG_FILE = Paths.get(args[0], CONFIG_FILE_NAME);
		
		GameApplet.startApplet(args, ChristmasGame.class);
	}

	protected void setMainReindeer(Reindeer deer) {
		mainReindeer = deer;
	}

	protected void setSleigh(SleighFollower sleigh) {
		this.sleigh = sleigh;
	}

	protected void setHouses(HouseGroup houses) {
		this.houses = houses;
	}

	protected void die() {
		currentScreen = GameScreen.DEAD;
		houses.stopMoving();
		for (Chimney c : chimneys) {
			c.stopMoving();
		}
	}

	protected HouseGroup getHouses() {
		return houses;
	}

	protected void addPoints(int amount, float xPos, float yPos) {
		points += amount;
		if (amount > 0)
			pointSound.restart();
		PointText text = new PointText(amount, xPos, yPos, getCanvasHeight());
		runner.addObject(text);
		group.addSprite(text);

		if (points >= speedRoundPoints && !speedRound)
			speedRound();
		if (points < speedRoundPoints && speedRound)
			normalMode();
		if (points >= narwhalModePoints && !narwhalMode)
			narwhalMode();
		if (points < narwhalModePoints && narwhalMode)
			speedRound();
	}

	@Override
	public void keyPressed() {
		switch (currentScreen) {
		case START:
		case DEAD:
			if (keyCode == ENTER) {
				gameInitialized = false;
				currentScreen = GameScreen.GAME;
			}
			break;
		case GAME:
			if (keyCode == UP) { // jump
				if (!jumpWasPressed) {
					jumpWasPressed = true;
					mainReindeer.jump();
				}
			}
			if (keyCode == LEFT) {
				leftPressed = true;
				mainReindeer.fly(-1);
			}
			if (keyCode == RIGHT) {
				rightPressed = true;
				mainReindeer.fly(1);
			}
			if (key == ' ') {
				sleigh.launchPresent();
			}
			break;
		}
	}

	@Override
	public void keyReleased() {
		switch (currentScreen) {
		case START:
			break;
		case GAME:
			if (keyCode == UP) { // jump
				jumpWasPressed = false;
			}
			if (keyCode == LEFT) {
				leftPressed = false;
				mainReindeer.fly(rightPressed ? 1 : 0);
			}
			if (keyCode == RIGHT) {
				rightPressed = false;
				mainReindeer.fly(leftPressed ? -1 : 0);
			}
			break;
		case DEAD:

			break;
		}
	}

	@Override
	public void setup() {
		super.setup();
		splashScreen = loadImage(RESOURCE_DIRECTORY .resolve("splashScreen.png")
				.toString());
		chimneys = new ArrayList<>();
		setCanvas(1920, 1080);
	}

	private void setupGame() {
		initializeGlobal(RESOURCE_DIRECTORY, CONFIG_FILE);
		ResourceManager resource = runner.getResources();

		deadScreen = resource.getImage("deadScreen");

		// setup sounds. only happens once
		mainMusic = resource.getSound("mainMusic", globalRunner);
		mainMusic.setVolume(resource.getFloat("mainMusicVolume"));
		mainMusic.loop();
		rainSound = resource.getSound("rainSound", globalRunner);
		rainSound.setVolume(resource.getFloat("rainVolume"));
		rainSound.loop();
		santaSound = resource.getSound("santaSound", globalRunner);
		santaSound.setVolume(resource.getFloat("santaSoundVolume"));
		santaSoundMinDelay = resource.getFloat("santaSoundMinDelay");
		santaSoundMaxDelay = resource.getFloat("santaSoundMaxDelay");
		nextSantaSoundTime = millis()
				+ (int) (random(santaSoundMinDelay, santaSoundMaxDelay) * 1000.0);
		santaSound.restart();

		speedRoundPoints = resource.getInt("speedRoundPoints");
		narwhalModePoints = resource.getInt("narwhalModePoints");
	}

	private void initialize() {
		initializeGame();
		
		mainMusic.setRate(1);

		box2d = new Box2DProcessing(this);

		MainGameInitializer initializer = new MainGameInitializer();
		initializer.initialize(this, runner, group, box2d);

		ResourceManager resource = runner.getResources();

		minChimneyWait = (int) (resource.getFloat("minChimneyDelay") * 1000);
		maxChimneyWait = (int) (resource.getFloat("maxChimneyDelay") * 1000);
		nextChimneyTime = millis();
		chimneys.clear();

		pointSound = resource.getSound("pointSound", runner);
		pointSound.setVolume(resource.getFloat("pointVolume"));

		points = 0;
		deadMessage = -1;
		speedRound = false;
		narwhalMode = false;
		mainReindeer.jump();
	}

	private void normalMode() {
		speedRound = false;
		narwhalMode = false;
		runner.setSpeed(1);
		mainMusic.setRate(1);
	}

	private void speedRound() {
		speedRound = true;
		narwhalMode = false;
		runner.setSpeed(2);
		mainMusic.setRate(2);

	}

	private void narwhalMode() {
		narwhalMode = true;
		speedRound = false;
		runner.setSpeed(0.5f);
		mainMusic.setRate(0.5f);
	}

	@Override
	public void draw() {
		super.draw();
		
		switch (currentScreen) {
		case START:
			image(splashScreen, 0, 0, getCanvasWidth(), getCanvasHeight());
			// loads the fonts so they will be ready
			textSize(36);
			super.prepareFont();
			break;
		case GAME:
			gameFrame++;

			if (gameFrame == 0) {
				textSize(36);
				textAlign(CENTER);
				text("Loading...", getCanvasWidth() / 2, 
						getCanvasHeight() * .75f);
				break;
			}
			// no break, continue.
		case DEAD:
			if (!gameSetup) {
				setupGame();
				gameSetup = true;
			}
			if (!gameInitialized) {
				initialize();
				gameInitialized = true;
			}

			box2d.step((float)(millis()-lastTime) / 1000f * runner.getSpeed(),
					10, 8);
			box2d.world.clearForces();
			
			super.gameLoop();

			// chimney stuff starts here
			if (currentScreen == GameScreen.GAME 
					&& millis() > nextChimneyTime) {
				Chimney chimney = new Chimney(runner, box2d, this);
				group.addSprite(chimney);
				runner.addObject(chimney);
				chimneys.add(chimney);
				nextChimneyTime = millis()
						+ (int) random(minChimneyWait, maxChimneyWait);
			}
			List<Chimney> chimneysToDelete = new ArrayList<>();
			for (Chimney c : chimneys) {
				if (c.readyToDelete())
					chimneysToDelete.add(c);
			}
			for (Chimney c : chimneysToDelete) {
				chimneys.remove(c);
			}

			if (currentScreen == GameScreen.DEAD) {
				image(deadScreen, 0, 0, getCanvasWidth(), getCanvasHeight());
			}

			fill(255, 255, 255);
			textSize(36);
			textAlign(CENTER, TOP);
			text(points, getCanvasWidth() / 2, 
					currentScreen == GameScreen.GAME ? 16
							: getCanvasHeight() / 2);
			if (narwhalMode) {
				text("Narwhal Mode activated!", getCanvasWidth() / 2, 56);
			} else if (speedRound) {
				text("Speed Round!!", getCanvasWidth() / 2, 56);
			}
			if (currentScreen == GameScreen.DEAD && points <= 0) {
				if (deadMessage < 0)
					deadMessage = (int) floor(random(0, 9));
				String deadText = "";
				switch (deadMessage) {
				case 0:
					deadText = "You tried.";
					break;
				case 1:
					deadText = "Good effort.";
					break;
				case 2:
					deadText = "We're proud of you.";
					break;
				case 3:
					deadText = "Everyone's a winner.";
					break;
				case 4:
					deadText = "In your heart, you won.";
					break;
				case 5:
					deadText = "So close.";
					break;
				case 6:
					deadText = "Keep trying.";
					break;
				case 7:
					deadText = "Never give up.";
					break;
				case 8:
					deadText = "You can be a winner if you imagine.";
					break;
				default:
					deadText = "";
				}
				text(deadText, getCanvasWidth() / 2,
						getCanvasHeight() / 2 + 40);
			}

			if (millis() > nextSantaSoundTime) {
				santaSound.restart();
				nextSantaSoundTime = millis()
						+ (int) (random(santaSoundMinDelay, santaSoundMaxDelay)
						* 1000.0);
			}

			break;
		}
		
		lastTime = millis();
	}

}
