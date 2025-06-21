#!/bin/bash

echo "=== Batch CCSS - Exécution Finale ==="
echo ""

# Charger la configuration email
echo "📧 Chargement de la configuration..."
source config_email.env

echo "✅ Configuration:"
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