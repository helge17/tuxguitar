package app.tuxguitar.editor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.composition.TGDoubleBarAction;
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.io.tg.TGSongReaderImpl;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;


public class TestDoubleBar {

	private TGActionManager actionManager;
	private TGContext context;
	private TGSongManager songManager;
	private TGActionContext actionContext;

	public TestDoubleBar() throws IOException {
		this.context = new TGContext();
		this.actionManager = TGActionManager.getInstance(context);
		this.songManager = new TGSongManager();
		this.actionContext = new TGActionContext() {
		};
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, songManager);
	}

	@Test
	public void testDoubleBar() throws IOException {
		this.actionManager.mapAction(TGDoubleBarAction.NAME, new TGDoubleBarAction(context));
		this.actionManager.mapAction(TGRepeatOpenAction.NAME, new TGRepeatOpenAction(context));
		this.actionManager.mapAction(TGRepeatCloseAction.NAME, new TGRepeatCloseAction(context));

		TGSong song = this.readTestSong();
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		
		// normal bar, no conflict with repeat
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, song.getMeasureHeader(5));
		// create
		this.actionManager.execute(TGDoubleBarAction.NAME, this.actionContext);
		assertTrue(song.getMeasureHeader(5).isDoubleBar());
		// delete
		this.actionManager.execute(TGDoubleBarAction.NAME, this.actionContext);
		assertFalse(song.getMeasureHeader(5).isDoubleBar());
		
		// create double bar, conflict with repeat open
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, song.getMeasureHeader(0));
		assertFalse(song.getMeasureHeader(0).isRepeatOpen());// just checking scenario validity
		// create double bar
		this.actionManager.execute(TGDoubleBarAction.NAME, this.actionContext);
		assertTrue(song.getMeasureHeader(0).isDoubleBar());
		assertFalse(song.getMeasureHeader(1).isRepeatOpen());	// repeat open deleted

		// create double bar, conflict with repeat close
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, song.getMeasureHeader(3));
		assertTrue(song.getMeasureHeader(3).getRepeatClose() > 0);// just checking scenario validity
		// create double bar
		this.actionManager.execute(TGDoubleBarAction.NAME, this.actionContext);
		assertTrue(song.getMeasureHeader(3).isDoubleBar());
		assertFalse(song.getMeasureHeader(3).getRepeatClose() > 0);	// repeat close deleted

		// create repeat open, conflict with double bar
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, song.getMeasureHeader(5));
		this.actionManager.execute(TGDoubleBarAction.NAME, this.actionContext);
		assertTrue(song.getMeasureHeader(5).isDoubleBar());
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, song.getMeasureHeader(6));
		this.actionManager.execute(TGRepeatOpenAction.NAME, this.actionContext);
		assertFalse(song.getMeasureHeader(5).isDoubleBar());	// double bar deleted
		assertTrue(song.getMeasureHeader(6).isRepeatOpen());

		// create repeat close, conflict with double bar
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, song.getMeasureHeader(7));
		this.actionContext.setAttribute(TGRepeatCloseAction.ATTRIBUTE_REPEAT_COUNT, 1);
		this.actionManager.execute(TGDoubleBarAction.NAME, this.actionContext);
		assertTrue(song.getMeasureHeader(7).isDoubleBar());
		this.actionManager.execute(TGRepeatCloseAction.NAME, this.actionContext);
		assertFalse(song.getMeasureHeader(7).isDoubleBar());	// double bar deleted
		assertTrue(song.getMeasureHeader(7).getRepeatClose() > 0);

		// no double bar on last measure
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, song.getMeasureHeader(8));
		this.actionManager.execute(TGDoubleBarAction.NAME, this.actionContext);
		assertFalse(song.getMeasureHeader(8).isDoubleBar());
	}
	
	private TGSong readTestSong() throws IOException {
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setContext(new TGSongStreamContext());
		handle.setInputStream(getClass().getClassLoader().getResource("testDoubleBar_20.tg").openStream());
		handle.setFactory(new TGFactory());
		TGSongReaderImpl songReader = new TGSongReaderImpl();
		songReader.read(handle);
		return handle.getSong();
	}
	
}
