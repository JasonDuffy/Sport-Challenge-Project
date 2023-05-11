package de.hsesslingen.scpprojekt.scp.DTO.Converter;

import de.hsesslingen.scpprojekt.scp.DTO.ImageDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Service.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImageConverter {
    
    @Autowired
    ImageStorageService imageStorageService;
    
    public ImageDTO convertEntityToDto(Image image){
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setId(image.getId());
        imageDTO.setName(image.getName());
        imageDTO.setType(image.getType());
        imageDTO.setData(image.getData());
        return imageDTO;
    }
    public List<ImageDTO> convertEntityListToDtoList(List<Image> imageList){
        List<ImageDTO> imageDTOS = new ArrayList<>();

        for(Image image : imageList)
            imageDTOS.add(convertEntityToDto(image));

        return imageDTOS;
    }

    public Image convertDtoToEntity(ImageDTO imageDTO) throws NotFoundException {
        Image image = new Image();
        image.setId(imageDTO.getId());
        image.setName(imageDTO.getName());
        image.setType(imageDTO.getType());
        image.setData(imageDTO.getData());
        return image;
    }

    public List<Image> convertDtoListToEntityList(List<ImageDTO> imageDTOS) throws NotFoundException {
        List<Image> imageList = new ArrayList<>();

        for(ImageDTO newimageDTO: imageDTOS)
            imageList.add(convertDtoToEntity(newimageDTO));

        return imageList;
    }
}
