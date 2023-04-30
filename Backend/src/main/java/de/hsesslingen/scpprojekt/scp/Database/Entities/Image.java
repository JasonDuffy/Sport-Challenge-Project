package de.hsesslingen.scpprojekt.scp.Database.Entities;

import jakarta.persistence.*;

/**
 * Image entity for Database
 * Colums:
 *      id: Primary key
 *      name: Filename on Upload with ending
 *      data: ImageData in Base64
 *      type: Filetype for example (image/png)
 *
 * @author Robin Hackh
 */

@Entity
@Table(name = "Image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "type", nullable = false)
    private String type;

    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

    public Image(){}

    public Image(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
