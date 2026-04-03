package app.tuxguitar.action;

public interface TGActionInterceptor {

	public boolean intercept(String id, TGActionContext context) throws TGActionException;
}
