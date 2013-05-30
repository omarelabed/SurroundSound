package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class SurroundSoundOrganizer extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	public String name;
	
	public static Finder<Long, SurroundSoundOrganizer> finder = new Finder<Long, SurroundSoundOrganizer>(Long.class, SurroundSoundOrganizer.class);

	public static List<SurroundSoundOrganizer> all() {
		return finder.all();
	}

	public static SurroundSoundOrganizer get(Long id){
		return finder.ref(id);
	}

	
	public static SurroundSoundOrganizer addNew(SurroundSoundOrganizer organizer){
		organizer.save();
		return organizer;
	}
	
	public SurroundSoundOrganizer(String name){
		this.name=name;
	}
	
}
