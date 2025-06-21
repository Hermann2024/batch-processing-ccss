#!/bin/bash

echo "=== Test Email Outlook/Live ==="
echo ""

# Charger la configuration
source config_email.env

echo "📧 Configuration:"
echo "   Host: smtp-mail.outlook.com"
echo "   Port: 587"
echo "   Username: $MAIL_USERNAME"
echo "   Password: $MAIL_PASSWORD"
echo ""

# Compiler et lancer un test d'email
echo "🧪 Test d'envoi d'email..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation"
    exit 1
fi

echo "✅ Compilation réussie"
echo ""

# Lancer le test avec le profil production
echo "📤 Envoi d'un email de test..."
mvn spring-boot:run -Dspring-boot.run.profiles=prod -Dspring.batch.job.enabled=false -Dtest.email=true -q

echo ""
echo "✅ Test terminé"
echo "📧 Vérifiez votre boîte mail: $EMAIL_TO" 