package containers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

/* Create Simple JADE Container */
public class SimpleContainer {
    public static void main(String[] args) throws ControllerException {
        /* Create instance de l'environnement de l'exécution de JADE */
        Runtime runtime = Runtime.instance();
        /* Configuer des paramètres */
        ProfileImpl profile = new ProfileImpl();
        //Spécifier ou se trouve MainContainer (car le conteneur a besoin de se connecter à MainConatiner pour faire partie dans le plateforme)
        profile.setParameter(Profile.MAIN_HOST, "192.168.88.1");
        /* Create Simple Container avec runtime et lui donner paramètre profile */
        AgentContainer container = runtime.createAgentContainer(profile);
        /* Démarrer MainConatiner */
        container.start();
    }
}
