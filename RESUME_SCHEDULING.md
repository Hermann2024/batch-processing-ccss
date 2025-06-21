# Résumé - Scheduling Automatique et Déploiement Serveur

## 🎯 Objectif Atteint

✅ **Scheduling automatique toutes les minutes** configuré et fonctionnel  
✅ **Déploiement Docker** prêt pour serveur de production  
✅ **Service systemd** pour démarrage automatique au boot  
✅ **API REST** complète pour monitoring  
✅ **Scripts de déploiement** automatisés  
✅ **Documentation complète** pour maintenance  

## 🚀 Fonctionnalités Implémentées

### 1. Scheduling Automatique
- **Service** : `BatchSchedulerService.java`
- **Fréquence** : Toutes les minutes (`0 * * * * *`)
- **Gestion d'erreurs** : Complète avec logging détaillé
- **Paramètres uniques** : Génération automatique pour éviter les conflits

### 2. API REST Étendue
- `GET /api/batch/health` : Health check
- `GET /api/batch/scheduler/info` : Informations du scheduler
- `POST /api/batch/teletravail-ccss` : Lancement manuel
- `POST /api/batch/teletravail-ccss/scheduler` : Lancement via scheduler

### 3. Configuration Docker
- **Dockerfile** : Image Java 17 optimisée
- **docker-compose.yml** : Stack complet avec MariaDB
- **Variables d'environnement** : Configuration flexible
- **Volumes** : Persistance des données et logs

### 4. Service Systemd
- **Fichier** : `batch-processing.service`
- **Démarrage automatique** : Au boot du serveur
- **Gestion** : Start/Stop/Restart/Status
- **Logs** : Intégration avec journalctl

### 5. Scripts de Déploiement
- `deploy-server.sh` : Déploiement automatique complet
- `install-service.sh` : Installation du service systemd
- `test-scheduler.sh` : Test du scheduling
- `test-deployment.sh` : Validation de la configuration

## 📁 Fichiers Créés/Modifiés

### Nouveaux Fichiers
```
src/main/java/com/intech/batch/service/BatchSchedulerService.java
src/main/resources/application-scheduler.yml
Dockerfile
docker-compose.yml
deploy-server.sh
install-service.sh
test-scheduler.sh
test-deployment.sh
batch-processing.service
GUIDE_DEPLOIEMENT_SERVEUR.md
cron-configurations.md
RESUME_SCHEDULING.md
```

### Fichiers Modifiés
```
src/main/java/com/intech/batch/controller/BatchController.java
README.md
```

## 🔧 Configuration Actuelle

### Scheduling
```yaml
# src/main/resources/application-scheduler.yml
scheduling:
  cron:
    expression: "0 * * * * *"  # Toutes les minutes
    description: "Lancement automatique toutes les minutes"
```

### Docker
```yaml
# docker-compose.yml
services:
  batch-app:
    build: .
    environment:
      SPRING_PROFILES_ACTIVE: scheduler
      EMAIL_PASSWORD: ${EMAIL_PASSWORD}
    ports:
      - "8080:8080"
    restart: unless-stopped
```

### Email
```yaml
# Configuration Outlook
SPRING_MAIL_HOST: smtp-mail.outlook.com
SPRING_MAIL_PORT: 587
SPRING_MAIL_USERNAME: nanahermann@outlook.com
SPRING_MAIL_PASSWORD: ${EMAIL_PASSWORD}
```

## 🚀 Déploiement sur Serveur

### Étape 1 : Préparation
```bash
# Cloner le projet sur le serveur
git clone <votre-repo> batch-processing
cd batch-processing

# Rendre les scripts exécutables
chmod +x deploy-server.sh install-service.sh test-scheduler.sh test-deployment.sh
```

### Étape 2 : Configuration
```bash
# Éditer docker-compose.yml avec votre email et mot de passe
# Ou utiliser le script interactif
./deploy-server.sh "votre_mot_de_passe_email"
```

### Étape 3 : Déploiement
```bash
# Déploiement automatique
./deploy-server.sh

# Installation du service systemd (optionnel)
./install-service.sh
```

