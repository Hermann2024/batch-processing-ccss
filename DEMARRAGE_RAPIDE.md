# 🚀 Démarrage Rapide - Scheduling Automatique

## 🎯 Objectif
Lancer rapidement le batch avec scheduling automatique toutes les minutes et envoi automatique des rapports par email.

## ⚡ Démarrage en 3 Étapes

### Étape 1 : Configuration Email
Éditez `docker-compose.yml` et remplacez :
```yaml
SPRING_MAIL_USERNAME: votre_email@outlook.com
SPRING_MAIL_PASSWORD: ${EMAIL_PASSWORD}
```

### Étape 2 : Déploiement Automatique
```bash
# Lancer le déploiement (le script vous demandera le mot de passe email)
./deploy-server.sh

# Ou avec le mot de passe en paramètre
./deploy-server.sh "votre_mot_de_passe_email"
```

### Étape 3 : Vérification
```bash
# Vérifier que tout fonctionne
curl http://localhost:8080/api/batch/health
curl http://localhost:8080/api/batch/scheduler/info

# Voir les logs en temps réel
docker-compose logs -f batch-app
```

## ✅ C'est tout !

Votre batch se lance maintenant **automatiquement toutes les minutes** et envoie les rapports par email.

## 📊 Monitoring Rapide

### Vérifier le statut
```bash
# Statut des conteneurs
docker-compose ps

# Logs en temps réel
docker-compose logs -f batch-app

# Health check
curl http://localhost:8080/api/batch/health
```

### Informations du scheduler
```bash
# Configuration du cron
curl http://localhost:8080/api/batch/scheduler/info

# Lancement manuel pour test
curl -X POST http://localhost:8080/api/batch/teletravail-ccss
```

## 🔧 Commandes Utiles

### Gestion de l'application
```bash
# Démarrer
docker-compose up -d

# Arrêter
docker-compose down

# Redémarrer
docker-compose restart batch-app

# Logs
docker-compose logs -f batch-app
```

### Service systemd (optionnel)
```bash
# Installer le service pour démarrage au boot
./install-service.sh

# Gérer le service
sudo systemctl start batch-processing
sudo systemctl stop batch-processing
sudo systemctl status batch-processing
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

## 🔄 Modifier la Fréquence

### Toutes les 5 minutes
Éditez `src/main/resources/application-scheduler.yml` :
```yaml
scheduling:
  cron:
    expression: "0 */5 * * * *"
```

### Toutes les heures
```yaml
scheduling:
  cron:
    expression: "0 0 * * * *"
```

### Du lundi au vendredi à 9h00
```yaml
scheduling:
  cron:
    expression: "0 0 9 * * MON-FRI"
```

### Redémarrer après modification
```bash
docker-compose restart batch-app
```

## 🛠️ Dépannage Rapide

### Port déjà utilisé
```bash
# Tuer les processus sur le port 8080
sudo kill -9 $(sudo lsof -t -i:8080)
```

### Email non envoyé
```bash
# Vérifier la configuration
docker-compose logs batch-app | grep -i mail

# Tester manuellement
curl -X POST http://localhost:8080/api/batch/teletravail-ccss
```

### Batch ne se lance pas automatiquement
```bash
# Vérifier le scheduler
curl http://localhost:8080/api/batch/scheduler/info

# Vérifier les logs
docker-compose logs batch-app | grep -i scheduler
```

## 📁 Fichiers Importants

### Scripts principaux
- `deploy-server.sh` : Déploiement automatique
- `install-service.sh` : Installation service systemd
- `test-scheduler.sh` : Test du scheduling

### Configuration
- `docker-compose.yml` : Configuration Docker
- `src/main/resources/application-scheduler.yml` : Configuration cron
- `Dockerfile` : Image Docker

### Documentation
- `README.md` : Documentation complète
- `GUIDE_DEPLOIEMENT_SERVEUR.md` : Guide détaillé
- `cron-configurations.md` : Options de scheduling

## 🎉 Résultat

✅ **Batch automatique toutes les minutes**  
✅ **Emails automatiques avec rapports**  
✅ **Monitoring via API REST**  
✅ **Logs détaillés**  
✅ **Redémarrage automatique au boot**  

---

**🚀 Votre application est maintenant opérationnelle avec un scheduling automatique toutes les minutes !** 