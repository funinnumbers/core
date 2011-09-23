package eu.funinnumbers.engine.localization;

/**
 * Created by IntelliJ IDEA.
 * User: logaras
 * Date: Mar 22, 2010
 * Time: 1:32:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalizationQueueData {

    private final String stationMac;
    private final String guardianMac;
    private final Integer rssi;
    private final Integer lqi;
    private final int param;

    public LocalizationQueueData(final String station, final String guardian, final Integer rssi, final Integer lqi, final int param) {
        this.stationMac = station;
        this.guardianMac = guardian;
        this.rssi = rssi;
        this.lqi = lqi;
        this.param = param;

    }

    public String getStationMac() {
        return stationMac;
    }

    public String getGuardianMac() {
        return guardianMac;
    }

    public Integer getRssi() {
        return rssi;
    }

    public Integer getLqi() {
        return lqi;
    }

    public Integer getParam() {
        return param;
    }
}
