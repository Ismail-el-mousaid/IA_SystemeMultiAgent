package agentsVendeur;

import agentsAcheteur.AcheteurLivreAgent;
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

public class VendeurLivreContainer extends Application {

    protected VendeurLivreAgent vendeurLivreAgent;
    //Pour afficher les msg recus par l'agent
    protected ListView<String> listViewMessages;
    protected ObservableList<String> observableListData;
    AgentContainer agentContainer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        startContainer();

        primaryStage.setTitle("Vendeur Livre Container");
        /* Create interface pour add new Agent Vendeur à partir de ce interface */
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        Label label = new Label("Nom Agent");
        TextField textFieldAgentName = new TextField();
        Button buttonDeploy = new Button("Deploy");
        hBox.getChildren().addAll(label, textFieldAgentName, buttonDeploy);

        BorderPane borderPane = new BorderPane();

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        observableListData = FXCollections.observableArrayList();
        listViewMessages = new ListView<String>(observableListData);
        vBox.getChildren().add(listViewMessages);
        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        /* Deploy les nouveaux Agent Vendeur */
        buttonDeploy.setOnAction((evt)->{
            try {
                String nameAgent = textFieldAgentName.getText();
                AgentController agentController =
                        agentContainer.createNewAgent(
                                nameAgent,
                                VendeurLivreAgent.class.getName(),
                                new Object[] {this});
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        });


    }

    private void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "192.168.88.1");
        // Create Container
        agentContainer = runtime.createAgentContainer(profile);
        // Deploy AcheteurLivreAgent dans ce container
       /* AgentController agentController =
                agentContainer.createNewAgent(
                        "VendeurLivreAgent",
                        VendeurLivreAgent.class.getName(),
                        new Object[] {this});
        agentController.start();   */

    }

    /* Pour afficher les msg recus par l'agent */
    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{  // Pour resoudre l'exception du thread
            observableListData.add(aclMessage.getSender().getName()+
                    "=>"+aclMessage.getContent());
        });

    }


}
