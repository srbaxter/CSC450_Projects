Savanna Baxter & Linda Vue 
Project 3
CSC 450/750

Below, we have stated how to test the three different scenarios. Please keep in mind that some of the facts will be printed out of order, depending on which agent gets to execute their assert commands first. However, if you base the timing based on the print statements that we put in ourselves (not the Jess facts), then the order of sequence is correct. 


To test the scenario where:
Buyer sends RFQ
Seller sends Quote
Buyer accepts quote
-Run the program normally, with the arguments of:
-gui b:examples.JessAgent"(buyer-agent)";s:examples.JessAgent"(seller-agent)"

The console should have printed the following statements for the accept scenario:
Agent name is seller-agent.
Agent name is buyer-agent.
 ==> Focus MAIN
 ==> Focus MAIN
 ==> f-0 (MAIN::initial-fact)
 ==> f-0 (MAIN::initial-fact)
==> Activation: MAIN::agent_details :  f-0
==> Activation: MAIN::agent_details :  f-0
FIRE 1 MAIN::agent_details f-0
FIRE 1 MAIN::agent_details f-0
 ==> f-1 (MAIN::myname (name "buyer-agent"))
 ==> f-1 (MAIN::myname (name "seller-agent"))
 ==> f-2 (MAIN::mytype (name "buyer"))
 ==> f-2 (MAIN::mytype (name "seller"))
 ==> f-3 (MAIN::agentstolookup (name "seller-agent"))
 ==> f-3 (MAIN::agentstolookup (name "buyer-agent"))
 ==> f-4 (MAIN::item (id 1) (name "Ball") (price 2.0))
 ==> f-4 (MAIN::item (id 1) (name "Ball") (price 2.0))
 <== Focus MAIN
 <== Focus MAIN
Registered agent seller-agent of type seller
Registered agent buyer-agent of type buyer
Hello! I am seller-agent. s@10.139.60.196:1099/JADE is ready.
Hello! I am buyer-agent. b@10.139.60.196:1099/JADE is ready.
 ==> f-5 (MAIN::rfq (receiver seller-agent) (itemID 1))
Buyer-agent has sent a RFQ to seller-agent
 ==> f-5 (MAIN::quotetosend (name "Ball") (itemID 1) (price 2.0))
Seller agent has sent the quote for the Ball of $2.0
seller-agent: Waiting to receive a message
 ==> f-6 (MAIN::acceptQuote (name "Ball") (itemID 1) (price 2.0))
Buyer-agent has accepted the quote for the Ball of $2.0)
buyer-agent: Waiting to receive a message


To test the scenario where:
Buyer sends RFQ
Seller send Quote
Buyer rejects quote
-In the seller-agent.clp file, on line 41, change the value of "price" to 10.00
-Then, run the program normally, with the arguments of:
-gui b:examples.JessAgent"(buyer-agent)";s:examples.JessAgent"(seller-agent)"

The console should have printed the following statements for the reject scenario:
Agent name is seller-agent.
Agent name is buyer-agent.
 ==> Focus MAIN
 ==> Focus MAIN
 ==> f-0 (MAIN::initial-fact)
 ==> f-0 (MAIN::initial-fact)
==> Activation: MAIN::agent_details :  f-0
==> Activation: MAIN::agent_details :  f-0
FIRE 1 MAIN::agent_details f-0
FIRE 1 MAIN::agent_details f-0
 ==> f-1 (MAIN::myname (name "buyer-agent"))
 ==> f-1 (MAIN::myname (name "seller-agent"))
 ==> f-2 (MAIN::mytype (name "seller"))
 ==> f-2 (MAIN::mytype (name "buyer"))
 ==> f-3 (MAIN::agentstolookup (name "buyer-agent"))
 ==> f-3 (MAIN::agentstolookup (name "seller-agent"))
 ==> f-4 (MAIN::item (id 1) (name "Ball") (price 10.0))
 ==> f-4 (MAIN::item (id 1) (name "Ball") (price 2.0))
 <== Focus MAIN
 <== Focus MAIN
Registered agent buyer-agent of type buyer
Registered agent seller-agent of type seller
Hello! I am buyer-agent. b@10.139.60.196:1099/JADE is ready.
Hello! I am seller-agent. s@10.139.60.196:1099/JADE is ready.
 ==> f-5 (MAIN::rfq (receiver seller-agent) (itemID 1))
Buyer-agent has sent a RFQ to seller-agent
 ==> f-5 (MAIN::quotetosend (name "Ball") (itemID 1) (price 10.0))
Seller agent has sent the quote for the Ball of $10.0
seller-agent: Waiting to receive a message
 ==> f-6 (MAIN::rejectQuote (name "Ball") (itemID 1) (price 10.0))
Buyer-agent has rejected the quote for the Ball of $10.0)
buyer-agent: Waiting to receive a message

To test the scenario where:
Buyer sends RFQ
Seller sends denyRequest
-In the buyer-agent.clp file, on line 42, change the value of "id" to 2
-Then, run the program normally, with the arguments of:
-gui b:examples.JessAgent"(buyer-agent)";s:examples.JessAgent"(seller-agent)"

The console should have printed the following statements for the deny request scenario:
Agent name is seller-agent.
Agent name is buyer-agent.
 ==> Focus MAIN
 ==> Focus MAIN
 ==> f-0 (MAIN::initial-fact)
 ==> f-0 (MAIN::initial-fact)
==> Activation: MAIN::agent_details :  f-0
==> Activation: MAIN::agent_details :  f-0
FIRE 1 MAIN::agent_details f-0
FIRE 1 MAIN::agent_details f-0
 ==> f-1 (MAIN::myname (name "seller-agent"))
 ==> f-1 (MAIN::myname (name "buyer-agent"))
 ==> f-2 (MAIN::mytype (name "buyer"))
 ==> f-2 (MAIN::mytype (name "seller"))
 ==> f-3 (MAIN::agentstolookup (name "buyer-agent"))
 ==> f-3 (MAIN::agentstolookup (name "seller-agent"))
 ==> f-4 (MAIN::item (id 1) (name "Ball") (price 10.0))
 ==> f-4 (MAIN::item (id 2) (name "Ball") (price 2.0))
 <== Focus MAIN
 <== Focus MAIN
Registered agent seller-agent of type seller
Registered agent buyer-agent of type buyer
Hello! I am seller-agent. s@10.139.60.196:1099/JADE is ready.
Hello! I am buyer-agent. b@10.139.60.196:1099/JADE is ready.
 ==> f-5 (MAIN::rfq (receiver seller-agent) (itemID 1))
Buyer-agent has sent a RFQ to seller-agent
 ==> f-5 (MAIN::denyRequest (itemID 2))
Seller agent has denied the request for the itemID of 2
seller-agent: Waiting to receive a message
buyer-agent: Waiting to receive a message

