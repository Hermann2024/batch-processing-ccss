#!/bin/bash

# Script d'installation du service systemd
# Usage: ./install-service.sh

set -e

echo "🔧 Installation du service systemd pour Batch Processing CCSS"
echo "============================================================"

# Obtenir le chemin absolu du répertoire actuel
CURRENT_DIR=$(pwd)
echo "📁 Répertoire de l'application: $CURRENT_DIR"

# Copier le fichier de service
sudo cp batch-processing.service /etc/systemd/system/

# Modifier le WorkingDirectory dans le service
sudo sed -i "s|/path/to/your/batch-processing|$CURRENT_DIR|g" /etc/systemd/system/batch-processing.service

# Recharger systemd
sudo systemctl daemon-reload

# Activer le service pour qu'il démarre au boot
sudo systemctl enable batch-processing.service

echo "✅ Service installé et activé !"
echo ""
echo "🔧 Commandes de gestion du service :"
echo "   - Démarrer: sudo systemctl start batch-processing"
echo "   - Arrêter: sudo systemctl stop batch-processing"
echo "   - Redémarrer: sudo systemctl restart batch-processing"
echo "   - Statut: sudo systemctl status batch-processing"
echo "   - Logs: sudo journalctl -u batch-processing -f"
echo ""
echo "🔄 Le service démarrera automatiquement au prochain boot du serveur"
echo "📧 N'oubliez pas de configurer le mot de passe email dans docker-compose.yml" 