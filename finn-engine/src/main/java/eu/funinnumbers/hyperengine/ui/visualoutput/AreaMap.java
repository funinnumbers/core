package eu.funinnumbers.hyperengine.ui.visualoutput;

import eu.funinnumbers.hyperengine.HyperEngineLogic;
import traer.physics.Particle;
import traer.physics.ParticleSystem;

import java.util.HashMap;

/**
 * Draws the map of players.
 */
public class AreaMap { //NOPMD

    /**
     * Parent applet.
     */
    private final HyperEngine parent;

    /**
     * Particle system.
     */
    private final ParticleSystem system;

    /**
     * Association of players to game.
     */
    private final HashMap<String, Particle> particlesMap = new HashMap<String, Particle>();

    /**
     * Association of players to game.
     */
    private final HashMap<String, Particle> destMap = new HashMap<String, Particle>();

    private final int width;

    private final int height;

    private final int posX;

    private final int posY;

    private final int[] bottomX, topX;
    private final int[] bottomY, topY;

    /**
     * Default constructor.
     *
     * @param thisParent the Parent applet.
     */
    public AreaMap(final HyperEngine thisParent) {
        parent = thisParent;

        // initialize particle system
        system = new ParticleSystem(0, 0.5f);

        posX = parent.getWidth() / 2 + 40;
        posY = parent.getHeight() / 2 + 40;
        width = parent.getWidth() / 2 - 80;
        height = parent.getHeight() / 2 - 80;

        // Initialize Areas
        bottomX = new int[12];
        bottomY = new int[12];
        topX = new int[12];
        topY = new int[12];
        for (int i = 0; i < 4; i++) {
            topX[i] = i * width / 4;
            topY[i] = 0;
            bottomX[i] = (i + 1) * width / 4;
            bottomY[i] = height / 3;

            topX[i + 4] = i * width / 4;
            topY[i + 4] = height / 3;
            bottomX[i + 4] = (i + 1) * width / 4;
            bottomY[i + 4] = 2 * height / 3;

            topX[i + 8] = i * width / 4;
            topY[i + 8] = 2 * height / 3;
            bottomX[i + 8] = (i + 1) * width / 4;
            bottomY[i + 8] = height;
        }
    }

    /**
     *
     */
    public void fixParticlesPosition() { //NOPMD
        synchronized (HyperEngineLogic.getInstance().getPlayersMap()) {
            final HashMap<String, String> playersMap = HyperEngineLogic.getInstance().getPlayersMap();
            for (final String mac : playersMap.keySet()) {
                if (!particlesMap.containsKey(mac)) {
                    final String gameID = playersMap.get(mac);
                    final float initPosX = getPosX(gameID);
                    final float initPosY = getPosY(gameID);
                    final Particle par = system.makeParticle(1f, initPosX, initPosY, 0f);
                    particlesMap.put(mac, par);

                    final Particle dest = system.makeParticle(4f, initPosX, initPosY, 0f);
                    dest.makeFixed();
                    destMap.put(mac, dest);
                    system.makeAttraction(par, dest, -100.0f, 10);
                    system.makeSpring(par, dest, 0.01f, 0.01f, 5);

                } else {
                    if (parent.random(10) < 0.2) {
                        final Particle par = particlesMap.get(mac);
                        if (par.position().x() > width) {
                            par.position().setX(width - 10);
                        } else if (par.position().x() < 0) {
                            par.position().setX(10);
                        }
                        if (par.position().y() > height) {
                            par.position().setY(height - 10);
                        } else if (par.position().y() < 0) {
                            par.position().setY(10);
                        }

                        final String gameID = playersMap.get(mac);
                        final Particle dest = destMap.get(mac);
                        float newPosX = getPosX(gameID);
                        float newPosY = getPosY(gameID);
                        if (newPosX > width) {
                            newPosX = width - 10;
                        }
                        if (newPosY > height) {
                            newPosY = height - 10;
                        }
                        //par.position().add(newPosX, newPosY, 0);
                        dest.position().set(newPosX, newPosY, 0);
                    }
                }
            }

            for (final String mac : particlesMap.keySet()) {
                if (!playersMap.containsKey(mac)) {
                    final Particle par = particlesMap.get(mac);
                    system.removeParticle(par);
                }
            }
        }
    }

