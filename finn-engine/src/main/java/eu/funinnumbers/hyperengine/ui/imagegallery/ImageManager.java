package eu.funinnumbers.hyperengine.ui.imagegallery;

import processing.core.PApplet;
import processing.core.PImage;
import eu.funinnumbers.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Handles a collection of images.
 */
public final class ImageManager { //NOPMD

    /**
     * Single instance of manager.
     */
    private static ImageManager thisInstance;

    /**
     * Directory name containing images.
     */
    public static final String dirImages = "src/eu.funinnumbers.hyperengine/ui/imagegallery/img";

    /**
     * Set of image filenames.
     */
    private Collection<String> imageFilenames;

    /**
     * Set of images.
     */
    private List<PImage> images; //NOPMD

    /**
     * Source of randomness.
     */
    private final Random random;
    
    /**
     * Private constructor.
     */
    private ImageManager() {
        images = new ArrayList<PImage>();
        random = new Random();

        // Locate image file names.
        locateImages();
    }

    /**
     * Provides access to the single instance of the manager.
     *
     * @return reference to the unique instance of the ImageManager.
     */
    public static ImageManager getInstance() {
        synchronized (ImageManager.class) {
            if (thisInstance == null) {
                thisInstance = new ImageManager();
            }
        }

        return thisInstance;
    }

    /**
     * Locate all image filenames found in the directory.
     */
    private void locateImages() {
        // Locate all files in directory
        imageFilenames = locateFiles(dirImages);
    }

    /**
     * Locate all files containing poems.
     *
     * @param dirName name of directory to examine.
     * @return collection of file names.
     */
    private Collection<String> locateFiles(final String dirName) {
        final ArrayList<String> allFiles = new ArrayList<String>();

        // get a list of all files in a directory
        final File dir = new File(dirName);
        final String[] files = dir.list();
        for (final String file : files) {
            // filter text documents
            if (file.endsWith(".jpg")) {
                allFiles.add(file);
            }
        }

        return allFiles;
    }

    /**
     * Return a random image from the list.
     *
     * @return a re-sized image.
     */
    public PImage nextImage() {
        return images.get(random.nextInt(images.size()));
    }

    /**
     * Load all images and re-size the based on maximum size.
     * @param panel the Processing applet that will display the gallery.
     * @param maxWidth maximum width of gallery images.
     * @param maxHeight maximum height of gallery images.
     */
    public void loadImages(final PApplet panel, final int maxWidth, final int maxHeight) { //NOPMD
        for (final String imageFilename : imageFilenames) {
            Logger.getInstance().debug("Loading gallery image " + dirImages + "/" + imageFilename);
            final PImage thisImage = panel.loadImage(dirImages + "/" + imageFilename);

            // Re-size image based on width
            if (thisImage.width > maxWidth) {
                final int height = (maxWidth / thisImage.width) * thisImage.height;
                thisImage.resize(maxWidth, height);

            } else if (thisImage.width < maxWidth * 0.8) {
                final int height = (maxWidth / thisImage.width) * thisImage.height;
                thisImage.resize(maxWidth, height);
            }

            // Re-size image based on height
            if (thisImage.height > maxHeight) {                
                final int width = (maxHeight / thisImage.height) * thisImage.width;
                thisImage.resize(width, maxHeight);
            }

            // Store image
            images.add(thisImage);
        }
    }

}
