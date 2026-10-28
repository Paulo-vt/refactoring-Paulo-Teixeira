package fr.efrei.transfret;

import fr.efrei.transport.contrat.ModeTransport;
import fr.efrei.transport.contrat.TypeClient;
import fr.efrei.transport.contrat.TypeMarchandise;

import java.util.List;

import static fr.efrei.transport.contrat.ModeTransport.*;
import static fr.efrei.transport.contrat.TypeClient.*;

public class CalculateurPrix {

    public static final double TARIF_ROUTE_KM = 1.20;
    public static final double TARIF_RAIL_KM = 0.8;
    public static final double TARIF_MER_KM = 0.5;
    public static final double TARIF_AIR_KM = 0.6;
    public static final double TARIF_ROUTE_KG = 0.10;
    public static final double TARIF_RAIL_KG = 0.07;
    public static final double TARIF_MER_KG = 0.03;
    public static final double TARIF_AIR_KG = 4.5;
    public static final int TARIF_AIR_KG_VOL = 167;
    public static final int TARIF_RAIL_MIN = 150;
    public static final int TARIF_MER_MIN = 500;
    public static final double TARIF_SUP_FRAGILE = 1.15;
    public static final double TARIF_SUP_DANGEREUX = 1.30;
    public static final double TARIF_SUP_URGENT = 1.5;
    public static final double TARIF_SUP_REFRIGERE = 1.20;
    public static final double REMISE_CLIENT_PREMIUM = 0.80;
    public static final double REMISE_CLIENT_MIN = 0.85;
    public static final double REMISE_CLIENT_MAX = 0.90;
    public static final int TARIF_ASSURANCE_MIN = 15;
    public static final double TARIF_ASSURANCE = 0.02;

    public double calculerPrixHT(List<Colis> colis, int distance, ModeTransport mode, boolean urgent, boolean assurance, TypeClient typeClient) {
        double poids = 0;
        double volume = 0;
        double valeur = 0;
        boolean fragile = false;
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            poids = poids + c.getPoids();
            volume = volume + c.getVolume();
            valeur = valeur + c.getValeurDeclaree();
            if (c.getType() == TypeMarchandise.FRAGILE) fragile = true;
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }

        // tarif de base selon le mode
        double prix;
        if (mode == ROUTE) {
            prix = distance * TARIF_ROUTE_KM + poids * TARIF_ROUTE_KG;
        } else if (mode.equals(RAIL)) {
            prix = distance * TARIF_RAIL_KM + poids * TARIF_RAIL_KG;
            if (prix < TARIF_RAIL_MIN) prix = TARIF_RAIL_MIN;
        } else if (mode.equals(MER)) {
            prix = distance * TARIF_MER_KM + poids * TARIF_MER_KG;
            if (prix < TARIF_MER_MIN) prix = TARIF_MER_MIN;
        } else if (mode.equals(AIR)) {
            double poidsTaxable = poids;
            if (volume * TARIF_AIR_KG_VOL > poids) {
                poidsTaxable = volume * TARIF_AIR_KG_VOL;
            }
            prix = distance * TARIF_AIR_KM + poidsTaxable * TARIF_AIR_KG;
        } else {
            throw new IllegalArgumentException("Mode inconnu : " + mode);
        }

        // suppléments
        if (fragile) prix = prix * TARIF_SUP_FRAGILE;
        if (dangereux) prix = prix * TARIF_SUP_DANGEREUX;
        if (refrigere) prix = prix * TARIF_SUP_REFRIGERE;
        if (urgent) prix = prix * TARIF_SUP_URGENT;

        // remise client
        if (typeClient.equals(ENTREPRISE)) {
            if (prix > 5000) {
                prix = prix * REMISE_CLIENT_MIN;
            } else {
                prix = prix * REMISE_CLIENT_MAX;
            }
        } else if (typeClient.equals(PREMIUM)) {
            prix = prix * REMISE_CLIENT_PREMIUM;
        }

        // assurance
        if (assurance) {
            double montantAssurance = valeur * TARIF_ASSURANCE;
            if (montantAssurance < TARIF_ASSURANCE_MIN) montantAssurance = TARIF_ASSURANCE_MIN;
            prix = prix + montantAssurance;
        }
        return Math.round(prix * 100.0) / 100.0;
    }
}
