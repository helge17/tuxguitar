package app.tuxguitar.io.base;

import java.io.IOException;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGSongWriterHelper extends TGSongPersistenceHelper {

	public TGSongWriterHelper(TGContext context){
		super(context);
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try {
			boolean success = false;

			if( handle.getFormat() == null ) {
				handle.setFormat(this.detectFileFormat(handle));
			}

			if( handle.getFormat() != null ) {
				TGSongWriter writer = TGFileFormatManager.getInstance(this.getContext()).findSongWriter(handle.getFormat());
				if( writer != null ){
					this.leaveAudioOut(handle);
					writer.write(handle);
					success = true;
				}
			}

			if(!success) {
				throw new TGFileFormatException(TGFileFormatException.UNSUPPORTED_FORMAT);
			}
		} catch(TGFileFormatException tgFileFormatException) {
			throw tgFileFormatException;
		} catch(Throwable throwable) {
			throw new TGFileFormatException(throwable);
		} finally {
			try {
				handle.getOutputStream().close();
			} catch (IOException e) {
				throw new TGFileFormatException(e);
			}
		}
	}

	// a format other than TuxGuitar's cannot hold audio: its writer gets a copy of the song without the audio tracks (and
	// their channels); the song itself is left as it is
	private void leaveAudioOut(TGSongWriterHandle handle) {
		TGSong song = handle.getSong();
		if( song != null && !TGFileFormatManager.getInstance(this.getContext()).isNativeFileFormat(handle.getFormat()) ) {
			TGSongManager songManager = new TGSongManager();
			TGSong copy = song.clone(songManager.getFactory());
			for( int number = copy.countTracks(); number > 0; number -- ) {
				if( songManager.getTrack(copy, number).isAudio() ) {
					songManager.removeTrack(copy, number);
				}
			}
			handle.setSong(copy);
		}
	}

	public TGFileFormat detectFileFormat(TGSongWriterHandle handle) throws IOException {
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.getContext());

		String mimeType = handle.getContext().getAttribute(ATTRIBUTE_MIME_TYPE);
		if( mimeType != null ) {
			TGFileFormat fileFormat = fileFormatManager.findWriterFileFormatByMimeType(mimeType);
			if( fileFormat != null ) {
				return fileFormat;
			}
		}

		String formatCode = handle.getContext().getAttribute(ATTRIBUTE_FORMAT_CODE);
		if( formatCode != null ) {
			TGFileFormat fileFormat = fileFormatManager.findWriterFileFormatByCode(formatCode);
			if( fileFormat != null ) {
				return fileFormat;
			}
		}
		return null;
	}
}
