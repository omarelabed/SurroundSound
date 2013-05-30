package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceException;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import com.restfb.types.Location;

@Entity
public class SurroundSoundLocation extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public String city;
	public String country;
	public Double latitude;
	public Double longitude;
	public String state;
	public String street;
	public String zip;

	public static Finder<String, SurroundSoundLocation> finder = new Finder<String, SurroundSoundLocation>(String.class, SurroundSoundLocation.class);
	
	public static List<SurroundSoundLocation> all() {
		return finder.all();
	}
	
	public static SurroundSoundLocation create(SurroundSoundLocation location) {
		try {
			location.save();
		} catch (PersistenceException e) {
			// TODO: handle exception
		}
		return location;
	}
	
	public SurroundSoundLocation(Location location) {
		try {
			this.city=location.getCity();
			this.country=location.getCountry();
			this.latitude=location.getLatitude();
			this.longitude=location.getLongitude();
			this.state=location.getState();
			this.street=location.getStreet();
			this.zip=location.getZip();			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
