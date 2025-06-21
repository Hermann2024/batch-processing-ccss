#!/bin/bash

# Script simple pour tester le batch CCSS

echo "=== Test Simple du Batch CCSS ==="

# Demander le mot de passe MariaDB
echo -n "Mot de passe MariaDB: "
read -s DB_PASSWORD
echo ""

# Compiler le projet
echo "🔨 Compilation..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation"
    exit 1
fi

echo "✅ Compilation réussie"

# Créer le dossier output
mkdir -p output

# Lancer l'application avec le mot de passe
echo "🚀 Lancement du batch..."
echo "Le batch va traiter tes données et générer les rapports..."
echo ""

# Lancer avec le mot de passe en variable d'environnement
DB_PASSWORD="$DB_PASSWORD" mvn spring-boot:run \
    -Dspring.profiles.active=prod \
    -Dspring.datasource.password="$DB_PASSWORD" \
    -Dspring.main.web-application-type=none \
    -Dspring.batch.job.enabled=true \
    -Dspring.batch.job.names=teletravailCcssJob 