    private float getPosX(final String game) { //NOPMD
        float initPosX = 0;
        if (game.endsWith(HyperEngineLogic.TOW)) {
            initPosX = parent.random(topX[6], bottomX[7] - 10);

        } else if (game.endsWith(HyperEngineLogic.CI)) {
            initPosX = parent.random(topX[3], bottomX[3] - 10);

        } else if (game.endsWith(HyperEngineLogic.CIMAGE)) {
            initPosX = parent.random(topX[2], bottomX[2]);

        } else if (game.endsWith(HyperEngineLogic.MAGNETIZE)) {
            initPosX = parent.random(topX[9], bottomX[11] - 10);

        } else if (game.endsWith(HyperEngineLogic.HYPERAPP)) {
            initPosX = parent.random(topX[8] + bottomX[8] / 4, bottomX[8] - 10);

        } else {
            initPosX = parent.random(bottomX[8] - 20, bottomX[8] - 10);
        }

        return initPosX;
    }

    private float getPosY(final String game) { //NOPMD
        float initPosY = 0;
        if (game.endsWith(HyperEngineLogic.TOW)) {
            initPosY = parent.random(topY[6], bottomY[7]);

        } else if (game.endsWith(HyperEngineLogic.CI)) {
            initPosY = parent.random(topY[3] + 10, bottomY[3]);

        } else if (game.endsWith(HyperEngineLogic.CIMAGE)) {
            initPosY = parent.random(topY[2] + 10, bottomY[2]);

        } else if (game.endsWith(HyperEngineLogic.MAGNETIZE)) {
            initPosY = parent.random(topY[9], bottomY[11] - 10);

        } else if (game.endsWith(HyperEngineLogic.HYPERAPP)) {
            initPosY = parent.random(topY[5], bottomY[8] - 10);

        } else {
            initPosY = parent.random(bottomY[8] - 20, bottomY[8] - 10);
        }

        return initPosY;
    }

    public void tick() {
        system.tick();
    }

    /**
     * Redraws the map and the pointer.
     */
    public void draw() {
        system.tick();

        // eraser
        parent.fill(0, 80);
        parent.noStroke();
        parent.rect(posX, posY, width, height);

        // draw borders
        parent.stroke(255, 0, 0);
        parent.noFill();
        parent.strokeWeight(5);
        parent.rect(posX, posY, width, height);

        // draw foyer
        parent.line(posX + topX[1], posY + topY[1], posX + bottomX[8], posY + (4 * bottomY[8] / 5));
        parent.line(posX + bottomX[8], posY + (4 * bottomY[8] / 5) + 20, posX + bottomX[8], posY + bottomY[8]);

        // draw areas
        parent.fill(255, 0, 0);
        parent.strokeWeight(1);
        parent.rect(posX + bottomX[1], posY + bottomY[1], 10, 10);
        parent.rect(posX + bottomX[2], posY + bottomY[2], 10, 10);
        parent.rect(posX + topX[10], posY + topY[10], 10, 10);
        parent.rect(posX + topX[11], posY + topY[11], 10, 10);

        // draw bar
        parent.fill(255, 0, 0);
        parent.strokeWeight(1);
        parent.rect(posX + topX[8], posY + topY[8], bottomX[8] / 4, 5);
        parent.rect(posX + bottomX[8] / 4, posY + topY[8], 5, bottomY[8] - topY[8]);

        // Draw particles
        parent.strokeWeight(1);
        parent.fill(0, 255, 255);

        for (final Particle par : particlesMap.values()) {
            parent.ellipse(posX + par.position().x(), posY + par.position().y(), 5, 5);
        }
    }

}

