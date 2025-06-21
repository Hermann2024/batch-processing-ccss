package com.intech.batch.repository;

import com.intech.batch.entity.Conge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CongeRepository extends JpaRepository<Conge, Long> {
    
    /**
     * Trouve tous les congés d'un collaborateur pour une période
     */
    List<Conge> findByCollaborateurIdCollAndDateCongeBetweenOrderByDateCongeAsc(
            @Param("collaborateurId") Integer collaborateurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    /**
     * Compte les jours de congés d'un collaborateur pour une période
     */
    @Query("SELECT COUNT(c) FROM Conge c " +
           "WHERE c.collaborateur.idColl = :collaborateurId " +
           "AND c.dateConge BETWEEN :dateDebut AND :dateFin")
    Long countJoursConges(@Param("collaborateurId") Integer collaborateurId,
                          @Param("dateDebut") LocalDate dateDebut,
                          @Param("dateFin") LocalDate dateFin);
    
    /**
     * Compte les jours de congés avec coefficient (prend en compte les demi-journées)
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN c.halfDayPeriod IS NULL THEN 1.0 ELSE 0.5 END), 0.0) FROM Conge c " +
           "WHERE c.collaborateur.idColl = :collaborateurId " +
           "AND c.dateConge BETWEEN :dateDebut AND :dateFin")
    Double countJoursCongesAvecCoefficient(@Param("collaborateurId") Integer collaborateurId,
                                           @Param("dateDebut") LocalDate dateDebut,
                                           @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve les congés par type
     */
    List<Conge> findByTypeCongId(Integer typeCongId);
    
    /**
     * Trouve les congés d'un collaborateur pour une année
     */
    @Query("SELECT c FROM Conge c " +
           "WHERE c.collaborateur.idColl = :collaborateurId " +
           "AND YEAR(c.dateConge) = :annee " +
           "ORDER BY c.dateConge ASC")
    List<Conge> findByCollaborateurIdCollAndAnnee(@Param("collaborateurId") Integer collaborateurId,
                                                  @Param("annee") int annee);
    
    /**
     * Trouve les congés d'un collaborateur jusqu'à une date donnée
     */
    @Query("SELECT c FROM Conge c " +
           "WHERE c.collaborateur.idColl = :collaborateurId " +
           "AND c.dateConge <= :dateFin " +
           "ORDER BY c.dateConge ASC")
    List<Conge> findByCollaborateurIdCollAndDateCongeLessThanEqual(@Param("collaborateurId") Integer collaborateurId,
                                                                   @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve les congés par type et période
     */
    @Query("SELECT c FROM Conge c " +
           "WHERE c.typeCongId = :typeCongId " +
           "AND c.dateConge BETWEEN :dateDebut AND :dateFin")
    List<Conge> findByTypeCongIdAndDateCongeBetween(@Param("typeCongId") Integer typeCongId,
                                                    @Param("dateDebut") LocalDate dateDebut,
                                                    @Param("dateFin") LocalDate dateFin);

    @Query("SELECT c FROM Conge c " +
            "WHERE c.collaborateur.idColl = :collaborateurId " +
            "AND c.dateConge BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY c.dateConge ASC")
    List<Conge> findCongesByCollaborateurAndPeriod(
            @Param("collaborateurId") Integer collaborateurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    @Query("SELECT c FROM Conge c " +
            "WHERE c.collaborateur.idColl = :collaborateurId " +
            "AND c.dateConge BETWEEN :dateDebut AND :dateFin " +
            "AND c.typeCongId IN :typesConge " +
            "ORDER BY c.dateConge ASC")
    List<Conge> findCongesByCollaborateurAndPeriodAndTypes(
            @Param("collaborateurId") Integer collaborateurId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("typesConge") List<Integer> typesConge);
} 