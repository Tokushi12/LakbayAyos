package edu.cit.ballener.lakbayayos.Repository;
import edu.cit.ballener.lakbayayos.Entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    List<Part> findByNameContainingIgnoreCase(String name);

    List<Part> findByCategoryContainingIgnoreCase(String category);

    List<Part> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);

    List<Part> findByIsAvailableTrue();
}