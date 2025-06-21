#!/bin/bash

# Script pour tester la connexion à la base de données et vérifier les données

echo "=== Test de connexion à la base de données RH ==="

# Paramètres de connexion (à adapter selon votre configuration)
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_PASSWORD="password"  # À modifier selon vos paramètres
DB_NAME="rh"

echo "Connexion à MariaDB..."
echo "Host: $DB_HOST:$DB_PORT"
echo "Base: $DB_NAME"

# Test de connexion
echo "🔍 Test de connexion..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Erreur: Impossible de se connecter à MariaDB"
    echo "Vérifiez que MariaDB est démarré et que les paramètres de connexion sont corrects"
    exit 1
fi

echo "✅ Connexion réussie !"

# Vérifier que la base existe
echo "🔍 Vérification de l'existence de la base '$DB_NAME'..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "USE \`$DB_NAME\`;" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Erreur: La base '$DB_NAME' n'existe pas"
    echo "Exécutez d'abord le script import_dump.sh"
    exit 1
fi

echo "✅ Base '$DB_NAME' trouvée !"

# Vérifier les tables principales
echo ""
echo "📊 Vérification des tables principales..."

echo "Tables disponibles:"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SHOW TABLES;" | grep -E "(collab|teletravail|conge|typett|typecong)"

echo ""
echo "📈 Statistiques des données:"

echo "Nombre de collaborateurs:"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT COUNT(*) as nb_collaborateurs FROM collab;"

echo "Nombre de collaborateurs actifs (sans date de sortie):"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT COUNT(*) as nb_actifs FROM collab WHERE DateSort IS NULL OR DateSort > CURDATE();"

echo "Nombre de jours de télétravail:"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT COUNT(*) as nb_teletravail FROM teletravail;"

echo "Nombre de jours de congés:"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT COUNT(*) as nb_conges FROM conge;"

echo ""
echo "🔍 Types de télétravail disponibles:"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT * FROM typett;"

echo ""
echo "🔍 Types de congés disponibles (premiers 10):"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT * FROM typecong LIMIT 10;"

echo ""
echo "📅 Exemples de données de télétravail (premiers 5):"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT t.idteletravail, c.Trigramme, c.Nom, t.DateTeletravail, t.half_day_period FROM teletravail t JOIN collab c ON t.Collaborateur_idColl = c.idColl LIMIT 5;"

echo ""
echo "📅 Exemples de données de congés (premiers 5):"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT c.idConge, col.Trigramme, col.Nom, c.DateConge, c.TypeCong_idTypeCong, c.half_day_period FROM conge c JOIN collab col ON c.Collaborateur_idColl = col.idColl LIMIT 5;"

echo ""
echo "🎯 Test de requête pour le calcul CCSS (exemple pour 2024):"
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "
SELECT 
    c.Trigramme,
    c.Nom,
    c.TpsPart,
    COUNT(t.idteletravail) as nb_jours_tt,
    COUNT(t.idteletravail) * 100.0 / 200 as pourcentage_estime
FROM collab c
LEFT JOIN teletravail t ON c.idColl = t.Collaborateur_idColl 
    AND YEAR(t.DateTeletravail) = 2024
WHERE c.DateSort IS NULL OR c.DateSort > CURDATE()
GROUP BY c.idColl, c.Trigramme, c.Nom, c.TpsPart
LIMIT 5;"

echo ""
echo "✅ Test de connexion terminé avec succès !"
echo ""
echo "🎉 La base de données est prête pour le batch CCSS !"
echo "Vous pouvez maintenant lancer l'application avec:"
echo "mvn spring-boot:run -Dspring.profiles.active=prod" 