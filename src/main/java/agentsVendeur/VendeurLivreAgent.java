package agentsVendeur;

import agentsAcheteur.AcheteurLivreContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendeurLivreAgent extends GuiAgent {

    protected VendeurLivreContainer gui;

    @Override
    protected void setup() {
        if(getArguments().length==1){
            gui = (VendeurLivreContainer) getArguments()[0];
            gui.vendeurLivreAgent=this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        /* Au démarrage de l'agent Vendeur doit "Publier son service" dans l'annuaire (Agent DF) */
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                /* Create Description Agent */
                DFAgentDescription agentDescription = new DFAgentDescription();
                agentDescription.setName(getAID());  //Nom du Agent
                /* Create Service */
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");  //Type du service
                serviceDescription.setName("Vente-livres");  //Service offré par l'agent
                agentDescription.addServices(serviceDescription); //Add service dans la description du l'agent
                try {
                    DFService.register(myAgent, agentDescription); //Enregistrer nom de l'agent et son description dans DF (agent se trouve dans mainContainer)
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            //Cet methode va attendre le msg
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if(aclMessage!=null){
                    //loger msg dans l'interface
                    gui.logMessage(aclMessage);

                    switch (aclMessage.getPerformative()) {
                        /* Repondre l'agent Acheteur */
                        case ACLMessage.CFP:
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(500+new Random().nextInt(1000)));  // Envoyer le prix du Livre
                            send(reply);
                            break;
                        /* Répondre l'agent Acheteur */
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage aclMessage2 = aclMessage.createReply();
                            aclMessage2.setPerformative(ACLMessage.AGREE);
                            send(aclMessage2);
                            break;
                        default:
                            break;
                    }
                } else {
                    block();
                }
            }
        });

    }


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    /* S'execute pendant la destruction de ce agent */
    @Override
    protected void takeDown() {
        /* Tous les services publiés par ce agent doit etre supprimer */
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
