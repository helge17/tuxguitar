/**
 * 
 */
package org.herac.tuxguitar.io.abc.base;

import java.util.Properties;

/**
 * @author peter
 *
 */
public class ABCOctaveDatabase {

	private Properties database;

	public ABCOctaveDatabase() {
		this.database=new Properties();
	}
	
	public void store(int i, int pitch) {
		this.database.setProperty(String.valueOf(pitch), String.valueOf(i));
	}

	public int recall(int pitch) {
		String r=this.database.getProperty(String.valueOf(pitch), "0");
		return Integer.parseInt(r);
	}

	public void reset() {
		this.database.clear();
	}

}
