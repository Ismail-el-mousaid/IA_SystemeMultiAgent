package agentsConsumer;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/* Dans ce container on va déployer l'agent 'ConsumerAgent' */
/* JavaFx (extends Application) permet de créer interface graphique */
/* Cet classe représente l'interface graphique */
public class ConsumerContainer extends Application {
    // ConsumerContainer(interface graphique) a besoin de réference vers Agent
    protected ConsumerAgent consumerAgent;
    //Pour afficher le msg répondu par l'agent dont lequel nous avons envoie le msg
    protected ObservableList<String> observableListData;


    public static void main(String[] args) throws ControllerException {
        //lancer JavaFx (interface)
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Démarrer Container
        startContainer();
        //Spécifier le titre de la fenetre
        primaryStage.setTitle("Consumateur Livre Container");
        BorderPane borderPane = new BorderPane();

        HBox hBox1 = new HBox();
        hBox1.setPadding(new Insets(10));
        hBox1.setSpacing(10);
        Label labelNomLivre = new Label("Nom Livre: ");
        TextField textFieldNomLivre = new TextField();
        Button buttonOk = new Button("Acheter");
        hBox1.getChildren().addAll(labelNomLivre, textFieldNomLivre, buttonOk);
        borderPane.setTop(hBox1);
        observableListData = FXCollections.observableArrayList();
        ListView<String> listViewMessage = new ListView<String>(observableListData);
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);
        vBox.getChildren().addAll(listViewMessage);
        borderPane.setCenter(vBox);

        buttonOk.setOnAction(evt->{
            String nomLivre = textFieldNomLivre.getText();
            //observableListData.add(nomLivre);
            // Transmet le paremetre nomLivre vers methode onGuiEvent de Agent
            GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter(nomLivre);
            consumerAgent.onGuiEvent(guiEvent);
        });

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void startContainer() throws StaleProxyException {
        /* Create instance de l'environnement de l'exécution de JADE */
        Runtime runtime = Runtime.instance();
        /* Configuer des paramètres */
        ProfileImpl profile = new ProfileImpl();
        //Spécifier ou se trouve MainContainer (car le conteneur a besoin de se connecter à MainConatiner pour faire partie dans le plateforme)
        profile.setParameter(Profile.MAIN_HOST, "192.168.88.1");
        /* Create  Container avec runtime et lui donner paramètre profile */
        AgentContainer container = runtime.createAgentContainer(profile);
        /* Déployer l'agent 'ConsumerAgent' dans ce conteneur */
        AgentController consumerController =
                container.createNewAgent("consumer", "agentsConsumer.ConsumerAgent", new Object[] {this}); //On donne un nom à l'agent et son classe et on peut transmet des paramètres avec elles (dans ce exemple on transmet ConsumerContainer)
        /* Démarrer MainConatiner */
        consumerController.start();
    }


    /* Pour afficher les msg recus par l'agent */
    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{  // Pour resoudre l'exception du thread
            observableListData.add(aclMessage.getSender().getName()+
                    "=>"+aclMessage.getContent());
        });

    }


}
