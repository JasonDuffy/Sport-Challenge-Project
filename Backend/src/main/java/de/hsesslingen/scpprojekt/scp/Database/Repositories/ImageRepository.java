package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Image Repository (needed for the standard functions)
 *
 * @author Robin Hackh
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

}
