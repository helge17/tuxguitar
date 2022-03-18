package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import java.util.List;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTBeginSetProgramCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTCloseEffectUICommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTEndSetProgramCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTGetChunkCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTGetNumInputsCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTGetNumOutputsCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTGetNumParamsCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTGetParameterCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTGetParameterLabelCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTGetParameterNameCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTGetVersionCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTIsEffectUIAvailableCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTIsEffectUIOpenCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTIsUpdatedCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTOpenEffectUICommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTProcessReplacingCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTSendMessagesCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTSetActiveCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTSetBlockSizeCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTSetChunkCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTSetParameterCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTSetSampleRateCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTStartProcessCommand;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTStopProcessCommand;

public final class VSTEffect {
	
	private VSTSession session;
	
	public VSTEffect(VSTSession session) {
		this.session = session;
	}
	
	public void close(){
		if(!this.isClosed() ){
			this.session.close();
		}		
	}
	
	public int getVersion(){
		if(!this.isClosed() ){
			return new VSTGetVersionCommand(this.getConnection()).safeProcess(0);
		}
		return 0;
	}
	
	public void setActive(boolean value){
		if(!this.isClosed() ){
			new VSTSetActiveCommand(this.getConnection(), value).safeProcess();
		}
	}
	
	public void startProcess(){
		if(!this.isClosed() ){
			new VSTStartProcessCommand(this.getConnection()).safeProcess();
		}
	}
	
	public void stopProcess(){
		if(!this.isClosed() ){
			new VSTStopProcessCommand(this.getConnection()).safeProcess();
		}
	}
	
	public void sendMessages(List<byte[]> messages){
		if(!this.isClosed() ){
			new VSTSendMessagesCommand(this.getConnection(), messages).safeProcess();
		}
	}
	
	public void sendProcessReplacing(float[][] inputs, float[][] outputs, int blocksize){
		if(!this.isClosed() ){
			new VSTProcessReplacingCommand(this.getConnection(), inputs, outputs, blocksize).safeProcess();
		}
	}
	
	public int getNumParams(){
		if(!this.isClosed() ){
			return new VSTGetNumParamsCommand(this.getConnection()).safeProcess(0);
		}
		return 0;
	}
	
	public int getNumInputs(){
		if(!this.isClosed() ){
			return new VSTGetNumInputsCommand(this.getConnection()).safeProcess(0);
		}
		return 0;
	}
	
	public int getNumOutputs(){
		if(!this.isClosed() ){
			return new VSTGetNumOutputsCommand(this.getConnection()).safeProcess(0);
		}
		return 0;
	}
	
	public void setBlockSize( int value ){
		if(!this.isClosed() ){
			new VSTSetBlockSizeCommand(this.getConnection(), value).safeProcess();
		}
	}
	
	public void setSampleRate( float value ){
		if(!this.isClosed() ){
			new VSTSetSampleRateCommand(this.getConnection(), value).safeProcess();
		}
	}
	
	public void setParameter( int index , float value ){
		if(!this.isClosed() ){
			new VSTSetParameterCommand(this.getConnection(), index, value).safeProcess();
		}
	}
	
	public float getParameter( int index ){
		if(!this.isClosed() ){
			return new VSTGetParameterCommand(this.getConnection(), index).safeProcess(0f);
		}
		return 0f;
	}
	
	public String getParameterName( int index ){
		if(!this.isClosed() ){
			return new VSTGetParameterNameCommand(this.getConnection(), index).safeProcess("");
		}
		return null;
	}
	
	public String getParameterLabel( int index ){
		if(!this.isClosed() ){
			return new VSTGetParameterLabelCommand(this.getConnection(), index).safeProcess("");
		}
		return null;
	}
	
	public void setChunk(byte[] chunk){
		if(!this.isClosed() ){
			new VSTSetChunkCommand(this.getConnection(), chunk).safeProcess();
		}
	}
	
	public byte[] getChunk(){
		if(!this.isClosed() ){
			return new VSTGetChunkCommand(this.getConnection()).safeProcess(null);
		}
		return null;
	}
	
	public void beginSetProgram(){
		if(!this.isClosed() ){
			new VSTBeginSetProgramCommand(this.getConnection()).safeProcess();
		}
	}
	
	public void endSetProgram(){
		if(!this.isClosed() ){
			new VSTEndSetProgramCommand(this.getConnection()).safeProcess();
		}
	}
	
	public void openNativeEditor(){
		if(!this.isClosed() ){
			new VSTOpenEffectUICommand(this.getConnection()).safeProcess();
		}
	}
	
	public void closeNativeEditor(){
		if(!this.isClosed() ){
			new VSTCloseEffectUICommand(this.getConnection()).safeProcess();
		}
	}
	
	public boolean isNativeEditorOpen(){
		if(!this.isClosed() ){
			return new VSTIsEffectUIOpenCommand(this.getConnection()).safeProcess(false);
		}
		return false;
	}
	
	public boolean isEditorAvailable(){
		if(!this.isClosed() ){
			return new VSTIsEffectUIAvailableCommand(this.getConnection()).safeProcess(false);
		}
		return false;
	}
	
	public boolean isUpdated(){
		if(!this.isClosed() ){
			return new VSTIsUpdatedCommand(this.getConnection()).safeProcess(false);
		}
		return false;
	}
	
	public boolean isClosed(){
		return this.session.isClosed();
	}
	
	public VSTSession getSession() {
		return this.session;
	}
	
	public VSTConnection getConnection() {
		return this.session.getConnection();
	}
}
