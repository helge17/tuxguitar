package org.herac.tuxguitar.community.io;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.MessageDialog;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.tg.TGOutputStream;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGShareSong {

	private TGContext context;
	
	public TGShareSong(TGContext context) {
		this.context = context;
	}
	
	public void process( TGSong song ) {
		try {
			TGShareFile file = new TGShareFile();
			file.setFile( getSongBytes( song ) );
			this.processDialog(file , null );
		} catch (Throwable throwable ){
			MessageDialog.errorMessage(throwable);
		}
	}
	
	public void processDialog( final TGShareFile file , final String errors ) {
		TGSynchronizer.instance().executeLater(new Runnable() {
			public void run() {
				TGShareFileDialog fileDialog = new TGShareFileDialog(getContext(), file , errors);
				fileDialog.open();
				if( fileDialog.isAccepted() ){
					processUpload( file );
				}
			}
		});
	}
	
	public void processAuthDialog( final TGShareFile file ) {
		TGSynchronizer.instance().executeLater(new Runnable() {
			public void run() throws TGException {
				TGCommunityAuthDialog authDialog = new TGCommunityAuthDialog(getContext());
				authDialog.open();
				if( authDialog.isAccepted() ){
					processUpload( file );
				}
			}
		});
	}
	
	public void processUpload( final TGShareFile file ) {
		this.setActiveMode();
		
		new Thread( new Runnable() {
			public void run() throws TGException {
				try {
					TGShareSongConnection share = new TGShareSongConnection(getContext());
					share.uploadFile(file , TGShareSong.this );
				} catch (Throwable throwable ){
					MessageDialog.errorMessage(throwable);
				}
			}
		} ).start();
	}
	
	public void processResult( TGShareSongResponse response, TGShareFile file ){
		this.setPassiveMode();
		
		try {
			String status = response.getStatus();
			if( status != null && status.equals(TGShareSongConnection.HTTP_STATUS_OK) ){
				MessageDialog.infoMessage("File Uploaded", "File upload completed!!");
			}
			else if( status != null && status.equals(TGShareSongConnection.HTTP_STATUS_UNAUTHORIZED) ){
				processAuthDialog( file );
			}
			else if( status != null && status.equals(TGShareSongConnection.HTTP_STATUS_INVALID) ){
				String message = new String();
				List<String> messages = new ArrayList<String>();
				response.loadMessages( messages );
				Iterator<String> it = messages.iterator();
				while( it.hasNext() ){
					message += ( (String) it.next() + "\r\n" );
				}
				processDialog( file , message );
			}
			else{
				processDialog( file , "Error: " + status );
			}
		} catch (Throwable throwable ){
			MessageDialog.errorMessage(throwable);
		}
	}
	
	private byte[] getSongBytes( TGSong song ) throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TGOutputStreamBase tgStream = new TGOutputStream();
		tgStream.init( new TGFactory() , out );
		tgStream.writeSong(song);
		out.close();
		return out.toByteArray();
	}
	
	public void setActiveMode(){
		TuxGuitar.getInstance().lock();
		TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
	}
	
	public void setPassiveMode(){
		TuxGuitar.getInstance().loadCursor(SWT.CURSOR_ARROW);
		TuxGuitar.getInstance().unlock();
	}

	public TGContext getContext() {
		return context;
	}
}
