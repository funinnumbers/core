package eu.funinnumbers.hyperengine.ui.visualoutput.tasks;

import eu.funinnumbers.hyperengine.ui.visualoutput.HyperEngine;

import java.util.TimerTask;

/**
 * Switch Gallery Images.
 */
public class GallerySwitchTask extends TimerTask { //NOPMD

    /**
     * Parent applet.
     */
    private final HyperEngine parent;

    /**
     * Default Constructor.
     *
     * @param thisPanel the Processing applet.
     */
    public GallerySwitchTask(final HyperEngine thisPanel) {
        parent = thisPanel;
    }

    /**
     * Switch images.
     */
    public void run() { //NOPMD
        if (parent.getGalleryAlpha() > 0) {
            parent.setGalleryAlpha(parent.getGalleryAlpha() - 10);
        } else {
            parent.setGalleryAlpha(0);
        }

        if (parent.getGalleryNewAlpha() < 255) {
            parent.setGalleryNewAlpha(parent.getGalleryNewAlpha() + 10);
        } else {
            parent.setGalleryNewAlpha(255);
        }

        if ((parent.getGalleryAlpha() == 0) && (parent.getGalleryNewAlpha() == 255)) {
            this.cancel();
            parent.setImgGallery(parent.getImgGalleryNew());
            parent.setGalleryPosX(parent.getGalleryNewPosX());
            parent.setGalleryPosY(parent.getGalleryNewPosY());
            parent.setGalleryAlpha(255);
            parent.setGalleryNewAlpha(0);
        }
    }

}
