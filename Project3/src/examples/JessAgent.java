/**
 * Savanna Baxter & Linda Vue
 * CSC 450/750 Project 3
 * 3/28/15
 * Please see the attached README file for instructions on how to run this project for the 3
 * scenarios of: accept, reject and deny requests. 
 */

package examples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Map;

import java.util.Iterator;

import jess.Fact;
import jess.Jesp;
import jess.JessException;
import jess.Rete;
import jess.Value;
import jess.ValueVector;

public class JessAgent extends Agent {
	
	Rete jess; // holds the pointer to jess

	private HashMap<String, AID> agentCache;
	
	private String agentName;
	private String agentType;
	private ArrayList<String> agentCapabilities;
	private ArrayList<String> agentsToLookup;
	
	
	static String buyerItemID = "";
	static String sellerItemID = "";
	static double bPrice = 0;
	static double sPrice = 0;
	static boolean quoteSent = false;
	static boolean rfq = false;


	protected void setup() {
		
		agentCapabilities = new ArrayList<String>();
		agentsToLookup = new ArrayList<String>();
		
		Object[] args = getArguments();
		
		if(args != null && args.length > 0){
		    agentName = (String) args[0];
		    System.out.println("Agent name is "+ agentName + ".");

		}else{
			System.out.print("Agent name not specified!!");
			doDelete();
		}

		// initialize jess
		initializeJess(agentName + ".clp");
		

		// read name, type, capabilities, and agents to lookup
		readAgentDetails();
		
		
		agentCache = new HashMap<String, AID>();
		

		

		// Register self
		registerSelf(agentType, agentName);
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		lookup(agentsToLookup);


		// add behavior
		this.addBehaviour(new JessAgentBehavior());

		// Printout a welcome message
		System.out.println("Hello! I am " + agentName + ". " + getAID().getName()
				+ " is ready.");
	}
	
	private void foo(Object next) {
		// TODO Auto-generated method stub
		
	}

