package de.thoms.kuekels.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FangRepository extends JpaRepository<Fang, Long> {

	
    @Query("SELECT f FROM Fang f WHERE f.user = :user")
    List<Fang> findByUser(@Param("user") String user);
    
    @Query("SELECT sum(f.menge) FROM Fang f WHERE f.fischart = :fisch and f.released = false")
    Optional<Long> findAlleMengeByFischart(@Param("fisch") String fisch);
	
    @Query("SELECT sum(f.gewicht) FROM Fang f WHERE f.fischart = :fisch and f.released = false")
    Optional<Long> findAlleGewichtByFischart(@Param("fisch") String fisch);
    
    @Query("SELECT f FROM Fang f WHERE f.datum BETWEEN :startDate AND :endDate")
    List<Fang> findAllByDatumBetweenNative(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT f FROM Fang f WHERE f.user = :user AND f.datum BETWEEN :startDate AND :endDate")
	List<Fang> findAllByDatumBetweenNative(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,@Param("user")  String user);
  
    @Query("SELECT sum(f.menge) FROM Fang f WHERE f.fischart = :fisch and f.released = false AND f.datum BETWEEN :startDate AND :endDate AND f.user = :user")
    Optional<Long> findAlleMengeByFischartUserDatum(@Param("fisch") String fisch,@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,@Param("user") String user);
	
    @Query("SELECT sum(f.gewicht) FROM Fang f WHERE f.fischart = :fisch and f.released = false AND f.datum BETWEEN :startDate AND :endDate AND f.user = :user")
    Optional<Long> findAlleGewichtByFischartUserDatum(@Param("fisch") String fisch,@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,@Param("user") String user);
    
    @Query("SELECT sum(f.menge) FROM Fang f WHERE f.fischart = :fisch and year(f.datum) = :jahr")
    Optional<Long> findAlleMengeByFischartandJahr(@Param("fisch") String fisch,@Param("jahr") int jahr);
    
    @Query("SELECT sum(f.menge) FROM Fang f WHERE f.fischart = :fisch and f.released = false AND f.datum BETWEEN :startDate AND :endDate")
    Optional<Long> findAlleMengeByFischartDatum(@Param("fisch") String fisch,@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
	
    @Query("SELECT sum(f.gewicht) FROM Fang f WHERE f.fischart = :fisch and f.released = false AND f.datum BETWEEN :startDate AND :endDate")
    Optional<Long> findAlleGewichtByFischartDatum(@Param("fisch") String fisch,@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
	
}
