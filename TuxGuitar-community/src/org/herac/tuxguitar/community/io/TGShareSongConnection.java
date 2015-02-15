package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.auth.TGCommunityAuth;
import org.herac.tuxguitar.util.TGContext;

public class TGShareSongConnection {
	
	public static final String HTTP_STATUS_OK = "200";
	public static final String HTTP_STATUS_UNAUTHORIZED = "401";
	public static final String HTTP_STATUS_INVALID = "400";

	private TGContext context;
	private TGCommunityAuth auth;
	private String status;
	
	public TGShareSongConnection(TGContext context){
		this.context = context;
		this.auth = TGCommunitySingleton.getInstance(this.context).getAuth();
		this.auth.update();
	}
	
	public void uploadFile( TGShareFile file , TGShareSong callback ) throws Throwable {
		TGShareSongRequest request = new TGShareSongRequest(this.context, this.auth, file);
		TGShareSongResponse response = request.getResponse();
		callback.processResult(response, file);
	}
	
	public String getStatus(){
		return this.status;
	}
}
