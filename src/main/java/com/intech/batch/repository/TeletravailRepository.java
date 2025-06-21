package com.intech.batch.repository;

import com.intech.batch.entity.Teletravail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TeletravailRepository extends JpaRepository<Teletravail, Long> {
    
    /**
     * Trouve tous les jours de télétravail d'un collaborateur pour une période
     */
    List<Teletravail> findByCollaborateurIdCollAndDateTeletravailBetweenOrderByDateTeletravailAsc(
            @Param("collaborateurId") Integer collaborateurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    /**
     * Compte les jours de télétravail d'un collaborateur pour une période
     */
    @Query("SELECT COUNT(t) FROM Teletravail t " +
           "WHERE t.collaborateur.idColl = :collaborateurId " +
           "AND t.dateTeletravail BETWEEN :dateDebut AND :dateFin")
    Long countJoursTeletravail(@Param("collaborateurId") Integer collaborateurId,
                               @Param("dateDebut") LocalDate dateDebut,
                               @Param("dateFin") LocalDate dateFin);
    
    /**
     * Compte les jours de télétravail avec coefficient (prend en compte les demi-journées)
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN t.halfDayPeriod IS NULL THEN 1.0 ELSE 0.5 END), 0.0) FROM Teletravail t " +
           "WHERE t.collaborateur.idColl = :collaborateurId " +
           "AND t.dateTeletravail BETWEEN :dateDebut AND :dateFin")
    Double countJoursTeletravailAvecCoefficient(@Param("collaborateurId") Integer collaborateurId,
                                                @Param("dateDebut") LocalDate dateDebut,
                                                @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve tous les jours de télétravail pour une période donnée
     */
    @Query("SELECT t FROM Teletravail t " +
            "WHERE t.collaborateur.idColl = :collaborateurId " +
            "AND t.dateTeletravail BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY t.dateTeletravail ASC")
    List<Teletravail> findTeletravailsByCollaborateurAndPeriod(
            @Param("collaborateurId") Integer collaborateurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve les jours de télétravail d'un collaborateur pour une année
     */
    @Query("SELECT t FROM Teletravail t " +
            "WHERE t.collaborateur.idColl = :collaborateurId " +
            "AND t.dateTeletravail BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY t.dateTeletravail ASC")
    List<Teletravail> findByCollaborateurIdCollAndAnnee(@Param("collaborateurId") Integer collaborateurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve les jours de télétravail d'un collaborateur jusqu'à une date donnée
     */
    @Query("SELECT t FROM Teletravail t " +
            "WHERE t.collaborateur.idColl = :collaborateurId " +
            "AND t.dateTeletravail <= :dateLimite " +
            "ORDER BY t.dateTeletravail ASC")
    List<Teletravail> findByCollaborateurIdCollAndDateTeletravailLessThanEqual(@Param("collaborateurId") Integer collaborateurId,
            @Param("dateLimite") LocalDate dateLimite);
    
    /**
     * Trouve les jours de télétravail par type
     */
    List<Teletravail> findByTypeTTId(Integer typeTTId);
} 