package com.intech.batch.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "teletravail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teletravail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idteletravail")
    private Integer idTeletravail;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Collaborateur_idColl")
    private Collaborateur collaborateur;
    
    @Column(name = "TypeTT_idTypeTT")
    private Integer typeTTId;
    
    @Column(name = "idValidePar")
    private Integer idValidePar;
    
    @Column(name = "DateTeletravail")
    private LocalDate dateTeletravail;
    
    @Column(name = "Remarques", length = 250)
    private String remarques;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "half_day_period")
    private HalfDayPeriod halfDayPeriod;
    
    public enum HalfDayPeriod {
        MORNING,
        AFTERNOON
    }
    
    // Méthode utilitaire pour vérifier si c'est un jour complet de télétravail
    public boolean isJourComplet() {
        return halfDayPeriod == null;
    }
    
    // Méthode utilitaire pour vérifier si c'est une demi-journée
    public boolean isDemiJournee() {
        return halfDayPeriod != null;
    }
    
    // Méthode pour obtenir le coefficient de télétravail (1.0 pour jour complet, 0.5 pour demi-journée)
    public double getCoefficientTeletravail() {
        return isDemiJournee() ? 0.5 : 1.0;
    }
} 