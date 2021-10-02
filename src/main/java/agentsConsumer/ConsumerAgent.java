package agentsConsumer;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* Create Agent */
// Quand on veut associer avec interface on extends GuiAgent (pas Agent)
public class ConsumerAgent extends GuiAgent {
    // Agent a besoin de réference vers ConsumerContainer(interface graphique)
    protected ConsumerContainer consumerContainer;

    /* La première méthode qui s'exécute */
    @Override
    protected void setup() {
        // Recuperer le prametre transmit dans le deployement
        String nomLivre = null;
       /* if (this.getArguments().length == 1) {
            nomLivre = (String) this.getArguments()[0];
        }  */
        /* Faire relation bidirectionel entre Consumer(interface) et Agent */
        if (this.getArguments().length == 1) {
            consumerContainer=(ConsumerContainer) getArguments()[0];
            consumerContainer.consumerAgent=this;
        }

        System.out.println("Initialisation de l'agent, son nom: " + this.getAID().getName());
        System.out.println("je suis entraine d'acheter le livre: " + nomLivre);
        /* Dans cet méthode on affecte les 3 comportements à ce l'agent */
        // Add un comportement Génerique (l'agent doit faire 10 tentatives pour atteindre quelque chose)
       /* addBehaviour(new Behaviour() {
            private int counter=0;
            @Override
            public void action() {
                System.out.println("---------------");
                System.out.println("Step "+counter);
                System.out.println("---------------");
                ++counter;
            }

            @Override
            public boolean done() {
                return (counter==10); //Quand atteindre 10 va retourner true
            }
        });  */

        //Add One-shot Behaviour qui permet d'exécuter la méthode action() une seule fois
      /*  addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("One Shot Behaviour");
            }
        });  */


        //Add Cyclic Behaviour qui s'ecécute d'une manière répétitive (done() toujourse false)
    /*    addBehaviour(new CyclicBehaviour() {
            private int counter=0;
            @Override
            public void action() {
                System.out.println("Counter =>"+counter);
                ++counter;
            }
        }); */

        // Add Ticket Behaviour qui s'exécute sa tache périodiquement (chaque 5 min par exemple)
      /*  addBehaviour(new TickerBehaviour(this, 1000) {  //this=agent & 1000=1
            @Override
            protected void onTick() {
                System.out.println("Tic ");
                System.out.println("Nom Agent: "+myAgent.getAID().getLocalName());
            }
        });  */

        // Add Waker Behaviour qui s'exécute en temp prédefinie
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm");
        Date date = null;
        try {
             date = dateFormat.parse("17/09/2021:12:44");
        } catch (ParseException e) {
            e.printStackTrace();
        }
       /* addBehaviour(new WakerBehaviour(this, date) {
            @Override
            protected void onWake() {
                System.out.println("Warker behaviour ...");
            }
        });  */

        // Add Parallel Behaviour : Pour qu'un agent puisse exécuter pls comportements à la fois
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("One Shot Behaviour");
            }
        });

        parallelBehaviour.addSubBehaviour(new WakerBehaviour(this, date) {
            @Override
            protected void onWake() {
                System.out.println("Warker behaviour ...");
            }
        });

        // Recevoir des message d'apres la boite a lettre de l'agent
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();  //Recevoir message
                /* On peut filtrer les message recevais  */
            /*    MessageTemplate messageTemplate =
                        MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage aclMessage2 = receive(messageTemplate); //on recoit seulement les msgs de type PROPOSE
                // Pour faire 2 conditions
                MessageTemplate messageTemplate =
                        MessageTemplate.and(
                            MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                            MessageTemplate.MatchLanguage("fr")
                        );
             */
                if(aclMessage!=null){
                    System.out.println("Celui qui envoie le message: "+aclMessage.getSender().getName());
                    System.out.println("Contenu du message: "+aclMessage.getContent());
                    System.out.println("L'acte de communication: "+ACLMessage.getPerformative(aclMessage.getPerformative()));
                    //Repondre celui qui envoie msg
                 /*   ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM); //type de msg
                    reply.addReceiver(aclMessage.getSender()); //le destinataire
                    reply.setContent("Prix=900");  //contenu du msg
                    send(reply); //L'envoie */

                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.CONFIRM:
                            consumerContainer.logMessage(aclMessage);
                            break;

                        default:
                            break;
                    }

                } else{
                    block();  //arreter CycleBehaviour jusqu'a recevais msg
                    System.out.println("block..........");
                }
            }
        });

    }

    /* S'exécute avant la migration de l'agent vers un autre container*/
    @Override
    protected void beforeMove() {
        try {
            System.out.println("Avant de se déplacer from "+this.getContainerController().getContainerName()); //Spécifier nom de conteneur
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    /* S'exécute avant la migration */
    @Override
    protected void afterMove() {
        try {
            System.out.println("Après de se déplacer vers "+this.getContainerController().getContainerName());
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    /* S'exécute juste avant le destruction de l'agent */
    @Override
    protected void takeDown() {
        System.out.println("je suis entraine de mourrir");
    }

    /* S'exécute quand il arrive un evenement */
    @Override
    protected void onGuiEvent(GuiEvent evt) {
        //Recuperer parametre nomLivre
        if(evt.getType()==1){   //Si le type de l'evenement egale 1
            String nomLivre = (String) evt.getParameter(0);
            /* L'envoie de message vers agent Vendeur de Livre */
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent("Je veux consommer livre: "+nomLivre+" ?");
            aclMessage.addReceiver(new AID("AcheteurLivreAgent", AID.ISLOCALNAME)); //Spécifier le nom local de l'agent destinateur
            send(aclMessage);
        }
    }
}
