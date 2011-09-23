package eu.funinnumbers.hyperengine.ui.visualoutput;

import codeanticode.gsvideo.GSMovie;
import eu.funinnumbers.hyperengine.ui.imagegallery.ImageManager;
import eu.funinnumbers.hyperengine.ui.videogallery.VideoManager;
import eu.funinnumbers.hyperengine.ui.visualoutput.tasks.GalleryTask;
import eu.funinnumbers.hyperengine.ui.visualoutput.tasks.LayoutSwitchTask;
import eu.funinnumbers.hyperengine.ui.visualoutput.tasks.UpdateAreaMapTask;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.Timer;

/**
 * Rich Front End for the HyperEngine.
 */
public class HyperEngine extends PApplet {

    /**
     * Event Logo.
     */
    private final PImage EVENT_LOGO = loadImage("src/eu.funinnumbers.hyperengine/ui/visualoutput/img/event-logo.jpg");

    /**
     * Horizontal, Vertical position of event logo.
     */
    private int eventLogoPosX, eventLogoPosY;

    /**
     * Event Logo Tall.
     */
    private final PImage EVENT_LOGO_TALL = loadImage("src/eu.funinnumbers.hyperengine/ui/visualoutput/img/event-logo-tall.jpg");

    /**
     * Horizontal, Vertical position of event logo tall.
     */
    private int eventLogoTallPosX, eventLogoTallPosY;

    /**
     * IP Camera latest image.
     */
    private PImage imgIPCamera = loadImage("src/eu.funinnumbers.hyperengine/ui/visualoutput/img/ip-camera.jpg"); //NOPMD

    /**
     * Horizontal, Vertical position of IP Camera Logo when in 1x2 layout.
     */
    private int ipCameraPosX, ipCameraPosY;

    /**
     * Gallery latest image.
     */
    private PImage imgGallery = loadImage("src/eu.funinnumbers.hyperengine/ui/visualoutput/img/ip-camera.jpg");

    /**
     * Horizontal, Vertical position and alpha of Gallery Image.
     */
    private int galleryPosX, galleryPosY, galleryAlpha;

    /**
     * Gallery latest image.
     */
    private PImage imgGalleryNew = loadImage("src/eu.funinnumbers.hyperengine/ui/visualoutput/img/ip-camera.jpg");

    /**
     * Horizontal, Vertical position and alpha of Gallery Image.
     */
    private int galleryNewPosX, galleryNewPosY, galleryNewAlpha;

    private GSMovie movieClip;

    private AreaMap areaMap;

    /**
     * Width of screen.
     */
    private final int screenWidth;

    /**
     * Height of screen.
     */
    private final int screenHeight;

    /**
     * Image change timer.
     */
    private Timer timer = new Timer(); //NOPMD

    /**
     * Flag for layout mode.
     */
    private boolean isLayout2x2;

    /**
     * 1000ms = 1s.
     */
    public final static int TIMER_GALLERY_CHANGE = 50;

    /**
     * 1000ms = 1s.
     */
    public final static int TIMER_GALLERY_NEXT_IMAGE = 5000;

    /**
     * 1000ms = 1s.
     */
    public final static int TIMER_LAYOUT_CHANGE = 2 * 60 * 1000;

    /**
     * Default constructor.
     *
     * @param width  -- width of screen.
     * @param height -- height of screen
     */
    public HyperEngine(final int width, final int height) {
        screenWidth = width;
        screenHeight = height;
        isLayout2x2 = true;
    }

    /**
     * Initialize the environment.
     */
    @Override
    public void setup() {
        // Setup Processing
        size(screenWidth, screenHeight, OPENGL);
        smooth();
        frameRate(30);
        background(0);

        // Load AreaMap
        areaMap = new AreaMap(this);

        // Load gallery images
        ImageManager.getInstance().loadImages(this, screenWidth / 2 - 20, screenHeight / 2 - 20);

        // Load Gallery videos
        movieClip = VideoManager.getInstance().nextVideo(this);
        if (movieClip != null) {
            movieClip.loop();
        }

        // Prepare the environment for the 2x2 layout.
        setupTwoByTwo();

        // Prepare the environment for the 1x2 layout.
        setupOneByTwo();

        // Initialize tasks
        timer.scheduleAtFixedRate(new GalleryTask(this), TIMER_GALLERY_NEXT_IMAGE, TIMER_GALLERY_NEXT_IMAGE);
        timer.scheduleAtFixedRate(new LayoutSwitchTask(this), TIMER_LAYOUT_CHANGE, TIMER_LAYOUT_CHANGE);
        timer.scheduleAtFixedRate(new UpdateAreaMapTask(areaMap), 1000, 1000);
       // timer.scheduleAtFixedRate(new TestAreaMapTask(this), 100, 10000);
    }

