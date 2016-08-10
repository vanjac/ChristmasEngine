package engine.sound;

import engine.*;
import java.nio.file.*;
import java.util.Collection;
import java.io.*;
import javax.sound.sampled.*;

/**
 * A wrapper for javax.sound.sampled.Clip.
 * Sounds add themselves to the GameRunner, so you don't have to.
 * @author jacob
 *
 */
public class Sound implements GameObject {
	private final Clip clip;
	
	private enum ActionWhenFinished {
		STOP, LOOP, DELETE
	}
	
	// one or the other of these might be supported
	private boolean isPlaying;
	private final float duration;
	private final FloatControl gainControl;
	private final FloatControl volumeControl;
	private ActionWhenFinished finishedAction;
	
	private boolean delete, cDelete;
	
	/**
	 * Construct a Sound using the audio file at the specified path. Sounds
	 * will automatically add themselves to the GameRunner.
	 * @param runner the GameRunner to use for updates
	 * @param file the path to the audio file
	 */
	public Sound(GameRunner runner, Path file) {
		try {
			AudioInputStream audioIn =
					AudioSystem.getAudioInputStream(file.toFile());
			// for some reason clips must be created in this way to prevent
			// "Invalid format" errors
			// see: http://stackoverflow.com/a/30833750
			DataLine.Info info =
					new DataLine.Info(Clip.class, audioIn.getFormat());
	        clip = (Clip)AudioSystem.getLine(info);
			clip.open(audioIn);
			
		} catch (IOException|UnsupportedAudioFileException
				|LineUnavailableException e) {
			throw new IOError(e);
		}
		
		runner.addObject(this);
		
		delete = cDelete = false;
		
		if(clip.isControlSupported(FloatControl.Type.VOLUME)) {
			volumeControl = (FloatControl)
					clip.getControl(FloatControl.Type.VOLUME);
			gainControl = null;
		} else if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			gainControl = (FloatControl)
					clip.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl = null;
		} else {
			throw new Error("Neither VOLUME nor MASTER_GAIN is supported!");
		}
		
		isPlaying = false;
		duration = (float)clip.getMicrosecondLength() / 1000.0f;
		
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
		if(isPlaying() && getTime() >= duration()) {
			switch(finishedAction) {
			case STOP:
				stop(); //the next play() call will start at the beginning
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
		if(!isPlaying()) {
			isPlaying = true;
			clip.start();
		}
	}
	
	/**
	 * Pause the sound at its current position.
	 */
	public void pause() {
		if(isPlaying()) {
			isPlaying = false;
			clip.stop();
		}
	}
	
	/**
	 * Pause the sound and jump to the beginning.
	 */
	public void stop() {
		pause();
		clip.setMicrosecondPosition(0);
	}
	
	/**
	 * Jump to the beginning and start playing.
	 */
	public void restart() {
		clip.setMicrosecondPosition(0);
		play();
	}
	
	/**
	 * If not already playing, jump to the beginning and start playing.
	 * Otherwise do nothing.
	 */
	public void restartIfStopped() {
		if(!isPlaying()) {
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
		return (float)clip.getMicrosecondPosition() / 1000.0f;
	}
	
	/**
	 * Jump to a position in the sound. If the sound is not playing, this is
	 * the position it will start at when it starts playing.
	 * @param time the position, in seconds
	 */
	public void jump(float time) {
		System.out.println("Jump!");
		clip.setMicrosecondPosition((long)(time * 1000.0));
		if(isPlaying()) {
			clip.stop();
			clip.start();
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
	 * Set the playback rate of the sound. NOT SUPPORTED!
	 * @param rate the number of sound seconds played per game-time second.
	 * 1 is normal speed, 2 is twice as fast, 0.5 is twice as slow, etc.
	 */
	public void setRate(float rate) {
		; // not supported
	}
	
	/**
	 * Get the playback rate of the sound.
	 * @return the number of sound seconds played per game-time second.
	 * 1 is normal speed, 2 is twice as fast, 0.5 is twice as slow, etc.
	 */
	public float getRate() {
		return 1.0f;
	}
	
	/**
	 * Set the volume of the sound.
	 * @param volume the volume; must be 0.0 to 1.0 (silent to full volume).
	 */
	public void setVolume(float volume) {
		if(gainControl != null) {
			float dB = (float) (Math.log10(volume) * 20.0);
			gainControl.setValue(dB);
		} else {
			volumeControl.setValue(volume * 65536.0f);
		}
	}
	
	/**
	 * Get the volume of the sound
	 * @return the volume, from 0.0 to 1.0 (silent to full volume).
	 */
	public float getVolume() {
		if(gainControl != null) {
			float dB = gainControl.getValue();
			return (float)Math.pow(10.0, (dB / 20.0));
		} else {
			return volumeControl.getValue() / 65536.0f;
		}
	}
}
