package models;

import java.util.Calendar;
import java.util.List;


import play.db.ebean.Model;

import javax.persistence.*;

import controllers.SurroundSoundController;

@Entity
public class SurroundSoundLastUpdate extends Model{

	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	public Calendar lastUpdate;
	
	public static Finder<Long, SurroundSoundLastUpdate> finder = new Finder<Long, SurroundSoundLastUpdate>(Long.class, SurroundSoundLastUpdate.class);

	public static List<SurroundSoundLastUpdate> all() {
		return finder.all();
	}
		
	public static SurroundSoundLastUpdate get(Long id){
		return finder.ref(id);
	}

	
	public static boolean needsUpdate(){
		try {
			long last = SurroundSoundLastUpdate.get(new Long(1)).lastUpdate.getTimeInMillis();
			System.out.println("last: "+last);
			long now = Calendar.getInstance().getTimeInMillis();
			return (now > (last+SurroundSoundController.REFRESH_RATE));			
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}
	
	public SurroundSoundLastUpdate(){
		this.lastUpdate=Calendar.getInstance();
	}

	public static void update(SurroundSoundLastUpdate SurroundSoundLastUpdate) {
		try {
			all().get(0).lastUpdate=Calendar.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			try {
				SurroundSoundLastUpdate.save();
			} catch (Exception e1) {
				e1.printStackTrace();
				// TODO: handle exception
			}
		}
	}
	
}
