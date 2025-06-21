#!/bin/bash

set -e

echo "=== Configuration Email pour le Batch CCSS ==="
echo ""

# Chargement de la configuration de base
if [ -f config.env ]; then
  source config.env
fi

# Demander les identifiants Gmail
echo "📧 Configuration Email Gmail"
echo "================================"
echo ""

# Demander l'email
read -p "Entrez votre email Gmail: " GMAIL_USER
if [ -z "$GMAIL_USER" ]; then
    echo "❌ Email requis pour continuer"
    exit 1
fi

# Demander le mot de passe d'application
echo ""
echo "⚠️  IMPORTANT: Utilisez un mot de passe d'application Gmail, pas votre mot de passe principal"
echo "Pour créer un mot de passe d'application:"
echo "1. Allez sur https://myaccount.google.com/"
echo "2. Sécurité > Connexion à Google > Mots de passe d'application"
echo "3. Générez un mot de passe pour 'Mail'"
echo ""
read -s -p "Entrez votre mot de passe d'application Gmail: " GMAIL_PASS
echo ""

if [ -z "$GMAIL_PASS" ]; then
    echo "❌ Mot de passe requis pour continuer"
    exit 1
fi

echo ""
echo "✅ Configuration email validée"
echo "📧 Email: $GMAIL_USER"
echo "📬 Destinataire: $EMAIL_TO"
echo ""

# Vérification de la base de données
echo "🔍 Vérification de la base de données..."
if mysql -u root -proot -e "USE rh; SELECT COUNT(*) as nb_collaborateurs FROM collab;" > /dev/null 2>&1; then
    echo "✅ Connexion à la base de données réussie"
else
    echo "❌ Erreur de connexion à la base de données"
    exit 1
fi

# Compilation du projet
echo ""
echo "🔨 Compilation du projet..."
mvn clean compile
echo "✅ Compilation réussie"

echo ""
echo "🚀 Lancement du batch avec envoi d'email..."
echo "Profile: prod"
echo "Base de données: $DB_NAME sur $DB_HOST:$DB_PORT"
echo ""

# Lancement du batch avec les variables d'environnement email
MAIL_USERNAME="$GMAIL_USER" \
MAIL_PASSWORD="$GMAIL_PASS" \
EMAIL_ENABLED=true \
mvn spring-boot:run -Dspring-boot.run.profiles=prod 