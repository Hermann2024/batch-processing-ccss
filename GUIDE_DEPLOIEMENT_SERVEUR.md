# Guide de Déploiement sur Serveur - Batch Processing CCSS

## 🎯 Objectif
Déployer l'application Batch Processing CCSS sur un serveur avec lancement automatique toutes les minutes et envoi automatique des rapports par email.

## 📋 Prérequis Serveur

### Système d'exploitation
- Ubuntu 20.04+ / CentOS 8+ / Debian 11+
- 2GB RAM minimum (4GB recommandé)
- 10GB espace disque minimum
- Accès root ou sudo

### Logiciels requis
- Docker
- Docker Compose
- Java 17 (pour la compilation)
- Maven (pour la compilation)

## 🚀 Déploiement Rapide

### 1. Préparation du serveur
```bash
# Mettre à jour le système
sudo apt update && sudo apt upgrade -y

# Installer les dépendances
sudo apt install -y curl wget git openjdk-17-jdk maven
```

### 2. Cloner et configurer l'application
```bash
# Cloner le projet (remplacez par votre repo)
git clone <votre-repo> batch-processing
cd batch-processing

# Rendre les scripts exécutables
chmod +x deploy-server.sh install-service.sh
```

### 3. Configuration de l'email
Éditez le fichier `docker-compose.yml` et remplacez :
- `nanahermann@outlook.com` par votre email
- `${EMAIL_PASSWORD}` par votre mot de passe d'application

### 4. Déploiement automatique
```bash
# Lancer le déploiement
./deploy-server.sh

# Ou avec le mot de passe en paramètre
./deploy-server.sh "votre_mot_de_passe"
```

### 5. Installation du service systemd (optionnel)
```bash
# Installer le service pour démarrage automatique au boot
./install-service.sh
```

## 🔧 Configuration Avancée

### Modifier la fréquence du batch
Éditez `src/main/resources/application-scheduler.yml` :

```yaml
scheduling:
  cron:
    expression: "0 */5 * * * *"  # Toutes les 5 minutes
    # expression: "0 0 * * * *"  # Toutes les heures
    # expression: "0 0 9 * * MON-FRI"  # Du lundi au vendredi à 9h00
```

### Variables d'environnement
```bash
# Dans docker-compose.yml ou .env
SPRING_PROFILES_ACTIVE=scheduler
EMAIL_PASSWORD=votre_mot_de_passe
SPRING_MAIL_USERNAME=votre_email@outlook.com
```

## 📊 Monitoring et Logs

### Vérifier le statut
```bash
# Statut des conteneurs
docker-compose ps

# Logs de l'application
docker-compose logs -f batch-app

# Logs du scheduler
tail -f logs/batch-scheduler.log
```

### Endpoints de monitoring
- **Health Check**: `http://votre-serveur:8080/api/batch/health`
- **Scheduler Info**: `http://votre-serveur:8080/api/batch/scheduler/info`
- **Lancement manuel**: `POST http://votre-serveur:8080/api/batch/teletravail-ccss`

### Logs systemd (si service installé)
```bash
# Voir les logs du service
sudo journalctl -u batch-processing -f

# Statut du service
sudo systemctl status batch-processing
```

## 🔄 Gestion du Service

### Commandes Docker Compose
```bash
# Démarrer
docker-compose up -d

# Arrêter
docker-compose down

# Redémarrer
docker-compose restart

# Voir les logs
docker-compose logs -f
```

### Commandes systemd (si installé)
```bash
# Démarrer le service
sudo systemctl start batch-processing

# Arrêter le service
sudo systemctl stop batch-processing

# Redémarrer le service
sudo systemctl restart batch-processing

# Statut du service
sudo systemctl status batch-processing
```

## 📧 Configuration Email

### Gmail
```yaml
SPRING_MAIL_HOST: smtp.gmail.com
SPRING_MAIL_PORT: 587
SPRING_MAIL_USERNAME: votre_email@gmail.com
SPRING_MAIL_PASSWORD: votre_mot_de_passe_app
```

### Outlook/Live
```yaml
SPRING_MAIL_HOST: smtp-mail.outlook.com
SPRING_MAIL_PORT: 587
SPRING_MAIL_USERNAME: votre_email@outlook.com
SPRING_MAIL_PASSWORD: votre_mot_de_passe_app
```

### Créer un mot de passe d'application
1. Allez dans les paramètres de votre compte email
2. Activez l'authentification à 2 facteurs
3. Générez un mot de passe d'application
4. Utilisez ce mot de passe dans la configuration

## 🛠️ Dépannage

### Problèmes courants

#### Port déjà utilisé
```bash
# Vérifier les ports utilisés
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
# Vérifier les logs du scheduler
docker-compose logs batch-app | grep -i scheduler

# Vérifier la configuration cron
curl http://localhost:8080/api/batch/scheduler/info
```

## 📈 Performance et Optimisation

### Ressources recommandées
- **CPU**: 2 cores minimum
- **RAM**: 4GB minimum
- **Disque**: 20GB minimum
- **Réseau**: Connexion stable

### Optimisation JVM
```bash
# Dans docker-compose.yml
JAVA_OPTS: "-Xmx2g -Xms1g -XX:+UseG1GC"
```

### Sauvegarde des données
```bash
# Sauvegarder la base de données
docker-compose exec mariadb mysqldump -u root -proot rh > backup_$(date +%Y%m%d).sql

# Sauvegarder les rapports
tar -czf rapports_$(date +%Y%m%d).tar.gz output/
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

### Mise à jour régulière
```bash
# Mettre à jour les images Docker
docker-compose pull
docker-compose up -d

# Mettre à jour le système
sudo apt update && sudo apt upgrade -y
```

## 📞 Support

En cas de problème :
1. Vérifiez les logs : `docker-compose logs -f`
2. Testez la santé : `curl http://localhost:8080/api/batch/health`
3. Vérifiez la configuration email
4. Consultez ce guide de dépannage

---

**✅ Votre batch se lance maintenant automatiquement toutes les minutes et envoie les rapports par email !** 