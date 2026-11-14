package app.tuxguitar.io.synth;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

public class TGSynthAudioSettings {

	public static final AudioFormat DEFAULT_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
	public static final AudioFileFormat.Type DEFAULT_TYPE = AudioFileFormat.Type.WAVE;

	private AudioFileFormat.Type type;
	private AudioFormat format;
	private String export;
	private String extension;

	public TGSynthAudioSettings() {
		this.setType(DEFAULT_TYPE);
		this.setFormat(DEFAULT_FORMAT);
	}

	public AudioFileFormat.Type getType() {
		return this.type;
	}

	public void setType(AudioFileFormat.Type type) {
		this.type = type;
	}

	public AudioFormat getFormat() {
		return this.format;
	}

	public void setFormat(AudioFormat format) {
		this.format = format;
	}

	// a registered audio exporter's format (see TGSynthAudioExporter), with its files' extension; null: Java Sound's
	public String getExport() {
		return this.export;
	}

	public void setExport(String export, String extension) {
		this.export = export;
		this.extension = extension;
	}

	// the extension of the files written: the exporter's format's, else the type's
	public String getExtension() {
		return (this.export != null ? this.extension : this.type.getExtension());
	}
}
