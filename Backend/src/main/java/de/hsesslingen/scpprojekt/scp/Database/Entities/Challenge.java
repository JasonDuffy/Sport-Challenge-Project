package de.hsesslingen.scpprojekt.scp.Database.Entities;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Challenge entity for Database
 * Colums:
 *      id: Primary key
 *      name: Challenge name
 *      description: Challenge description
 *      start_date: Challenge start date (year, month, day, hours, minutes, seconds)
 *      end_date: Challenge end date (year, month, day, hours, minutes, seconds)
 *      image_id: Foreign key of Image entity
 *      target_distance: Distance gaol for defined for the Challenge
 *
 * @author Robin Hackh
 */
@Entity
@Table(name = "Challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;
    @Column(name = "target_distance", nullable = false)
    private float targetDistance;

    public Challenge() {}

    public Challenge(String name, String description, Date startDate, Date endDate, Image image, float targetDistance) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.targetDistance = targetDistance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public float getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(float targetDistance) {
        this.targetDistance = targetDistance;
    }
}