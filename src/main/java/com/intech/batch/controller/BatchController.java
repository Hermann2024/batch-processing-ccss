package com.intech.batch.controller;

import com.intech.batch.service.BatchSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchController {
    
    private final JobLauncher jobLauncher;
    private final Job teletravailCcssJob;
    private final BatchSchedulerService batchSchedulerService;
    
    /**
     * Déclenche le batch de traitement du télétravail CCSS
     */
    @PostMapping("/teletravail-ccss")
    public ResponseEntity<Map<String, Object>> lancerBatchTeletravail() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Démarrage du batch télétravail CCSS");
            
            // Créer des paramètres uniques pour éviter les conflits
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("dateExecution", LocalDateTime.now().toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            // Lancer le job
            var jobExecution = jobLauncher.run(teletravailCcssJob, jobParameters);
            
            response.put("success", true);
            response.put("message", "Batch lancé avec succès");
            response.put("jobExecutionId", jobExecution.getId());
            response.put("status", jobExecution.getStatus().toString());
            response.put("startTime", jobExecution.getStartTime());
            
            log.info("Batch télétravail CCSS lancé avec succès - JobExecutionId: {}", jobExecution.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Le job est déjà en cours d'exécution", e);
            response.put("success", false);
            response.put("error", "Le job est déjà en cours d'exécution");
            return ResponseEntity.badRequest().body(response);
            
        } catch (JobRestartException e) {
            log.error("Erreur lors du redémarrage du job", e);
            response.put("success", false);
            response.put("error", "Erreur lors du redémarrage du job");
            return ResponseEntity.badRequest().body(response);
            
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Le job a déjà été exécuté avec les mêmes paramètres", e);
            response.put("success", false);
            response.put("error", "Le job a déjà été exécuté avec les mêmes paramètres");
            return ResponseEntity.badRequest().body(response);
            
        } catch (JobParametersInvalidException e) {
            log.error("Paramètres de job invalides", e);
            response.put("success", false);
            response.put("error", "Paramètres de job invalides");
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Erreur inattendue lors du lancement du batch", e);
            response.put("success", false);
            response.put("error", "Erreur inattendue: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Lance le batch via le scheduler (pour tests)
     */
    @PostMapping("/teletravail-ccss/scheduler")
    public ResponseEntity<Map<String, Object>> lancerBatchViaScheduler() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Lancement du batch via le scheduler");
            batchSchedulerService.lancerBatchManuel();
            
            response.put("success", true);
            response.put("message", "Batch lancé via le scheduler");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors du lancement via le scheduler", e);
            response.put("success", false);
            response.put("error", "Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Informations sur le scheduling automatique
     */
    @GetMapping("/scheduler/info")
    public ResponseEntity<Map<String, Object>> getSchedulerInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("schedulingEnabled", true);
        response.put("cronExpression", "0 * * * * *"); // Toutes les minutes
        response.put("description", "Lancement automatique toutes les minutes");
        response.put("nextExecution", "Calculé automatiquement par Spring");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint de santé pour vérifier que l'application fonctionne
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Batch Processing CCSS");
        response.put("scheduling", "ACTIVE");
        return ResponseEntity.ok(response);
    }
} 