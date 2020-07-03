package fr.nicoevgl.escapegameonline;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe mère
 */
public class Game {
    static Logger logger = LogManager.getLogger(Game.class);
    InputStream input;

    protected IAttacker attacker;
    protected IDefender defender;

    int combinationSize;                // Taille de la combinaison //
    int[] combination;                  // tableau combinaisons //
    String[] tabCombi;
    int[] proposition;                  // tableau proposition*/
    String[] response;                  // réponse à la combinaison ( +, - ou = ) //
    int nbEssaysMax;                    // nombre d'essais maximum //
    String[] tableau;                   // tableau de caractères //
    int[] min;                          // valeurs minimales d'une combinaison //
    int[] max;                          // valeurs maximales d'une combinaison //
    int[] computerProposition;          // première combinaison proposées par l'IA //
    int[] newComputerProposition;       // nouvelles combinaisons proposées par l'IA //
    int[] computerCombination;          // combinaison générées automatiquement par l'IA //
    String mode_Developpeur;            // mode développeur (activé ou non) //
    protected boolean modeDeveloppeur;  // boolean mode développeur //
    static boolean modeDev = false;

    /**
     * Constructeurs
     */
    public Game() {
        try {
            input = new FileInputStream("config.properties");
            Properties prop = new Properties();

            // load propoerties file //

            prop.load(input);
            combinationSize = Integer.parseInt(prop.getProperty("taille.combinaison"));
            nbEssaysMax = Integer.parseInt(prop.getProperty("nombre.essai"));
            if (modeDev) {
                mode_Developpeur = "active";
            } else {
                mode_Developpeur = (prop.getProperty("mode.developpeur"));
            }
        } catch (IOException ex) {
            logger.error("Problème de téléchargement du fichier de configuration");
        }
        combination = new int[combinationSize];
        tabCombi = new String[combinationSize];
        response = new String[combinationSize];
        proposition = new int[combinationSize];
        tableau = new String[combinationSize];
        computerProposition = new int[combinationSize];
        newComputerProposition = new int[combinationSize];
        computerCombination = new int[combinationSize];
        max = new int[] {9, 9, 9, 9};
        min = new int[] {0, 0, 0, 0};
    }

    /**
     * Méthode qui permet de lancer le mode de jeu selectionné par le joueur.
     *
     * @param nbMode : mode de jeu saisi par le joueur.
     */
    public void runSelectedMode(int nbMode) {

        switch (nbMode) {
            case 1:
                System.out.println("Vous avez choisi le mode Challenger.");
                attacker = new HumanPlayer();
                defender = new IA();
                challengerMode();
                break;
            case 2:
                System.out.println("Vous avez choisi le mode Défenseur.");
                attacker = new IA();
                defender = new HumanPlayer();
                defenderMode();
                break;
            case 3:
                System.out.println("Vous avez choisi le mode Duel.");
                attacker = new HumanPlayer();
                defender = new IA();
                duelMode();
                break;
            case 4:
                System.out.println("Vous avez choisis de quitter le jeu.");
                break;
            default:
                System.out.println("Ce mode de jeu n'existe pas.");
        }
    }

    /** Modes de jeu **/

    /**
     * Mode Duel
     */
    public void duelMode() {
        logger.trace("Lancement du mode Duel");
        int nbEssays = 0;
        boolean resultGame;

        System.out.println("Règles du jeu :");
        System.out.println("Vous jouez à tour de rôle avec l'IA, le premier à découvrir la combinaison de l'autre remporte la partie. ");
        System.out.println("N'oubliez pas, vous n'avez que " + nbEssaysMax + " essais !" + "\n");

        computerCombination = defender.generateCombi();
        if (isModeDeveloppeur()) {
            logger.info("Mode développeur activé");
            System.out.print("La combinaison générée par le système est : " + "\n");
            putResultCombi(computerCombination);
        }
        combination = attacker.generateCombi();
        System.out.println("Votre combinaison est : ");
        putResultCombi(combination);
        do {
            nbEssays++;
            proposition = attacker.generateProp();
            System.out.print("Proposition : ");
            putResultCombi(proposition);
            response = compare(computerCombination, proposition);
            System.out.print("Réponse : ");
            putResponse(response);
            resultGame = ResultGame(response);
            if (resultGame == true) {
                System.out.println("Bravo ! Vous avez trouvé la combinaison de l'IA ! Vous remportez la partie ! " + " \n");
                break;
            } else {
                System.out.println("Vous n'avez pas trouvé la combinaison de l'IA" + "\n");
            }
            System.out.println("L'IA pense a une combinaison ...");
            if (nbEssays <= 1) {
                computerProposition = defender.generateProp();
                System.out.print("Proposition de l'IA : ");
            } else {
                computerProposition = defender.generateNewProp(combination, computerProposition);
                System.out.println("Proposition IA ");
            }
            putResultCombi(computerProposition);
            response = compare(combination, computerProposition);
            System.out.print("Réponse : ");
            putResponse(response);
            resultGame = ResultGame(response);
            if (resultGame == true) {
                System.out.println("Perdu ! L'IA a était plus rapide et a découvert votre combinaison !");
                break;
            } else {
                System.out.println("L'IA n'as pas trouvé votre combinaison..." + "\n");
            }
            resultGame = ResultGame(response);
        } while (!resultGame && nbEssays < nbEssaysMax);
        if (nbEssays == nbEssaysMax || resultGame == true) {
            System.out.println("La combinaison de l'IA était : ");
            putResultCombi(computerCombination);
        }
        System.out.println("Fin de la partie.");
        logger.trace("Fin de la partie");
    }

