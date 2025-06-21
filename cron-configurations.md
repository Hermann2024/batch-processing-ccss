# Configurations Cron pour le Scheduling Automatique

## 🎯 Objectif
Ce guide présente les différentes configurations cron disponibles pour adapter la fréquence de lancement du batch selon vos besoins.

## ⏰ Format Cron
```
┌───────────── seconde (0-59)
│ ┌─────────── minute (0-59)
│ │ ┌───────── heure (0-23)
│ │ │ ┌─────── jour du mois (1-31)
│ │ │ │ ┌───── mois (1-12)
│ │ │ │ │ ┌─── jour de la semaine (0-7, 0=7=dimanche)
│ │ │ │ │ │
* * * * * *
```

## 🔄 Configurations Recommandées

### 1. Toutes les minutes (Configuration actuelle)
```yaml
scheduling:
  cron:
    expression: "0 * * * * *"
    description: "Lancement automatique toutes les minutes"
```
**Utilisation** : Développement, tests, monitoring en temps réel

### 2. Toutes les 5 minutes
```yaml
scheduling:
  cron:
    expression: "0 */5 * * * *"
    description: "Lancement automatique toutes les 5 minutes"
```
**Utilisation** : Production avec surveillance rapprochée

### 3. Toutes les 15 minutes
```yaml
scheduling:
  cron:
    expression: "0 */15 * * * *"
    description: "Lancement automatique toutes les 15 minutes"
```
**Utilisation** : Production standard

### 4. Toutes les heures
```yaml
scheduling:
  cron:
    expression: "0 0 * * * *"
    description: "Lancement automatique toutes les heures"
```
**Utilisation** : Production avec surveillance horaire

### 5. Toutes les 2 heures
```yaml
scheduling:
  cron:
    expression: "0 0 */2 * * *"
    description: "Lancement automatique toutes les 2 heures"
```
**Utilisation** : Production avec surveillance modérée

### 6. Tous les jours à 9h00
```yaml
scheduling:
  cron:
    expression: "0 0 9 * * *"
    description: "Lancement automatique tous les jours à 9h00"
```
**Utilisation** : Production avec rapport quotidien

### 7. Du lundi au vendredi à 9h00
```yaml
scheduling:
  cron:
    expression: "0 0 9 * * MON-FRI"
    description: "Lancement automatique du lundi au vendredi à 9h00"
```
**Utilisation** : Production en semaine uniquement

### 8. Du lundi au vendredi toutes les heures
```yaml
scheduling:
  cron:
    expression: "0 0 * * * MON-FRI"
    description: "Lancement automatique du lundi au vendredi toutes les heures"
```
**Utilisation** : Production en semaine avec surveillance horaire

### 9. Le premier jour de chaque mois à 8h00
```yaml
scheduling:
  cron:
    expression: "0 0 8 1 * *"
    description: "Lancement automatique le premier jour de chaque mois à 8h00"
```
**Utilisation** : Rapport mensuel

### 10. Tous les lundis à 8h00
```yaml
scheduling:
  cron:
    expression: "0 0 8 * * MON"
    description: "Lancement automatique tous les lundis à 8h00"
```
**Utilisation** : Rapport hebdomadaire

## 🔧 Comment Modifier la Configuration

### Option 1 : Modifier le fichier de configuration
Éditez `src/main/resources/application-scheduler.yml` :
```yaml
scheduling:
  cron:
    expression: "0 */5 * * * *"  # Toutes les 5 minutes
    description: "Lancement automatique toutes les 5 minutes"
```

### Option 2 : Variable d'environnement
Dans `docker-compose.yml` :
```yaml
environment:
  SCHEDULING_CRON_EXPRESSION: "0 */5 * * * *"
```

### Option 3 : Profil Spring Boot
Créer `src/main/resources/application-production.yml` :
```yaml
scheduling:
  cron:
    expression: "0 0 9 * * MON-FRI"  # Du lundi au vendredi à 9h00
```

## 📊 Impact sur les Performances

### Fréquence élevée (toutes les minutes)
- ✅ Surveillance en temps réel
- ✅ Détection rapide des problèmes
- ❌ Charge serveur élevée
- ❌ Nombreux emails envoyés

### Fréquence modérée (toutes les heures)
- ✅ Équilibre performance/surveillance
- ✅ Charge serveur acceptable
- ✅ Emails raisonnables
- ⚠️ Délai de détection des problèmes

### Fréquence faible (quotidienne)
- ✅ Charge serveur minimale
- ✅ Emails limités
- ❌ Délai de détection important
- ❌ Surveillance limitée

## 🎯 Recommandations par Environnement

### Développement
```yaml
expression: "0 * * * * *"  # Toutes les minutes
```

### Tests
```yaml
expression: "0 */5 * * * *"  # Toutes les 5 minutes
```

### Production Standard
```yaml
expression: "0 0 */2 * * *"  # Toutes les 2 heures
```

### Production Critique
```yaml
expression: "0 0 * * * *"  # Toutes les heures
```

### Production avec Contraintes
```yaml
expression: "0 0 9 * * MON-FRI"  # Du lundi au vendredi à 9h00
```

## 🔄 Redémarrage après Modification

### Avec Docker Compose
```bash
# Redémarrer l'application
docker-compose restart batch-app

# Ou reconstruire complètement
docker-compose down
docker-compose up -d --build
```

### Avec Service Systemd
```bash
# Redémarrer le service
sudo systemctl restart batch-processing

# Vérifier le statut
sudo systemctl status batch-processing
```

### Avec Maven
```bash
# Arrêter l'application (Ctrl+C)
# Puis relancer
mvn spring-boot:run -Dspring-boot.run.profiles=scheduler
```

## 📝 Exemples d'Expressions Cron Avancées

### Toutes les 30 minutes en heures ouvrables
```yaml
expression: "0 */30 9-17 * * MON-FRI"
```

### Toutes les heures sauf la nuit
```yaml
expression: "0 0 8-18 * * *"
```

### Le 15 et le 30 de chaque mois
```yaml
expression: "0 0 9 15,30 * *"
```

### Tous les jours à 8h00, 12h00 et 17h00
```yaml
expression: "0 0 8,12,17 * * *"
```

## ⚠️ Points d'Attention

### 1. Paramètres uniques
Spring Batch nécessite des paramètres uniques pour chaque exécution. Le scheduler génère automatiquement :
- Timestamp unique
- Date d'exécution unique

### 2. Gestion des erreurs
Le scheduler gère automatiquement :
- Jobs déjà en cours d'exécution
- Erreurs de paramètres
- Erreurs de base de données
- Erreurs d'envoi d'email

### 3. Logs
Toutes les exécutions sont loggées avec :
- Horodatage
- Statut de l'exécution
- Durée de traitement
- Erreurs éventuelles

### 4. Monitoring
Utilisez les endpoints API pour surveiller :
```bash
# Informations du scheduler
curl http://localhost:8080/api/batch/scheduler/info

# Health check
curl http://localhost:8080/api/batch/health
```

---

**💡 Conseil** : Commencez avec une fréquence élevée pour les tests, puis ajustez selon vos besoins de production. 