    /**
     * Initialize the layout when in 2x2 mode.
     */
    public void setupTwoByTwo() {
        // Resize event logo image based on width
        final int maxWidth = screenWidth / 2 - 20;
        final int maxHeight = (maxWidth / EVENT_LOGO.width) * EVENT_LOGO.height;
        EVENT_LOGO.resize(maxWidth, maxHeight);

        // Resize event logo image based on height
        if (EVENT_LOGO.height > screenHeight / 2) {
            final int height = screenHeight / 2 - 20;
            final int width = (height / EVENT_LOGO.height) * EVENT_LOGO.width;
            EVENT_LOGO.resize(width, height);
        }

        // Calculate proper position for image
        eventLogoPosX = ((screenWidth / 2) - EVENT_LOGO.width) / 2;
        eventLogoPosY = ((screenHeight / 2) - EVENT_LOGO.height) / 2;

        // Resize IP camera image based on width, height
        if (imgIPCamera.width > screenWidth / 2) {
            final int width = screenWidth / 2;
            final int height = (width / imgIPCamera.width) * imgIPCamera.height;
            imgIPCamera.resize(width, height);
        }

        // Resize event logo image based on height
        if (imgIPCamera.height > screenHeight / 2) {
            final int height = screenHeight / 2;
            final int width = (height / imgIPCamera.height) * imgIPCamera.width;
            imgIPCamera.resize(width, height);
        }

        // Resize movie clip
        /*
        int height = (((screenWidth / 2) - 20) / movieClip.width) * movieClip.height;
        movieClip.width = screenWidth / 2 - 20;
        movieClip.height = height;

        if (movieClip.height > screenHeight / 2) {
            int width = (((screenHeight / 2) - 20) / movieClip.height) * movieClip.width;
            movieClip.height = screenHeight / 2 - 20;
            movieClip.width = width;
        }
        */
    }

    /**
     * Initialize the layout when in 1x2 mode.
     */
    public void setupOneByTwo() {
        // Resize event logo image based on width
        final int maxWidth = screenWidth / 2 - 20;
        final int maxHeight = (maxWidth / EVENT_LOGO_TALL.width) * EVENT_LOGO_TALL.height;
        EVENT_LOGO_TALL.resize(maxWidth, maxHeight);

        // Resize event logo image based on height
        if (EVENT_LOGO_TALL.height > screenHeight) {
            final int height = screenHeight - 20;
            final int width = (height / EVENT_LOGO_TALL.height) * EVENT_LOGO_TALL.width;
            EVENT_LOGO_TALL.resize(width, height);
        }

        // Calculate proper position for image
        eventLogoTallPosX = ((screenWidth / 2) - EVENT_LOGO_TALL.width) / 2;
        eventLogoTallPosY = (screenHeight - EVENT_LOGO_TALL.height) / 2;

        // Calculate proper position for IP image
        ipCameraPosX = (screenWidth / 2) + ((screenWidth / 2) - imgIPCamera.width) / 2;
        ipCameraPosY = ((screenHeight / 2) - imgIPCamera.height) / 2;

        // calculate proper position for Image gallery image
        imgGallery = ImageManager.getInstance().nextImage();
        galleryPosX = ((screenWidth / 2) - imgGallery.width) / 2;
        galleryPosY = (screenHeight / 2) + ((screenHeight / 2) - imgGallery.height) / 2;
        galleryAlpha = 255;

        galleryNewPosX = ((screenWidth / 2) - imgGallery.width) / 2;
        galleryNewPosY = (screenHeight / 2) + ((screenHeight / 2) - imgGallery.height) / 2;
        galleryNewAlpha = 0;
    }

