#!/bin/bash

# Script de lancement sécurisé pour le batch CCSS
# Ce script demande le mot de passe MariaDB de manière sécurisée

echo "=== Batch CCSS - Exécution Sécurisée ==="
echo ""

# Charger la configuration email
echo "📧 Chargement de la configuration..."
source config_email.env

echo "✅ Configuration:"
echo "   De: $MAIL_USERNAME"
echo "   À: $EMAIL_TO"
echo "   Activé: $EMAIL_ENABLED"
echo ""

# Charger la configuration
if [ -f "config.env" ]; then
    echo "📋 Chargement de la configuration..."
    source config.env
else
    echo "❌ Erreur: Fichier config.env non trouvé"
    exit 1
fi

# Vérifier les prérequis
echo "🔍 Vérification des prérequis..."

# Vérifier Java
if ! command -v java &> /dev/null; then
    echo "❌ Erreur: Java n'est pas installé"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Erreur: Java 17 ou supérieur requis (version actuelle: $JAVA_VERSION)"
    exit 1
fi

echo "✅ Java $JAVA_VERSION détecté"

# Vérifier Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Erreur: Maven n'est pas installé"
    exit 1
fi

echo "✅ Maven détecté"

# Vérifier MariaDB/MySQL
if ! command -v mysql &> /dev/null; then
    echo "❌ Erreur: MySQL/MariaDB client n'est pas installé"
    exit 1
fi

echo "✅ MySQL/MariaDB client détecté"

# Demander le mot de passe MariaDB de manière sécurisée
echo ""
echo "🔐 Configuration de la base de données..."
echo -n "Mot de passe MariaDB pour l'utilisateur '$DB_USER': "
read -s DB_PASSWORD
echo ""

# Tester la connexion à la base de données
echo "🔍 Test de connexion à la base de données..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Erreur: Impossible de se connecter à la base de données"
    echo "Vérifiez le mot de passe et que MariaDB est démarré"
    exit 1
fi

echo "✅ Connexion à la base de données réussie"

# Vérifier que la base existe
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "USE \`$DB_NAME\`;" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Erreur: La base '$DB_NAME' n'existe pas"
    echo "Exécutez d'abord le script import_dump.sh"
    exit 1
fi

echo "✅ Base '$DB_NAME' trouvée"

# Compiler le projet
echo "📦 Compilation..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation"
    exit 1
fi

echo "✅ Compilation réussie"
echo ""

# Lancer le batch avec timeout (5 minutes maximum)
echo "🔄 Lancement du batch (timeout: 5 minutes)..."
timeout 300 mvn spring-boot:run -Dspring-boot.run.profiles=prod -q

if [ $? -eq 124 ]; then
    echo "⚠️  Timeout atteint - Le batch a pris trop de temps"
    echo "📧 L'email peut être envoyé en arrière-plan"
else
    echo "✅ Batch terminé normalement"
fi

echo ""
echo "📧 Vérifiez votre boîte mail: $EMAIL_TO"
echo "📁 Fichiers générés dans le dossier output/" 