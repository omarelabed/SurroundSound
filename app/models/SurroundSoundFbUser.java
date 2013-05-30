package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;

import play.db.ebean.Model;

import ch.qos.logback.core.joran.action.ParamAction;

import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.FacebookType.Metadata;
import com.restfb.types.Event;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.User;
import com.restfb.types.User.Education;
import com.restfb.types.User.Sport;
import com.restfb.types.User.Work;

import controllers.SurroundSoundController;

@Entity
public class SurroundSoundFbUser extends Model {

	private static final long serialVersionUID = 1L;
	
//	@OneToMany
//	public SurroundSoundFbEvent event;
	public String bigAvatar;
	public String picture;
	@Id
	public Long eventId;
	@Id
	public Long id;
	public String name;
//	public String about;
//	public String bio;
//	public String birthday;
//	public Date birthdayAsDate;
//	public List<Education> education;
//	public String email;
//	public List<NamedFacebookType> athletes;
//	public List<NamedFacebookType> favoriteTeams;
//	public String firstName;
//	public String gender;
//	public NamedFacebookType hometown;
//	public String hometownName;
//	public List<String> interestedIn;
//	public List<NamedFacebookType> languages;
//	public String lastName;
//	public String link;
//	public String locale;
//	public NamedFacebookType location;
//	public List<String> meetingFor;
//	public Metadata metadata;
//	public String middleName;
//	public String name;
//	public String political;
//	public String quotes;
//	public String relationshipStatus;
//	public String religion;
//	public NamedFacebookType significantOther;
//	public List<Sport> sports;
//	public String thirdPartyId;
//	public Double timezone;
//	public Date updateTime;
//	public String username;
//	public Boolean verified;
//	public String website;
//	public List<Work> work;

	
//	@ManyToMany
//	public List<SurroundSoundFbEvent> events = new ArrayList<SurroundSoundFbEvent>();
	
//	public Long eventId;
	
	public static Finder<String, SurroundSoundFbUser> finder = new Finder<String, SurroundSoundFbUser>(String.class, SurroundSoundFbUser.class);
	
	public static List<SurroundSoundFbUser> all() {
		return finder.all();
	}

	public static SurroundSoundFbUser create(Long eventId, User user){
		return create(new SurroundSoundFbUser(eventId, user));
	}
	
	public static SurroundSoundFbUser create(SurroundSoundFbUser user) {
		try {
			user.save();
		} catch (PersistenceException e) {
			// TODO: handle exception
		}
		return user;
	}

	
	public SurroundSoundFbUser(Long eventId, User user){
//		this.eventId=eventId;
//		this.events.add(SurroundSoundFbEvent.get(eventId));
//		System.out.println("New SurroundSoundFbUser: "+user.getId()+" - "+user.getName());
		try {
//			System.out.println("\t>>>> fetching avatar...");
			JsonObject javatar = SurroundSoundController.FB_CLIENT.fetchObject(user.getId(), JsonObject.class, Parameter.with("fields", "picture.type(large)"));
//			System.out.println("\t>>>>javatar:"+javatar);
			this.bigAvatar=javatar.getJsonObject("picture").getJsonObject("data").getString("url");
//			System.out.println("\t\t"+this.bigAvatar);
		} catch (Exception e) {
			System.out.println("\t>>>>ERROR. NO PICTURE for "+user.getLink());
			e.printStackTrace();
			// TODO: handle exception
		}
		try {
//			this.event=SurroundSoundFbEvent.get(eventId);
//			this.about=user.getAbout();
//			this.bio=user.getBio();
//			this.birthday=user.getBirthday();
//			this.birthdayAsDate=user.getBirthdayAsDate();
//			this.education=user.getEducation();
//			this.email=user.getEmail();
//			this.athletes=user.getFavoriteAthletes();
//			this.favoriteTeams=user.getFavoriteTeams();
//			this.firstName=user.getFirstName();
//			this.gender=user.getGender();
//			this.hometown=user.getHometown();
//			this.hometownName=user.getHometownName();
			this.id=new Long(user.getId());
//			this.interestedIn=user.getInterestedIn();
//			this.languages=user.getLanguages();
//			this.lastName=user.getLastName();
//			this.link=user.getLink();
//			this.locale=user.getLocale();
//			this.location=user.getLocation();
//			this.meetingFor=user.getMeetingFor();
//			this.metadata=user.getMetadata();
//			this.middleName=user.getMiddleName();
			this.name=user.getName();
//			this.political=user.getPolitical();
//			this.quotes=user.getQuotes();
//			this.relationshipStatus=user.getRelationshipStatus();
//			this.religion=user.getReligion();
//			this.significantOther=user.getSignificantOther();
//			this.sports=user.getSports();
//			this.thirdPartyId=user.getThirdPartyId();
//			this.timezone=user.getTimezone();
//			this.updateTime=user.getUpdatedTime();
//			this.username=user.getUsername();
//			this.verified=user.getVerified();
//			this.website=user.getWebsite();
//			this.work=user.getWork();
//			this.picture = user.getMetadata().getConnections().getPicture();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}
