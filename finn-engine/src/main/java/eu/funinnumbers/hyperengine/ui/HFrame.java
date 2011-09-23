package eu.funinnumbers.hyperengine.ui;

import eu.funinnumbers.hyperengine.ui.ipcameraoutput.IPCameraFrame;
import eu.funinnumbers.hyperengine.ui.visualoutput.HyperEngine;
import eu.funinnumbers.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;

/**
 * Main UI frame for the hyper eu.funinnumbers.engine.
 */
public class HFrame extends JFrame {

    /**
     * Version for Serialization.
     */
    public static final long serialVersionUID = 42L; //NOPMD

    /**
     * Default constructor.
     */
    public HFrame() {
        super("Hyper Engine");

        final int[] pixels = new int[16 * 16];
        final Image image = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(16, 16, pixels, 0, 16));
        final Cursor transparentCursor =
                Toolkit.getDefaultToolkit().createCustomCursor
                        (image, new Point(0, 0), "invisibleCursor");

        // Make mouse cursor transparent (i.e. hide)
        setCursor(transparentCursor);

        //Get Screen Size
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // make sure frame is maximized state
        setExtendedState(Frame.MAXIMIZED_BOTH);

        // also remove any decorations
        setUndecorated(true);

        //Set default close operation on the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set Frames Dimensions
        setSize(dim.width, dim.height);
        //setSize(1024, 768);

        //Set frame's visibility
        setVisible(true);

        //Set frames background Color
        getContentPane().setBackground(Color.black);

        // Processing panel for visual output.
        final HyperEngine heng = new HyperEngine(getWidth(), getHeight() - 100);
        heng.init();
        this.add(heng);

        // pause a bit for HyperEngine to initialize
        try {
            Thread.sleep(5000);
        } catch (Exception ex) {
            Logger.getInstance().debug("Could not wait for Processing to initialize");
        }

        // IPCamera panel for grabbing frames
        final IPCameraFrame ipcam = new IPCameraFrame(getWidth(), getHeight() - 100);
        ipcam.setEnabled(true);
        ipcam.setVisible(true);
    }

}
