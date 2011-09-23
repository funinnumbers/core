package eu.funinnumbers.hyperengine.ui.ipcameraoutput;

import player.PlayerControl;
import player.PlayerFactory;
import eu.funinnumbers.util.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Periodically grabs a frame from the IP camera.
 */
public class IPCameraFrame extends JFrame {

    /**
     * The URL of the IP camera.
     */
    private final String cameraURL = "rtsp://192.168.1.50:7070";

    /**
     * This is the control interface for the player.
     */
    private final PlayerControl m4Player;

    /**
     * Default constructor.
     *
     * @param thisPanel the HyperEngine panel.
     */
    public IPCameraFrame(final int screenwidth, final int screenheight) {
        super("IP Camera grubber");

        // Get Processing stuff
        final int width = screenwidth / 2 - 20;
        final int height = screenheight / 2 - 20;
        final int posX = screenwidth / 2 + 10;
        final int posY = 10;

        // Create an instance of PlayerControl on which we can open the content etc
        m4Player = PlayerFactory.createMPEG4Player();
        m4Player.setAutoSize(false);
        m4Player.setScaling(false);
        m4Player.setPlayerEndAction(PlayerControl.END_ACTION_CONTINUE);

        // setup JFrame
        setUndecorated(true);
        setSize(width, height);
        getContentPane().setBackground(Color.black);
        add(m4Player.getRendererComponent());
        validate();
        pack();
        setVisible(true);
        setLocation(posX, posY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Stop & close MPEG-4 player (should not be needed here really)
        // but just in case there was a problem its safe to do this
        m4Player.stopUrl();

        try {
            m4Player.open(cameraURL);
            m4Player.start();

            Logger.getInstance().debug("Connected to IP Camera at " + cameraURL);

        } catch (Exception ex) {
            Logger.getInstance().debug("Unable to connect to IP camera", ex);
            m4Player.stopUrl();
        }
    }

    /**
     * Grab the frame from the camera.
     */
    public void run() {
//        try {
//            Logger.getInstance().debug("Starting grub of image " + imgCounter);
//            long timerValue = System.nanoTime();
//
//            // Access the screen capture interface
//            final PlayerScreenCapture psc = m4Player.getScreenCapture();
//
//            if (psc != null) {
//                Logger.getInstance().debug("[" + (System.nanoTime() - timerValue) / 1000000 + " ms] Grubbed image " + imgCounter);
//                timerValue = System.nanoTime();
//                final String thisImgUrl = imageURL + imgCounter + ".jpg";
//
//                final FileOutputStream fos = new FileOutputStream(thisImgUrl);
//                psc.write(PlayerScreenCapture.FORMAT_JPG, fos);
//                fos.close();
//
//                Logger.getInstance().debug("[" + (System.nanoTime() - timerValue) / 1000000 + " ms] Dumped image " + imgCounter);
//                timerValue = System.nanoTime();
//
//                // Load & resize image using Processing format
//                final int width = parent.getImgIPCamera().width;
//                final int height = parent.getImgIPCamera().height;
//                final PImage newIPCameraImg = parent.loadImage(thisImgUrl);
//                newIPCameraImg.resize(width, height);
//
//                Logger.getInstance().debug("[" + (System.nanoTime() - timerValue) / 1000000 + " ms] Resized image " + imgCounter);
//                timerValue = System.nanoTime();
//
//                // Update parent panel
//                parent.setImgIPCamera(newIPCameraImg);
//
//                Logger.getInstance().debug("[" + (System.nanoTime() - timerValue) / 1000000 + " ms] Updated processing with image " + imgCounter);
//
//                Logger.getInstance().debug("Image " + imgCounter + " grabbed to " + thisImgUrl);
//                // Increase counter
//                imgCounter++;
//
//            }
//        } catch (Exception ex) {
//            Logger.getInstance().debug("Unable to grab image from IP Camera", ex);
//        }
    }

}
