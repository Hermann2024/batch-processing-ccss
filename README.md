# Batch Processing CCSS - Télétravail

Application Spring Boot pour le traitement automatique des données de télétravail selon les exigences CCSS.

## 🚀 Fonctionnalités

- ✅ **Traitement automatique** des données de télétravail
- ✅ **Génération de rapports CSV** détaillés
- ✅ **Envoi automatique par email** des rapports
- ✅ **Scheduling automatique** toutes les minutes
- ✅ **API REST** pour lancement manuel
- ✅ **Monitoring et logs** complets
- ✅ **Déploiement Docker** prêt pour serveur
- ✅ **Service systemd** pour démarrage automatique

## 📋 Prérequis

- Java 17+
- Maven 3.6+
- MariaDB/MySQL
- Compte email (Gmail/Outlook) avec mot de passe d'application

## 🛠️ Installation Locale

### 1. Configuration de la base de données
```bash
# Créer la base de données
mysql -u root -p
CREATE DATABASE rh;
USE rh;
```

### 2. Configuration de l'email
Éditez `src/main/resources/application.yml` :
```yaml
spring:
  mail:
    host: smtp-mail.outlook.com
    port: 587
    username: votre_email@outlook.com
    password: votre_mot_de_passe_app
```

### 3. Compilation et lancement
```bash
# Compiler
mvn clean compile

# Lancer avec scheduling automatique
mvn spring-boot:run -Dspring-boot.run.profiles=scheduler
```

## 🔄 Scheduling Automatique

### Configuration
Le batch se lance automatiquement toutes les minutes par défaut.

**Modifier la fréquence** dans `src/main/resources/application-scheduler.yml` :
```yaml
scheduling:
  cron:
    expression: "0 */5 * * * *"  # Toutes les 5 minutes
    # expression: "0 0 * * * *"  # Toutes les heures
    # expression: "0 0 9 * * MON-FRI"  # Du lundi au vendredi à 9h00
```

### Test du scheduling
```bash
# Tester le scheduling automatique
./test-scheduler.sh
```

## 🐳 Déploiement sur Serveur

### Déploiement rapide
```bash
# Script de déploiement automatique
./deploy-server.sh "votre_mot_de_passe_email"

# Installer le service systemd (démarrage au boot)
./install-service.sh
```

### Configuration Docker
```bash
# Variables d'environnement
export EMAIL_PASSWORD="votre_mot_de_passe"

# Démarrage
docker-compose up -d

# Logs
docker-compose logs -f batch-app
```

## 📊 API REST

### Endpoints disponibles

#### Health Check
```bash
GET http://localhost:8080/api/batch/health
```

#### Informations du Scheduler
```bash
GET http://localhost:8080/api/batch/scheduler/info
```

#### Lancement manuel du batch
```bash
POST http://localhost:8080/api/batch/teletravail-ccss
```

#### Lancement via scheduler
```bash
POST http://localhost:8080/api/batch/teletravail-ccss/scheduler
```

## 📧 Configuration Email

### Gmail
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: votre_email@gmail.com
    password: votre_mot_de_passe_app
```

### Outlook/Live
```yaml
spring:
  mail:
    host: smtp-mail.outlook.com
    port: 587
    username: votre_email@outlook.com
    password: votre_mot_de_passe_app
```

### Créer un mot de passe d'application
1. Allez dans les paramètres de votre compte email
2. Activez l'authentification à 2 facteurs
3. Générez un mot de passe d'application
4. Utilisez ce mot de passe dans la configuration

## 📁 Structure des fichiers

```
Batch-processing/
├── src/main/java/com/intech/batch/
│   ├── BatchProcessingApplication.java
│   ├── config/
│   │   └── BatchConfig.java
│   ├── controller/
│   │   └── BatchController.java
│   ├── service/
│   │   ├── BatchSchedulerService.java    # Nouveau : Scheduling automatique
│   │   ├── CalculTeletravailService.java
│   │   ├── CsvExportService.java
│   │   └── EmailService.java
│   └── ...
├── output/                               # Rapports générés
├── logs/                                 # Logs de l'application
├── docker-compose.yml                    # Configuration Docker
├── Dockerfile                           # Image Docker
├── deploy-server.sh                     # Script de déploiement
├── install-service.sh                   # Installation service systemd
└── test-scheduler.sh                    # Test du scheduling
```

## 🔧 Scripts Utiles

### Scripts de déploiement
- `deploy-server.sh` : Déploiement automatique sur serveur
- `install-service.sh` : Installation du service systemd
- `test-scheduler.sh` : Test du scheduling automatique

### Scripts de batch
- `run_batch.sh` : Lancement simple du batch
- `run_batch_with_email.sh` : Lancement avec envoi email
- `run_batch_automatique.sh` : Lancement automatique

## 📊 Monitoring

### Logs
```bash
# Logs de l'application
tail -f logs/app.log

# Logs Docker
docker-compose logs -f batch-app

# Logs systemd (si installé)
sudo journalctl -u batch-processing -f
```

### Statut
```bash
# Statut des conteneurs
docker-compose ps

# Statut du service
sudo systemctl status batch-processing

# Health check
curl http://localhost:8080/api/batch/health
```

## 🛠️ Dépannage

### Problèmes courants

#### Port déjà utilisé
```bash
# Vérifier les ports
sudo netstat -tulpn | grep :8080

# Tuer le processus
sudo kill -9 <PID>
```

#### Email non envoyé
```bash
# Vérifier les logs
docker-compose logs batch-app | grep -i mail

# Tester la configuration
curl -X POST http://localhost:8080/api/batch/teletravail-ccss
```

#### Batch ne se lance pas automatiquement
```bash
# Vérifier le scheduler
curl http://localhost:8080/api/batch/scheduler/info

# Vérifier les logs
docker-compose logs batch-app | grep -i scheduler
```

## 📈 Performance

### Ressources recommandées
- **CPU** : 2 cores minimum
- **RAM** : 4GB minimum
- **Disque** : 20GB minimum
- **Réseau** : Connexion stable

### Optimisation
```bash
# JVM optimisée
JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"
```

## 🔒 Sécurité

### Firewall
```bash
# Ouvrir uniquement les ports nécessaires
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP (si nginx)
sudo ufw allow 8080  # Application
sudo ufw enable
```

### Variables d'environnement
```bash
# Créer un fichier .env sécurisé
echo "EMAIL_PASSWORD=votre_mot_de_passe_securise" > .env
chmod 600 .env
```

## 📞 Support

En cas de problème :
1. Vérifiez les logs : `docker-compose logs -f`
2. Testez la santé : `curl http://localhost:8080/api/batch/health`
3. Vérifiez la configuration email
4. Consultez le guide de déploiement : `GUIDE_DEPLOIEMENT_SERVEUR.md`

---

**✅ Votre batch se lance maintenant automatiquement toutes les minutes et envoie les rapports par email !**

## 📝 Changelog

### Version 2.0.0
- ✅ Ajout du scheduling automatique toutes les minutes
- ✅ Service systemd pour démarrage au boot
- ✅ Déploiement Docker complet
- ✅ API REST pour monitoring
- ✅ Scripts de déploiement automatique
- ✅ Guide de déploiement serveur complet

### Version 1.0.0
- ✅ Traitement des données de télétravail
- ✅ Génération de rapports CSV
- ✅ Envoi automatique par email
- ✅ API REST pour lancement manuel 