package org.herac.tuxguitar.app;

import org.herac.tuxguitar.app.util.ArgumentParser;

public class TGMain {
	
	public static void main(String[] args){
		ArgumentParser argumentParser = new ArgumentParser(args);
		if(argumentParser.processAndExit()){
			return;
		}
		
		TuxGuitar.getInstance().createApplication(argumentParser.getURL());
		System.exit(0);
	}	
}