    /**
     * Mode défenseur
     */
    public void defenderMode() {
        logger.trace("Lancement du mode Défenseur");

        int nbEssays = 0;
        boolean resultGame;

        System.out.println("Règles du jeu :");
        System.out.println("Vous définissez une combinaison à " + combinationSize + " chiffres. ");
        System.out.println("L'IA a " + nbEssaysMax + " essais pour décovurir votre combinaison" + "\n");
        combination = defender.generateCombi();
        System.out.println("Votre combinaison est : ");
        putResultCombi(combination);
        System.out.println("L'IA pense a une combinaison ..." + "\n");
        proposition = attacker.generateProp();
        response = compare(combination, proposition);
        System.out.print("Proposition de l'IA :  ");
        putResultCombi(proposition);
        System.out.print("Réponse : ");
        putResponse(response);
        resultGame = ResultGame(response);
        nbEssays++;
        if (resultGame == true) {
            System.out.println("Trop facile ! J'ai trouvé votre combinaison du premier coup !");
        } else {
            do {
                if (nbEssays <= 1) {
                    computerProposition = attacker.generateNewProp(combination, proposition);
                } else {
                    computerProposition = attacker.generateNewProp(combination, computerProposition);
                }
                response = compare(combination, computerProposition);
                System.out.println("Proposition de l'IA ");
                putResultCombi(computerProposition);
                System.out.print("Réponse : ");
                putResponse(response);
                resultGame = ResultGame(response);
                nbEssays++;
            } while (!resultGame && nbEssays < nbEssaysMax);
            if (resultGame == true) {
                System.out.println("HaHa ! J'ai réussis à trouver votre combinaison !");
            } else {
                System.out.println("Sniiif ! J'ai perdu... Je n'ai pas trouvé la combinaison...");
            }
        }
        System.out.println("Fin de la partie.");
        logger.trace("Fin de la partie");
    }

    /**
     * Mode Challenger
     */
    public void challengerMode() {
        logger.trace("Lancement du mode Challenger");

        int nbEssays = 0;
        boolean resultGame;

        System.out.println("Règle du jeu : ");
        System.out.println("Le système définit une combinaison de " + combinationSize + " chiffres aleatoirement ");
        System.out.println("A vous de trouver cette combinaison !");
        System.out.println("Attention vous n'avez que " + nbEssaysMax + " essais !" + "\n");
        combination = defender.generateCombi();
        if (isModeDeveloppeur()) {
            logger.info("Mode développeur activé");
            System.out.print("La combinaison générée par le système est : ");
            putResultCombi(combination);
        }
        do {
            proposition = attacker.generateProp();
            System.out.print("Proposition : ");
            putResultCombi(proposition);
            response = compare(combination, proposition);
            System.out.print("Réponse : ");
            putResponse(response);
            resultGame = ResultGame(response);
            nbEssays++;
        } while (!resultGame && nbEssays < nbEssaysMax);
        if (resultGame == true) {
            System.out.println("Bravo ! Vous avez trouvez la combinaison de l'IA !");
        } else {
            System.out.println("Perdu ! La combinaison de l'IA n'a pas était découverte");
            System.out.println("La combinaison était : ");
            putResultCombi(combination);
        }
        System.out.println("Fin de la partie.");
        logger.trace("Fin de la partie");
    }

    /**
     * isModeDeveloppeur verifie si le mode développeur est activé ou non
     *
     * @return boolean modeDeveloppeur
     */
    public boolean isModeDeveloppeur() {
        modeDeveloppeur = false;
        if (mode_Developpeur.equals("active"))
            modeDeveloppeur = true;
        return modeDeveloppeur;
    }


    /**
     * putResultCombi affiche les éléments d'un tableau d'entiers
     *
     * @param tableau [] int
     */
    public void putResultCombi(int[] tableau) {
        for (int i = 0; i < tableau.length; i++) {
            System.out.print(tableau[i]);
        }
        System.out.print("\n");
    }

    /**
     * compare deux combinaisons qui prend en paramètre deux tableaux : la combinaison secrète et la proposition.
     *
     * @param combination [] int
     * @param proposition [] int
     * @return String[] response
     */
    public String[] compare(int[] combination, int[] proposition) {
        int i = 0;
        do {
            if (combination[i] < proposition[i]) {
                response[i] = "-";
            } else if (combination[i] > proposition[i]) {
                response[i] = "+";
            } else {
                response[i] = "=";
            }

            i++;
        } while (i < 4);
        return response;
    }

    /**
     * putResponse affiche les éléments d'un tableau de chaine de caractères
     *
     * @param tableau [] String
     */
    public void putResponse(String[] tableau) {
        for (int i = 0; i < tableau.length; i++) {
            System.out.print(tableau[i]);
        }
        System.out.println("\n");
    }

    /**
     * ResultGame affiche le résultat de la comparaison des deux combinaisons.
     * si tout le tableau contient " = ", elle retourne true, si non false.
     *
     * @param response [] String
     * @return boolean x
     */
    public boolean ResultGame(String[] response) {
        boolean x = true;

        for (int i = 0; i < response.length; i++) {
            if (response[i].equals("+") || response[i].equals("-")) {
                x = false;
            }
        }
        return x;
    }
}
