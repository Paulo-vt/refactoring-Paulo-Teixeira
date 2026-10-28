package fr.efrei.transfret;

import fr.efrei.transport.contrat.ModeTransport;
import fr.efrei.transport.contrat.TypeMarchandise;

import java.util.List;

import static fr.efrei.transport.contrat.ModeTransport.*;

public class ValidateurExpedition {

    public boolean estValide(Client client, List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent) {
        if (client == null) {
            System.out.println("ERREUR : client obligatoire");
            return false;
        }
        if (colis == null || colis.isEmpty()) {
            System.out.println("ERREUR : au moins un colis est nécessaire");
            return false;
        }
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            if (c.getPoids() <= 0) {
                System.out.println("ERREUR : poids invalide pour " + c.getDescription());
                return false;
            }
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }
        if (dangereux && mode.equals(AIR)) {
            System.out.println("ERREUR : les matières dangereuses sont interdites en transport aérien");
            return false;
        }
        if (refrigere && !(mode.equals(ROUTE) || mode.equals(MER))) {
            System.out.println("ERREUR : le transport réfrigéré n'est possible que par route ou par mer");
            return false;
        }
        if (urgent && mode.equals(MER)) {
            System.out.println("ERREUR : pas d'option urgente pour le transport maritime");
            return false;
        }
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            System.out.println("ERREUR : trajet inconnu " + villeDepart + " -> " + villeArrivee);
            return false;
        }
        return true;
    }
}