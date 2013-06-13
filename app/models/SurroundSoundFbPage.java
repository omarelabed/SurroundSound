/* AVAILABLE METHODS OF Class Page (http://restfb.com/javadoc/index.html)
	String	getAbout()
	General information about this page.
	String	getAccessToken()
	An admin access_token for this page.
	Boolean	getCanPost()
	Indicates whether the current session user can post on this page.
	Integer	getCheckins()
	The total number of users who have checked in to the Page.
	String	getCompanyOverview()
	Overview of the page's company.
	Page.Cover	getCover()
	The cover photo.
	String	getDescription()
	A description of this page.
	String	getFounded()
	When the page was founded.
	String	getGeneralInfo()
	The general information for a page.
	Boolean	getIsCommunityPage()
	Is this a community page?
	Boolean	getIsPublished()
	Indicates whether the page is published and visible to non-admins.
	Long	getLikes()
	The number of likes the page has.
	String	getLink()
	The page's link.
	Location	getLocation()
	The location of the place this page represents.
	String	getMission()
	The page's mission.
	String	getPhone()
	The phone number (not always normalized for country code) for the Page.
	String	getPicture()
	The page's picture.
	String	getProducts()
	The page's products.
	Long	getTalkingAboutCount()
	The number of people that are talking about this page (last seven days).
	String	getUsername()
	The page's username.
 */

package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.*;

import com.avaje.ebean.ValidationException;
import com.restfb.Connection;
import com.restfb.types.Event;
import com.restfb.types.FacebookType.Metadata;
import com.restfb.types.Page;

import controllers.SurroundSoundController;

import play.Logger;
import play.db.ebean.Model;

@Entity
public class SurroundSoundFbPage extends Model {

	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	public String name;
	
	public String about;
	public String accessToken;
	public String canPost;
	public String category;
	public Integer checkins;
	@Column(length=1000)
	public String companyOverview;
//	public SurroundSoundFbCover cover;
	@Column(length = 5000)
	public String description;
	public String founded;
	public String generalInfo;
	public Boolean isCommunityPage;
	public Boolean isPublished;
	public Long likes;
	public String link;
	public SurroundSoundLocation location;
	public Metadata metadata;
	public String mission;
	public String phone;
	public String pictures;
	@Column(length=1000)
	public String products;
	public Long talkingAboutCount;
	public String username;
//	public String troll;

//	@OneToMany(cascade=CascadeType.ALL)
//	public List<SurroundSoundFbEvent> events = new ArrayList<SurroundSoundFbEvent>();
	
	public static Finder<Long, SurroundSoundFbPage> finder = new Finder<Long, SurroundSoundFbPage>(Long.class, SurroundSoundFbPage.class);

	public static List<SurroundSoundFbPage> all() {
		return finder.all();
	}
	
	public static SurroundSoundFbPage create(SurroundSoundFbPage page) {
		try {
			page.save();
		} catch (ValidationException e) {
			e.printStackTrace();
			// TODO: handle exception
		} catch (PersistenceException e) {
//			page.refresh();
		}
		return page;
	}
	
	public static SurroundSoundFbPage create(Page page){
		SurroundSoundFbPage p = get(new Long(page.getId()));
		if (p==null){
//			System.out.println("new Page???");
			SurroundSoundFbPage sspage = new SurroundSoundFbPage(page);
			sspage.save();
			return create(sspage);
		} else return p;
	}
	
	public static SurroundSoundFbPage get(Long id){
		return finder.ref(id);
	}

	public static List<SurroundSoundFbEvent> initEvents(Long pageId){
		String s = String.valueOf(pageId);
		List<Event> events = SurroundSoundController.FB_CLIENT.fetchConnection(s+"/events", Event.class).getData();
		Iterator<Event> it = events.iterator();
		List<SurroundSoundFbEvent> eventlist = new ArrayList<SurroundSoundFbEvent>();
		System.out.println("EVENTS:"+events.size());
		while (it.hasNext()){
//			System.out.println("something...");
			Event event = it.next();
//			System.out.println("EVENTNAME: "+event.getName());
			SurroundSoundFbEvent ssevent = SurroundSoundFbEvent.create(new SurroundSoundFbEvent(pageId, event));
//			System.out.println("ssevent: "+ssevent.name);
			eventlist.add(ssevent);
		}
//		System.out.println("eventlist: "+eventlist);
		return eventlist;
	}
	
	public SurroundSoundFbPage(Page page){
//		if (page==null)
//		System.out.println("new Page: "+page.getName());
		try {
//			this.events.addAll(initEvents(new Long(page.getId())));
			this.about=page.getAbout();
			this.accessToken=page.getAccessToken();
			this.category=page.getCategory();
			this.checkins=page.getCheckins();
			this.companyOverview=page.getCompanyOverview();
//			this.cover= SurroundSoundFbCover.create(new SurroundSoundFbCover(new Long(page.getId()), page.getCover()));
//			this.description=page.getDescription();
			this.founded=page.getFounded();
			this.id=new Long(page.getId());
			this.isCommunityPage=page.getIsCommunityPage();
			this.isPublished=page.getIsPublished();
			this.likes=page.getLikes();
			this.link=page.getLink();
			this.location=new SurroundSoundLocation(page.getLocation());
			this.metadata=page.getMetadata();
			this.mission=page.getMission();
			this.name=page.getName();
			this.phone=page.getPhone();
			this.pictures=page.getPicture();
			this.products=page.getProducts();
			this.talkingAboutCount=page.getTalkingAboutCount();
			this.username=page.getUsername();
			// Fetch all the events of this page
			fetchEvents(this.id);
		} catch (Exception e) {
			Logger.warn("SurroundSoundFbPage(): couldn't instantiate on page ["+page.getId()+"] "+page.getName());
		}
	}

	private void fetchEvents(Long pageId) {
		if (pageId==null) Logger.warn("SurroundSoundFbPage.fetchEvents(): pageId="+pageId);
		Logger.info("**** fetchEvents("+pageId+")");
		String s = String.valueOf(pageId);
		Connection<Event> eventConn = SurroundSoundController.FB_CLIENT.fetchConnection(s+"/events", Event.class);
//		Logger.info("**** ("+pageId+") eventConn: "+eventConn.toString());
		Iterator<List<Event>> connIt = eventConn.iterator();
		while(connIt.hasNext()){
			List<Event> connection = connIt.next();
//			Logger.info("**** ("+pageId+") CONNECTION: "+connection);
			if (connection.size()>0){
				Event event = connection.get(0);
				if (event==null) Logger.warn("fetchEvents("+pageId+") in event is null:"+event+"\n\t full set of connections:"+connection);
				else SurroundSoundFbEvent.create(new SurroundSoundFbEvent(pageId, event));
//				Logger.info("**** ("+pageId+") Connection 0: "+event.toString());
			}
		}
//		List<Event> data = eventConn.getData();
//		Logger.info("data: "+data.toString());
////		List<Event> events = SurroundSoundController.FB_CLIENT.fetchConnection(s+"/events", Event.class).getData();
////		Iterator<Event> it = events.iterator();
//		Iterator<Event> it = data.iterator();
////		System.out.println("EVENTS:"+events.size());
//		while (it.hasNext()){
//			Event event = it.next();
////			System.out.println("Got "+event.getId()+": "+event.getName());
//			SurroundSoundFbEvent e = SurroundSoundFbEvent.create(new SurroundSoundFbEvent(pageId, event));
////			System.out.println("Created event: "+e.id+" - "+e.name);
//		}
	}
}
