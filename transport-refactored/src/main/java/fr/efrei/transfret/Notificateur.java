package fr.efrei.transfret;

import static fr.efrei.transport.contrat.TypeClient.*;

public class Notificateur {

    public void notifier(Client client, String sujet, String corps, String sms) {
        EmailService.envoyer(client.getEmail(), sujet, corps);
        if (client.getType().equals(PREMIUM)) {
            new SmsService().envoyerSms(client.getTelephone(), sms);
        }
    }
}
