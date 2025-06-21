package com.intech.batch.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "collab")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collaborateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idColl")
    private Integer idColl;
    
    @Column(name = "Trigramme", length = 3, columnDefinition = "CHAR(3)")
    private String trigramme;
    
    @Column(name = "Nom", length = 30)
    private String nom;
    
    @Column(name = "Prénom", length = 30)
    private String prenom;
    
    @Column(name = "Email", length = 255)
    private String email;
    
    @Column(name = "TpsPart", precision = 4, scale = 2, nullable = false)
    private BigDecimal tpsPart; // Taux d'occupation (ex: 0.80 pour 80%)
    
    @Column(name = "DateEnt")
    private LocalDate dateEnt; // Date d'entrée
    
    @Column(name = "DateSort")
    private LocalDate dateSort; // Date de sortie
    
    @Column(name = "DateNaiss")
    private LocalDate dateNaiss; // Date de naissance
    
    @Column(name = "noSS", length = 15)
    private String noSS; // Numéro de sécurité sociale
    
    @Column(name = "TelDom", length = 30)
    private String telDom;
    
    @Column(name = "TelPort", length = 30)
    private String telPort;
    
    @Column(name = "AdrRue", length = 125)
    private String adrRue;
    
    @Column(name = "CodePost", length = 10)
    private String codePost;
    
    @Column(name = "Occupation", precision = 8, scale = 4)
    private BigDecimal occupation; // Occupation en pourcentage
    
    @Column(name = "NbHeurDef", precision = 5, scale = 2)
    private BigDecimal nbHeurDef; // Nombre d'heures par défaut
    
    @Column(name = "TtThreshold")
    private Integer ttThreshold; // Seuil de télétravail
    
    @Column(name = "retributionBase100")
    private Integer retributionBase100;
    
    // Méthode utilitaire pour obtenir le nom complet
    public String getNomComplet() {
        return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
    }
    
    // Méthode pour vérifier si le collaborateur est actif
    public boolean isActif() {
        return dateSort == null || dateSort.isAfter(LocalDate.now());
    }
    
    // Méthode pour obtenir le taux d'occupation en pourcentage
    public double getTauxOccupationPourcentage() {
        return tpsPart != null ? tpsPart.doubleValue() * 100 : 0.0;
    }
} 