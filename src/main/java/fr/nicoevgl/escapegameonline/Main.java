package fr.nicoevgl.escapegameonline;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals("modeDev")) {
            Game.modeDev = true;
        }
        logger.info("Démarrage du jeu");

        int nbMode = 0;
        int nbChoice = 0;

        do {
            System.out.println("Bienvenue dans EscapeGame Online !" + "\n");
            System.out.println("Choisissez votre mode de jeu");
            System.out.println("1 - Mode Challenger");
            System.out.println("2 - Mode Défenseur");
            System.out.println("3 - Mode Duel");
            System.out.println("4 - Quitter");

            Scanner scMode = new Scanner(System.in);
            Scanner scChoice = new Scanner(System.in);
            Game game = new Game();

            try {
                nbMode = scMode.nextInt();
            } catch (Exception e) {
                logger.error("Erreur de saisi");
                logger.error("Saisir uniquement des valeurs, aucun caractère spécial ni aucune chaine de caractères n'est valable");

            }
            do {
                game.runSelectedMode(nbMode);
                System.out.println("1 : Recommencer  2 : Retour au menu principal  3 : Fermer l'application");
                try {
                    nbChoice = scChoice.nextInt();
                } catch (Exception e) {
                    logger.error("Erreur de saisi");
                    logger.error("Saisir uniquement des valeurs, aucun caractère spécial ni aucune chaine de caractères n'est valable");
                }
                switch (nbChoice) {
                    case 1:
                        break;
                    case 2:
                        System.out.println("Retour au menu principal..." + "\n");
                        logger.trace("Retour au menu principal");
                        break;
                    case 3:
                        System.out.println("Au revoir...");
                        break;
                    default:
                        System.out.println("Veuillez saisir une valeur correcte...");
                        nbChoice = 2;
                }
            } while (nbChoice == 1);
        } while (nbChoice == 2);
        logger.info("Fermeture de l'application");
    }
}
