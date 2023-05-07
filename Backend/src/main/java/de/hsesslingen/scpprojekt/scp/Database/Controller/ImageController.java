package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ImageRepository;
import de.hsesslingen.scpprojekt.scp.Database.Service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * REST controller for Image.
 *
 * @author Robin Hackh, Tom Nguyen Dinh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private ImageRepository imageRepository;

    /**
     * REST API for uploading an Image
     *
     * @param file Image that should be stored
     * @param request automatically filled by browser
     * @return A 200 Code if it worked 417 otherwise
     */
    @Operation(summary = "Stores the given file(image)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File successfully stored", content = @Content),
            @ApiResponse(responseCode = "417", description = "Something went wrong storing the file", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/", consumes = "multipart/form-data")
    public ResponseEntity<HttpStatus> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try {
                imageStorageService.store(file);

                return new ResponseEntity<>(HttpStatus.OK);
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning Image data of a given ID
     *
     * @param id ID of the Image that should be returned
     * @param request automatically filled by browser
     * @return Image data corresponding to the given ID 404 otherwise
     */
    @Operation(summary = "Get image by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File found",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Image.class))}),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/" , produces = "application/json")
    public ResponseEntity<Image> getImageById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Image> imageData = imageRepository.findById(id);
            if (imageData.isPresent()) {
                return new ResponseEntity<>(imageData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }
    }

    /**
     *
     * @param id ID of the image which should be updated
     * @param file Image which should be stored
     * @param request automatically filled by browser
     * @return Image data corresponding to ID else 404 for not Found or 417 for something went wrong
     */
    @Operation(summary = "Updates a image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Image.class))}),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "417", description = "Something went wrong updating the image", content = @Content)
    })
    @PutMapping(path= "/{id}/",consumes = "multipart/form-data",produces= "application/json")
    public ResponseEntity<Image> updateImage(@PathVariable("id") long id, @RequestParam("file") MultipartFile file, HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Image> imageData = imageRepository.findById(id);
            if(imageData.isPresent()){
            try {
                Image newImage = imageData.get();
                newImage.setData(file.getBytes());
                newImage.setName(file.getOriginalFilename());
                newImage.setType(file.getContentType());

                return new ResponseEntity<>(imageRepository.save(newImage),HttpStatus.OK);
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
