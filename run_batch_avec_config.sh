#!/bin/bash

echo "=== Batch CCSS - Exécution avec Configuration ==="
echo ""

# Vérifier si le fichier de configuration existe
if [ ! -f "config_email.env" ]; then
    echo "❌ Fichier config_email.env manquant"
    echo "   Créez-le avec vos paramètres email"
    exit 1
fi

# Charger la configuration
echo "📧 Chargement de la configuration email..."
source config_email.env

# Vérifier si le mot de passe est configuré
if [ "$MAIL_PASSWORD" = "YOUR_APP_PASSWORD" ]; then
    echo "❌ Erreur: Vous devez configurer votre mot de passe d'application Gmail"
    echo "   Éditez le fichier config_email.env et remplacez YOUR_APP_PASSWORD"
    echo "   Guide: https://support.google.com/accounts/answer/185833"
    exit 1
fi

echo "✅ Configuration chargée:"
echo "   De: $MAIL_USERNAME"
echo "   À: $EMAIL_TO"
echo "   Activé: $EMAIL_ENABLED"
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