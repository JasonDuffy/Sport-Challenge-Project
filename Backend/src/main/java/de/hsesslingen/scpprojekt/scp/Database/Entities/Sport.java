package de.hsesslingen.scpprojekt.scp.Database.Entities;
/**
 * Challenge entity for Database
 * Colums:
 *      id: Primary key
 *      name: Sport name
 *      factor: Faktor of the sport
 *
 * @author Tom Nguyen Dinh
 */
import jakarta.persistence.*;
@Entity
@Table(name = "sport")
public class Sport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "factor", nullable = false)
    private float factor;

    public Sport() {}

    public Sport(String name, float factor){
        this.name = name;
        this.factor = factor;
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

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }
}