package eu.funinnumbers.hyperengine.sound;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;


public class SoundTesting extends PApplet {
    private static final String SAMP_ROOT = "src/eu.funinnumbers.hyperengine/sound/samples/";
    Minim minim;
    AudioPlayer song;

    public void setup() {
        size(100, 100);

        minim = new Minim(this);

        // this loads mysong.wav from the data folder
        song = minim.loadFile(SAMP_ROOT + "groove.mp3");
        song.play();
    }

    public void draw() {
        background(0);
    }

    public void stop() {
        song.close();
        minim.stop();

        super.stop();
    }

}
