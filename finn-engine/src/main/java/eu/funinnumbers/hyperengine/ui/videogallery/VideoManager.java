package eu.funinnumbers.hyperengine.ui.videogallery;

import codeanticode.gsvideo.GSMovie;
import processing.core.PApplet;
import eu.funinnumbers.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles a collection of videos.
 */
public final class VideoManager { //NOPMD

    /**
     * Single instance of manager.
     */
    private static VideoManager thisInstance;

    /**
     * Directory name containing videos.
     */
    public static final String dirVideos = "/home/ichatz/Local/finn/src/eu.funinnumbers.hyperengine/ui/videogallery/video";

    /**
     * Set of video filenames.
     */
    private List<String> videoFilenames;

    /**
     * Source of randomness.
     */
    private final Random random;

    /**
     * Private constructor.
     */
    private VideoManager() {
        random = new Random();

        // Locate video file names.
        locateVideos();
    }

    /**
     * Provides access to the single instance of the manager.
     *
     * @return reference to the unique instance of the VideoManager.
     */
    public static VideoManager getInstance() {
        synchronized (VideoManager.class) {
            if (thisInstance == null) {
                thisInstance = new VideoManager();
            }
        }

        return thisInstance;
    }

    /**
     * Locate all video filenames found in the directory.
     */
    private void locateVideos() {
        // Locate all files in directory
        videoFilenames = locateFiles(dirVideos);
    }

    /**
     * Locate all files containing poems.
     *
     * @param dirName name of directory to examine.
     * @return collection of file names.
     */
    private List<String> locateFiles(final String dirName) { //NOPMD
        final ArrayList<String> allFiles = new ArrayList<String>();

        // get a list of all files in a directory
        final File dir = new File(dirName);
        final String[] files = dir.list();
	if ((files == null) || (files.length < 1)) {
		return allFiles;
	}
        for (final String file : files) {
            // filter text documents
            if (file.endsWith(".mov")) {
                allFiles.add(file);
            }
        }

        return allFiles;
    }

    /**
     * Return a random video from the list.
     *
     * @param panel the Processing applet that will display the gallery.
     * @return a video.
     */
    public GSMovie nextVideo(final PApplet panel) {
        if (videoFilenames.isEmpty()) {
            return null;
        }
        final String filename = dirVideos + "/" + videoFilenames.get(random.nextInt(videoFilenames.size()));
        Logger.getInstance().debug("Video Gallery chose clip " + filename);
        return new GSMovie(panel, filename);
    }

}
