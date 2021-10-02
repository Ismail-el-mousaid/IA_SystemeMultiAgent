package agentsAcheteur;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
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

public class AcheteurLivreContainer extends Application {

    protected AcheteurLivreAgent acheteurLivreAgent;
    //Pour afficher les msg recus par l'agent
    protected ListView<String> listViewMessages;
    protected ObservableList<String> observableListData;

    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();

        primaryStage.setTitle("Acheteur Livre Container");
        BorderPane borderPane = new BorderPane();

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        observableListData = FXCollections.observableArrayList();
        listViewMessages = new ListView<String>(observableListData);
        vBox.getChildren().add(listViewMessages);
        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "192.168.88.1");
        // Create Container
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        // Deploy AcheteurLivreAgent dans ce container
        AgentController agentController =
                agentContainer.createNewAgent(
                        "AcheteurLivreAgent",
                        AcheteurLivreAgent.class.getName(),
                        new Object[] {this});
        agentController.start();

    }

    /* Pour afficher les msg recus par l'agent */
    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{  // Pour resoudre l'exception du thread
            observableListData.add(aclMessage.getSender().getName()+
                    "=>"+aclMessage.getContent());
        });

    }


}
