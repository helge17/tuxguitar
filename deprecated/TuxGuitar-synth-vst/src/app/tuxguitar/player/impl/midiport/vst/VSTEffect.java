package app.tuxguitar.player.impl.midiport.vst;

import java.util.List;

import app.tuxguitar.midi.synth.remote.TGConnection;
import app.tuxguitar.midi.synth.remote.TGSession;
import app.tuxguitar.player.impl.midiport.vst.command.VSTBeginSetProgramCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTCloseEffectUICommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTEndSetProgramCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTFocusEffectUICommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTGetChunkCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTGetNumInputsCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTGetNumOutputsCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTGetNumParamsCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTGetParameterCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTGetParameterLabelCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTGetParameterNameCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTGetVersionCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTIsEffectUIAvailableCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTIsEffectUIOpenCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTIsUpdatedCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTOpenEffectUICommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTProcessReplacingCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTSendMessagesCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTSetActiveCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTSetBlockSizeCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTSetChunkCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTSetParameterCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTSetSampleRateCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTStartProcessCommand;
import app.tuxguitar.player.impl.midiport.vst.command.VSTStopProcessCommand;

public final class VSTEffect {

	private TGSession session;

	public VSTEffect(TGSession session) {
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

	public void focusNativeEditor(){
		if(!this.isClosed() ){
			new VSTFocusEffectUICommand(this.getConnection()).safeProcess();
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

	public TGSession getSession() {
		return this.session;
	}

	public TGConnection getConnection() {
		return this.session.getConnection();
	}
}
