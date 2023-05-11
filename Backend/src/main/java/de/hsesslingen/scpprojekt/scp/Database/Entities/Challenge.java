package de.hsesslingen.scpprojekt.scp.Database.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

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
 * @author Robin Hackh, Jason Patrick Duffy
 */
@Entity
@Table(name = "Challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "image_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Image image;
    @Column(name = "target_distance", nullable = false)
    private float targetDistance;

    public Challenge() {}

    public Challenge(String name, String description, LocalDateTime startDate, LocalDateTime endDate, Image image, float targetDistance) {
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
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