### Étape 4 : Vérification
```bash
# Test de santé
curl http://localhost:8080/api/batch/health

# Informations du scheduler
curl http://localhost:8080/api/batch/scheduler/info

# Logs en temps réel
docker-compose logs -f batch-app
```

## 📊 Monitoring et Maintenance

### Commandes Utiles
```bash
# Statut des conteneurs
docker-compose ps

# Logs de l'application
docker-compose logs -f batch-app

# Redémarrer l'application
docker-compose restart batch-app

# Mettre à jour
docker-compose down
docker-compose up -d --build
```

### Service Systemd
```bash
# Statut du service
sudo systemctl status batch-processing

# Logs du service
sudo journalctl -u batch-processing -f

# Redémarrer le service
sudo systemctl restart batch-processing
```

### API Monitoring
```bash
# Health check
curl http://localhost:8080/api/batch/health

# Informations scheduler
curl http://localhost:8080/api/batch/scheduler/info

# Lancement manuel
curl -X POST http://localhost:8080/api/batch/teletravail-ccss
```

## 🔄 Personnalisation de la Fréquence

### Modifier la fréquence
Éditez `src/main/resources/application-scheduler.yml` :

```yaml
# Toutes les 5 minutes
expression: "0 */5 * * * *"

# Toutes les heures
expression: "0 0 * * * *"

# Du lundi au vendredi à 9h00
expression: "0 0 9 * * MON-FRI"
```

### Redémarrer après modification
```bash
# Avec Docker
docker-compose restart batch-app

# Avec systemd
sudo systemctl restart batch-processing
```

## 📧 Configuration Email

### Créer un mot de passe d'application
1. Allez dans les paramètres de votre compte email
2. Activez l'authentification à 2 facteurs
3. Générez un mot de passe d'application
4. Utilisez ce mot de passe dans la configuration

### Test de l'email
```bash
# Lancer le batch manuellement
curl -X POST http://localhost:8080/api/batch/teletravail-ccss

# Vérifier les logs d'email
docker-compose logs batch-app | grep -i mail
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

#### Base de données non accessible
```bash
# Vérifier le conteneur MariaDB
docker-compose logs mariadb

# Redémarrer la base de données
docker-compose restart mariadb
```

#### Email non envoyé
```bash
# Vérifier les logs d'email
docker-compose logs batch-app | grep -i mail

# Tester la configuration SMTP
curl -X POST http://localhost:8080/api/batch/teletravail-ccss
```

#### Batch ne se lance pas automatiquement
```bash
# Vérifier le scheduler
curl http://localhost:8080/api/batch/scheduler/info

# Vérifier les logs
docker-compose logs batch-app | grep -i scheduler
```

## 📈 Performances

### Ressources recommandées
- **CPU** : 2 cores minimum
- **RAM** : 4GB minimum
- **Disque** : 20GB minimum
- **Réseau** : Connexion stable

### Optimisation JVM
```bash
# Dans docker-compose.yml
JAVA_OPTS: "-Xmx2g -Xms1g -XX:+UseG1GC"
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

### Variables d'environnement sécurisées
```bash
# Créer un fichier .env
echo "EMAIL_PASSWORD=votre_mot_de_passe_securise" > .env
chmod 600 .env
```

## 📝 Logs et Rapports

### Logs automatiques
- **Application** : `logs/app.log`
- **Docker** : `docker-compose logs -f batch-app`
- **Systemd** : `sudo journalctl -u batch-processing -f`

### Rapports générés
- **CSV détaillé** : `output/rapport_teletravail_ccss.csv`
- **CSV synthèse** : `output/synthese_ccss_rapport_teletravail_ccss.csv`
- **Email automatique** : Envoyé à chaque exécution

## 🎉 Résultat Final

✅ **Le batch se lance automatiquement toutes les minutes**  
✅ **Les rapports sont envoyés par email automatiquement**  
✅ **L'application redémarre automatiquement au boot du serveur**  
✅ **Monitoring complet via API REST**  
✅ **Logs détaillés pour le suivi**  
✅ **Configuration flexible pour différents environnements**  

---

**🚀 Votre application est maintenant prête pour la production avec un scheduling automatique toutes les minutes !** 