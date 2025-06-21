#!/bin/bash

# Script de déploiement pour serveur
# Usage: ./deploy-server.sh [email_password]

set -e

echo "🚀 Déploiement du Batch Processing CCSS sur serveur"
echo "=================================================="

# Vérifier si Docker est installé
if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé. Installation en cours..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sh get-docker.sh
    sudo usermod -aG docker $USER
    echo "✅ Docker installé. Veuillez vous reconnecter pour appliquer les permissions."
    exit 1
fi

# Vérifier si Docker Compose est installé
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose n'est pas installé. Installation en cours..."
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "✅ Docker Compose installé."
fi

# Demander le mot de passe email si pas fourni
EMAIL_PASSWORD=${1:-""}
if [ -z "$EMAIL_PASSWORD" ]; then
    echo -n "🔐 Entrez le mot de passe de l'email (nanahermann@outlook.com): "
    read -s EMAIL_PASSWORD
    echo
fi

# Exporter la variable d'environnement
export EMAIL_PASSWORD

echo "📦 Compilation de l'application..."
mvn clean package -DskipTests

echo "🐳 Construction et démarrage des conteneurs..."
docker-compose down
docker-compose build --no-cache
docker-compose up -d

echo "⏳ Attente du démarrage des services..."
sleep 30

echo "🔍 Vérification du statut des services..."
docker-compose ps

echo "🏥 Test de santé de l'application..."
curl -f http://localhost:8080/api/batch/health || echo "⚠️  L'application n'est pas encore prête, attendez quelques secondes..."

echo "📊 Informations sur le scheduling..."
curl -f http://localhost:8080/api/batch/scheduler/info || echo "⚠️  Le scheduler n'est pas encore accessible..."

echo ""
echo "✅ Déploiement terminé !"
echo ""
echo "📋 Informations importantes :"
echo "   - Application: http://localhost:8080"
echo "   - Health check: http://localhost:8080/api/batch/health"
echo "   - Scheduler info: http://localhost:8080/api/batch/scheduler/info"
echo "   - Base de données: localhost:3306 (root/root)"
echo "   - Rapports: ./output/"
echo "   - Logs: ./logs/"
echo ""
echo "🔄 Le batch se lance automatiquement toutes les minutes"
echo "📧 Les rapports sont envoyés par email à nanahermann@outlook.com"
echo ""
echo "🔧 Commandes utiles :"
echo "   - Voir les logs: docker-compose logs -f batch-app"
echo "   - Arrêter: docker-compose down"
echo "   - Redémarrer: docker-compose restart"
echo "   - Mettre à jour: ./deploy-server.sh [password]" 