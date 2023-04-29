package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;


/**
 * Service functions for the Image class
 *
 * @author Robin Hackh
 */
@Service
public class ImageStorageService {

    @Autowired
    private ImageRepository imageRepository;

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
}
