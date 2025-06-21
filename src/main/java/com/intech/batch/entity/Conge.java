package com.intech.batch.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "conge")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConge")
    private Integer idConge;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Collaborateur_idColl")
    private Collaborateur collaborateur;
    
    @Column(name = "TypeCong_idTypeCong")
    private Integer typeCongId;
    
    @Column(name = "DateConge")
    private LocalDate dateConge;
    
    @Column(name = "AnoConge")
    private Integer anoConge;
    
    @Column(name = "group_conge_id", length = 36)
    private String groupCongeId;
    
    @Column(name = "id_parentalLeave")
    private Integer idParentalLeave;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "half_day_period")
    private HalfDayPeriod halfDayPeriod;
    
    public enum HalfDayPeriod {
        MORNING,
        AFTERNOON
    }
    
    // Méthode utilitaire pour vérifier si c'est un jour complet de congé
    public boolean isJourComplet() {
        return halfDayPeriod == null;
    }
    
    // Méthode utilitaire pour vérifier si c'est une demi-journée
    public boolean isDemiJournee() {
        return halfDayPeriod != null;
    }
    
    // Méthode pour obtenir le coefficient de congé (1.0 pour jour complet, 0.5 pour demi-journée)
    public double getCoefficientConge() {
        return isDemiJournee() ? 0.5 : 1.0;
    }
} 