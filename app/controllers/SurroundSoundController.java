package controllers;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.persistence.Tuple;

import models.AppData;
import models.AppLogger;
import models.SurroundSoundFbEvent;
import models.SurroundSoundFbPage;
import models.SurroundSoundLastUpdate;
//import models.Location;
//import models.Parameters;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.*;
import play.libs.Json;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.*;
import play.mvc.WebSocket.Out;

import views.html.*;

import com.restfb.*;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.json.JsonObject;
import com.restfb.types.Event;
import com.restfb.types.Page;
import com.restfb.types.User;

public class SurroundSoundController extends Controller {

	public static final long REFRESH_RATE = 18000000; //18000000;// 3 minutes  // (In milliseconds)
	public static String appName = "SurroundSound";
     public static String wsAddress = "ws://pdnet.inf.unisi.ch:2222/SurroundSound/socket";
//    public static String wsAddress = "ws://localhost:9000/SurroundSound/socket";
	//display size: small(600x1080), big(1320x1080), fullscreen(1920x1080)

	public static HashMap<String, Sockets> displaySockets = new HashMap<String, Sockets>();
	public static HashMap<WebSocket.Out<JsonNode>, String> displaySocketReverter = new HashMap<WebSocket.Out<JsonNode>, String>();

	public static Calendar lastPushToAllClients = Calendar.getInstance();
	
	public static FacebookClient FB_CLIENT;

     private static String[] pageNamesArr = {
         "52722248335",
         "88585210034",
         "115823771813441",
         "122909424460471",
         "Acbess.GSV",
         "aiesec.lugano",
         "arteurbanalugano",
         "baroopslugano",
         "classcafelugano",
         "cluboneofficial",
         "estivaljazz",
         "estivalugano",
         "foce.lugano",
         "thejokershop",
         "longlakefestival",
         "lugano.buskers",
         "lugano.bynight",
         "luniversoOFFICIAL",
         "lusti.org",
         "milklugano",
//         "pastoraleuniversitaria.lugano",
         "thedjmarathon",
         "theoriginalslugano",
         "usiuniversity",
         "vivacafe.lugano",
         "wkndlugano"
     };
	
     public static ArrayList<String> pageNames = new ArrayList<String>(Arrays.asList(pageNamesArr));
	private static ArrayList<Long> pageIds = new ArrayList<Long>();
	private static ArrayList<SurroundSoundFbPage> ssFbPages = new ArrayList<SurroundSoundFbPage>();
	private static int lastCount = 0; 

	public static ArrayList<Long> getPageIds(){
		return pageIds;
	}

	public static Result test(String displayID, String size) {
		Result r = index(displayID, size);
//		pushToAllClients();
		return r;
	}

	public static Result index(String displayID, String size) {
		Logger.info(appName+".displayConnecting, displayId: "+displayID+" ,size: "+size);
		AppLogger.addNew(new AppLogger(appName, "displayConnecting", new Date().toString(), "", "null"));
		if(displayID == null) displayID = "99";
		if(size == null) size = "fullscreen";
		return ok(views.html.surroundSound.render(displayID, wsAddress, size));
	}

	public static void refresh(){
		fetchEvents();
		if (needsUpdate()){
			pushToAllClients();
		} else {
			Logger.warn("No update needed???");
		}
//		if (hasNewEvents()) push();
	}

	private static boolean needsUpdate() {
		return (pastUpdated||todayUpdated||soonUpdated||hasNewEvents()||pushIsOld());
	}

	private static boolean pushIsOld() {
		Calendar c = Calendar.getInstance();
		return ((lastPushToAllClients.get(Calendar.YEAR)<c.get(Calendar.YEAR))
				|| ((lastPushToAllClients.get(Calendar.YEAR)>=c.get(Calendar.YEAR))
						&& (lastPushToAllClients.get(Calendar.MONTH)<c.get(Calendar.MONTH)))
				|| ((lastPushToAllClients.get(Calendar.YEAR)>=c.get(Calendar.YEAR))
						&& (lastPushToAllClients.get(Calendar.MONTH)>=c.get(Calendar.MONTH))
						&& (lastPushToAllClients.get(Calendar.DAY_OF_YEAR)<c.get(Calendar.DAY_OF_MONTH))));
	}

