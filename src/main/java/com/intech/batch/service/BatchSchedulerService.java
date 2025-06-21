package com.intech.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BatchSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(BatchSchedulerService.class);
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job teletravailCcssJob;
    
    /**
     * Lance le batch automatiquement toutes les minutes
     * Cron: seconde minute heure jour mois jour-semaine
     */
    @Scheduled(cron = "0 * * * * *") // Toutes les minutes à la seconde 0
    public void lancerBatchAutomatique() {
        try {
            logger.info("=== DÉBUT LANCEMENT AUTOMATIQUE DU BATCH TÉLÉTRAVAIL CCSS ===");
            
            // Créer des paramètres uniques pour chaque exécution
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("dateExecution", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            // Lancer le job
            var jobExecution = jobLauncher.run(teletravailCcssJob, jobParameters);
            
            logger.info("✅ Batch lancé avec succès - JobExecutionId: {}, Status: {}", 
                       jobExecution.getId(), jobExecution.getStatus());
            
        } catch (JobExecutionAlreadyRunningException e) {
            logger.error("❌ Erreur: Le job est déjà en cours d'exécution", e);
        } catch (JobRestartException e) {
            logger.error("❌ Erreur: Impossible de redémarrer le job", e);
        } catch (JobInstanceAlreadyCompleteException e) {
            logger.error("❌ Erreur: Le job est déjà terminé avec ces paramètres", e);
        } catch (JobParametersInvalidException e) {
            logger.error("❌ Erreur: Paramètres de job invalides", e);
        } catch (Exception e) {
            logger.error("❌ Erreur inattendue lors du lancement du batch", e);
        } finally {
            logger.info("=== FIN LANCEMENT AUTOMATIQUE DU BATCH ===");
        }
    }
    
    /**
     * Méthode de test pour lancer le batch manuellement
     */
    public void lancerBatchManuel() {
        logger.info("Lancement manuel du batch demandé");
        lancerBatchAutomatique();
    }
} 