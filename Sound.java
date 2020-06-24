package version2;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

import java.io.File;

/**
 * [Sound.java]
 * This class plays music and sound effects. Create a new instance of this to play sounds.
 * Author: Andy Wang
 * Date: 14 June 2020
 */
public class Sound {
    public static final Sound HIT_SOUND = new Sound("Sound/PlayerHit.wav");
    public static final Sound DEATH = new Sound("Sound/Death.wav");
    public static final Sound PIG_OINK = new Sound("Sound/PigOink.wav");
    public static final Sound COW_MOO = new Sound("Sound/CowMoo.wav");
    public static final Sound ZOMBIE_GROAN = new Sound("Sound/ZombieGroan.wav");
    public static final Sound FOOTSTEPS = new Sound("Sound/Footsteps.wav");
    
    private Clip clipSave; //The sound clip to be played
    private File soundFile; //The sound's file
    
    /**
     * This constructor initalises a sound effect given the name of the sound file.
     * @param name The name of the sound file.
     */
    public Sound(String name) {
        this.soundFile = new File(name);
        
        try {
            this.clipSave = AudioSystem.getClip();
            this.clipSave.open(AudioSystem.getAudioInputStream(this.soundFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * The play method plays a sound effect.
     */
    public void play() {
        //Only play the sound if the sound is not currently playing
        if (!this.clipSave.isActive()) {
            //Reset the sound to the beginning
            this.clipSave.setMicrosecondPosition(0);
            this.clipSave.start();
        }
    }
    /**
     * The stop method stops a sound effect and resets it.
     */
    public void stop() {
        this.clipSave.stop();
        this.clipSave.setMicrosecondPosition(0);
    }
    /**
     * The isPlaying method determines if a sound is currently playing.
     * @return If the sound is playing or not.
     */
    public boolean isPlaying() {
        return this.clipSave.isRunning();
    }
}