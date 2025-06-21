# 📧 Guide de Configuration Email - Gmail

## 🎯 Objectif

Configurer l'envoi automatique des rapports CCSS par email à la fin du traitement du batch.

## ⚠️ Important : Sécurité Gmail

**NE JAMAIS utiliser votre mot de passe principal Gmail !** 
Utilisez un **mot de passe d'application** spécifiquement créé pour cette application.

## 🔧 Configuration étape par étape

### 1. Activer l'authentification à 2 facteurs

1. Allez sur [myaccount.google.com](https://myaccount.google.com)
2. Cliquez sur **Sécurité**
3. Activez **"Connexion à Google"** > **"Validation en 2 étapes"**

### 2. Créer un mot de passe d'application

1. Dans **Sécurité** > **Connexion à Google**
2. Cliquez sur **"Mots de passe d'application"**
3. Sélectionnez **"Mail"** comme application
4. Cliquez sur **"Générer"**
5. **Copiez le mot de passe généré** (16 caractères)

### 3. Configurer le fichier config.env

Modifiez le fichier `config.env` avec vos informations :

```bash
# Configuration Email Gmail
MAIL_USERNAME=votre-email@gmail.com
MAIL_PASSWORD=votre-mot-de-passe-d-application-16-caracteres

# Configuration du destinataire
EMAIL_TO=kenzopharell@gmail.com
```

### 4. Tester la configuration

```bash
# Lancer l'application
./run_batch.sh

# Déclencher le batch
curl -X POST http://localhost:8080/api/batch/teletravail/declencher
```

## 📧 Format de l'email reçu

### Sujet
```
Rapport Télétravail CCSS - [Mois] [Année]
```

### Contenu
- **Statistiques globales** : Nombre de collaborateurs, conformité
- **Liste des non-conformes** : Détails des collaborateurs hors limites
- **Pièces jointes** :
  - `rapport_teletravail_ccss_YYYY-MM.csv` : Rapport détaillé
  - `synthese_teletravail_ccss_YYYY-MM.csv` : Synthèse

### Exemple d'email
```
📊 Rapport Télétravail CCSS

Période de calcul : Janvier 2024
Date de génération : 21/06/2024 10:30

📈 Statistiques Globales
- Total collaborateurs : 150
- Conformes CCSS : 120 (80.0%)
- Non conformes : 30 (20.0%)

⚠️ Collaborateurs Non Conformes
- Dupont Jean (ABC) : 15.2% - Pourcentage trop faible
- Martin Marie (DEF) : 52.1% - Pourcentage trop élevé

📎 Pièces Jointes
- rapport_teletravail_ccss_2024-01.csv
- synthese_teletravail_ccss_2024-01.csv
```

## 🔍 Dépannage

### Erreur "Authentication failed"
- Vérifiez que vous utilisez un mot de passe d'application
- Vérifiez que l'authentification à 2 facteurs est activée
- Vérifiez que le mot de passe est correctement copié

### Erreur "Username and Password not accepted"
- Vérifiez que `MAIL_USERNAME` est votre adresse Gmail complète
- Vérifiez que `MAIL_PASSWORD` est le mot de passe d'application (16 caractères)

### Erreur "Connection timeout"
- Vérifiez votre connexion internet
- Vérifiez que Gmail n'est pas bloqué par votre pare-feu

### Email non reçu
- Vérifiez vos spams
- Vérifiez que `EMAIL_TO` est correct
- Vérifiez les logs de l'application

## 🛡️ Sécurité

### Bonnes pratiques
- ✅ Utiliser un mot de passe d'application
- ✅ Ne jamais commiter les mots de passe dans Git
- ✅ Utiliser des variables d'environnement
- ✅ Limiter l'accès aux comptes Gmail

### À éviter
- ❌ Utiliser votre mot de passe principal
- ❌ Partager les mots de passe d'application
- ❌ Stocker les mots de passe en clair dans le code

## 🔄 Configuration automatique

Le batch enverra automatiquement un email :
1. **À la fin de chaque traitement**
2. **Avec les rapports en pièces jointes**
3. **Avec un résumé des statistiques**
4. **Avec la liste des non-conformes**

## 📞 Support

En cas de problème :
1. Vérifiez les logs de l'application
2. Testez la configuration avec un email simple
3. Vérifiez les paramètres Gmail
4. Consultez ce guide de dépannage 

# Guide Configuration Email Gmail

## 📧 Configuration des identifiants Gmail

Pour que le batch CCSS puisse envoyer les rapports par email, vous devez configurer un mot de passe d'application Gmail.

### 🔐 Étape 1 : Créer un mot de passe d'application

1. **Allez sur votre compte Google** : https://myaccount.google.com/
2. **Cliquez sur "Sécurité"** dans le menu de gauche
3. **Trouvez "Connexion à Google"** et cliquez dessus
4. **Cliquez sur "Mots de passe d'application"**
5. **Sélectionnez "Mail"** dans le menu déroulant
6. **Cliquez sur "Générer"**
7. **Copiez le mot de passe de 16 caractères** généré

### 🚀 Étape 2 : Lancer le batch avec email

Exécutez le script interactif :

```bash
./run_batch_with_email.sh
```

Le script vous demandera :
- Votre email Gmail
- Le mot de passe d'application généré

### 📋 Exemple d'utilisation

```bash
$ ./run_batch_with_email.sh

=== Configuration Email pour le Batch CCSS ===

📧 Configuration Email Gmail
================================

Entrez votre email Gmail: votre-email@gmail.com

⚠️  IMPORTANT: Utilisez un mot de passe d'application Gmail, pas votre mot de passe principal
Pour créer un mot de passe d'application:
1. Allez sur https://myaccount.google.com/
2. Sécurité > Connexion à Google > Mots de passe d'application
3. Générez un mot de passe pour 'Mail'

Entrez votre mot de passe d'application Gmail: [mot de passe masqué]

✅ Configuration email validée
📧 Email: votre-email@gmail.com
📬 Destinataire: kenzopharell@gmail.com

🔍 Vérification de la base de données...
✅ Connexion à la base de données réussie

🔨 Compilation du projet...
✅ Compilation réussie

🚀 Lancement du batch avec envoi d'email...
```

### 📬 Contenu de l'email envoyé

L'email contiendra :
- **Sujet** : "Rapport Télétravail CCSS - [MOIS]"
- **Pièces jointes** :
  - `rapport_teletravail_ccss.csv` : Rapport détaillé par collaborateur
  - `synthese_ccss_rapport_teletravail_ccss.csv` : Synthèse globale
- **Corps du message** : Statistiques et résumé des résultats

### 🔒 Sécurité

- ✅ Le mot de passe d'application est temporaire et sécurisé
- ✅ Les identifiants ne sont pas sauvegardés sur le disque
- ✅ L'email est envoyé uniquement à l'adresse configurée

### 🛠️ Dépannage

**Erreur "Invalid credentials"** :
- Vérifiez que vous utilisez bien un mot de passe d'application
- Assurez-vous que l'authentification à 2 facteurs est activée

**Erreur "Access denied"** :
- Vérifiez que l'email Gmail est correct
- Assurez-vous que les "applications moins sécurisées" sont autorisées

**Pas d'email reçu** :
- Vérifiez les spams
- Vérifiez l'adresse de destination dans `config.env` 