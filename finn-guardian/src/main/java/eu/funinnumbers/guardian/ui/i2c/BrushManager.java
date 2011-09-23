package eu.funinnumbers.guardian.ui.i2c;

import com.sun.spot.peripheral.II2C;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.io.IIOPin;
import com.sun.spot.sensorboard.peripheral.LEDColor;

import java.io.IOException;

/**
 * Input/Output Pins Manager.
 */
public class BrushManager {

    /**
     * BlinkM -- i2c communication.
     */
    public final static int BLINKM_MODE = 1;

    /**
     * BlinkM address -- 0x00 is BDC.
     */
    private final byte address = 0x00;

    /**
     * Simple Leds -- using IIOPin[].
     */
    public final static int LED_MODE = 2;

    /**
     * Input/Output pins
     */
    private final IIOPin[] ioPins;

    /**
     * Interface for communicating with devices using the I2C protocol.
     */
    private final II2C i2c;

    /**
     * Brush Mode -- Simple leds or BlinkM (i2c).
     */
    private int mode;

    private final static int GREEN = 1;
    private final static int RED = 2;
    private final static int BLUE = 0;
    private final static int PUCE = 3;
    private final static int YELLOW = 4;
    private final static int OFF = 5;

    /**
     * current Color.
     */
    private int currentColor;

    /**
     * BlinkM off Command.
     */
    private final byte[] offCMD;

    /**
     * BlinkM stop Command.
     */
    private final byte[] stopCMD;

    /**
     * BlinkM Red color Command.
     */
    private final byte[] redCMD;

    /**
     * BlinkM Yellow color Command.
     */
    private final byte[] yellowCMD;

    /**
     * BlinkM Green color Command.
     */
    private final byte[] greenCMD;

    /**
     * BlinkM Blue color Command.
     */
    private final byte[] blueCMD;

    /**
     * BlinkM Puce color Command.
     */
    private final byte[] puceCMD;

    /**
     * static instance (ourInstance) initialized as null.
     */
    private static BrushManager thisCp = null;

    /**
     * Private constructor suppresses generation
     * of a (public) default constructor.
     */
    public BrushManager() {
        ioPins = EDemoBoard.getInstance().getIOPins();

        //initialize pins
        for (int pin = EDemoBoard.D0; pin < EDemoBoard.D3; pin++) {
            ioPins[pin].setAsOutput(true);
            ioPins[pin].setLow();
        }
        mode = -1;
        i2c = EDemoBoard.getInstance().getI2C();

        offCMD = new byte[4];
        offCMD[0] = 'n';
        offCMD[1] = (byte) 0;
        offCMD[2] = (byte) 0;
        offCMD[3] = (byte) 0;

        stopCMD = new byte[1];
        stopCMD[0] = (byte) 111;

        redCMD = new byte[4];
        redCMD[0] = 'n';
        redCMD[1] = (byte) 0xff;
        redCMD[2] = (byte) 0x00;
        redCMD[3] = (byte) 0x00;

        yellowCMD = new byte[4];
        yellowCMD[0] = 'n';
        yellowCMD[1] = 102;
        yellowCMD[2] = 90;
        yellowCMD[3] = 0;

        greenCMD = new byte[4];
        greenCMD[0] = 'n';
        greenCMD[1] = 0;
        greenCMD[2] = (byte) 0xff;
        greenCMD[3] = 0;

        blueCMD = new byte[4];
        blueCMD[0] = 'n';
        blueCMD[1] = 0;
        blueCMD[2] = 0;
        blueCMD[3] = (byte) 0xff;

        puceCMD = new byte[4];
        puceCMD[0] = 'n';
        puceCMD[1] = (byte) 0xff;
        puceCMD[2] = 0;
        puceCMD[3] = (byte) 0xff;

    }

    /**
     * BrushManager  is loaded on the first execution of BrushManager.getInstance()
     * or the first access to BrushManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static BrushManager getInstance() {
        synchronized (BrushManager.class) {
            if (thisCp == null) {
                thisCp = new BrushManager();
            }
            return thisCp;
        }
    }

    /**
     * Set Brush Mode.
     *
     * @param mode an int with mode
     */
    public void setMode(final int mode) {
        this.mode = mode;
        if (mode == BLINKM_MODE) {
            try {
                i2c.open();
                i2c.write(address, stopCMD, 0, 1);
                i2c.write(address, offCMD, 0, offCMD.length);
                currentColor = OFF;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set off BrushManager.
     */
    public void setOffBrush() {
        if (mode == LED_MODE) {
            for (int pin = EDemoBoard.D0; pin < EDemoBoard.D3; pin++) {
                ioPins[pin].setHigh();
            }
        } else if (mode == BLINKM_MODE) {
            try {
                i2c.write(address, stopCMD, 0, 1);
                i2c.write(address, offCMD, 0, offCMD.length);
                currentColor = OFF;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Set the Bruch Color.
     *
     * @param color the LEDColor
     */
    public void setColor(final LEDColor color) {
        if (mode == LED_MODE) {
            setOffBrush();
            if (color.equals(LEDColor.GREEN)) {
                ioPins[GREEN].setLow();
                System.out.println("GREEN");
            } else if (color.equals(LEDColor.RED)) {
                System.out.println("RED");
                ioPins[RED].setLow();
            } else if (color.equals(LEDColor.BLUE)) {
                System.out.println("BLUE");
                ioPins[BLUE].setLow();
            } else if (color.equals(LEDColor.YELLOW)) {
                System.out.println("YELLOW");
                ioPins[RED].setLow();
                ioPins[GREEN].setLow();
            } else if (color.equals(LEDColor.PUCE)) {
                System.out.println("PUCE");
                ioPins[RED].setLow();
                ioPins[BLUE].setLow();
            }
        } else if (mode == BLINKM_MODE) {
            if (color.equals(LEDColor.GREEN) && currentColor != GREEN) {
                try {
                    i2c.write(address, greenCMD, 0, greenCMD.length);
                    System.out.println("GREEN");
                    currentColor = GREEN;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (color.equals(LEDColor.RED) && currentColor != RED) {
                try {
                    i2c.write(address, redCMD, 0, redCMD.length);
                    System.out.println("RED");
                    currentColor = RED;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (color.equals(LEDColor.BLUE) && currentColor != BLUE) {
                try {
                    i2c.write(address, blueCMD, 0, blueCMD.length);
                    System.out.println("BLUE");
                    currentColor = BLUE;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (color.equals(LEDColor.YELLOW) && currentColor != YELLOW) {
                try {
                    i2c.write(address, yellowCMD, 0, yellowCMD.length);
                    System.out.println("YELLOW");
                    currentColor = YELLOW;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (color.equals(LEDColor.PUCE) && currentColor != PUCE) {
                try {
                    i2c.write(address, puceCMD, 0, puceCMD.length);
                    System.out.println("PUCE");
                    currentColor = PUCE;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


