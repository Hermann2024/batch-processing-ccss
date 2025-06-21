#!/bin/bash

# Script pour importer le dump de la base de données RH
# Assurez-vous que MariaDB est installé et en cours d'exécution

echo "=== Import du dump de la base de données RH ==="

# Paramètres de connexion (à adapter selon votre configuration)
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_PASSWORD="password"  # À modifier selon vos paramètres
DB_NAME="rh"
DUMP_FILE="/Users/hermann.nana/Downloads/install 2/rh_dump.sql"

echo "Connexion à MariaDB..."
echo "Host: $DB_HOST:$DB_PORT"
echo "Base: $DB_NAME"
echo "Dump: $DUMP_FILE"

# Vérifier si le fichier dump existe
if [ ! -f "$DUMP_FILE" ]; then
    echo "❌ Erreur: Le fichier dump n'existe pas: $DUMP_FILE"
    exit 1
fi

# Créer la base de données si elle n'existe pas
echo "📦 Création de la base de données '$DB_NAME' si elle n'existe pas..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS \`$DB_NAME\` CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci;"

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la création de la base de données"
    exit 1
fi

# Importer le dump
echo "📥 Import du dump dans la base '$DB_NAME'..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < "$DUMP_FILE"

if [ $? -eq 0 ]; then
    echo "✅ Import réussi !"
    echo ""
    echo "=== Informations sur la base importée ==="
    echo "Base de données: $DB_NAME"
    echo "Tables principales:"
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SHOW TABLES;" | grep -E "(collab|teletravail|conge|typett|typecong)"
    echo ""
    echo "Nombre de collaborateurs:"
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT COUNT(*) as nb_collaborateurs FROM collab;"
    echo ""
    echo "Nombre de jours de télétravail:"
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT COUNT(*) as nb_teletravail FROM teletravail;"
    echo ""
    echo "Types de télétravail disponibles:"
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT * FROM typett;"
    echo ""
    echo "Types de congés disponibles:"
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT * FROM typecong LIMIT 10;"
else
    echo "❌ Erreur lors de l'import du dump"
    exit 1
fi

echo ""
echo "🎉 Base de données prête pour le batch CCSS !"
echo "Vous pouvez maintenant lancer l'application avec:"
echo "mvn spring-boot:run -Dspring.profiles.active=prod" 