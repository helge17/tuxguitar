package app.tuxguitar.util.singleton;

import app.tuxguitar.util.TGContext;

public interface TGSingletonFactory<T> {

	T createInstance(TGContext context);
}
