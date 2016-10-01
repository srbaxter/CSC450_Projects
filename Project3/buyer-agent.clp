; Agent name holder
(deftemplate myname (slot name))

; Agent type holder
(deftemplate mytype (slot name))

; Domain fact represents an agent task: are asserted as agents bring them about
(deftemplate domainfact (slot name))

; Agent to lookup
(deftemplate agentstolookup (multislot name))

; Send an ACL message
(deftemplate sendmessage (slot receiver)(slot name))

(deftemplate message (slot name)) 

;Template for item
(deftemplate item (slot id(type INTEGER))(slot name)(slot price (type FLOAT)))

; Template for quote for an item
(deftemplate quote (slot name) (slot itemID (type STRING)) (slot price (type FLOAT)))

; Template for acceptQuote for an item
(deftemplate acceptQuote (slot name) (slot itemID (type STRING)) (slot price (type FLOAT)))

; Template for rejectQuote for an item
(deftemplate rejectQuote (slot name) (slot itemID (type STRING)) (slot price (type FLOAT)))

;Template for requestForQuote for an item
(deftemplate rfq(slot receiver)(slot itemID))


;======================
; Agent details
;======================

(defrule agent_details "agent details"
    => (assert (myname (name "buyer-agent")))   ; agent name
   	   (assert (mytype (name "buyer")))   ; agent name
       (assert (agentstolookup (name "seller-agent"))) ; agents to lookup
       (bind ?i (assert (item (id 1)(name "Ball")(price 2.00)))) ; item and it's details
    )

;===========================
; Standard jess setup
;===========================

(watch facts)
(watch all)
(reset) 

(run)



