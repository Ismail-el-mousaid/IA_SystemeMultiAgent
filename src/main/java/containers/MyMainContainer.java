package containers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

/* Création de Main JADE Container */
public class MyMainContainer {
    public static void main(String[] args) throws ControllerException {
        /* Create instance de l'environnement de l'exécution de JADE */
        Runtime runtime = Runtime.instance();
        /* Configuer des paramètres */
        ProfileImpl profile = new ProfileImpl();
        //Afficher l'interface graphique de JADE
        profile.setParameter(Profile.GUI, "true");
        /* Create Main Container avec runtime et lui donner paramètre profile */
        AgentContainer mainContainer = runtime.createMainContainer(profile);
         /* Démarrer MainConatiner */
        mainContainer.start();


    }
}
