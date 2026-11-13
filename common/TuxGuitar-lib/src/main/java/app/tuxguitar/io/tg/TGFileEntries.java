package app.tuxguitar.io.tg;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.util.TGVersion;

/**
 * The entries of a TuxGuitar file, read in one pass: version.txt read as soon as it comes (first, in the files TuxGuitar
 * writes) and refused there when the reader cannot read that version, content.xml kept as it is, each take's entry
 * decoded by the take codec as it streams by, once version.txt said format 3.0 (none without a codec), and whether any
 * other entry was there.
 */
final class TGFileEntries {

	// the version of a file, or why the reader cannot read it
	interface Versions {
		TGVersion readable(InputStream version) throws TGFileFormatException;
	}

	private TGVersion version;
	private byte[] content;
	private final Map<String, TGTake> takes = new HashMap<String, TGTake>();
	private boolean unknown;

	static TGFileEntries read(InputStream inputStream, TGTakeCodec codec, Versions versions) throws IOException, TGFileFormatException {
		TGFileEntries entries = new TGFileEntries();
		try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new BufferedInputStream(inputStream))) {
			for (ArchiveEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				entries.add(entry.getName(), zip, codec, versions);
			}
		}
		return entries;
	}

	private void add(String name, InputStream entry, TGTakeCodec codec, Versions versions) throws IOException, TGFileFormatException {
		if (TGStream.VERSION_FILE_NAME.equals(name)) {
			this.version = versions.readable(entry);
		} else if (TGStream.CONTENT_FILE_NAME.equals(name)) {
			this.content = IOUtils.toByteArray(entry);
		} else if (this.isTake(name, codec)) {
			this.takes.put(name, codec.read(entry));
		} else {
			this.unknown = true;
		}
	}

	private boolean isTake(String name, TGTakeCodec codec) {
		return this.hasAudio() && name.startsWith(TGAudioPart.TAKES) && name.endsWith("." + codec.getExtension());
	}

	// the version read says format 3.0, whose takes are entries (it is readable only with a take codec)
	boolean hasAudio() {
		return (this.version != null && this.version.getMajor() >= TGStream.AUDIO_FILE_FORMAT_TGVERSION.getMajor());
	}

	// none when the file has no version.txt
	TGVersion getVersion() {
		return this.version;
	}

	// none when the file has no content.xml
	InputStream getContent() {
		return (this.content != null ? new ByteArrayInputStream(this.content) : null);
	}

	// the takes read, unnumbered, by entry name
	Map<String, TGTake> getTakes() {
		return this.takes;
	}

	// an entry the file's version does not have (a take's in format 2.0)
	boolean hasUnknown() {
		return this.unknown;
	}
}
