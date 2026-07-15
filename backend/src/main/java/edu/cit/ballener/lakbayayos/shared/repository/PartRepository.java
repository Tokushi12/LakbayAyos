package edu.cit.ballener.lakbayayos.shared.repository;
import edu.cit.ballener.lakbayayos.shared.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    List<Part> findByNameContainingIgnoreCase(String name);

    List<Part> findByCategoryContainingIgnoreCase(String category);

    List<Part> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);

    List<Part> findByIsAvailableTrue();
}