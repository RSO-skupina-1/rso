package si.fri.rso.katalogdestinacij.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "katalog_destinacij_metadata")
@NamedQueries(value =
        {
                @NamedQuery(name = "KatalogDestinacijEntity.getAll",
                        query = "SELECT kd FROM KatalogDestinacijEntity kd"),
                /*@NamedQuery(name = "KatalogDestinacijEntity.getNearest",
                        query = "SELECT kd FROM KatalogDestinacijEntity kd ORDER BY SQRT(POWER((kd.longitude - :currentLongitude), 2) + POWER((kd.latitude - :currentLatitude), 2))"),
        */
        })


public class KatalogDestinacijEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    // add columns: location (longitude, latitude), accessibility, infrastructure, price, activities
    @Column(name = "location")
    private String location;

    @Column(name = "longitude")
    private float longitude;

    @Column(name = "latitude")
    private float latitude;


    @Column(name = "accessibility")
    private String accessibility;

    @Column(name = "infrastructure")
    private String infrastructure;

    @Column(name = "price")
    private float price;

    /*@Column(name = "activities")
    private ArrayList<String> activities;*/

    /* getter and setter methods for the entity's attributes*/
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(String infrastructure) {
        this.infrastructure = infrastructure;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    /*public ArrayList<String> getActivities() {
        return activities;
    }*/

    /*public void setActivities(ArrayList<String> activities) {
        this.activities = activities;
    }*/
}