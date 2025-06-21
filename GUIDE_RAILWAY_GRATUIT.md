# 🚀 Guide Déploiement Gratuit sur Railway

## 🎯 Objectif
Déployer votre batch de télétravail CCSS gratuitement sur Railway avec base de données PostgreSQL incluse.

## 📋 Prérequis

### Compte Railway
1. Aller sur [railway.app](https://railway.app)
2. Créer un compte gratuit
3. Installer Railway CLI : `npm install -g @railway/cli`

### Prérequis système
- Node.js (pour Railway CLI)
- Git
- Maven

## 🚀 Déploiement en 5 étapes

### Étape 1 : Préparer l'application
```bash
# Compiler l'application
mvn clean package -DskipTests

# Vérifier que le JAR existe
ls target/batch-processing-*.jar
```

### Étape 2 : Connexion Railway
```bash
# Se connecter à Railway
railway login

# Créer un nouveau projet (si nécessaire)
railway init
```

### Étape 3 : Ajouter une base de données
```bash
# Dans l'interface Railway ou via CLI
railway add

# Choisir "PostgreSQL"
# Railway créera automatiquement les variables d'environnement
```

### Étape 4 : Configurer les variables d'environnement
Dans l'interface Railway, ajouter :

```bash
# Email (obligatoire)
SPRING_MAIL_USERNAME=votre_email@outlook.com
SPRING_MAIL_PASSWORD=votre_mot_de_passe_app
SPRING_MAIL_HOST=smtp-mail.outlook.com
SPRING_MAIL_PORT=587

# Destinataires des rapports
EMAIL_RECIPIENTS=admin@votreentreprise.com,rh@votreentreprise.com

# Base de données (automatique avec Railway)
DATABASE_URL=postgresql://...
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
```

### Étape 5 : Déployer
```bash
# Déploiement automatique
./deploy-railway.sh

# Ou manuellement
railway up
```

## 🔧 Configuration de la base de données

### Migration depuis MariaDB vers PostgreSQL
Railway utilise PostgreSQL. Vous devrez :

1. **Exporter vos données MariaDB** :
```bash
mysqldump -u root -proot rh > backup_mariadb.sql
```

2. **Convertir pour PostgreSQL** :
```sql
-- Créer les tables dans PostgreSQL
CREATE TABLE collab (
    "idColl" SERIAL PRIMARY KEY,
    "Trigramme" CHAR(3),
    "Nom" VARCHAR(255),
    "Prenom" VARCHAR(255),
    "TpsPart" DECIMAL(3,2)
);

CREATE TABLE conge (
    "idConge" SERIAL PRIMARY KEY,
    "Collaborateur_idColl" INTEGER REFERENCES collab("idColl"),
    "TypeCong_idTypeCong" INTEGER,
    "DateConge" DATE,
    "AnoConge" INTEGER
);

CREATE TABLE teletravail (
    "idteletravail" SERIAL PRIMARY KEY,
    "Collaborateur_idColl" INTEGER REFERENCES collab("idColl"),
    "DateTeletravail" DATE,
    "TypeTeletravail" VARCHAR(50)
);
```

3. **Importer les données** :
```bash
# Via l'interface Railway ou psql
psql $DATABASE_URL < data_postgresql.sql
```

## 📊 Monitoring et Logs

### Vérifier le statut
```bash
# Statut du déploiement
railway status

# Logs en temps réel
railway logs

# Ouvrir l'application
railway open
```

### Endpoints disponibles
- **Health Check** : `https://votre-app.railway.app/api/batch/health`
- **Lancement manuel** : `POST https://votre-app.railway.app/api/batch/teletravail-ccss`
- **Info scheduler** : `https://votre-app.railway.app/api/batch/scheduler/info`

## ⚠️ Limitations du plan gratuit

### Railway Free Plan
- **500h/mois** (environ 20 jours)
- **512MB RAM**
- **1GB stockage**
- **Inactif après 5 minutes** (redémarrage automatique)

### Optimisations recommandées
1. **Réduire la fréquence** : Toutes les 5 minutes au lieu de 1 minute
2. **Limiter les logs** : Niveau INFO uniquement
3. **Nettoyer les fichiers** : Supprimer les anciens rapports

## 🔄 Mise à jour

### Déploiement automatique
```bash
# Railway se connecte automatiquement à votre repo GitHub
# Chaque push déclenche un nouveau déploiement
git push origin main
```

### Déploiement manuel
```bash
# Recompiler et redéployer
mvn clean package -DskipTests
railway up
```

## 🛠️ Dépannage

### Problèmes courants

#### Application ne démarre pas
```bash
# Vérifier les logs
railway logs

# Vérifier les variables d'environnement
railway variables
```

#### Base de données non accessible
```bash
# Vérifier la connexion
railway connect

# Tester la base
psql $DATABASE_URL -c "SELECT 1;"
```

#### Email non envoyé
```bash
# Vérifier la configuration SMTP
railway logs | grep -i mail

# Tester manuellement
curl -X POST https://votre-app.railway.app/api/batch/teletravail-ccss
```

## 💰 Coût

### Plan gratuit Railway
- **0€/mois** pour 500h
- **Base de données PostgreSQL incluse**
- **SSL automatique**
- **CDN global**

### Si vous dépassez 500h
- **5$/mois** pour 1000h supplémentaires
- **Ou passer sur un autre hébergeur gratuit**

## 🎉 Avantages du déploiement Railway

✅ **100% gratuit** pour 500h/mois  
✅ **Base de données PostgreSQL incluse**  
✅ **Déploiement automatique depuis GitHub**  
✅ **SSL/HTTPS automatique**  
✅ **Monitoring intégré**  
✅ **Logs en temps réel**  
✅ **Redémarrage automatique**  
✅ **CDN global**  

## 🚀 Prêt à déployer ?

Votre batch est maintenant **100% prêt** pour un déploiement gratuit sur Railway !

**Prochaines étapes :**
1. Créer un compte Railway
2. Exécuter `./deploy-railway.sh`
3. Configurer les variables d'environnement
4. Tester l'application

**Votre batch sera accessible gratuitement 24h/24 !** 🎉 