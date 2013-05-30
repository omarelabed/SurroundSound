package models;

import java.util.List;

import javax.persistence.*;

import com.restfb.types.Page.Cover;

import play.Logger;
import play.db.ebean.Model;

@Entity
public class SurroundSoundFbCover extends Model{

	private static final long serialVersionUID = 1L;

	public Integer offsetY;
	public String coverId;
	public String source;

	@Id
	public Long ownerId;

	public static Finder<Long, SurroundSoundFbCover> finder = new Finder<Long, SurroundSoundFbCover>(Long.class, SurroundSoundFbCover.class);

	public static List<SurroundSoundFbCover> all() {
		return finder.all();
	}

	public static SurroundSoundFbCover create(Long ownerId, Cover cover){
		return create(new SurroundSoundFbCover(ownerId, cover));
	}

	public static SurroundSoundFbCover create(SurroundSoundFbCover user) {
		try {
			user.save();
		} catch (PersistenceException e) {
			// TODO: handle exception
		}
		return user;
	}


	public SurroundSoundFbCover(Long ownerId, Cover cover) {
		if (cover!=null){
			try {
				this.coverId=cover.getCoverId();
				this.offsetY=cover.getOffsetY();
				this.source=cover.getSource();
				this.ownerId = ownerId;
			} catch (Exception e) {
				Logger.error("SurroundSoundFbCover: couldn't initialize object",e);
				// e.printStackTrace();
			}			
		} else Logger.warn("SurroundSoundFbCover("+ownerId+", "+cover+")");
	}

}
