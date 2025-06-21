#!/bin/bash

echo "=== Batch CCSS - Exécution Simple ==="
echo ""

# Configuration email préconfigurée
export MAIL_USERNAME="kenzopharell@live.fr"
export EMAIL_TO="kenzopharell@live.fr"
export EMAIL_ENABLED="true"

echo "📧 Configuration Email:"
echo "   De: $MAIL_USERNAME"
echo "   À: $EMAIL_TO"
echo "   Activé: $EMAIL_ENABLED"
echo ""

echo "⚠️  IMPORTANT: Assurez-vous d'avoir configuré un mot de passe d'application Outlook/Live"
echo "   Guide: https://support.microsoft.com/fr-fr/account-billing/utiliser-la-v%C3%A9rification-en-deux-%C3%A9tapes-et-les-mots-de-passe-d-applications-3e7c8604-1a9e-4d0b-89e0-2f7a67b7b3c4"
echo ""

# Demander le mot de passe d'application Outlook/Live
echo -n "Entrez votre mot de passe d'application Outlook/Live: "
read -s MAIL_PASSWORD
echo ""
export MAIL_PASSWORD

echo ""
echo "🚀 Compilation et lancement du batch..."
echo ""

# Compiler le projet
echo "📦 Compilation..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation"
    exit 1
fi

echo "✅ Compilation réussie"
echo ""

# Lancer le batch avec le profil production
echo "🔄 Lancement du batch..."
mvn spring-boot:run -Dspring-boot.run.profiles=prod -q

echo ""
echo "✅ Batch terminé !"
echo "📧 Vérifiez votre boîte mail: $EMAIL_TO" 