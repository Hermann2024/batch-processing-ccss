package com.intech.batch.service;

import com.intech.batch.dto.RapportTeletravailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${batch.csss.email.enabled:false}")
    private boolean emailEnabled;
    
    @Value("${batch.csss.email.to:kenzopharell@gmail.com}")
    private String emailTo;
    
    @Value("${batch.csss.email.from:your-email@gmail.com}")
    private String emailFrom;
    
    @Value("${batch.csss.email.subject:Rapport Télétravail CCSS - {mois}}")
    private String emailSubject;
    
    @Value("${batch.csss.output.directory:./output}")
    private String outputDirectory;
    
    /**
     * Envoie le rapport de télétravail par email
     */
    public void envoyerRapportEmail(List<RapportTeletravailDTO> rapports, YearMonth moisCalcul) {
        if (!emailEnabled) {
            log.info("Envoi d'email désactivé dans la configuration");
            return;
        }
        
        try {
            log.info("Envoi du rapport par email à {}", emailTo);
            
            // Créer le message MIME pour les pièces jointes
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Configuration de base
            helper.setTo(emailTo);
            helper.setFrom(emailFrom);
            helper.setSubject(genererSujet(moisCalcul));
            
            // Corps du message
            String corpsMessage = genererCorpsMessage(rapports, moisCalcul);
            helper.setText(corpsMessage, true); // true pour HTML
            
            // Ajouter les pièces jointes
            ajouterPiecesJointes(helper, moisCalcul);
            
            // Envoyer l'email
            mailSender.send(message);
            
            log.info("✅ Email envoyé avec succès à {}", emailTo);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email", e);
        }
    }
    
    /**
     * Génère le sujet de l'email
     */
    private String genererSujet(YearMonth moisCalcul) {
        String moisFormate = moisCalcul.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));
        return emailSubject.replace("{mois}", moisFormate);
    }
    
    /**
     * Génère le corps du message HTML
     */
    private String genererCorpsMessage(List<RapportTeletravailDTO> rapports, YearMonth moisCalcul) {
        StringBuilder html = new StringBuilder();
        
        // Calcul des statistiques
        long totalCollaborateurs = rapports.size();
        long conformes = rapports.stream().filter(r -> "CONFORME".equals(r.getStatut())).count();
        long nonConformes = totalCollaborateurs - conformes;
        double pourcentageConformite = totalCollaborateurs > 0 ? (conformes * 100.0) / totalCollaborateurs : 0.0;
        
        // En-tête
        html.append("<html><body>");
        html.append("<h2 style='color: #2c3e50;'>📊 Rapport Télétravail CCSS</h2>");
        html.append("<p><strong>Période de calcul :</strong> ").append(moisCalcul.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH))).append("</p>");
        html.append("<p><strong>Date de génération :</strong> ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.FRENCH))).append("</p>");
        
        // Statistiques
        html.append("<h3 style='color: #3498db;'>📈 Statistiques Globales</h3>");
        html.append("<table style='border-collapse: collapse; width: 100%; margin: 10px 0;'>");
        html.append("<tr style='background-color: #ecf0f1;'><th style='border: 1px solid #bdc3c7; padding: 8px; text-align: left;'>Métrique</th><th style='border: 1px solid #bdc3c7; padding: 8px; text-align: left;'>Valeur</th></tr>");
        html.append("<tr><td style='border: 1px solid #bdc3c7; padding: 8px;'>Total collaborateurs</td><td style='border: 1px solid #bdc3c7; padding: 8px;'>").append(totalCollaborateurs).append("</td></tr>");
        html.append("<tr><td style='border: 1px solid #bdc3c7; padding: 8px;'>Conformes CCSS</td><td style='border: 1px solid #bdc3c7; padding: 8px; color: #27ae60;'>").append(conformes).append(" (").append(String.format("%.1f%%", pourcentageConformite)).append(")</td></tr>");
        html.append("<tr><td style='border: 1px solid #bdc3c7; padding: 8px;'>Non conformes</td><td style='border: 1px solid #bdc3c7; padding: 8px; color: #e74c3c;'>").append(nonConformes).append(" (").append(String.format("%.1f%%", 100.0 - pourcentageConformite)).append(")</td></tr>");
        html.append("</table>");
        
        // Détails des collaborateurs non conformes
        if (nonConformes > 0) {
            html.append("<h3 style='color: #e74c3c;'>⚠️ Collaborateurs Non Conformes</h3>");
            html.append("<table style='border-collapse: collapse; width: 100%; margin: 10px 0;'>");
            html.append("<tr style='background-color: #ecf0f1;'><th style='border: 1px solid #bdc3c7; padding: 8px; text-align: left;'>Collaborateur</th><th style='border: 1px solid #bdc3c7; padding: 8px; text-align: left;'>Pourcentage TT</th><th style='border: 1px solid #bdc3c7; padding: 8px; text-align: left;'>Commentaire</th></tr>");
            
            rapports.stream()
                    .filter(r -> "NON_CONFORME".equals(r.getStatut()))
                    .forEach(rapport -> {
                        html.append("<tr>");
                        html.append("<td style='border: 1px solid #bdc3c7; padding: 8px;'>").append(rapport.getNomComplet()).append(" (").append(rapport.getMatricule()).append(")</td>");
                        html.append("<td style='border: 1px solid #bdc3c7; padding: 8px;'>").append(rapport.getPourcentageFormate()).append("</td>");
                        html.append("<td style='border: 1px solid #bdc3c7; padding: 8px;'>").append(rapport.getCommentaire()).append("</td>");
                        html.append("</tr>");
                    });
            
            html.append("</table>");
        }
        
        // Informations sur les pièces jointes
        html.append("<h3 style='color: #3498db;'>📎 Pièces Jointes</h3>");
        html.append("<ul>");
        html.append("<li><strong>rapport_teletravail_ccss_").append(moisCalcul.format(DateTimeFormatter.ofPattern("yyyy-MM"))).append(".csv</strong> : Rapport détaillé par collaborateur</li>");
        html.append("<li><strong>synthese_teletravail_ccss_").append(moisCalcul.format(DateTimeFormatter.ofPattern("yyyy-MM"))).append(".csv</strong> : Synthèse par statut</li>");
        html.append("</ul>");
        
        // Note de bas de page
        html.append("<hr style='margin: 20px 0;'>");
        html.append("<p style='color: #7f8c8d; font-size: 12px;'>");
        html.append("Ce rapport a été généré automatiquement par le système de traitement CCSS.<br>");
        html.append("Pour toute question, contactez l'équipe RH.");
        html.append("</p>");
        
        html.append("</body></html>");
        
        return html.toString();
    }
    
    /**
     * Ajoute les pièces jointes au message
     */
    private void ajouterPiecesJointes(MimeMessageHelper helper, YearMonth moisCalcul) throws MessagingException {
        String moisFormate = moisCalcul.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // Rapport détaillé - utiliser le nom de fichier réellement généré
        File rapportDetaille = new File(outputDirectory, "rapport_teletravail_ccss.csv");
        if (rapportDetaille.exists()) {
            // Renommer le fichier pour l'attachement avec le mois
            String nomAttachement = "rapport_teletravail_ccss_" + moisFormate + ".csv";
            helper.addAttachment(nomAttachement, new FileSystemResource(rapportDetaille));
            log.info("✅ Pièce jointe ajoutée : {}", nomAttachement);
        } else {
            log.warn("❌ Fichier rapport détaillé non trouvé : {}", rapportDetaille.getAbsolutePath());
        }
        
        // Synthèse - utiliser le nom de fichier réellement généré
        File synthese = new File(outputDirectory, "synthese_ccss_rapport_teletravail_ccss.csv");
        if (synthese.exists()) {
            // Renommer le fichier pour l'attachement avec le mois
            String nomAttachement = "synthese_teletravail_ccss_" + moisFormate + ".csv";
            helper.addAttachment(nomAttachement, new FileSystemResource(synthese));
            log.info("✅ Pièce jointe ajoutée : {}", nomAttachement);
        } else {
            log.warn("❌ Fichier synthèse non trouvé : {}", synthese.getAbsolutePath());
        }
    }
    
    /**
     * Envoie un email simple de notification
     */
    public void envoyerNotificationSimple(String sujet, String message) {
        if (!emailEnabled) {
            log.info("Envoi d'email désactivé dans la configuration");
            return;
        }
        
        try {
            SimpleMailMessage simpleMessage = new SimpleMailMessage();
            simpleMessage.setTo(emailTo);
            simpleMessage.setFrom(emailFrom);
            simpleMessage.setSubject(sujet);
            simpleMessage.setText(message);
            
            mailSender.send(simpleMessage);
            log.info("✅ Email de notification envoyé à {}", emailTo);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de notification", e);
        }
    }
} 