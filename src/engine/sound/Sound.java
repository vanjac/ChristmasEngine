package engine.sound;

import engine.*;
import processing.sound.*;
import java.nio.file.*;
import java.util.Collection;

/**
 * A wrapper for SoundFile, providing more control and bug fixes.
 * Sound is NOT thread-safe!
 * Sounds add themselves to the GameRunner, so you don't have to.
 * @author jacob
 *
 */
public class Sound implements GameObject {
	private final GameRunner run;
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
	public void start(int time) {
		
	}

	@Override
	public void think(int currentTime, int elapsedTime) {
		
	}

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
	
	public void play() {
		if(!isPlaying) {
			startTime = run.getTime();
			sound.jump(cueTime);
			isPlaying = true;
		}
	}
	
	public void pause() {
		if(isPlaying) {
			cueTime = getTime();
			sound.stop();
			isPlaying = false;
		}
	}
	
	public void stop() {
		pause();
		cueTime = 0;
	}
	
	public void restart() {
		cueTime = 0;
		play();
	}
	
	public void restartIfStopped() {
		if(!isPlaying) {
			restart();
		}
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void loopWhenFinished() {
		finishedAction = ActionWhenFinished.LOOP;
	}
	
	public void stopWhenFinished() {
		finishedAction = ActionWhenFinished.STOP;
	}
	
	public void deleteWhenFinished() {
		finishedAction = ActionWhenFinished.DELETE;
	}
	
	public void loop() {
		loopWhenFinished();
		restart();
	}
	
	public float getTime() {
		if(isPlaying) {
			return (float)(run.getTime() - startTime) / 1000.0f * rate;
		} else {
			return cueTime;
		}
	}
	
	public void jump(float time) {
		cueTime = time;
		if(isPlaying) {
			startTime = run.getTime() - (int)(time / rate * 1000);
			sound.jump(time);
		}
	}
	
	public float duration() {
		return duration;
	}
	
	public void beginning() {
		jump(0);
	}
	
	public void setRate(float rate) {
		float time = getTime();
		sound.rate(rate);
		this.rate = rate;
		jump(time); // sometimes setting the rate of a sound can restart it
	}
	
	public float getRate() {
		return rate;
	}
	
	public void setVolume(float volume) {
		sound.amp(volume);
		this.volume = volume;
	}
	
	public float getVolume() {
		return volume;
	}
	
	public void pan(float pan) {
		sound.pan(pan);
	}
}
