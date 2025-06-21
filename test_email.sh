#!/bin/bash

set -e

echo "=== Test Email Batch CCSS ==="
echo ""

# Demander les identifiants Gmail
echo "📧 Configuration Email Gmail"
echo "================================"
echo ""

read -p "Entrez votre email Gmail: " GMAIL_USER
if [ -z "$GMAIL_USER" ]; then
    echo "❌ Email requis pour continuer"
    exit 1
fi

echo ""
echo "⚠️  IMPORTANT: Utilisez un mot de passe d'application Gmail"
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
echo "📬 Destinataire: kenzopharell@gmail.com"
echo ""

# Compilation
echo "🔨 Compilation du projet..."
mvn clean compile
echo "✅ Compilation réussie"

echo ""
echo "🚀 Lancement du batch avec email..."
echo ""

# Lancement avec variables d'environnement
MAIL_USERNAME="$GMAIL_USER" \
MAIL_PASSWORD="$GMAIL_PASS" \
EMAIL_ENABLED=true \
mvn spring-boot:run -Dspring-boot.run.profiles=prod -Dspring-boot.run.arguments="--server.port=8081" 