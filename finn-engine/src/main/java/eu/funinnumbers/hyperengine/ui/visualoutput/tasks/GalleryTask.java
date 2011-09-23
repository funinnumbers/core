package eu.funinnumbers.hyperengine.ui.visualoutput.tasks;

import eu.funinnumbers.hyperengine.ui.imagegallery.ImageManager;
import eu.funinnumbers.hyperengine.ui.visualoutput.HyperEngine;
import processing.core.PImage;

import java.util.TimerTask;

/**
 * Choose next Gallery Image.
 */
public class GalleryTask extends TimerTask {

    /**
     * Parent applet.
     */
    private final HyperEngine parent;

    /**
     * Default Constructor.
     *
     * @param thisPanel the Processing applet.
     */
    public GalleryTask(final HyperEngine thisPanel) {
        parent = thisPanel;
    }

    /**
     * Switch images.
     */
    public void run() {
        // Choose a new image
        final PImage newImage = ImageManager.getInstance().nextImage();
        final int galleryPosX = ((parent.getWidth() / 2) - newImage.width) / 2;
        final int galleryPosY = (parent.getHeight() / 2) + ((parent.getHeight() / 2) - newImage.height) / 2;
        final int galleryAlpha = 0;

        // Update parent applet
        parent.setImgGalleryNew(newImage);
        parent.setGalleryNewPosX(galleryPosX);
        parent.setGalleryNewPosY(galleryPosY);
        parent.setGalleryNewAlpha(galleryAlpha);

        // Register Switch task
        parent.getTimer().scheduleAtFixedRate(new GallerySwitchTask(parent), HyperEngine.TIMER_GALLERY_CHANGE, HyperEngine.TIMER_GALLERY_CHANGE);
    }

}
