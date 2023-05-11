package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.DTO.Converter.ImageConverter;
import de.hsesslingen.scpprojekt.scp.DTO.ImageDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ImageRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * Service functions for the Image class
 *
 * @author Robin Hackh,Tom Nguyen Dinh
 */
@Service
public class ImageStorageService {

    @Autowired
    ImageRepository imageRepository;


    /**
     *
     * @param file The file(Image) you want store in the DB
     * @return A new image Entity filled with data of the given file.
     * @throws IOException
     */
    public Image store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Image image = new Image(fileName, file.getContentType(), file.getBytes());

        return imageRepository.save(image);
    }

    public Image get(Long ImageID) throws  NotFoundException {
        Optional<Image> image =  imageRepository.findById(ImageID);
        if(image.isPresent()){
            return  image.get();
        }throw new NotFoundException("Image with ID " +ImageID+" is not present in DB.");
    }

}
