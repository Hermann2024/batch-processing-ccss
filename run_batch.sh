#!/bin/bash

set -e

# Script de lancement complet pour le batch CCSS
# Ce script vérifie la configuration, teste la connexion et lance l'application

echo "=== Lancement du Batch CCSS - Télétravail ==="

# Charger la configuration
if [ -f "config.env" ]; then
    echo "📋 Chargement de la configuration..."
    source config.env
else
    echo "❌ Erreur: Fichier config.env non trouvé"
    echo "Créez le fichier config.env avec vos paramètres de connexion"
    exit 1
fi

# Vérifier le dump (optionnel)
if [ ! -f "$DUMP_FILE" ]; then
    echo "⚠️  Fichier dump non trouvé à l'emplacement : $DUMP_FILE (ce n'est pas bloquant si la base est déjà remplie)"
else
    echo "✅ Fichier dump trouvé : $DUMP_FILE"
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

# Tester la connexion à la base de données
echo "🔍 Test de connexion à la base de données..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Erreur: Impossible de se connecter à la base de données"
    echo "Vérifiez que MariaDB est démarré et que les paramètres dans config.env sont corrects"
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
echo "🔨 Compilation du projet..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation"
    exit 1
fi

echo "✅ Compilation réussie"

# Créer le dossier output s'il n'existe pas
mkdir -p output

# Lancer l'application
echo "🚀 Lancement de l'application..."
echo "Profile: prod"
echo "Base de données: $DB_NAME sur $DB_HOST:$DB_PORT"
echo ""
echo "L'application sera accessible sur: http://localhost:8080"
echo "Pour déclencher le batch: curl -X POST http://localhost:8080/api/batch/teletravail/declencher"
echo ""
echo "Appuyez sur Ctrl+C pour arrêter l'application"
echo ""

# Lancement du batch avec le profil prod forcé
mvn spring-boot:run -Dspring-boot.run.profiles=prod 