    /**
     * Draw the current frame.
     */
    @Override
    public void draw() {        
        //background(0);

        if (isLayout2x2) {
            // Choose 2x2 layout.
            drawTwoByTwo();

        } else {
            // Choose 1x2 layout.
            drawOneByTwo();
        }
    }

    /**
     * Splits the screen in 2x2.
     */
    public void drawTwoByTwo() {
        // Draw frame
        /*stroke(255);
        line(0, screenHeight / 2, screenWidth, screenHeight / 2);
        line(screenWidth / 2, 0, screenWidth / 2, screenHeight);
        noStroke();
        */

        // Clear top left
        fill(0, 0, 0);
        noStroke();
        rect(0, 0, width / 2, height);

        // Top Left
        imageMode(CORNERS);
        image(EVENT_LOGO, eventLogoPosX, eventLogoPosY);

        // Top-right is occupied by IP camera

        // Bottom-left
        tint(255, galleryAlpha);
        image(imgGallery, galleryPosX, galleryPosY);
        tint(255, galleryNewAlpha);
        image(imgGalleryNew, galleryNewPosX, galleryNewPosY);
        noTint();

/*        // Bottom-right is occupied by video (temporary)
        if ((movieClip != null) && (movieClip.available())) {
//            tint(255, 20);
            movieClip.read();
            image(movieClip, 850, 535, 830, 505);
//            noTint();
        }

        */
        
        // Bottom Right
        areaMap.draw();
    }

    /**
     * Splits the screen in 1x2.
     */
    public void drawOneByTwo() {
        // Draw frame
        /*
        stroke(255);
        line(screenWidth / 2, screenHeight / 2, screenWidth, screenHeight / 2);
        line(screenWidth / 2, 0, screenWidth / 2, screenHeight);
        noStroke();
        */

        // Clear left
        fill(0, 0, 0);
        noStroke();
        rect(0, 0, width / 2, height);        

        // Top Left
        imageMode(CORNERS);
        image(EVENT_LOGO_TALL, eventLogoTallPosX, eventLogoTallPosY);

        // Top-right is occupied by IP camera

        // Bottom Right
        areaMap.draw();
    }

    public Timer getTimer() {
        return timer;
    }

    public PImage getImgIPCamera() {
        return imgIPCamera;
    }

    public int getIpCameraPosX() {
        return ipCameraPosX;
    }

    public int getIpCameraPosY() {
        return ipCameraPosY;
    }

    public void setImgGallery(final PImage imgGallery) {
        this.imgGallery = imgGallery;
    }

    public int getGalleryPosX() {
        return galleryPosX;
    }

    public void setGalleryPosX(final int galleryPosX) {
        this.galleryPosX = galleryPosX;
    }

    public int getGalleryPosY() {
        return galleryPosY;
    }

    public void setGalleryPosY(final int galleryPosY) {
        this.galleryPosY = galleryPosY;
    }

    public int getGalleryAlpha() {
        return galleryAlpha;
    }

    public void setGalleryAlpha(final int galleryAlpha) {
        this.galleryAlpha = galleryAlpha;
    }

    public PImage getImgGalleryNew() {
        return imgGalleryNew;
    }

    public void setImgGalleryNew(final PImage imgGalleryNew) {
        this.imgGalleryNew = imgGalleryNew;
    }

    public int getGalleryNewPosX() {
        return galleryNewPosX;
    }

    public void setGalleryNewPosX(final int galleryNewPosX) {
        this.galleryNewPosX = galleryNewPosX;
    }

    public int getGalleryNewPosY() {
        return galleryNewPosY;
    }

    public void setGalleryNewPosY(final int galleryNewPosY) {
        this.galleryNewPosY = galleryNewPosY;
    }

    public int getGalleryNewAlpha() {
        return galleryNewAlpha;
    }

    public void setGalleryNewAlpha(final int galleryNewAlpha) {
        this.galleryNewAlpha = galleryNewAlpha;
    }

    public void setLayout2x2(final boolean layout2x2) {
        isLayout2x2 = layout2x2;
    }
}
