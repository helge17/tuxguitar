package org.herac.tuxguitar.player.impl.jsa.assistant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.jsa.midiport.MidiPortSynthesizer;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiConfigUtils;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

public class SBInstaller {

	private static final String SB_PREFIX = "soundbank";
	private static final String SB_EXTENSION = ".gm";
	
	private boolean cancelled;
	
	private TGContext context;
	
	private URL url;
	private File tmpPath;
	private File dstPath;
	
	private MidiPortSynthesizer synthesizer;
	private SBInstallerlistener listener;
	
	public SBInstaller(TGContext context, URL url,File tmpPath,File dstPath,MidiPortSynthesizer synthesizer, SBInstallerlistener listener){
		this.context = context;
		this.url = url;
		this.tmpPath = tmpPath;
		this.dstPath = dstPath;
		this.synthesizer = synthesizer;
		this.listener = listener;
		this.cancelled = false;
	}
	
	public void process(){
		File tmpFile = new File(this.tmpPath.getAbsolutePath() + File.separator + "soundbank.zip");
		boolean success = download(this.url, tmpFile);
		if(success && !isCancelled()){
			File sbFile = uncompress(tmpFile);
			if(!isCancelled() && sbFile != null){
				install(sbFile);
			}
		}
		if( tmpFile.exists() ){
			tmpFile.delete();
		}
		this.listener.notifyFinish();
	}
	
	private boolean download(URL url, File dst){
		try{
			this.listener.notifyProcess(TuxGuitar.getProperty("jsa.soundbank-assistant.process.downloading"));
			
			InputStream is = url.openStream();
			
			OutputStream os = new FileOutputStream(dst);
			
			byte[] buffer = new byte[1024];
			int length = 0;
			while(!isCancelled() && (length = is.read(buffer)) != -1 ){
				os.write(buffer,0,length);
			}
			is.close();
			os.flush();
			os.close();
			
			return true;
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return false;
	}
	
	private File uncompress(File file){
		try{
			this.listener.notifyProcess(TuxGuitar.getProperty("jsa.soundbank-assistant.process.uncompressing",new String[]{file.getAbsolutePath()}));
			
			if(file.exists()){
				File soundbank = null;
				
				ZipInputStream is = new ZipInputStream(new FileInputStream(file));
				ZipEntry entry = null;
				while( ( entry = is.getNextEntry() ) != null ){
					
					String name = entry.getName();					
					if(name.indexOf(SB_PREFIX) == 0 && name.indexOf(SB_EXTENSION) == (name.length() - SB_EXTENSION.length())){
						soundbank = new File(this.dstPath.getAbsolutePath() + File.separator + name);
						OutputStream os = new FileOutputStream(soundbank);
						byte[] buffer = new byte[1024];
						int length = 0;
						while( (length = is.read(buffer)) != -1){
							os.write(buffer,0,length);
						}
						os.flush();
						os.close();
					}
				}
				is.close();
				
				
				return soundbank;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	private void install(File file){
		try {
			this.listener.notifyProcess(TuxGuitar.getProperty("jsa.soundbank-assistant.process.installing",new String[]{file.getAbsolutePath()}));
			
			if( ! this.synthesizer.loadSoundbank(file) ){
				this.listener.notifyFailed(new MidiPlayerException(TuxGuitar.getProperty("jsa.error.soundbank.custom")));
				return;
			}
			
			TGConfigManager config = MidiConfigUtils.getConfig(this.context);
			config.setValue(MidiConfigUtils.SOUNDBANK_KEY,file.getAbsolutePath());
			config.save();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
