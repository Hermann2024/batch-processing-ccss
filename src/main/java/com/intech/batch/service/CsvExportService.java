package com.intech.batch.service;

import com.intech.batch.dto.RapportTeletravailDTO;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvExportService {
    
    @Value("${batch.csss.output.directory:./output}")
    private String outputDirectory;
    
    @Value("${batch.csss.output.filename:rapport_teletravail_ccss.csv}")
    private String outputFilename;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Exporte le rapport de télétravail en CSV selon les exigences CCSS
     */
    public String exporterRapportCsv(List<RapportTeletravailDTO> rapports) throws IOException {
        log.info("Export du rapport télétravail CCSS en CSV - {} collaborateurs", rapports.size());
        
        // Créer le répertoire de sortie s'il n'existe pas
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        // Créer le fichier CSV
        Path csvFile = outputPath.resolve(outputFilename);
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile.toFile()))) {
            // Écrire l'en-tête
            String[] header = {
                "Matricule",
                "Nom",
                "Prénom",
                "Nom Complet",
                "Taux d'Occupation (%)",
                "Mois de Calcul",
                "Date Début Année",
                "Date Fin Mois",
                "Nb Jours Télétravail",
                "Nb Jours Travaillés",
                "Pourcentage Télétravail (%)",
                "Statut CCSS",
                "Commentaire"
            };
            writer.writeNext(header);
            
            // Écrire les données
            for (RapportTeletravailDTO rapport : rapports) {
                String[] row = {
                    rapport.getMatricule(),
                    rapport.getNom(),
                    rapport.getPrenom(),
                    rapport.getNomComplet(),
                    rapport.getTauxOccupationFormate(),
                    rapport.getMoisCalcul() != null ? rapport.getMoisCalcul().toString() : "",
                    rapport.getDateDebutAnnee() != null ? rapport.getDateDebutAnnee().format(DATE_FORMATTER) : "",
                    rapport.getDateFinMois() != null ? rapport.getDateFinMois().format(DATE_FORMATTER) : "",
                    String.valueOf(rapport.getNbJoursTeletravail()),
                    String.valueOf(rapport.getNbJoursTravailles()),
                    rapport.getPourcentageFormate(),
                    rapport.getStatut(),
                    rapport.getCommentaire()
                };
                writer.writeNext(row);
            }
        }
        
        log.info("Rapport CSV exporté avec succès: {}", csvFile.toAbsolutePath());
        return csvFile.toAbsolutePath().toString();
    }
    
    /**
     * Exporte un rapport de synthèse en CSV
     */
    public String exporterRapportSyntheseCsv(List<RapportTeletravailDTO> rapports) throws IOException {
        log.info("Export du rapport de synthèse CCSS en CSV");
        
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        Path csvFile = outputPath.resolve("synthese_ccss_" + outputFilename);
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile.toFile()))) {
            // En-tête pour la synthèse
            String[] header = {
                "Statut",
                "Nombre de Collaborateurs",
                "Pourcentage Total"
            };
            writer.writeNext(header);
            
            // Compter les collaborateurs par statut
            long conformes = rapports.stream().filter(r -> "CONFORME".equals(r.getStatut())).count();
            long nonConformes = rapports.stream().filter(r -> "NON_CONFORME".equals(r.getStatut())).count();
            
            // Calculer les pourcentages
            double totalCollaborateurs = rapports.size();
            double pourcentageConformes = totalCollaborateurs > 0 ? (conformes * 100.0) / totalCollaborateurs : 0.0;
            double pourcentageNonConformes = totalCollaborateurs > 0 ? (nonConformes * 100.0) / totalCollaborateurs : 0.0;
            
            // Écrire les données de synthèse
            writer.writeNext(new String[]{"CONFORME", String.valueOf(conformes), String.format("%.2f%%", pourcentageConformes)});
            writer.writeNext(new String[]{"NON_CONFORME", String.valueOf(nonConformes), String.format("%.2f%%", pourcentageNonConformes)});
            writer.writeNext(new String[]{"TOTAL", String.valueOf((int)totalCollaborateurs), "100.00%"});
        }
        
        log.info("Rapport de synthèse CSV exporté: {}", csvFile.toAbsolutePath());
        return csvFile.toAbsolutePath().toString();
    }
    
    /**
     * Génère un rapport textuel détaillé
     */
    public String genererRapportTextuel(List<RapportTeletravailDTO> rapports) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPPORT TÉLÉTRAVAIL CCSS ===\n");
        sb.append("Date de génération: ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        sb.append("Nombre total de collaborateurs: ").append(rapports.size()).append("\n\n");
        
        // Statistiques
        long conformes = rapports.stream().filter(r -> "CONFORME".equals(r.getStatut())).count();
        long nonConformes = rapports.stream().filter(r -> "NON_CONFORME".equals(r.getStatut())).count();
        
        sb.append("STATISTIQUES:\n");
        sb.append("- Conformes à l'accord-cadre CCSS: ").append(conformes).append("\n");
        sb.append("- Non conformes: ").append(nonConformes).append("\n");
        sb.append("- Pourcentage de conformité: ").append(String.format("%.2f%%", (conformes * 100.0) / rapports.size())).append("\n\n");
        
        // Détail par collaborateur
        sb.append("DÉTAIL PAR COLLABORATEUR:\n");
        sb.append("=".repeat(80)).append("\n");
        
        for (RapportTeletravailDTO rapport : rapports) {
            sb.append("Matricule: ").append(rapport.getMatricule()).append("\n");
            sb.append("Nom: ").append(rapport.getNomComplet()).append("\n");
            sb.append("Taux d'occupation: ").append(rapport.getTauxOccupationFormate()).append("\n");
            sb.append("Jours télétravail: ").append(rapport.getNbJoursTeletravail()).append(" / ").append(rapport.getNbJoursTravailles()).append("\n");
            sb.append("Pourcentage: ").append(rapport.getPourcentageFormate()).append("\n");
            sb.append("Statut: ").append(rapport.getStatut()).append("\n");
            sb.append("Commentaire: ").append(rapport.getCommentaire()).append("\n");
            sb.append("-".repeat(40)).append("\n");
        }
        
        return sb.toString();
    }
} 