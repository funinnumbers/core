package eu.funinnumbers.station.ui.sound;

import eu.funinnumbers.util.Logger;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Play WAV(AUFF, SND, AU might also be supported) sound files asynchronously (in a separate thread,
 * without interruption of main program). It is possible to use it in console or GUI Java programs for
 * playing user notification sounds.
 */
public class AsyncPlayWav extends Thread { //NOPMD

    /**
     * File to reproduce.
     */
    private final String filename;

    /**
     * simple balance control: toggles only left or right channel of stereo sound.
     */
    private final Position curPosition;

    private final static int BUFFER_SIZE = 524288; // 128Kb

    enum Position {
        LEFT, RIGHT, NORMAL
    }

    /**
     * Default constructor.
     *
     * @param wavfile file to reproduce.
     */
    public AsyncPlayWav(final String wavfile) {
        filename = wavfile;
        curPosition = Position.NORMAL;
    }

    /**
     * Advanced constructor.
     *
     * @param wavfile  file to reproduce.
     * @param position balance control.
     */
    public AsyncPlayWav(final String wavfile, final Position position) {
        filename = wavfile;
        curPosition = position;
    }

    /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's <code>run</code> method is called;
     * otherwise, this method does nothing and returns.
     * <p/>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see #start()
     * @see #stop()
     * @see #Thread(ThreadGroup, Runnable, String)
     */
    public void run() { //NOPMD

        final File soundFile = new File(filename);
        if (!soundFile.exists()) {
            Logger.getInstance().debug("Wave file not found: " + filename);
            return;
        }

        AudioInputStream audioInputStream;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);

        } catch (UnsupportedAudioFileException e1) {
            Logger.getInstance().debug("Unsupported Audio File", e1);
            return;

        } catch (IOException e1) {
            Logger.getInstance().debug("IO Error reading Audio File", e1);
            return;
        }

        final AudioFormat format = audioInputStream.getFormat();
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine auline;

        try {
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) {
            Logger.getInstance().debug("Unavailable Line", e);
            return;
        } catch (Exception e) {
            Logger.getInstance().debug("Generic Exception", e);
            return;
        }

        if (auline.isControlSupported(FloatControl.Type.PAN)) {
            final FloatControl pan = (FloatControl) auline
                    .getControl(FloatControl.Type.PAN);
            if (curPosition == Position.RIGHT) {
                pan.setValue(1.0f);
            } else if (curPosition == Position.LEFT) {
                pan.setValue(-1.0f);
            }
        }

        auline.start();
        int nBytesRead = 0;
        final byte[] abData = new byte[BUFFER_SIZE];

        try {
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    auline.write(abData, 0, nBytesRead);
                }
            }

        } catch (IOException e) {
            Logger.getInstance().debug("IO Error reading Audio File", e);

        } finally {
            auline.drain();
            auline.close();
        }

    }
}
