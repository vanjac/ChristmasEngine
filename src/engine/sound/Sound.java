package engine.sound;

import engine.*;
import processing.sound.*;
import java.nio.file.*;
import java.util.Collection;

/**
 * A wrapper for SoundFile, providing more control and hiding some of
 * SoundFile's strange bugs. Sound is NOT thread-safe!
 * Sounds add themselves to the GameRunner, so you don't have to.
 * @author jacob
 *
 */
public class Sound implements GameObject {
	private final GameRunner run;
	
	// documentation for SoundFile is here:
	// https://processing.org/reference/libraries/sound/SoundFile.html
	private final SoundFile sound;
	
	private enum ActionWhenFinished {
		STOP, LOOP, DELETE
	}
	
	private final float duration;
	
	private float cueTime;
	private int startTime;
	private boolean isPlaying;
	private float rate;
	private float volume;
	private ActionWhenFinished finishedAction;
	
	private boolean delete, cDelete;
	
	/**
	 * Construct a Sound using the audio file at the specified path. Sounds
	 * will automatically add themselves to the GameRunner.
	 * @param runner the GameRunner to use for updates
	 * @param file the path to the audio file
	 */
	public Sound(GameRunner runner, Path file) {
		run = runner;
		runner.addObject(this);
		sound = new SoundFile(runner.getApplet(), file.toString());
		
		delete = cDelete = false;
		
		duration = sound.duration();
		
		cueTime = 0;
		startTime = runner.getTime();
		isPlaying = false;
		rate = 1;
		volume = 1;
		finishedAction = ActionWhenFinished.STOP;
	}

	@Override
	public void start(int time) { }

	@Override
	public void think(int currentTime, int elapsedTime) { }

	@Override
	public Collection<GameObject> update() {
		cDelete = delete;
		
		//if at the end of the SoundFile
		if(isPlaying && getTime() > duration) {
			isPlaying = false;
			switch(finishedAction) {
			case STOP:
				cueTime = 0; //the next play() call will start at the beginning
				break;
			case DELETE:
				delete();
				break;
			case LOOP:
				restart();
				break;
			}
		}
		
		return null;
	}

	@Override
	public boolean readyToDelete() {
		return cDelete;
	}
	
	public void delete() {
		delete = true;
		stop();
	}
	
	
	/* Audio Controls: */
	
	/**
	 * Start playing the sound at its current position.
	 */
	public void play() {
		if(!isPlaying) {
			startTime = run.getTime();
			sound.jump(cueTime);
			isPlaying = true;
		}
	}
	
	/**
	 * Pause the sound at its current position.
	 */
	public void pause() {
		if(isPlaying) {
			cueTime = getTime();
			sound.stop();
			isPlaying = false;
		}
	}
	
	/**
	 * Pause the sound and jump to the beginning.
	 */
	public void stop() {
		pause();
		cueTime = 0;
	}
	
	/**
	 * Jump to the beginning and start playing.
	 */
	public void restart() {
		cueTime = 0;
		play();
	}
	
	/**
	 * If not already playing, jump to the beginning and start playing.
	 * Otherwise do nothing.
	 */
	public void restartIfStopped() {
		if(!isPlaying) {
			restart();
		}
	}
	
	/**
	 * Check if the sound is currently playing
	 * @return true if the sound is playing
	 */
	public boolean isPlaying() {
		return isPlaying;
	}
	
	/**
	 * Set the sound to loop when it is finished. It will keep looping until
	 * it is instructed otherwise.
	 */
	public void loopWhenFinished() {
		finishedAction = ActionWhenFinished.LOOP;
	}
	
	/**
	 * Set the sound to stop when it is finished. This is the default setting.
	 */
	public void stopWhenFinished() {
		finishedAction = ActionWhenFinished.STOP;
	}
	
	/**
	 * Set the sound to delete itself from the GameRunner when it is finished.
	 * It is a bad idea to keep references to a sound that has deleted itself.
	 */
	public void deleteWhenFinished() {
		finishedAction = ActionWhenFinished.DELETE;
	}
	
	/**
	 * Jump to the beginning and start looping the sound forever.
	 */
	public void loop() {
		loopWhenFinished();
		restart();
	}
	
	/**
	 * Get the current position of the sound. If the sound is not playing, this
	 * is the position it will start at when it starts playing.
	 * @return the position, in seconds
	 */
	public float getTime() {
		if(isPlaying) {
			return (float)(run.getTime() - startTime) / 1000.0f * rate;
		} else {
			return cueTime;
		}
	}
	
	/**
	 * Jump to a position in the sound. If the sound is not playing, this is
	 * the position it will start at when it starts playing.
	 * @param time the position, in seconds
	 */
	public void jump(float time) {
		cueTime = time;
		if(isPlaying) {
			startTime = run.getTime() - (int)(time / rate * 1000);
			sound.jump(time);
		}
	}
	
	/**
	 * Get the length of the sound if it were played at normal rate, regardless
	 * of what the current rate is.
	 * @return the length of the sound in seconds
	 */
	public float duration() {
		return duration;
	}
	
	/**
	 * Jump to the beginning.
	 */
	public void beginning() {
		jump(0);
	}
	
	/**
	 * Set the playback rate of the sound.
	 * @param rate the number of sound seconds played per game-time second.
	 * 1 is normal speed, 2 is twice as fast, 0.5 is twice as slow, etc.
	 */
	public void setRate(float rate) {
		float time = getTime();
		sound.rate(rate);
		this.rate = rate;
		jump(time); // sometimes setting the rate of a sound can restart it
	}
	
	/**
	 * Get the playback rate of the sound.
	 * @return the number of sound seconds played per game-time second.
	 * 1 is normal speed, 2 is twice as fast, 0.5 is twice as slow, etc.
	 */
	public float getRate() {
		return rate;
	}
	
	/**
	 * Set the volume of the sound.
	 * @param volume the volume; must be 0.0 to 1.0 (silent to full volume).
	 */
	public void setVolume(float volume) {
		sound.amp(volume);
		this.volume = volume;
	}
	
	/**
	 * Get the volume of the sound
	 * @return the volume, from 0.0 to 1.0 (silent to full volume).
	 */
	public float getVolume() {
		return volume;
	}
	
	/**
	 * For Mono files only: move the sound in a stereo panorama.
	 * @param pan the panoramic position: -1.0 is the left channel, 1.0 is the
	 * right channel, 0.0 plays equally in both channels.
	 */
	public void pan(float pan) {
		sound.pan(pan);
	}
}
