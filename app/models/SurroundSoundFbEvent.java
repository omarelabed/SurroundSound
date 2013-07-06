package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.*;

import org.codehaus.jackson.JsonNode;

import com.avaje.ebean.Query;
import com.restfb.Connection;
import com.restfb.Parameter;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Event;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.User;
import com.restfb.types.Venue;

import controllers.SurroundSoundController;

import play.Logger;
import play.db.ebean.Model;
import play.libs.Json;

@Entity
public class SurroundSoundFbEvent extends Model implements Comparator<SurroundSoundFbEvent>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	// facebook related data
	public String name;
	public String ownerName;
	public String ownerId;
	@Column(length=10000)
	public String description;
	public Calendar startTime;
	public Date endTime;
	public String location;
	public SurroundSoundVenue venue;
	public String rsvpStatus;
	public String privacy;
	public Date updateTime;

	// the page, or owner of the event
	public Long pageId;

	// cover
	public String coverId;
	public String coverSource;
	public String coverOffsetY;
	public String coverOffsetX;

	// date
	public String date;
	public String time;
	public String day;

	// cover
	public Long coverOffset;

	// additional data
	public int attendance;
	public String place;
	public String link;
	public int karma;
	// timeFlag can be either "past", "today" or "soon"
	public String timeFlag;

	public static Finder<Long, SurroundSoundFbEvent> finder = new Finder<Long, SurroundSoundFbEvent>(Long.class, SurroundSoundFbEvent.class);

	public static List<SurroundSoundFbEvent> all() {
		return finder.all();
	}

	// Given a SurroundSoundFBEvent, it will try to save it to the DB
	public static SurroundSoundFbEvent create(SurroundSoundFbEvent event) {
		if (event==null) Logger.warn("SurroundSoundFbEvent.create(): The given event is null!");
		try {
			event.save();
			Logger.info("Event "+event.id+": "+event.name+" has been saved.");
		} catch (PersistenceException e) {
			// If saving fails, it will attempt an update on an existing one with the same id
			Logger.info("I didn't save ["+event.id+": "+event.name+"] because it is probably already existing. I will try to update it instead...");
			try {
				SurroundSoundFbEvent oldEvent = SurroundSoundFbEvent.get(event.id);
				if (oldEvent==null) Logger.warn("SurroundSoundFbEvent.create(): I couldn't find the old event ["+event.id+"]: "+event.name);
				else if (diff(oldEvent, event)) {
					event.karma=oldEvent.karma;
					event.update();
					setUpdateFlag(event);
				}
			} catch (Exception e2) {
				Logger.warn("I couldn't update event ["+event.id+"]: "+event.name+"] ");
			}
		}
		return event;
	}
	
	// Refreshes the timeFlag of all the SourroundSoundFbEvent entities in the DB
	public static void refreshAllTimeFlags(){
		Iterator<SurroundSoundFbEvent> it = all().iterator();
		while(it.hasNext()){
			it.next().refreshTimeFlag();
		}
	}

	// refreshes the timeFlag of this event
	private void refreshTimeFlag() {
		this.setTimeFlag();
	}

	// sets the event relative updated time flag; according to the given event's timeFlag 
	private static void setUpdateFlag(SurroundSoundFbEvent event) {
		String fl = event.timeFlag;
		if (fl.equals("today")) SurroundSoundController.todayUpdated=true;
		if (fl.equals("soon")) SurroundSoundController.soonUpdated=true;
		else SurroundSoundController.pastUpdated=true;
	}

	// Sets the timeFlag of this event
	private void setTimeFlag() {
		this.timeFlag = computeTimeFlag(this.startTime);
		this.save();
	}

	// computes a timeFlag according to the given startTime
	private static String computeTimeFlag(Calendar startTime) {
		String timeFlag;
		Calendar now = Calendar.getInstance();
		int currentDay = now.get(Calendar.DAY_OF_MONTH);
		int currentMonth = now.get(Calendar.MONTH);
		int currentYear = now.get(Calendar.YEAR);
		Logger.info("currentDay = "+currentDay+"; currentMonth = "+currentMonth+"; currentYear = "+currentYear);
		
		int eDay = startTime.get(Calendar.DAY_OF_MONTH);
		int eMonth = startTime.get(Calendar.MONTH);
		int eYear = startTime.get(Calendar.YEAR);
		long timeInMillis = startTime.getTimeInMillis();
		Logger.info("\teDay = "+eDay+"; eMonth = "+eMonth+"; eYear = "+eYear+"; timeInMillis = "+timeInMillis);
		startTime.setTimeInMillis(timeInMillis);

		if (eYear<currentYear) timeFlag="past";
		else if ((eYear==currentYear)&&(eMonth<currentMonth)) timeFlag="past";
		else if ((eYear==currentYear)&&(eMonth==currentMonth)&&(eDay<currentDay)) timeFlag="past";
		else if ((currentDay==eDay)&&(currentMonth==eMonth)&&(currentYear==eYear)) timeFlag="today";
		else timeFlag="soon";
		return timeFlag;
	}

	private static boolean diff(SurroundSoundFbEvent oldEvent,
			SurroundSoundFbEvent newEvent) {
		if ((oldEvent==null)||(newEvent==null)) Logger.warn("SurroundSoundFbEvent.diff(): some input is null: oldEvent="+oldEvent+"; newEvent="+newEvent);
		boolean sameCoverSource = newEvent.coverSource.equals(oldEvent.coverSource);
		boolean sameDate = newEvent.date.equals(oldEvent.date);
		boolean sameDescription = newEvent.description.equals(oldEvent.description);
		boolean samePlace = newEvent.place.equals(oldEvent.place);
		if (!samePlace) Logger.warn("SurroundSoundFbEvent.diff(): place has changed on event ["+oldEvent.id+"] "+oldEvent.name+" - !samePlace = "+!samePlace);
		boolean sameName = newEvent.name.equals(oldEvent.name);
		boolean sameUpdateTime = (oldEvent.updateTime==null)||(newEvent.updateTime.equals(oldEvent.updateTime));
		boolean sameTimeFlag = (oldEvent.timeFlag.equals(newEvent.timeFlag));
		return !sameCoverSource||!sameDate||!sameDescription||!samePlace||!sameName||!sameUpdateTime||sameTimeFlag;
	}
	
	public static List<SurroundSoundFbEvent> getByTimeTag(String flag){
		ArrayList<SurroundSoundFbEvent> r = new ArrayList<SurroundSoundFbEvent>();
		Iterator<SurroundSoundFbEvent> it = all().iterator();
		while (it.hasNext()) {
			SurroundSoundFbEvent e = it.next();
			if (flag.equals(e.timeFlag)) r.add(e);
		}
		Collections.sort(r, new SurroundSoundFbEvent());
		return r;
	}
	
	public static List<SurroundSoundFbUser> initAttending(Long eventId){
		Logger.info("\t>> Fetching data about people attending (might take some time)");
		String s = String.valueOf(eventId);
		Connection<User> users =  SurroundSoundController.FB_CLIENT.fetchConnection(s+"/attending", User.class);
		List<User> usersList = users.getData();
		Iterator<User> it = usersList.iterator();
		List<SurroundSoundFbUser> attending = new ArrayList<SurroundSoundFbUser>();
		while (it.hasNext()){
			System.out.print(".");
			User u = it.next();
			attending.add(SurroundSoundFbUser.create(new SurroundSoundFbUser(eventId, u)));
		}
		System.out.println();
		return attending;
	}

	public static SurroundSoundFbEvent get(Long id){
		return finder.ref(id);
	}

	public SurroundSoundFbEvent(Long pageId, Event event){
//		Logger.info("New SurroundSoundFbEvent: ["+event.getId()+"] "+event.getName()+"\n\tDescription: "+event.getDescription());
		//		this.attending.addAll(initAttending(this.id));
		//		System.out.println("attending:"+this.attending.size());
		//		this.attendance = initAttending(this.id).size();
		//		fetchAttending(this.id);
//		this.attendance = getAttendance(event);

		this.id = new Long(event.getId());
		Venue v = event.getVenue();
		String loc = event.getLocation();
		if (v!=null) this.place=v.toString();
		else if (loc!=null) this.place=loc;
		else this.place="Lugano";

		this.name = event.getName();
		NamedFacebookType owner = event.getOwner();
		if (owner!=null){
			this.ownerName = event.getOwner().getName();
			this.ownerId = event.getOwner().getId();			
		}
		this.description = event.getDescription();
		try {
			JsonObject jdescr = SurroundSoundController.FB_CLIENT.fetchObject(event.getId(), JsonObject.class, Parameter.with("fields", "description"));
			JsonObject whatWeGet = SurroundSoundController.FB_CLIENT.fetchObject(event.getId(), JsonObject.class);
//			Logger.warn("jdescr: "+jdescr.toString());
//			Logger.warn("whatWeGet: "+whatWeGet.toString());
			this.description = jdescr.getString("description").replace("\n", "<br/>");
			Logger.warn("SurroundSoundFbEvent("+pageId+", ["+event.getId()+"] "+event.getName()+")"+this.description);
		} catch (Exception e) {
			Logger.warn("I couldn't get the description of the event ["+event.getId()+"]"+event.getName(), e);
			this.description = "No description available for this event...";
		}
		JsonNode jevent = Json.toJson(event);
//		System.out.println("\tjevent.description: "+jevent.get("description"));
		this.startTime = Calendar.getInstance();
		this.startTime.setTime(event.getStartTime());
		this.day = new SimpleDateFormat("EEE").format(event.getStartTime());
		this.date = new SimpleDateFormat("MMM dd yyyy").format(event.getStartTime());
		this.time = new SimpleDateFormat("HH:mm").format(event.getStartTime());

		this.endTime = event.getEndTime();
		this.link = "https://www.facebook.com/events/"+this.id;
		this.location = event.getLocation();
		this.rsvpStatus = event.getRsvpStatus();
		this.privacy = event.getPrivacy();
		this.updateTime = event.getUpdatedTime();
		this.pageId=pageId;
		this.coverSource="/assets/images/cover.png";
		String[] coverData=fetchCoverData(event.getId());
		this.coverId=coverData[0];
		this.coverSource=coverData[1];
		this.coverOffsetY=coverData[2];
		this.coverOffsetX=coverData[3];
		JsonObject jheight = SurroundSoundController.FB_CLIENT.fetchObject(coverId, JsonObject.class, Parameter.with("fields", "height"));
		this.coverOffset=(jheight.getLong("height")/100)*(new Long(this.coverOffsetY));
		this.karma = 0;
		this.setTimeFlag();
		this.updateTime=event.getUpdatedTime();
	}

	private int getAttendance(Event event) {
		JsonObject attending = SurroundSoundController.FB_CLIENT.fetchObject(event.getId(), JsonObject.class, Parameter.with("fields", "attending"));
		JsonArray data = attending.getJsonArray("data");
		return data.length();
	}

	private void fetchAttending(Long eventId) {
		initAttending(eventId);
	}

	public SurroundSoundFbEvent() {
		// TODO Auto-generated constructor stub
	}

	private String[] fetchCoverData(String eventId) {
//		System.out.println(">> fetchCoverData("+eventId+")");
		JsonObject cov = SurroundSoundController.FB_CLIENT.fetchObject(eventId, JsonObject.class, Parameter.with("fields", "cover"));
		String[] coverData = new String[5];
		coverData[1] = "/assets/images/cover.png";
		if (cov.has("cover")){
			JsonObject jcov = cov.getJsonObject("cover");
			coverData[0] = jcov.getString("cover_id");
			coverData[1] = jcov.getString("source");
			coverData[2] = jcov.getString("offset_y");
			coverData[3] = jcov.getString("offset_x");
		}
		System.out.println("\t"+coverData);
		return coverData;
	}

	private void init(Long eventId) {
		Logger.info("Initializing "+SurroundSoundFbEvent.get(eventId).name+"...");
		initAttending(eventId);
	}

	@Override
	public int compare(SurroundSoundFbEvent e1, SurroundSoundFbEvent e2) {
		return e1.startTime.compareTo(e2.startTime);
	}

	public static int getKarma(Long eventId) {
		return SurroundSoundFbEvent.get(eventId).karma;
	}

	public static void upVote(Long eventId) {
		SurroundSoundFbEvent.get(eventId).upVote();
	}

	private void upVote() {
		this.karma = this.karma+1;
	}

	public static void downVote(Long eventId) {
		SurroundSoundFbEvent.get(eventId).downVote();
		SurroundSoundFbEvent.get(eventId).save();
	}

	private void downVote() {
		this.karma=this.karma-1;
	}
}
