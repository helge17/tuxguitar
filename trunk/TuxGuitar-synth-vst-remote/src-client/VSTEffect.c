#include <stdlib.h>
#include "VSTEffect.h"
#include "VSTPlugin.h"
#include "VSTPluginLoader.h"

void VSTEffect_malloc(VSTEffectHandle **handle, VSTPluginHandle *plugin)
{
	if( plugin != NULL && plugin->library != NULL ){
		(*handle) = (VSTEffectHandle *) malloc( sizeof(VSTEffectHandle) );
		(*handle)->effect = NULL;
		(*handle)->ui = NULL;
		
		VSTPluginMain( &(plugin->library) , &((*handle)->effect) , VSTPluginCallback );
		if( (*handle)->effect != NULL && (*handle)->effect->magic != kEffectMagic) {
			(*handle)->effect = NULL;
		}
	}
}

void VSTEffect_delete(VSTEffectHandle **handle)
{
	if( (*handle) != NULL) {
		if( (*handle)->effect != NULL){
			(*handle)->effect = NULL;
		}
		free ( (*handle) );
		
		(*handle) = NULL;
	}
}

void VSTEffect_openEffect(VSTEffectHandle *handle)
{
	if( handle != NULL && handle->effect != NULL){
		handle->effect->resvd1 = NULL;
		memcpy(&handle->effect->resvd1, &handle, sizeof(handle->effect->resvd1));
		handle->effect->dispatcher (handle->effect, effOpen, 0, 0, 0, 0);
	}
}

void VSTEffect_closeEffect(VSTEffectHandle *handle)
{
	if( handle != NULL && handle->effect != NULL){
		handle->effect->resvd1 = NULL;
		handle->effect->dispatcher (handle->effect, effClose, 0, 0, 0, 0);
		handle->effect = NULL;
	}
}

void VSTEffect_setActive(VSTEffectHandle *handle, int value)
{
	if( handle != NULL && handle->effect != NULL){
		int vstVersion = handle->effect->dispatcher (handle->effect, effGetVstVersion, 0, 0, NULL, 0);
		if( vstVersion >= 2 && value == 0 ) {
			handle->effect->dispatcher (handle->effect, effStopProcess, 0, value, NULL, 0);
		}
		
		handle->effect->dispatcher (handle->effect, effMainsChanged, 0, value, NULL, 0);
		
		if( vstVersion >= 2 && value == 1 ) {
			handle->effect->dispatcher (handle->effect, effStartProcess, 0, value, NULL, 0);
		}
	}
}

void VSTEffect_setUpdated(VSTEffectHandle *handle, bool value)
{
	if( handle != NULL ){
		handle->updated = value;
	}
}

void VSTEffect_getUpdated(VSTEffectHandle *handle, bool *value)
{
	if( handle != NULL ){
		(*value) = handle->updated;
	}
}

void VSTEffect_getNumParams(VSTEffectHandle *handle, int *value)
{
	if( handle != NULL && handle->effect != NULL){
		(*value) = handle->effect->numParams;
	}
}

void VSTEffect_getNumInputs(VSTEffectHandle *handle, int *value)
{
	if( handle != NULL && handle->effect != NULL) {
		(*value) = handle->effect->numInputs;
	}
}

void VSTEffect_getNumOutputs(VSTEffectHandle *handle, int *value)
{
	if( handle != NULL && handle->effect != NULL) {
		(*value) = handle->effect->numOutputs;
	}
}

void VSTEffect_setBlockSize(VSTEffectHandle *handle, int value)
{
	if( handle != NULL && handle->effect != NULL) {
		handle->effect->dispatcher (handle->effect, effSetBlockSize, 0, value, 0, 0);
	}
}

void VSTEffect_setSampleRate(VSTEffectHandle *handle, float value)
{
	if( handle != NULL && handle->effect != NULL) {
		handle->effect->dispatcher (handle->effect, effSetSampleRate, 0, 0, 0, value);
	}
}

void VSTEffect_setParameter(VSTEffectHandle *handle, int index, float value)
{
	if( handle != NULL && handle->effect != NULL) {
		handle->effect->setParameter(handle->effect, index, value);
	}
}

