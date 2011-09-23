package eu.funinnumbers.station.communication.localization;

/**
 * Save LocLog Data before send them to Engine.
 */
public class LocLogData {

    /**
     * RSSI Value.
     */
    private int rssi; //NOPMD

    /**
     * LQI value.
     */
    private int lqi; //NOPMD

    /**
     * SunSpot Mac Address.
     */
    private String macAddress; //NOPMD

    /**
     * Extra Parameter.
     */
    private int param; //NOPMD

    /**
     * Define if this is a synchronization signal from the device.
     */
    private boolean isSynch; //NOPMD

    /**
     * Logs the link quality indicator and received signal strength of a mac address.
     *
     * @param rssi rss indicator as integer
     * @param lqi  lq indicator as integer
     * @param mac  mac address as String
     */
    public LocLogData(final int rssi, final int lqi, final String mac) {
        this.rssi = rssi;
        this.lqi = lqi;
        this.macAddress = mac;
        this.param = -1;
        this.isSynch = false;
    }

    /**
     * Logs the link quality indicator and received signal strength of a mac address.
     * Use this method to if you want to log to file and take special parameters into account.
     *
     * @param rssi  rss indicator as integer
     * @param lqi   lq indicator as integer
     * @param mac   mac address as String
     * @param param special parameter integer
     */
    public LocLogData(final int rssi, final int lqi, final String mac, final int param) {
        this.rssi = rssi;
        this.lqi = lqi;
        this.macAddress = mac;
        this.param = param;
        this.isSynch = false;
    }

    /**
     * Logs a synchronization signal from the device.
     *
     * @param mac the MAC address of the device.
     */
    public LocLogData(final String mac) {
        this.rssi = -50;
        this.lqi = -50;
        this.macAddress = mac;
        this.param = 0;
        this.isSynch = true;
    }

    /**
     * Returns the RSSI.
     *
     * @return an integer with the RSSI value
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * Returns the LQI.
     *
     * @return an integer with the LQI value.
     */
    public int getLqi() {
        return lqi;
    }

    /**
     * Returns the SunSPOT mac Address.
     *
     * @return a string with the Sunspot mac Address.
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Returns the extra Parameter.
     *
     * @return an integer with the extra Prameter.
     */
    public int getParam() {
        return param;
    }

    /**
     * Returns if this is a synchronization signal from the device.
     *
     * @return a boolean with the isSynch value.
     */
    public boolean isSynch() {
        return isSynch;
    }
}
