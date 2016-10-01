package examples;

public class Commitment {
	
	private String debtor;
	private String creditor;
	private String antecedent;
	private String consequent;
	private String status;

	public Commitment(){
		
	}

	public Commitment(String debtor, String creditor, String antecedent,
			String consequent, String status) {
		this.debtor = debtor;
		this.creditor = creditor;
		this.antecedent = antecedent;
		this.consequent = consequent;
		this.status = status;
	}

	
	
	public String getDebtor() {
		return debtor;
	}
	public void setDebtor(String debtor) {
		this.debtor = debtor;
	}
	public String getCreditor() {
		return creditor;
	}
	public void setCreditor(String creditor) {
		this.creditor = creditor;
	}
	public String getAntecedent() {
		return antecedent;
	}
	public void setAntecedent(String antecedent) {
		this.antecedent = antecedent;
	}
	public String getConsequent() {
		return consequent;
	}
	public void setConsequent(String consequent) {
		this.consequent = consequent;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