void VSTEffect_getParameter(VSTEffectHandle *handle, int index, float *value)
{
	if( handle != NULL && handle->effect != NULL) {
		(*value) = handle->effect->getParameter(handle->effect, index);
	}
}

void VSTEffect_getParameterName(VSTEffectHandle *handle, int index, const char* value)
{
	if( handle != NULL && handle->effect != NULL) {
		handle->effect->dispatcher (handle->effect, effGetParamName, index, 0, (void *) value, 0);
	}
}

void VSTEffect_getParameterLabel(VSTEffectHandle *handle, int index, const char* value)
{
	if( handle != NULL && handle->effect != NULL) {
		handle->effect->dispatcher (handle->effect, effGetParamLabel, index, 0, (void *) value, 0);
	}
}

void VSTEffect_setChunk(VSTEffectHandle *handle, int length, char** value)
{
	if( handle != NULL && handle->effect != NULL) {
		if (((handle->effect->flags & effFlagsProgramChunks) != 0)) {
			handle->effect->dispatcher (handle->effect, effSetChunk, 0, length, value, 0);
		}
	}
}

void VSTEffect_getChunk(VSTEffectHandle *handle, int* length, char** value)
{
	if( handle != NULL && handle->effect != NULL) {
		if (((handle->effect->flags & effFlagsProgramChunks) != 0)) {
			(*length) = handle->effect->dispatcher (handle->effect, effGetChunk, 0, 0, value, 0);
		} else {
			(*length) = 0;
		}
	}
}


void VSTEffect_beginSetProgram(VSTEffectHandle *handle)
{
	if( handle != NULL && handle->effect != NULL) {
		handle->effect->dispatcher (handle->effect, effBeginSetProgram, 0, 0, NULL, 0);
	}
}

void VSTEffect_endSetProgram(VSTEffectHandle *handle)
{
	if( handle != NULL && handle->effect != NULL) {
		handle->effect->dispatcher (handle->effect, effEndSetProgram, 0, 0, NULL, 0);
	}
}

void VSTEffect_sendMessages(VSTEffectHandle *handle, unsigned char** messages, int length)
{
	if( handle != NULL && handle->effect != NULL) {
		VstEvents *midi_events = (VstEvents *) malloc( (sizeof(VstEvents) - 2) + (sizeof(VstEvent *) * length) );
		midi_events->numEvents = length;
		midi_events->reserved = 0;

		for( int i = 0 ; i < midi_events->numEvents ; i ++ ) {
			midi_events->events[i] = (VstEvent *) malloc( sizeof( VstMidiEvent ) );

			((VstMidiEvent *)midi_events->events[i])->type = kVstMidiType;
			((VstMidiEvent *)midi_events->events[i])->byteSize = sizeof(VstMidiEvent);
			((VstMidiEvent *)midi_events->events[i])->deltaFrames = 0;
			((VstMidiEvent *)midi_events->events[i])->flags = 0;
			((VstMidiEvent *)midi_events->events[i])->noteLength = 0;
			((VstMidiEvent *)midi_events->events[i])->noteOffset = 0;
			((VstMidiEvent *)midi_events->events[i])->detune = 0;
			((VstMidiEvent *)midi_events->events[i])->noteOffVelocity = 0;
			((VstMidiEvent *)midi_events->events[i])->reserved1 = 0;
			((VstMidiEvent *)midi_events->events[i])->reserved2 = 0;
			((VstMidiEvent *)midi_events->events[i])->midiData[0] = (messages[i][0] | (unsigned char) messages[i][1]);
			((VstMidiEvent *)midi_events->events[i])->midiData[1] = messages[i][2];
			((VstMidiEvent *)midi_events->events[i])->midiData[2] = messages[i][3];
			((VstMidiEvent *)midi_events->events[i])->midiData[3] = 0;
		}
		
		handle->effect->dispatcher (handle->effect, effProcessEvents, 0, 0, midi_events, 0);
		
		delete midi_events;
	}
}

void VSTEffect_sendProcessReplacing(VSTEffectHandle *handle, float** inputs, float** outputs, int blockSize)
{
	if( handle != NULL && handle->effect != NULL) {
		handle->effect->processReplacing(handle->effect, inputs, outputs, blockSize);
	}
}
