package com.example.Eps.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    @Autowired
    private JavaMailSender mailSender;

    @Async // Hace que el correo se envíe de fondo y no ponga lento el kiosco
    public void enviarTurnoPorCorreo(String correoDestino, String codigoTurno, String servicio) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(correoDestino);
            mensaje.setSubject("🎫 Su Turno Digital - Salud Intotal EPS");
            
            String cuerpo = "¡Hola!\n\n"
                    + "Su turno ha sido registrado exitosamente en nuestro sistema.\n\n"
                    + "===============================\n"
                    + "   CÓDIGO DE TURNO: " + codigoTurno + "\n"
                    + "   SERVICIO: " + servicio.replace("_", " ") + "\n"
                    + "===============================\n\n"
                    + "Por favor, esté atento a las pantallas de la sala de espera. "
                    + "Cuando su código parpadee, diríjase al módulo indicado.\n\n"
                    + "Gracias por utilizar los servicios digitales de Salud Intotal.";
            
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
            System.out.println("📧 Correo de notificación enviado con éxito a: " + correoDestino);
        } catch (Exception e) {
            System.err.println("❌ No se pudo enviar el correo: " + e.getMessage());
        }
    }
}