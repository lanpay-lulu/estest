package es_test.model;

/**
 * Created by lanpay on 2017/7/7.
 */
public class Accident {
    public String ID;
    public String name;
    public double lat;
    public double lng;
    public String pID; // person id

    public long ts; // timestamp

    public Accident (String ID, String name, double lat, double lng, String pID, long ts) {
        this.ID = ID;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.pID = pID;
        this.ts = ts;
    }

}
