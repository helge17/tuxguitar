package org.herac.tuxguitar.android.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

import com.sun.media.sound.AudioFloatConverter;


public class TGMixer extends TGAbstractLine implements Mixer {
	
	public static final Mixer.Info MIXER_INFO = new TGMixerInfo();
	
	private List<DataLine.Info> sourceLineInfo;
	private List<DataLine.Info> targetLineInfo;
	
	private Map<Class<?>, Line> lines;
	
	public TGMixer() {
		super(new Line.Info(Mixer.class));
		
		this.lines = new HashMap<Class<?>, Line>();
		this.sourceLineInfo = new ArrayList<DataLine.Info>();
		this.targetLineInfo = new ArrayList<DataLine.Info>();
		
		List<AudioFormat> formats = new ArrayList<AudioFormat>();
		for (int channels = 1; channels <= 2; channels++) {
			formats.add(new AudioFormat(Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, 8, channels, channels, AudioSystem.NOT_SPECIFIED, false)); 
			formats.add(new AudioFormat(Encoding.PCM_UNSIGNED, AudioSystem.NOT_SPECIFIED, 8, channels, channels, AudioSystem.NOT_SPECIFIED, false));
				for (int bits = 16; bits < 32; bits += 8) {
					formats.add(new AudioFormat(Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, bits, channels, channels * bits / 8, AudioSystem.NOT_SPECIFIED, false));
					formats.add(new AudioFormat(Encoding.PCM_UNSIGNED, AudioSystem.NOT_SPECIFIED, bits, channels, channels * bits / 8, AudioSystem.NOT_SPECIFIED, false));
					formats.add(new AudioFormat(Encoding.PCM_SIGNED, AudioSystem.NOT_SPECIFIED, bits, channels, channels * bits / 8, AudioSystem.NOT_SPECIFIED, true));
					formats.add(new AudioFormat(Encoding.PCM_UNSIGNED, AudioSystem.NOT_SPECIFIED, bits, channels, channels * bits / 8, AudioSystem.NOT_SPECIFIED, true));
				}
				formats.add(new AudioFormat(AudioFloatConverter.PCM_FLOAT, AudioSystem.NOT_SPECIFIED, 32, channels, channels * 4, AudioSystem.NOT_SPECIFIED, false));
				formats.add(new AudioFormat(AudioFloatConverter.PCM_FLOAT, AudioSystem.NOT_SPECIFIED, 32, channels, channels * 4, AudioSystem.NOT_SPECIFIED, true));
				formats.add(new AudioFormat(AudioFloatConverter.PCM_FLOAT, AudioSystem.NOT_SPECIFIED, 64, channels, channels * 8, AudioSystem.NOT_SPECIFIED, false));
				formats.add(new AudioFormat(AudioFloatConverter.PCM_FLOAT, AudioSystem.NOT_SPECIFIED, 64, channels, channels * 8, AudioSystem.NOT_SPECIFIED, true));
			}
			
			this.sourceLineInfo.add(new DataLine.Info(SourceDataLine.class, formats.toArray(new AudioFormat[formats.size()]), AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED));
	}

	@Override
	public Mixer.Info getMixerInfo() {
		return MIXER_INFO;
	}

	@Override
	public Line.Info[] getSourceLineInfo() {
		return this.sourceLineInfo.toArray(new Line.Info[this.sourceLineInfo.size()]);
	}

	@Override
	public Line.Info[] getSourceLineInfo(Line.Info info) {
		List<Line.Info> infos = new ArrayList<Line.Info>();
		for(Line.Info sourceLineInfo : this.sourceLineInfo) {
			if( info.matches(sourceLineInfo) ) {
				infos.add(sourceLineInfo);
			}
		}
        return infos.toArray(new Line.Info[infos.size()]);
	}
	
	@Override
	public Line.Info[] getTargetLineInfo() {
		return this.targetLineInfo.toArray(new Line.Info[this.targetLineInfo.size()]);
	}

	@Override
	public Line.Info[] getTargetLineInfo(Line.Info info) {
		List<Line.Info> infos = new ArrayList<Line.Info>();
		for(Line.Info targetLineInfo : this.targetLineInfo) {
			if( info.matches(targetLineInfo) ) {
				infos.add(targetLineInfo);
			}
		}
        return infos.toArray(new Line.Info[infos.size()]);
	}

	public boolean isSourceLineSupported(Line.Info info) {
		if( info != null ) {
			for(Line.Info sourceLineInfo : this.sourceLineInfo) {
				if( info.matches(sourceLineInfo) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isTargetLineSupported(Line.Info info) {
		if( info != null ) {
			for(Line.Info targetLineInfo : this.targetLineInfo) {
				if( info.matches(targetLineInfo) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean isLineSupported(Line.Info info) {
		return (this.isSourceLineSupported(info) || this.isTargetLineSupported(info));
	}

	@Override
	public Line getLine(final Line.Info info) throws LineUnavailableException {
		if(!this.isLineSupported(info)) {
			throw new IllegalArgumentException("Line unsupported: " + info);
		}
		if((info.getLineClass() == SourceDataLine.class)) {
			return this.findOrCreateLine(TGSourceDataLine.class, new Callable<Line>() {
				public Line call() {
					return new TGSourceDataLine((DataLine.Info) info);
				}
			});
		}
		
		throw new IllegalArgumentException("Line unsupported: " + info);
	}

	public Line findOrCreateLine(Class<?> lineClass, Callable<Line> lineFactory) {
		try {
			if( this.lines.containsKey(lineClass) ) {
				return this.lines.get(lineClass);
			}
			
			this.lines.put(lineClass, lineFactory.call());
			
			return findOrCreateLine(lineClass, lineFactory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int getMaxLines(Line.Info info) {
		if( this.isLineSupported(info) ) {
			return AudioSystem.NOT_SPECIFIED;
		}
		return 0;
	}

	@Override
	public Line[] getSourceLines() {
		return this.getOpenedLines(SourceDataLine.class);
	}

	@Override
	public Line[] getTargetLines() {
        return this.getOpenedLines(TargetDataLine.class);
	}

	public Line[] getOpenedLines(Class<?> lineClass) {
		List<Line> lines = new ArrayList<Line>();
		for(Line line : this.lines.values()) {
			if( line.isOpen() && line.getLineInfo().getLineClass() == lineClass ) {
				lines.add(line);
			}
		}
        return lines.toArray(new Line[lines.size()]);
	}
	
	@Override
	public void synchronize(Line[] lines, boolean maintainSync) {
		throw new IllegalArgumentException("Synchronization not supported by this mixer.");
	}

	@Override
	public void unsynchronize(Line[] lines) {
		throw new IllegalArgumentException("Synchronization not supported by this mixer.");
	}

	@Override
	public boolean isSynchronizationSupported(Line[] lines, boolean maintainSync) {
		return false;
	}
	
	private static class TGMixerInfo extends Mixer.Info {
		
		private static final String MIXER_NAME = "TGMixer";
		private static final String MIXER_VENDOR = "org.herac.tuxguitar";
		private static final String MIXER_DESCRIPTION = "org.herac.tuxguitar";
		private static final String MIXER_VERSION = "1.0";
		
		public TGMixerInfo() {
			super(MIXER_NAME, MIXER_VENDOR, MIXER_DESCRIPTION, MIXER_VERSION);
		}
	}
}
