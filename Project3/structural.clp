
;=====================
; Goal Structural Rules
;=====================

(defrule failed
    "goal failed"
    (domainfact (name ?f))
    ?goal <- (Goal {status == "active" || status == "inactive" || status == "suspended"} (failurecondition ?f))
    =>
    (modify ?goal (status "failed")))


(defrule satisfied
    "goal satisfied"
    (domainfact (name ?s))
    ?goal <- (Goal {status == "active" || status == "inactive" || status == "suspended"} (successcondition ?s))
    =>
    (modify ?goal (status "satisfied")))

 
(defrule inactive
    "consider a goal"
    ?a <- (goalaction (name "consider")(successcondition ?s)(failurecondition ?f))
    =>
    (add (new Goal "inactive" ?s ?f))
	(retract ?a))

(defrule active
    "activate a goal"
    ?a <- (goalaction (name "activate")(successcondition ?cond))
    ?goal <- (Goal (status "inactive")(successcondition ?cond))
    =>
    (modify ?goal (status "active"))
	(retract ?a))

(defrule suspend
    "suspend a goal"
    ?a <- (goalaction (name "suspend")(successcondition ?cond))
    ?goal <- (Goal {status == "active" || status == "inactive"}(successcondition ?cond))
    =>
    (modify ?goal (status "suspended"))
	(retract ?a))

(defrule reconsider
    "reconsider a goal"
    ?a <- (goalaction (name "reconsider")(successcondition ?cond))
    ?goal <- (Goal (status "suspended")(successcondition ?cond))
    =>
    (modify ?goal (status "inactive"))
	(retract ?a))


(defrule reactivate
    "reactivate a goal"
    ?a <- (goalaction (name "reactivate")(successcondition ?cond))
    ?goal <- (Goal (status "suspended")(successcondition ?cond))
    =>
    (modify ?goal (status "active"))
	(retract ?a))


(defrule terminated
    "terminate a goal"
    ?a <- (goalaction (name "terminate")(successcondition ?cond))
    ?goal <- (Goal {status == "active" || status == "inactive" || status == "suspended"} (successcondition ?cond))
    =>
    (modify ?goal (status "terminated"))
	(retract ?a))


;===========================
; Commitment Structural Rules
;===========================

; splitting the conditional rule into two: if debtor creates a commitment in its working memory, 
; debtor sends a message to the creditor. Upon receiving the message the creditor asserts the commitment in 
; its working memory
(defrule conditional
    "create a commitment"
    ?ca <- (commitaction (name "create")(debtor ?n)(creditor ?c)(antecedent ?a)(consequent ?q))
    =>
    (add (new Commitment (str-cat ?n) (str-cat ?c) (str-cat ?a) (str-cat ?q) "conditional"))
    (retract ?ca))


(defrule detached
    "detach"
    (domainfact (name ?f))
    ?commit <- (Commitment (status "conditional")(antecedent ?f))
    =>
    (modify ?commit (status "detached")))

(defrule cSatisfied
    "commitment satisfied"
    (domainfact (name ?f))
    ?commit <- (Commitment {status == "conditional" || status == "detached"} (consequent ?f))
    =>
    (modify ?commit (status "satisfied")))

; to be completed
