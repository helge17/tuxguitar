package app.tuxguitar.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.io.tg.TGSongWriterImpl;
import app.tuxguitar.resource.TGResourceLoader;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * The resources of a context, served from memory: only the ones put here exist.
 */
public class InMemoryResources implements TGResourceLoader {

	private final Map<String, byte[]> resources = new HashMap<String, byte[]>();

	public InMemoryResources(TGContext context) {
		TGResourceManager.getInstance(context).setResourceLoader(this);
	}

	public void put(String name, byte[] bytes) {
		resources.put(name, bytes);
	}

	public <T> Class<T> loadClass(String name) {
		return null;
	}

	public InputStream getResourceAsStream(String name) {
		byte[] bytes = resources.get(name);
		return (bytes == null ? null : new ByteArrayInputStream(bytes));
	}

	public URL getResource(String name) {
		return null;
	}

	public Enumeration<URL> getResources(String name) {
		return null;
	}

	// a new song with the given name, written in the TuxGuitar file format
	public static byte[] song(String name) throws TGFileFormatException {
		TGSongManager songManager = new TGSongManager();
		TGSong song = songManager.newSong();
		song.setName(name);
		return song(songManager, song);
	}

	// the song, written in the TuxGuitar file format
	public static byte[] song(TGSongManager songManager, TGSong song) throws TGFileFormatException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		TGSongWriterHandle handle = new TGSongWriterHandle();
		handle.setFactory(songManager.getFactory());
		handle.setSong(song);
		handle.setOutputStream(bytes);
		new TGSongWriterImpl().write(handle);
		return bytes.toByteArray();
	}
}
