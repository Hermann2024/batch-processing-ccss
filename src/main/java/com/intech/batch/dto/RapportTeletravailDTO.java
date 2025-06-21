package com.intech.batch.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RapportTeletravailDTO {
    
    // Informations du collaborateur
    private String matricule;
    private String nom;
    private String prenom;
    private String nomComplet;
    private Double tauxOccupation;
    
    // Période de calcul
    private YearMonth moisCalcul;
    private LocalDate dateDebutAnnee;
    private LocalDate dateFinMois;
    
    // Calculs selon les exigences CCSS
    private Integer nbJoursTeletravail;
    private Integer nbJoursTravailles;
    private Double pourcentageTeletravail;
    
    // Informations supplémentaires
    private String statut; // "CONFORME" si entre 25% et 49%, "NON_CONFORME" sinon
    private String commentaire;
    
    // Méthode pour calculer le statut selon les exigences CCSS
    public void calculerStatut() {
        if (pourcentageTeletravail != null) {
            if (pourcentageTeletravail >= 25.0 && pourcentageTeletravail < 50.0) {
                this.statut = "CONFORME";
                this.commentaire = "Conforme à l'accord-cadre CCSS (25% à 49%)";
            } else {
                this.statut = "NON_CONFORME";
                this.commentaire = "Non conforme à l'accord-cadre CCSS (doit être entre 25% et 49%)";
            }
        }
    }
    
    // Méthode pour formater le pourcentage pour l'affichage
    public String getPourcentageFormate() {
        if (pourcentageTeletravail != null) {
            return String.format("%.2f%%", pourcentageTeletravail);
        }
        return "N/A";
    }
    
    // Méthode pour obtenir le taux d'occupation en pourcentage
    public String getTauxOccupationFormate() {
        if (tauxOccupation != null) {
            return String.format("%.0f%%", tauxOccupation * 100);
        }
        return "N/A";
    }
} 