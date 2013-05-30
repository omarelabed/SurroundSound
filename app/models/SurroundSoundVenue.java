package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.restfb.types.Venue;

import play.db.ebean.Model;

@Entity
public class SurroundSoundVenue extends Model {

	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	public String street;
	public String city;
	public String state;
	public String zip;
	public String country;
	public Double latitude;
	public Double longitude;
	
	public static Finder<Long, SurroundSoundVenue> finder = new Finder<Long, SurroundSoundVenue>(Long.class, SurroundSoundVenue.class);

	public static List<SurroundSoundVenue> all() {
		return finder.all();
	}
	
	public static SurroundSoundVenue create(SurroundSoundVenue venue) {
		venue.save();
		return venue;
	}
	
	public static SurroundSoundVenue get(Long id){
		return finder.ref(id);
	}

	public SurroundSoundVenue(String street, String city, String state, String zip, String country, Double latitude, Double longitude){
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public SurroundSoundVenue(Venue venue) {
		try {
			this.street = venue.getStreet();
			this.city = venue.getCity();
			this.state = venue.getState();
			this.zip = venue.getZip();
			this.country = venue.getCountry();
			this.latitude = venue.getLatitude();
			this.longitude = venue.getLongitude();			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}