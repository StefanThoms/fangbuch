package de.thoms.kuekels.data;


import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long > {
	


}