package com.siemens.trainApp.notification_service.Service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationListener {

    @RabbitListener(queues = "${rabbitmq.queue.email.name}")
    public void processEmailNotification(String message) {

        System.out.println("========================================");
        System.out.println("S-a primit un mesaj nou in RabbitMQ!");

        // 1. Verificăm dacă este o notificare de ÎNTÂRZIERE (Cerința C din PDF)
        if (message.startsWith("ATENȚIE:") || message.contains("întârziere")) {
            System.out.println("🚨 TIP: ALERTĂ ÎNTÂRZIERE TREN");
            System.out.println("Se trimite email către toți pasagerii afectați...");
            System.out.println("Conținut: " + message);
        }
        // 2. Dacă are virgulă, este formatul tău original de REZERVARE (Cerința A din PDF)
        else if (message.contains(",")) {
            String[] data = message.split(",");

            if (data.length >= 2) {
                String customerEmail = data[0].trim();
                String scheduleId = data[1].trim();

                System.out.println("✅ TIP: CONFIRMARE REZERVARE BILET");
                System.out.println("Către: " + customerEmail);
                System.out.println("Mesaj: Rezervarea ta pentru cursa " + scheduleId + " a fost confirmată!");
            } else {
                System.err.println("❌ Format mesaj rezervare invalid: " + message);
            }
        }
        // 3. Caz de eroare pentru mesaje corupte
        else {
            System.err.println("❌ Format complet necunoscut primit in RabbitMQ: " + message);
        }

        System.out.println("========================================");
    }
}