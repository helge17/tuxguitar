package app.tuxguitar.editor.action.file;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.InMemoryResources;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.template.TGTemplate;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.song.models.TGSong;

/**
 * Pins TGLoadTemplateAction with a given template: the document takes the song of the template, through the load song
 * action; when the template cannot be read, the document takes a new song, through the new song action.
 *
 * Without a template the action reads the user's template from the user's home directory, deleting it when it cannot
 * be read, so that case is left untested.
 *
 * The bundled template "templates/blues.tg" holds a song named "Blues"; the document song reads back as
 * "name / track count".
 */
public class TestLoadTemplateAction {

	private final SongActionFixture fixture = new SongActionFixture();

	public TestLoadTemplateAction() throws TGFileFormatException {
		new InMemoryResources(fixture.context).put("templates/blues.tg", InMemoryResources.song("Blues"));
		TGDocumentManager.getInstance(fixture.context).getSong().setName("before");
		fixture.map(new TGLoadSongAction(fixture.context), new TGNewSongAction(fixture.context));
	}

	private String load(String resource) {
		TGTemplate template = new TGTemplate();
		template.setResource(resource);
		fixture.actionContext.setAttribute(TGLoadTemplateAction.ATTRIBUTE_TEMPLATE, template);
		fixture.execute(new TGLoadTemplateAction(fixture.context));
		TGSong song = TGDocumentManager.getInstance(fixture.context).getSong();
		return song.getName() + " / " + song.countTracks();
	}

	@TestFactory
	public List<DynamicTest> loadingATemplate() {
		return Arrays.asList(
			reads("the document takes the song of the template", "Blues / 1", () -> new TestLoadTemplateAction().load("blues.tg")),
			reads("an unreadable template gives a new song", " / 1", () -> new TestLoadTemplateAction().load("missing.tg")));
	}
}