	//	pushes an updated set of events to all the clients
	public static void pushToAllClients() {
		Logger.info("pushToAllClients()");
		Set<Entry<String, Sockets>> es = displaySockets.entrySet();
		Iterator<Entry<String, Sockets>> it = es.iterator();
		while(it.hasNext()){
			Entry<String, Sockets> e = it.next();
			Logger.info("push(): s.getKey()="+e.getKey()+"; s.getValue()="+e.getValue());
			Sockets s = e.getValue();
			ObjectNode msg = packFreshEvents();
			msg.put("writtenBy", "pushToAllClients");
//			msg.put("kind", "eventlist");
//			msg.put("kind", "update");
//			List<SurroundSoundFbEvent> all = SurroundSoundFbEvent.all();
//			Collections.sort(all, new SurroundSoundFbEvent());
//			JsonNode jall = Json.toJson(all);
//			msg.put("events", jall);
			s.wOut.write(msg);
		}
		lastPushToAllClients=Calendar.getInstance();
	}
	
	//	pushes all the events to the given client
	public static void pushEventsTo(Out<JsonNode> out, JsonNode event){
//		resetUpdatesFlags();
		ObjectNode msg = packFreshEvents();
		out.write(msg);

		String displayid = event.get("displayID").asText();
		if(displayid != "null"){
			displaySockets.put(event.get("displayID").asText(), new Sockets(out));
			displaySocketReverter.put(out, event.get("displayID").asText());
		}
		AppLogger.addNew(new AppLogger(appName, "displayNew", "data", "", "null"));

	}

//	private static void resetUpdatesFlags() {
//		pastUpdated=false;
//		todayUpdated=false;
//		soonUpdated=false;
//	}

	private static ObjectNode packFreshEvents() {
		ObjectNode msg = Json.newObject();
		msg.put("kind", "events");
		List<SurroundSoundFbEvent> all = SurroundSoundFbEvent.all();
		Collections.sort(all, new SurroundSoundFbEvent());
		
		Calendar filter = Calendar.getInstance();
		int currentDay = filter.get(Calendar.DAY_OF_MONTH);
		int currentMonth = filter.get(Calendar.MONTH);
		int currentYear = filter.get(Calendar.YEAR);
		Logger.info("currentDay = "+currentDay+"; currentMonth = "+currentMonth+"; currentYear = "+currentYear);
		
		List<SurroundSoundFbEvent> past = new ArrayList<SurroundSoundFbEvent>();
		List<SurroundSoundFbEvent> today = new ArrayList<SurroundSoundFbEvent>();
		List<SurroundSoundFbEvent> soon = new ArrayList<SurroundSoundFbEvent>();
		if (pastUpdated){
			past = SurroundSoundFbEvent.getByTimeTag("past");
			pastUpdated=false;
		} if (todayUpdated){
			today = SurroundSoundFbEvent.getByTimeTag("today");
			todayUpdated=false;
		} if (soonUpdated){
			soon = SurroundSoundFbEvent.getByTimeTag("soon");
			soonUpdated=false;
		}
		msg.put("past", Json.toJson(past));
		msg.put("today", Json.toJson(today));
		msg.put("soon", Json.toJson(soon));
		
		return msg;

//		Date currentDate = new Date();

//		while (allit.hasNext()){
//			SurroundSoundFbEvent e = allit.next();
//			String fl = e.timeFlag;
//			switch (fl) {
//			case "today": today.add(e);
//			case "soon": soon.add(e);
//			default: past.add(e);
//			}
			
//			Calendar startTime = e.startTime;
			
//			int eDay = startTime.get(Calendar.DAY_OF_MONTH);
//			int eMonth = startTime.get(Calendar.MONTH);
//			int eYear = startTime.get(Calendar.YEAR);
//			long timeInMillis = startTime.getTimeInMillis();
//            Logger.info("\teDay = "+eDay+"; eMonth = "+eMonth+"; eYear = "+eYear+"; timeInMillis = "+timeInMillis);
//			startTime.setTimeInMillis(timeInMillis);
//			Logger.info("\teDay = "+eDay+"; eMonth = "+eMonth+"; eYear = "+eYear+"; timeInMillis = "+timeInMillis);
//			
//			if (eYear<currentYear) past.add(e);
//            else if ((eYear==currentYear)&&(eMonth<currentMonth)) past.add(e);
//            else if ((eYear==currentYear)&&(eMonth==currentMonth)&&(eDay<currentDay)) past.add(e);
//			else if ((currentDay==eDay)&&(currentMonth==eMonth)&&(currentYear==eYear)) today.add(e);
//            else soon.add(e);
//		}
//		JsonNode jPast = Json.toJson(past);
//		msg.put("past", jPast);
//		JsonNode jToday = Json.toJson(today);
//		msg.put("today", jToday);
//		JsonNode jSoon = Json.toJson(soon);
//		msg.put("soon", jSoon);
//		return null;
	}

