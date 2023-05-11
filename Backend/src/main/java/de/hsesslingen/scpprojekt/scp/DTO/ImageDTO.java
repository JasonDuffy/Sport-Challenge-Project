package de.hsesslingen.scpprojekt.scp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long ID ;
    String name ;
    String type;
    byte[] data;

    public ImageDTO(){}
    public ImageDTO(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public long getId() {
        return ID;
    }

    public void setId(long ID) {
        this.ID = ID;
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
