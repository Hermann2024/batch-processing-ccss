# Utiliser Java 17 avec OpenJDK
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR de l'application
COPY target/batch-processing-0.0.1-SNAPSHOT.jar app.jar

# Créer le dossier de sortie pour les rapports
RUN mkdir -p /app/output

# Exposer le port 8080
EXPOSE 8080

# Variables d'environnement pour la configuration
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Commande pour démarrer l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 