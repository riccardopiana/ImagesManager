package beans;

import java.sql.Date;

public class Album {
	private int id;
	private String title;
	private String creator;
	private Date creationDate;
	
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date crationDate) {
		this.creationDate = crationDate;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
}
