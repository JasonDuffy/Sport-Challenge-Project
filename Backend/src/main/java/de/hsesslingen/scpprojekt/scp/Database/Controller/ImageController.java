package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ImageRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
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

import java.io.IOException;
import java.util.List;
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
        if (SAML2Service.isLoggedIn(request)){
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
        if (SAML2Service.isLoggedIn(request)){
            try {
                return new ResponseEntity<>(imageStorageService.get(id),HttpStatus.OK);
            }catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for deleting an image
     *
     * @param id imageID
     * @param request automatically filled by browser
     * @return OK 200 else 404 not found
     */
    @Operation(summary = "Delete image by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "delete File ",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Image.class))}),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping(path = "/{id}/" , produces = "application/json")
    public ResponseEntity<Image> deleteImageById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            try {
                imageStorageService.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for Deleting all images
     *
     * @param request automatically filled by browser
     * @return 200 if it worked
     */
    @Operation(summary = "Deletes all images")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All images successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAllImages(HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            imageStorageService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest APi for getting all images
     *
     * @param request automatically filled by browser
     * @return 200 for finding all
     */
    @Operation(summary = "Get all images")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<Image>> getAllImages(HttpServletRequest request){
        if (SAML2Service.isLoggedIn(request)){
            return new ResponseEntity<>(imageStorageService.getAll(), HttpStatus.OK);
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
        if (SAML2Service.isLoggedIn(request)){
           try {
               return new ResponseEntity<>(imageStorageService.update(id,file),HttpStatus.OK);
           } catch (NotFoundException e) {
               System.out.println(e.getMessage());
               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
           } catch (Exception e) {
               return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
           }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
