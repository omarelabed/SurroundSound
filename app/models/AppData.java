package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class AppData extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	public String appID;
	public String accessToken;
	public String accessTokenExpires;
	public String permissions;
	public String redirectURL;
	public String secret;
	
	public static Finder<Long, AppData> finder = new Finder<Long, AppData>(Long.class, AppData.class);

	public static List<AppData> all() {
		return finder.all();
	}

	public static AppData get(Long id){
		return finder.ref(id);
	}

	
	public static AppData addNew(AppData organizer){
		organizer.save();
		return organizer;
	}
	
	public AppData(String appID, String accessToken, String expires, String permissions, String redirectURL, String secret){
		this.appID = appID;
		this.accessToken = accessToken;
		this.accessTokenExpires = expires;
		this.permissions = permissions;
		this.redirectURL = redirectURL;
		this.secret = secret;
	}
}
