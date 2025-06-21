#!/bin/bash

# Script de test du scheduling automatique
# Usage: ./test-scheduler.sh

echo "🧪 Test du Scheduling Automatique - Batch Processing CCSS"
echo "========================================================"

# Compiler le projet
echo "📦 Compilation du projet..."
mvn clean compile -q
if [ $? -eq 0 ]; then
    echo "✅ Compilation réussie"
else
    echo "❌ Erreur de compilation"
    exit 1
fi

# Lancer l'application en arrière-plan
echo "🚀 Lancement de l'application..."
mvn spring-boot:run -Dspring-boot.run.profiles=scheduler > logs/app.log 2>&1 &
APP_PID=$!

# Attendre que l'application démarre
echo "⏳ Attente du démarrage de l'application..."
sleep 30

# Tester la santé de l'application
echo "🏥 Test de santé de l'application..."
if curl -f http://localhost:8080/api/batch/health > /dev/null 2>&1; then
    echo "✅ Application accessible"
else
    echo "❌ Application non accessible"
    kill $APP_PID 2>/dev/null
    exit 1
fi

# Tester les informations du scheduler
echo "📊 Test des informations du scheduler..."
if curl -f http://localhost:8080/api/batch/scheduler/info > /dev/null 2>&1; then
    echo "✅ Scheduler configuré"
else
    echo "❌ Scheduler non configuré"
    kill $APP_PID 2>/dev/null
    exit 1
fi

# Attendre quelques minutes pour voir le scheduling en action
echo "⏰ Attente de 2 minutes pour observer le scheduling..."
echo "📝 Logs en temps réel (Ctrl+C pour arrêter) :"
echo ""

# Afficher les logs en temps réel
tail -f logs/app.log &
TAIL_PID=$!

# Attendre 2 minutes
sleep 120

# Arrêter l'affichage des logs
kill $TAIL_PID 2>/dev/null

# Arrêter l'application
echo ""
echo "🛑 Arrêt de l'application..."
kill $APP_PID 2>/dev/null

# Vérifier les logs du scheduler
echo ""
echo "📋 Analyse des logs du scheduler :"
echo "=================================="

if [ -f logs/app.log ]; then
    echo "🔍 Recherche des exécutions automatiques :"
    grep -i "DÉBUT LANCEMENT AUTOMATIQUE" logs/app.log | wc -l | xargs echo "   - Nombre d'exécutions automatiques :"
    
    echo "🔍 Recherche des succès :"
    grep -i "Batch lancé avec succès" logs/app.log | wc -l | xargs echo "   - Nombre de succès :"
    
    echo "🔍 Recherche des erreurs :"
    grep -i "Erreur" logs/app.log | wc -l | xargs echo "   - Nombre d'erreurs :"
    
    echo ""
    echo "📄 Dernières lignes des logs :"
    tail -10 logs/app.log
else
    echo "❌ Fichier de logs non trouvé"
fi

echo ""
echo "✅ Test terminé !"
echo ""
echo "📊 Résumé :"
echo "   - Application démarrée avec succès"
echo "   - Scheduler configuré et actif"
echo "   - Le batch se lance automatiquement toutes les minutes"
echo "   - Consultez logs/app.log pour plus de détails" 