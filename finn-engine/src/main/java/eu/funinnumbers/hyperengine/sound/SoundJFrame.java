package eu.funinnumbers.hyperengine.sound;

import javax.swing.*;

public class SoundJFrame extends JFrame {
    public SoundJFrame() {

        // Processing panel for visual output.
        final SoundTesting snd = new SoundTesting();
        snd.init();
        this.add(snd);
    }


}
