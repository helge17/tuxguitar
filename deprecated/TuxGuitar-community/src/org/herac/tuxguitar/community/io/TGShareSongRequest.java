package org.herac.tuxguitar.community.io;

import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.herac.tuxguitar.community.auth.TGCommunityAuth;
import org.herac.tuxguitar.community.utils.TGCommunityWeb;
import org.herac.tuxguitar.util.TGContext;

public class TGShareSongRequest {
	
	private static final String EOL =  "\r\n";
	private static final String BOUNDARY = "*****";
	private static final String BOUNDARY_SEPARATOR = "--";
	
	private TGContext context;
	private TGShareFile file;
	private TGCommunityAuth auth;
	
	public TGShareSongRequest(TGContext context, TGCommunityAuth auth , TGShareFile file){
		this.context = context;
		this.auth = auth;
		this.file = file;
	}
	
	public TGShareSongResponse getResponse() throws Throwable {
		URL url = new URL(TGCommunityWeb.getHomeUrl(this.context) + "/rd.php/sharing/tuxguitar/upload.do");
		URLConnection conn = url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+BOUNDARY);
		
		DataOutputStream out = new DataOutputStream( conn.getOutputStream() );
		
		// auth
		out.writeBytes(BOUNDARY_SEPARATOR + BOUNDARY + EOL);
		out.writeBytes("Content-Disposition: form-data; name=\"auth\";" + EOL);
		out.writeBytes(EOL);
		out.writeBytes(this.auth.getAuthCode());
		out.writeBytes(EOL);
		
		// title
		out.writeBytes(BOUNDARY_SEPARATOR + BOUNDARY + EOL);
		out.writeBytes("Content-Disposition: form-data; name=\"title\";" + EOL);
		out.writeBytes(EOL);
		out.writeBytes(this.file.getTitle());
		out.writeBytes(EOL);
		
		// description
		out.writeBytes(BOUNDARY_SEPARATOR + BOUNDARY + EOL);
		out.writeBytes("Content-Disposition: form-data; name=\"description\";" + EOL);
		out.writeBytes(EOL);
		out.writeBytes(this.file.getDescription());
		out.writeBytes(EOL);
		
		// tagkeys
		out.writeBytes(BOUNDARY_SEPARATOR + BOUNDARY + EOL);
		out.writeBytes("Content-Disposition: form-data; name=\"tagkeys\";" + EOL);
		out.writeBytes(EOL);
		out.writeBytes(this.file.getTagkeys());
		out.writeBytes(EOL);
		
		// file
		out.writeBytes(BOUNDARY_SEPARATOR + BOUNDARY + EOL);
		out.writeBytes("Content-Disposition: form-data; name=\"fileName\";" + " filename=\"" + this.file.getFilename() +"\"" + EOL);
		out.writeBytes(EOL);
		out.write( this.file.getFile() );
		out.writeBytes(EOL);
		
		out.writeBytes(BOUNDARY_SEPARATOR + BOUNDARY + BOUNDARY_SEPARATOR + EOL);
		out.flush();
		out.close();
		
		return new TGShareSongResponse( conn.getInputStream() );
	}
}
