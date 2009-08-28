package org.herac.tuxguitar.community.io;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.tg.TGOutputStream;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGShareSong {

	public TGShareSong( ) {
		super();
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
		try {
			TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
				public void run() throws Throwable {
					TGShareFileDialog fileDialog = new TGShareFileDialog( file , errors );
					fileDialog.open();
					if( fileDialog.isAccepted() ){
						processUpload( file );
					}
				}
			});
		} catch (Throwable throwable ){
			MessageDialog.errorMessage(throwable);
		}
	}
	
	public void processAuthDialog( final TGShareFile file ) {
		try {
			TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
				public void run() throws Throwable {
					TGCommunityAuthDialog authDialog = new TGCommunityAuthDialog();
					authDialog.open();
					if( authDialog.isAccepted() ){
						processUpload( file );
					}
				}
			});
		} catch (Throwable throwable ){
			MessageDialog.errorMessage(throwable);
		}
	}
	
	public void processUpload( final TGShareFile file ) {
		this.setActiveMode();
		
		new Thread( new Runnable() {
			public void run() {
				try {
					TGShareSongConnection share = new TGShareSongConnection();
					share.uploadFile(file , TGShareSong.this );
				} catch (Throwable throwable ){
					MessageDialog.errorMessage(throwable);
				}
			}
		} ).start();
	}
	
	public void processResult( TGShareSongResponse response, TGShareFile file ){
		this.setPasiveMode();
		
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
				List messages = new ArrayList();
				response.loadMessages( messages );
				Iterator it = messages.iterator();
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
		TuxGuitar.instance().lock();
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		ActionLock.lock();
	}
	
	public void setPasiveMode(){
		ActionLock.unlock();
		TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
		TuxGuitar.instance().unlock();
	}
}
