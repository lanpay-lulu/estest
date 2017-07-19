package es_test.model;

/**
 * Created by lanpay on 2017/6/24.
 */
public class Item {
    private Long id;
    private String name;
    private String description;
    private int age;
    private double lat;
    private double lng;


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Item(Long id, String name, String description, int age) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.age = age;
        lat = 0;
        lng = 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }




}
