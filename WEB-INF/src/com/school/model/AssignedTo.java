package com.school.model;



public class AssignedTo {
    private String user;
	private String updated;

    public AssignedTo(String user, String updated) {
        this.user = user;
		this.updated=updated;
    }

    public String getUser() {
        return user;
    }

	
	public String getUpdated() {
        return updated;
	}
	


	public String toString(){
		if(updated.equals("no"))
			return user+"(Deleted)";
		return user;
	}
}
