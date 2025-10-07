package de.thoms.kuekels.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FischartRepository extends JpaRepository<Fischart, Long > {
	
	@Query("SELECT f FROM Fischart f ORDER BY f.name")
	List<Fischart> findAllOrderedByName();
	
	@Query("SELECT f FROM Fischart f WHERE f.name = :name")
	List<Fischart> findFisch(@Param("name") String name);

}