	private void readAgentDetails(){
		try{
			Iterator<Fact> facts = jess.listFacts();

			while (facts.hasNext()) {
				Fact fact = facts.next();
				
				if (fact.getName().equals("MAIN::myname")) {
						agentName = fact.getSlotValue("name").stringValue(null);
				}
				if (fact.getName().equals("MAIN::mytype")) {
					agentType = fact.getSlotValue("name").stringValue(null);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	private void initializeJess(String jessFile) {
		jess = new Rete();

		// Open the file test.clp
		FileReader fr = null;
		try {
			fr = new FileReader(jessFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Create a parser for the file, telling it where to take input
		// from and which engine to send the results to
		Jesp j = new Jesp(fr, jess);

		// parse and execute one construct, without printing a prompt
		try {
			j.parse(false);
		} catch (JessException e) {
			e.printStackTrace();
		}
		
	}

	private void registerSelf(String type, String name) {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Registered agent " + name + " of type " + type);
	}

	private void lookup(List<String> names) {
		Iterator<String> nameIter = names.iterator();

		while (nameIter.hasNext()) {
			String name = nameIter.next();

			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setName(name);
			template.addServices(sd);

			try {
				DFAgentDescription[] result = DFService.search(this, template);
				if (result[0] != null) {
					agentCache.put(name, result[0].getName());
				}
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}
	}

	private class JessAgentBehavior extends CyclicBehaviour {

		public void action() {
			
			// act (inform about commitment operations and facts)
			processSendMessage();

			//reason
			executeJess();
			
			work();
			// perceive
			ACLMessage msg = perceive();

			// update beliefs

			// message structure
			// commit_oper, create, debtor, creditor, antecedent, consequent
			// fact, pay
			parseAndAssert(msg.getContent());

			// reason
			executeJess();

			// work (achieve goals)
			work();
			
			// reason
			executeJess();

			// act (inform about commitment operations and facts)
			processSendMessage();
			
			//reason
			executeJess();


		}
		
		private void processSendMessage(){
			try{
				Iterator<Fact> facts = jess.listFacts();
				while (facts.hasNext()) {
					Fact fact = facts.next();
					if (fact.getName().equals("MAIN::sendmessage")) {
							sendmessage(agentCache.get(fact.getSlotValue("receiver").stringValue(null)), 
										fact.getSlotValue("name").stringValue(null));
							jess.retract(fact);
						}
					}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		/**
		 * Does all the work for the seller and buyer agents. 
		 */
		private void work() {	
			// get goals from working memory
			try {
				
				//Work for buyer agent
				if (agentName.equals("buyer-agent")) {
					Iterator<Fact> facts = jess.listFacts();
					while (facts.hasNext()) {
						Fact fact = facts.next();
						
						if(fact.getName().equals("MAIN::item")) {
							buyerItemID = fact.getSlotValue("id").toString();
							bPrice = Double.parseDouble(fact.getSlotValue("price").toString());
							
							//waiting for the sellerItemID and sPrice to be not the empty string or 0 
							while(sellerItemID.equals("") && sPrice == 0) {
							}
							
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							} 
							
							//Sending RFQ
							jess.executeCommand("(assert (rfq (receiver seller-agent)(itemID 1)))");
							System.out.println("Buyer-agent has sent a RFQ to seller-agent");
							rfq = true;
					
						
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							} 
							
							//Conditionals for the 3 scenarios
							if (sellerItemID.equals(buyerItemID) && sPrice > bPrice) { //REJECT SCENARIO
								jess.executeCommand("(assert (rejectQuote (name \"Ball\") (itemID "+ sellerItemID + ") (price " + sPrice + ")))");
								System.out.println("Buyer-agent has rejected the quote for the Ball of $" + sPrice +")");
							} else if (sellerItemID.equals(buyerItemID) && bPrice <= sPrice) { //ACCEPT SCENARIO
								jess.executeCommand("(assert (acceptQuote (name \"Ball\") (itemID "+ sellerItemID + ") (price " + sPrice + ")))");
								System.out.println("Buyer-agent has accepted the quote for the Ball of $" + sPrice +")");
							} else if (!sellerItemID.equals(buyerItemID)) { //SELLER-AGENT DENIES REQUEST
								//Do nothing
							}
						}
					}
				} else if (agentName.equals("seller-agent")) { //Work for seller agent
					Iterator<Fact> facts = jess.listFacts();
					while (facts.hasNext()) {
						Fact fact = facts.next();
						if(fact.getName().equals("MAIN::item")) {
							//Waiting for buyer-agent to make the rfq first 
							while(!rfq && buyerItemID.equals("") && bPrice == 0) {
							}
							
							sellerItemID = fact.getSlotValue("id").toString();
							sPrice = Double.parseDouble(fact.getSlotValue("price").toString());
						
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							} 
							
							//Conditionals for the 3 scenarios 
							if (buyerItemID.equals(sellerItemID)) { //Seller has the item the buyer is looking for
								jess.executeCommand("(assert (quotetosend (name \"Ball\") (itemID "+ sellerItemID + ") (price " + sPrice + ")))");
								System.out.println("Seller agent has sent the quote for the Ball of $" + sPrice);
								quoteSent = true;
							} else if (!buyerItemID.equals(sellerItemID)) { //Seller does not have the item the buyer is looking for
								jess.executeCommand("(assert (denyRequest (itemID " + buyerItemID +")))");
								System.out.println("Seller agent has denied the request for the itemID of " + buyerItemID);
								quoteSent = true;
							}
						}
						
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void executeJess() {
			// run jessengine
			try {
					jess.run();				
			} catch (JessException re) {
				re.printStackTrace(System.err);
			}
		}

		private void parseAndAssert(String msgContent) {
			
			try {
					jess.executeCommand("(assert (message (name \"" + msgContent + "\")))");
				} catch (JessException e) {
					e.printStackTrace();
				}
			
		}

		private ACLMessage perceive() {
			
			System.out.println(agentName + ": Waiting to receive a message");

	        ACLMessage msg = myAgent.blockingReceive();

	        System.out.println(agentName + ": Message received: " + msg);
	        
			return msg;

		}

		private void sendmessage(AID receiver, String content) {

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

			msg.addReceiver(receiver);
			msg.setContent(content);
			myAgent.send(msg);

			System.out.println(agentName + ": Message sent: " + msg);
		}
	}
	
}
