//import controllers.SurroundSoundController;
import java.util.ArrayList;
import java.util.Iterator;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;

import models.AppData;
import models.SurroundSoundOrganizer;

import controllers.SurroundSoundController;
import play.*;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("Application has started");
		Logger.info("Going to initialize");
		SurroundSoundController.init();
	}

	@Override
	public void onStop(Application app) {
		Logger.info("Application shutdown...");
	}
}