	private static boolean hasNewEvents() {
		int n = SurroundSoundFbEvent.all().size();
		boolean b = n>lastCount;
		if (b) lastCount=n;
		return b;
//		return true;
	}

	public static void startEventsScheduler(){
		Logger.info("eventsScheduler() ---- START SCHEDULER ---");
		final ScheduledFuture<?> refreshHandler = scheduler.scheduleAtFixedRate(refresher, 5, 10, TimeUnit.MINUTES);
	}

	//should end with the application
	public static void stopEventsScheduler(){
		Logger.info("eventsScheduler() ---- STOP SCHEDULER ---");
		scheduler.shutdown();
	}

	public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static boolean SurroundSoundFbEventUpdate;
	public static boolean todayUpdated = true;
	public static boolean soonUpdated = true;
	public static boolean pastUpdated = true;
	final static Runnable refresher = new Runnable() {
		public void run() {
			Logger.info("eventsScheduler(): checking for new events");
			refresh();
		}
	};

	public static WebSocket<JsonNode> webSocket() { 
		return new WebSocket<JsonNode>() {

			// Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out){

				//				System.out.println("************ stuff");
				// For each event received on the socket
				in.onMessage(new Callback<JsonNode>() {

					public void invoke(JsonNode event) {

						String messageKind = event.get("kind").asText();
						
						if(messageKind.equals("upvote")){
							Long eventId = new Long(event.get("eventId").asText());
							Logger.info("Upvote for event "+eventId+" - current karma: "+SurroundSoundFbEvent.getKarma(eventId));
							SurroundSoundFbEvent.upVote(eventId);
						}
						if(messageKind.equals("downvote")){
							Long eventId = new Long(event.get("eventId").asText());
							Logger.info("Downvote for event "+eventId+" - current karma: "+SurroundSoundFbEvent.getKarma(eventId));
							SurroundSoundFbEvent.downVote(eventId);
							Logger.info(eventId+"'s karma is "+SurroundSoundFbEvent.getKarma(eventId));
						}

						if (messageKind.equals("appReady")) {
							Logger.info("********* GOT MESSAGE REQUEST for the events *********");
							//							if(!displaySockets.containsKey(event.get("diplayID"))){
							Logger.info(appName+".Socket(): new displayID="+event.get("displayID")+" size="+event.get("size"));
							pushEventsTo(out, event);
						}

						if(messageKind.equals("eventlist")){
							Logger.info("********* GOT MESSAGE REQUEST for the events *********");
							if(!displaySockets.containsKey(event.get("displayID"))){

								Logger.info(appName+".Socket(): new displayID="+event.get("displayID")+" size="+event.get("size"));

								ObjectNode msg = Json.newObject();
								msg.put("kind", "eventlist");

								List<SurroundSoundFbEvent> all = SurroundSoundFbEvent.all();
								Collections.sort(all, new SurroundSoundFbEvent());
								JsonNode jall = Json.toJson(all);
								msg.put("events", jall);
								out.write(msg);

								String displayid = event.get("displayID").asText();
								if(displayid != "null"){
									displaySockets.put(event.get("displayID").asText(), new Sockets(out));
									displaySocketReverter.put(out, event.get("displayID").asText());
								}
								AppLogger.addNew(new AppLogger(appName, "eventList", "data", "", "null"));
							}
						}

						if(messageKind.equals("interaction")){
							Logger.info("Got message of kind "+messageKind+":\n\t"+event.toString());
							try {
								// Example: {
								//	"kind":"interaction",
								//	"displayID":99,
								String displayID = event.get("displayID").asText();
								//	"eventId":"294722627325192",
								String eventId = event.get("eventId").asText();
								//  "type":"showDetails",
								String type = event.get("type").asText();
								//  "timestamp":"2013-05-28T10:14:21.263Z"
								String timestamp= event.get("timestamp").asText();
								// }
								Logger.info("Display "+displayID+": someone is interacting with the display. "+event.toString());
								String content = type+" @ "+eventId;
								AppLogger.addNew(new AppLogger(appName, messageKind, timestamp, content, displayID));	
							} catch (Exception e) {
								Logger.error("Couldn't log an interaction of "+event.toString(), e);
							}
						}
						
						if(messageKind.equals("appClose")){
							Logger.info(appName+".webSocket(): appClose - displayID="+event.get("displayID")+" size="+event.get("size"));
						}
					}
				});//in.onMessage

				// When the socket connection is closed. 
				in.onClose(new Callback0() {
					public void invoke() { 
						String displayID =displaySocketReverter.get(out);
						displaySocketReverter.remove(out);
						displaySockets.remove(displayID);
						AppLogger.addNew(new AppLogger(appName, "displayDisconect", new Date().toString(),"", displayID));
						Logger.info(appName+".Socket(): display "+displayID+" is disconnected");// number of connected displays: "+displaySockets.size());
					}
				});
			}
		};
	} 

	public static class Sockets {
		public WebSocket.Out<JsonNode> wOut;

		public Sockets(Out<JsonNode> out) {
			this.wOut = out;
		}
	}
	//WS --------------------------------------------------------------------


	public static void fetchEvents(){
		Logger.info("Fetching events...");
		initPageIds();
		collectData();
	}

     private static void initPageIds() {
         Logger.info("initPageIds()");
         int l = pageNames.size();
         for (int i = 0; i < l; i++) {
             String pagename = pageNames.get(i);
             try {
                 Page page = FB_CLIENT.fetchObject(pagename, Page.class);
                 String idString = page.getId();
                 pageIds.add(new Long(idString));
                 Logger.info("\tgot "+pagename+"'s id: "+idString);
             } catch (FacebookOAuthException e) {
//                 Logger.error("I couldn't get the ID of "+pagename,e);
                 Logger.warn("We might need a fresh AccessToken! Please check its validity");
//                 refreshAccessToken();
                 return;
             }
         }
     }

	// from http://stackoverflow.com/questions/1485708/how-do-i-do-a-http-get-in-java
//	private static void refreshAccessToken() {
//		AccessToken at = SurroundSoundController.FB_CLIENT.obtainAppAccessToken(SurroundSoundController.appID, SurroundSoundController.secret);
//		Logger.info("****** ACCESS TOKEN : "+at.getAccessToken());
//	}

	private static void collectData() {
		Logger.info("collectData()");
		collectPages();
	}

	private static void collectPages() {
		Logger.info("collectPages()");
		java.util.Iterator<Long> it = pageIds.iterator();
		while(it.hasNext()){
			Long id = it.next();
			Page page = FB_CLIENT.fetchObject(String.valueOf(id), Page.class);
			if (page!=null){
				SurroundSoundFbPage.create(new SurroundSoundFbPage(page));
			}
		}
	}

	public static void init() {
		String at = "";
		try {
			Logger.warn(AppData.all().toString());
			at = AppData.get(new Long(1)).accessToken;
		} catch (Exception e) {
			Logger.error("Can't initialize a DefaultFacebookClient",e);
			Logger.warn("Please check the presence of the access token in the DB");
		}
		try {
			FB_CLIENT= new DefaultFacebookClient(at);
		} catch (Exception e) {
			// TODO: handle exception
		}
		startEventsScheduler();
	}
}