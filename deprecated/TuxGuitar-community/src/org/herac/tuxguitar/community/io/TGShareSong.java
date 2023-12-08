package org.herac.tuxguitar.community.io;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;
import org.herac.tuxguitar.io.tg.TGSongWriterImpl;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorManager;

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
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}
	
	public void processDialog( final TGShareFile file , final String errors ) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				new TGShareFileDialog(getContext(), file, errors, new Runnable() {
					public void run() {
						processUpload(file);
					}
				}).open();
			}
		});
	}
	
	public void processAuthDialog( final TGShareFile file ) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() throws TGException {
				new TGCommunityAuthDialog(getContext(), new Runnable() {
					public void run() {
						processUpload(file);
					}
				}, null).open();
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
					TGErrorManager.getInstance(getContext()).handleError(throwable);
				}
			}
		} ).start();
	}
	
	public void processResult( TGShareSongResponse response, TGShareFile file ){
		this.setPassiveMode();
		
		try {
			String status = response.getStatus();
			if( status != null && status.equals(TGShareSongConnection.HTTP_STATUS_OK) ){
				TGMessageDialogUtil.infoMessage(this.context, "File Uploaded", "File upload completed!!");
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
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}
	
	private byte[] getSongBytes( TGSong song ) throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TGSongWriterHandle tgHandle = new TGSongWriterHandle();
		tgHandle.setContext(new TGSongStreamContext());
		tgHandle.setFactory(new TGFactory());
		tgHandle.setSong(song);
		tgHandle.setOutputStream(out);
		TGSongWriter tgStream = new TGSongWriterImpl();
		tgStream.write(tgHandle);
		out.close();
		return out.toByteArray();
	}
	
	public void setActiveMode(){
		TGWindow.getInstance(this.context).loadBusyCursor();
	}
	
	public void setPassiveMode(){
		TGWindow.getInstance(this.context).loadDefaultCursor();
	}

	public TGContext getContext() {
		return context;
	}
}
