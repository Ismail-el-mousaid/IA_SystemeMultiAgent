package agentsAcheteur;


import agentsVendeur.VendeurLivreAgent;
import agentsVendeur.VendeurLivreContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class AcheteurLivreAgent extends GuiAgent {

    protected AcheteurLivreContainer gui;
    //Pour stocker les noms des vendeurs
    protected AID[] vendeurs;

    @Override
    protected void setup() {
        if(getArguments().length==1){
            gui = (AcheteurLivreContainer) getArguments()[0];
            gui.acheteurLivreAgent=this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        //Chaque 6s l'agent doit "Chercher les services" publié par les vendeurs dans DF
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 40000) {
            @Override
            protected void onTick() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");  //Spécifier le type de service cherché
                serviceDescription.setName("vente-livres");
                dfAgentDescription.addServices(serviceDescription);  //Ajouter service cherché dans description
                try {
                    //Stocker les resultats des services cherchés dans tableau
                    DFAgentDescription[] results = DFService.search(myAgent, dfAgentDescription);
                    //Get les noms des agents
                    vendeurs = new AID[results.length];
                    for (int i = 0; i < results.length; i++) {
                        vendeurs[i] = results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }


            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int counter=0;
            private List<ACLMessage> reponses = new ArrayList<ACLMessage>();
            //Cet methode va attendre le msg
            @Override
            public void action() {
                /*Spécifier Type de messge que l'agent recoit */
                MessageTemplate messageTemplate =
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                MessageTemplate.or(
                                        MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                        MessageTemplate.or(
                                                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                                MessageTemplate.or(
                                                        MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                                                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
                                                )
                                        )
                                )
                        );
                ACLMessage aclMessage = receive(messageTemplate);
                if(aclMessage!=null){
                    switch (aclMessage.getPerformative()) {  //Get Type message
                        /* Quand il recoit Request va contacter les Vendeurs */
                        case ACLMessage.REQUEST:         //Si il recoit le msg de type REQUEST (il le recoit à partir du Consumer)
                            String livre = aclMessage.getContent();
                            ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CFP);  //Pour l'envoyer msg à tous les vendeurs
                            aclMessage2.setContent(livre);
                            for (AID aid : vendeurs){
                                aclMessage2.addReceiver(aid);
                            }
                            send(aclMessage2);
                            break;
                        case ACLMessage.PROPOSE:
                            //Chaque fois l'agent Acheteur recoive  msg de l'agent Vendeur, counter++
                            ++counter;
                            //Sauvegarder les msg recevez par les agents Vendeurs
                            reponses.add(aclMessage);
                            //Choisir le meilleure offre
                            if (counter==vendeurs.length) {
                                ACLMessage meilleureOffre = reponses.get(0);
                                double miniPrix = Double.parseDouble(meilleureOffre.getContent());

                                for (ACLMessage offre: reponses){
                                    double prix = Double.parseDouble(meilleureOffre.getContent());
                                    if (prix<miniPrix){
                                        meilleureOffre = offre;
                                        miniPrix = prix;
                                    }
                                }
                                //Répondre Vendeur qui offre minimum du prix
                                ACLMessage aclMessageAccept = meilleureOffre.createReply();
                                aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                send(aclMessageAccept);
                            }
                            break;
                        /* Répondre Consumer */
                        case ACLMessage.AGREE:
                            ACLMessage aclMessage3 = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage3.addReceiver(new AID("consumer", AID.ISLOCALNAME));
                            aclMessage3.setContent(aclMessage.getContent());
                            send(aclMessage3);

                            break;
                        case ACLMessage.REFUSE:

                            break;
                        default:
                            break;
                    }

                    gui.logMessage(aclMessage);
                    //Après le recoit de msg, il doit répondre
                 /*   ACLMessage reply = aclMessage.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("D'accord");
                    send(reply);
                    //Après, l'agent acheteur va envoie une proposition vers Vendeur
                    ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CFP);  //CFP: Appel à une proposition
                    String livre = aclMessage.getContent();
                    aclMessage2.setContent(livre.substring(17));
                    aclMessage2.addReceiver(new AID("VendeurLivreAgent", AID.ISLOCALNAME));
                    send(aclMessage2);  */
                } else {
                    block();
                }
            }
        });

    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
