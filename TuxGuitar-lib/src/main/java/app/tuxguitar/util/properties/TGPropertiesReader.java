package app.tuxguitar.util.properties;

public interface TGPropertiesReader {

	public void readProperties(TGProperties properties, String module) throws TGPropertiesException;

}
