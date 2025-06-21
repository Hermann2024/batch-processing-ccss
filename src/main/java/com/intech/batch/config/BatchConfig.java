package com.intech.batch.config;

import com.intech.batch.dto.RapportTeletravailDTO;
import com.intech.batch.entity.Collaborateur;
import com.intech.batch.repository.CollaborateurRepository;
import com.intech.batch.service.CalculTeletravailService;
import com.intech.batch.service.CsvExportService;
import com.intech.batch.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {
    
    private final CalculTeletravailService calculTeletravailService;
    private final CsvExportService csvExportService;
    private final EmailService emailService;
    private final CollaborateurRepository collaborateurRepository;
    
    /**
     * Configuration du job de traitement du télétravail CCSS
     */
    @Bean
    public Job teletravailCcssJob(JobRepository jobRepository, Step calculTeletravailStep) {
        return new JobBuilder("teletravailCcssJob", jobRepository)
                .start(calculTeletravailStep)
                .build();
    }
    
    /**
     * Configuration de l'étape de calcul et export
     */
    @Bean
    public Step calculTeletravailStep(JobRepository jobRepository, 
                                     PlatformTransactionManager transactionManager) {
        return new StepBuilder("calculTeletravailStep", jobRepository)
                .<Collaborateur, RapportTeletravailDTO>chunk(10, transactionManager)
                .reader(collaborateurReader())
                .processor(collaborateurProcessor())
                .writer(rapportWriter())
                .build();
    }
    
    /**
     * Reader pour lire les collaborateurs
     */
    @Bean
    public ItemReader<Collaborateur> collaborateurReader() {
        // Pour l'exemple, on utilise le dernier mois échu
        YearMonth moisCalcul = YearMonth.now().minusMonths(1);
        
        List<Collaborateur> collaborateurs = collaborateurRepository
                .findActifsALaDate(moisCalcul.atEndOfMonth());
        
        log.info("Lecture de {} collaborateurs pour le mois {}", collaborateurs.size(), moisCalcul);
        
        return new ListItemReader<>(collaborateurs);
    }
    
    /**
     * Processor pour calculer le rapport de télétravail
     */
    @Bean
    public ItemProcessor<Collaborateur, RapportTeletravailDTO> collaborateurProcessor() {
        return collaborateur -> {
            // Utiliser le dernier mois échu pour le calcul
            YearMonth moisCalcul = YearMonth.now().minusMonths(1);
            
            log.debug("Traitement du collaborateur: {}", collaborateur.getTrigramme());
            
            RapportTeletravailDTO rapport = calculTeletravailService
                    .calculerRapportTeletravail(collaborateur, moisCalcul);
            
            return rapport;
        };
    }
    
    /**
     * Writer pour écrire les rapports et envoyer l'email
     */
    @Bean
    public ItemWriter<RapportTeletravailDTO> rapportWriter() {
        return items -> {
            try {
                // Dans Spring Batch 5.x, items est un Chunk, pas une List
                if (items != null && !items.isEmpty()) {
                    log.info("Écriture de {} rapports", items.size());
                    
                    // Utiliser le dernier mois échu pour le calcul
                    YearMonth moisCalcul = YearMonth.now().minusMonths(1);
                    
                    // Convertir le Chunk en List pour les services
                    List<RapportTeletravailDTO> rapportList = new ArrayList<>();
                    for (RapportTeletravailDTO rapport : items) {
                        if (rapport != null) {
                            rapportList.add(rapport);
                        }
                    }
                    
                    if (!rapportList.isEmpty()) {
                        // Exporter en CSV
                        String csvPath = csvExportService.exporterRapportCsv(rapportList);
                        log.info("Rapport CSV exporté: {}", csvPath);
                        
                        // Exporter la synthèse
                        String synthesePath = csvExportService.exporterRapportSyntheseCsv(rapportList);
                        log.info("Synthèse exportée: {}", synthesePath);
                        
                        // Afficher le rapport textuel dans les logs
                        String rapportTextuel = csvExportService.genererRapportTextuel(rapportList);
                        log.info("Rapport textuel:\n{}", rapportTextuel);
                        
                        // Afficher les statistiques
                        String statistiques = calculTeletravailService.obtenirStatistiques(moisCalcul);
                        log.info("Statistiques:\n{}", statistiques);
                        
                        // Envoyer l'email en arrière-plan (asynchrone)
                        log.info("Envoi du rapport par email à nanahermann@outlook.com (en arrière-plan)...");
                        CompletableFuture.runAsync(() -> {
                            try {
                                emailService.envoyerRapportEmail(rapportList, moisCalcul);
                                log.info("✅ Email envoyé avec succès à nanahermann@outlook.com");
                            } catch (Exception e) {
                                log.error("❌ Erreur lors de l'envoi de l'email", e);
                            }
                        });
                        
                        log.info("🎉 Traitement du batch CCSS terminé avec succès !");
                        log.info("📧 L'email sera envoyé en arrière-plan à nanahermann@outlook.com");
                    } else {
                        log.warn("Aucun rapport valide à traiter");
                    }
                } else {
                    log.warn("Aucun élément à traiter dans le writer");
                }
            } catch (Exception e) {
                log.error("Erreur lors de l'écriture des rapports", e);
                throw e;
            }
        };
    }
} 