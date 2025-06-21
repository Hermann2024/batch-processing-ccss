package com.intech.batch.repository;

import com.intech.batch.entity.Collaborateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long> {
    
    /**
     * Trouve tous les collaborateurs actifs (sans date de sortie ou date de sortie future)
     */
    @Query("SELECT c FROM Collaborateur c WHERE c.dateSort IS NULL OR c.dateSort > :date")
    List<Collaborateur> findActifsALaDate(@Param("date") LocalDate date);
    
    /**
     * Trouve un collaborateur par son trigramme
     */
    Collaborateur findByTrigramme(String trigramme);
    
    /**
     * Trouve les collaborateurs actifs pour une période donnée
     */
    @Query("SELECT c FROM Collaborateur c WHERE (c.dateSort IS NULL OR c.dateSort >= :dateDebut) " +
           "AND c.dateEnt <= :dateFin")
    List<Collaborateur> findActifsPourPeriode(@Param("dateDebut") LocalDate dateDebut, 
                                             @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve les collaborateurs par taux d'occupation
     */
    List<Collaborateur> findByTpsPartGreaterThan(java.math.BigDecimal tpsPart);
    
    /**
     * Trouve les collaborateurs par nom ou prénom (recherche insensible à la casse)
     */
    @Query("SELECT c FROM Collaborateur c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
           "OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :recherche, '%'))")
    List<Collaborateur> findByNomOrPrenomContainingIgnoreCase(@Param("recherche") String recherche);
} 