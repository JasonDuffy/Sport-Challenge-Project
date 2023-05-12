package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ImageRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Optional;
import java.io.InputStream;


/**
 * Service functions for the Image class
 *
 * @author Robin Hackh, Tom Nguyen Dinh, Jason Patrick Duffy
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
        if(!checkImage(file))
            throw new IOException(file.getOriginalFilename() + " is not an image!");

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

    /**
     * Tests if given file is an image
     * @param file File to be tested
     * @return True if file is an image, false otherwise
     */
    public Boolean checkImage(MultipartFile file){
        try (InputStream input = file.getInputStream()){
            return ImageIO.read(input) != null;
        } catch (Exception e){
            return false;
        }
    }
}
