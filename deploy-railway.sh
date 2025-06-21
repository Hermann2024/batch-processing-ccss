#!/bin/bash

# Script de déploiement automatique sur Railway
# Usage: ./deploy-railway.sh [email_password]

set -e

echo "🚀 Déploiement sur Railway - Batch Processing CCSS"
echo "=================================================="

# Vérifier les prérequis
if ! command -v railway &> /dev/null; then
    echo "❌ Railway CLI non installé. Installation..."
    npm install -g @railway/cli
fi

# Compiler l'application
echo "📦 Compilation de l'application..."
mvn clean package -DskipTests

# Vérifier que le JAR existe
if [ ! -f target/batch-processing-*.jar ]; then
    echo "❌ Erreur: JAR non trouvé après compilation"
    exit 1
fi

# Login Railway (si pas déjà connecté)
echo "🔐 Connexion à Railway..."
railway login

# Initialiser le projet Railway (si pas déjà fait)
if [ ! -f railway.json ]; then
    echo "❌ Fichier railway.json manquant"
    exit 1
fi

# Déployer
echo "🚀 Déploiement en cours..."
railway up

# Obtenir l'URL de l'application
echo "🌐 Récupération de l'URL..."
APP_URL=$(railway status --json | jq -r '.url')

if [ "$APP_URL" != "null" ] && [ "$APP_URL" != "" ]; then
    echo "✅ Déploiement réussi !"
    echo "🌐 URL de l'application: $APP_URL"
    echo "📊 Health check: $APP_URL/api/batch/health"
    echo "🎯 Endpoint batch: $APP_URL/api/batch/teletravail-ccss"
    
    # Tester l'application
    echo "🧪 Test de l'application..."
    sleep 30  # Attendre le démarrage
    
    if curl -f "$APP_URL/api/batch/health" > /dev/null 2>&1; then
        echo "✅ Application opérationnelle !"
    else
        echo "⚠️  Application déployée mais health check échoue"
        echo "📋 Vérifiez les logs: railway logs"
    fi
else
    echo "❌ Erreur lors du déploiement"
    echo "📋 Vérifiez les logs: railway logs"
    exit 1
fi

echo ""
echo "📋 Prochaines étapes:"
echo "1. Configurer les variables d'environnement dans Railway"
echo "2. Ajouter une base de données PostgreSQL"
echo "3. Configurer l'email"
echo ""
echo "🔧 Commandes utiles:"
echo "  railway logs          # Voir les logs"
echo "  railway status        # Statut du déploiement"
echo "  railway open          # Ouvrir l'application" 