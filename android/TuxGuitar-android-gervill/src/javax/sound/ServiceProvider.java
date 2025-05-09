package javax.sound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGServiceReader;

public class ServiceProvider {

	private static TGContext context;

	public static void setContext(TGContext context) {
		ServiceProvider.context = context;
	}

	public static List<?> getProviders(Class<?> providerClass) {
		List<Object> providers = new ArrayList<Object>();

		// Search available providers
		Iterator<?> it = TGServiceReader.lookupProviders(providerClass, TGResourceManager.getInstance(ServiceProvider.context));
		while( it.hasNext() ){
			providers.add(it.next());
		}
    	if( providers.isEmpty() ) {
		throw new RuntimeException("Invalid provider class: " + providerClass);
    	}

        return providers;
    }
}
