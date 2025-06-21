package com.intech.batch.service;

import com.intech.batch.dto.RapportTeletravailDTO;
import com.intech.batch.entity.Collaborateur;
import com.intech.batch.repository.CollaborateurRepository;
import com.intech.batch.repository.CongeRepository;
import com.intech.batch.repository.TeletravailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculTeletravailService {
    
    private final CollaborateurRepository collaborateurRepository;
    private final TeletravailRepository teletravailRepository;
    private final CongeRepository congeRepository;
    
    // Types de congés considérés comme travaillés selon les exigences CCSS
    // Ces IDs correspondent aux types de congés dans ta base de données
    private static final List<Integer> TYPES_CONGES_TRAVAILLES = List.of(
            2,  // Congés payés (à vérifier dans ta base)
            6,  // Congés exceptionnels (à vérifier dans ta base)
            10  // Autres congés travaillés (à vérifier dans ta base)
    );
    
    /**
     * Calcule le rapport de télétravail pour un collaborateur selon les exigences CCSS
     */
    public RapportTeletravailDTO calculerRapportTeletravail(Collaborateur collaborateur, YearMonth moisCalcul) {
        log.debug("Calcul du rapport télétravail pour {} - {}", collaborateur.getTrigramme(), moisCalcul);
        
        // Définir les dates de calcul
        LocalDate dateDebutAnnee = LocalDate.of(moisCalcul.getYear(), 1, 1);
        LocalDate dateFinMois = moisCalcul.atEndOfMonth();
        
        // Calculer le nombre de jours ouvrés entre début d'année et fin du mois
        int nbJoursOuvres = calculerJoursOuvres(dateDebutAnnee, dateFinMois);
        
        // Calculer le nombre de jours travaillés selon le taux d'occupation
        double tauxOccupation = collaborateur.getTpsPart() != null ? collaborateur.getTpsPart().doubleValue() : 1.0;
        int nbJoursTravailles = (int) Math.round(nbJoursOuvres * tauxOccupation);
        
        // Compter les jours de télétravail jusqu'à la fin du mois (avec coefficient pour demi-journées)
        Double nbJoursTeletravail = teletravailRepository.countJoursTeletravailAvecCoefficient(
                collaborateur.getIdColl(), dateDebutAnnee, dateFinMois);
        
        if (nbJoursTeletravail == null) {
            nbJoursTeletravail = 0.0;
        }
        
        // Calculer le pourcentage selon la formule CCSS
        double pourcentage = 0.0;
        if (nbJoursTravailles > 0) {
            pourcentage = (nbJoursTeletravail * 100.0) / nbJoursTravailles;
        }
        
        // Construire le DTO
        RapportTeletravailDTO rapport = RapportTeletravailDTO.builder()
                .matricule(collaborateur.getTrigramme())
                .nom(collaborateur.getNom())
                .prenom(collaborateur.getPrenom())
                .nomComplet(collaborateur.getNomComplet())
                .tauxOccupation(tauxOccupation)
                .moisCalcul(moisCalcul)
                .dateDebutAnnee(dateDebutAnnee)
                .dateFinMois(dateFinMois)
                .nbJoursTeletravail(nbJoursTeletravail.intValue())
                .nbJoursTravailles(nbJoursTravailles)
                .pourcentageTeletravail(pourcentage)
                .build();
        
        // Calculer le statut selon les exigences CCSS
        rapport.calculerStatut();
        
        log.debug("Rapport calculé pour {}: {} jours TT / {} jours travaillés = {}% ({})", 
                collaborateur.getTrigramme(), nbJoursTeletravail, nbJoursTravailles, 
                rapport.getPourcentageFormate(), rapport.getStatut());
        
        return rapport;
    }
    
    /**
     * Calcule le rapport de télétravail pour tous les collaborateurs actifs
     */
    public List<RapportTeletravailDTO> calculerRapportTeletravailTous(YearMonth moisCalcul) {
        log.info("Calcul du rapport télétravail pour tous les collaborateurs - {}", moisCalcul);
        
        List<Collaborateur> collaborateurs = collaborateurRepository.findActifsALaDate(moisCalcul.atEndOfMonth());
        
        return collaborateurs.stream()
                .map(collaborateur -> calculerRapportTeletravail(collaborateur, moisCalcul))
                .toList();
    }
    
    /**
     * Calcule le nombre de jours ouvrés entre deux dates (excluant les weekends)
     */
    private int calculerJoursOuvres(LocalDate dateDebut, LocalDate dateFin) {
        int nbJoursOuvres = 0;
        LocalDate date = dateDebut;
        
        while (!date.isAfter(dateFin)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                nbJoursOuvres++;
            }
            date = date.plusDays(1);
        }
        
        return nbJoursOuvres;
    }
    
    /**
     * Vérifie si un collaborateur est conforme aux exigences CCSS
     */
    public boolean estConformeCCSS(RapportTeletravailDTO rapport) {
        return "CONFORME".equals(rapport.getStatut());
    }
    
    /**
     * Génère un rapport détaillé pour un collaborateur
     */
    public String genererRapportDetaille(RapportTeletravailDTO rapport) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPPORT TÉLÉTRAVAIL CCSS ===\n");
        sb.append("Collaborateur: ").append(rapport.getNomComplet()).append(" (").append(rapport.getMatricule()).append(")\n");
        sb.append("Taux d'occupation: ").append(rapport.getTauxOccupationFormate()).append("\n");
        sb.append("Période: ").append(rapport.getMoisCalcul()).append("\n");
        sb.append("Jours de télétravail: ").append(rapport.getNbJoursTeletravail()).append(" / ").append(rapport.getNbJoursTravailles()).append("\n");
        sb.append("Pourcentage: ").append(rapport.getPourcentageFormate()).append("\n");
        sb.append("Statut: ").append(rapport.getStatut()).append("\n");
        sb.append("Commentaire: ").append(rapport.getCommentaire()).append("\n");
        return sb.toString();
    }
    
    /**
     * Obtient les statistiques globales
     */
    public String obtenirStatistiques(YearMonth moisCalcul) {
        List<RapportTeletravailDTO> rapports = calculerRapportTeletravailTous(moisCalcul);
        
        long totalCollaborateurs = rapports.size();
        long conformes = rapports.stream().filter(this::estConformeCCSS).count();
        long nonConformes = totalCollaborateurs - conformes;
        
        double pourcentageConformite = totalCollaborateurs > 0 ? (conformes * 100.0) / totalCollaborateurs : 0.0;
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== STATISTIQUES GLOBALES ===\n");
        sb.append("Mois: ").append(moisCalcul).append("\n");
        sb.append("Total collaborateurs: ").append(totalCollaborateurs).append("\n");
        sb.append("Conformes CCSS: ").append(conformes).append(" (").append(String.format("%.1f%%", pourcentageConformite)).append(")\n");
        sb.append("Non conformes: ").append(nonConformes).append(" (").append(String.format("%.1f%%", 100.0 - pourcentageConformite)).append(")\n");
        
        return sb.toString();
    }
} 