package examples;

public class Goal {
	
	private String status;
	private String successcondition;
	private String failurecondition;
		
	public Goal(String status, String successcondition, String failurecondition){
		this.status = status;
		this.successcondition = successcondition;
		this.failurecondition = failurecondition;
	}
	
	public String getStatus(){
		return status;
	}
	
	public String getSuccesscondition(){
		return successcondition;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public void setSuccesscondition(String successcondition){
		this.successcondition = successcondition;
	}

	public String getFailurecondition() {
		return failurecondition;
	}

	public void setFailurecondition(String failurecondition) {
		this.failurecondition = failurecondition;
	}

}
