package mclab1.sugar;

import com.orm.SugarRecord;

public class Owner extends SugarRecord<Owner> {

	public String uuid;
	public String userName;
	public String gender;
	public String locale;
	public String link;
	
	public Owner(){
		// TODO Auto-generated constructor stub
	}
	
	public Owner(String uuid, String userName, String gender, String locale, String link){
		this.uuid = uuid;
		this.userName = userName;
		this.gender = gender;
		this.locale = locale;
		this.link = link;
	}

}
