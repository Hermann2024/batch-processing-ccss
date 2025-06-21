#!/bin/bash

echo "=== Test Email Rapide ==="
echo ""

# Charger la configuration
source config_email.env

echo "📧 Configuration:"
echo "   Host: smtp-mail.outlook.com"
echo "   Port: 587"
echo "   Username: $MAIL_USERNAME"
echo "   Password: $MAIL_PASSWORD"
echo ""

# Créer un test d'email simple
echo "🧪 Création d'un test d'email..."
cat > TestEmail.java << 'EOF'
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class TestEmail {
    public static void main(String[] args) {
        String host = "smtp-mail.outlook.com";
        String port = "587";
        String username = "hermannnana@outlook.com";
        String password = "Test1981";
        
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "30000");
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.writetimeout", "30000");
        
        try {
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username));
            message.setSubject("Test Email Batch CCSS");
            message.setText("Ceci est un test d'email pour vérifier la configuration SMTP Outlook/Live.\n\nSi vous recevez cet email, la configuration est correcte !");
            
            Transport.send(message);
            System.out.println("✅ Email de test envoyé avec succès !");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
EOF

echo "✅ Test créé"
echo "📤 Envoi de l'email de test..."
echo ""

# Compiler et exécuter le test
javac -cp ".:$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')" TestEmail.java 2>/dev/null
if [ $? -eq 0 ]; then
    java -cp ".:$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')" TestEmail
else
    echo "❌ Erreur de compilation du test"
fi

echo ""
echo "📧 Vérifiez votre boîte mail: $EMAIL_TO"
echo "🧹 Nettoyage..."
rm -f TestEmail.java TestEmail.class 