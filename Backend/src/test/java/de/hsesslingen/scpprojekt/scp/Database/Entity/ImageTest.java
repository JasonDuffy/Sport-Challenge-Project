package de.hsesslingen.scpprojekt.scp.Database.Entity;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Image Entity Test
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ImageTest {
    private byte[] a =  {12,12,123};
    /**
     * Test if the Image is correctly created
     */
    @Test
    void ImageTest(){
        Image imageTest = new Image("Laufen","Png",a);
        assertEquals("Laufen",imageTest.getName());
        assertEquals("Png",imageTest.getType());
        assertEquals(a,imageTest.getData());

    }
}
