#include <stdlib.h>
#include <string.h>
#include "LV2.h"
#include "LV2Instance.h"
#include "LV2Plugin.h"
#include "LV2Feature.h"
#include "LV2Logger.h"

void LV2Instance_malloc(LV2Instance **handle, LV2Plugin* plugin, LV2Feature* feature, LV2Config *config)
{
	(*handle) = NULL;
	if( plugin != NULL && plugin->lilvPlugin != NULL ) {
		(*handle) = (LV2Instance *) malloc(sizeof(LV2Instance));
		(*handle)->plugin = plugin;
		(*handle)->config = config;
		(*handle)->lilvInstance = lilv_plugin_instantiate((*handle)->plugin->lilvPlugin, (*handle)->config->sampleRate, LV2Feature_getFeatures(feature));
		(*handle)->connections = (LV2PortConnection **) malloc(sizeof(LV2PortConnection *) * ((*handle)->plugin->portCount));
		(*handle)->atomChunkType = LV2Feature_map(feature, LV2_ATOM__Chunk);
		(*handle)->atomSequenceType = LV2Feature_map(feature, LV2_ATOM__Sequence);
		(*handle)->midiEventType = LV2Feature_map(feature, LV2_MIDI__MidiEvent);

		for (uint32_t i = 0; i < (*handle)->plugin->portCount; i ++) {
			(*handle)->connections[i] = (LV2PortConnection *) malloc(sizeof(LV2PortConnection));
			(*handle)->connections[i]->dataLocation = NULL;
			(*handle)->connections[i]->workBuffer = NULL;
			
			if( (*handle)->plugin->ports[i]->type == TYPE_CONTROL ) {
				(*handle)->connections[i]->dataLocation = malloc(sizeof(float));
				
				LilvNode* defaultValue = NULL;
				lilv_port_get_range((*handle)->plugin->lilvPlugin, (*handle)->plugin->ports[i]->lilvPort, &defaultValue, NULL, NULL);
				*((float *) (*handle)->connections[i]->dataLocation) = lilv_node_as_float(defaultValue);
				lilv_node_free(defaultValue);
			}
			else if( (*handle)->plugin->ports[i]->type == TYPE_AUDIO ) {
				(*handle)->connections[i]->dataLocation = malloc(sizeof(float) * (*handle)->config->bufferSize);

				for(int s = 0 ; s < (*handle)->config->bufferSize; s ++) {
					((float *) (*handle)->connections[i]->dataLocation)[s] = 0.0f;
				}
			}
			else if( (*handle)->plugin->ports[i]->type == TYPE_EVENT ) {
				(*handle)->connections[i]->dataLocation = malloc((*handle)->config->eventBufferSize);
				(*handle)->connections[i]->workBuffer = malloc((*handle)->config->eventBufferSize);
				
				((LV2_Atom_Sequence *) (*handle)->connections[i]->dataLocation)->atom.size = 0;
				((LV2_Atom_Sequence *) (*handle)->connections[i]->workBuffer)->atom.size = 0;
			}
			
			if( (*handle)->connections[i]->dataLocation != NULL ) {
				lilv_instance_connect_port((*handle)->lilvInstance, i, (*handle)->connections[i]->dataLocation);
			}
		}

		lilv_instance_activate((*handle)->lilvInstance);
	}
}

void LV2Instance_free(LV2Instance **handle)
{
	if( (*handle) != NULL ){
		lilv_instance_deactivate((*handle)->lilvInstance);
		lilv_instance_free((*handle)->lilvInstance);
		
		for (uint32_t i = 0; i < (*handle)->plugin->portCount; i ++) {
			if( (*handle)->connections[i]->dataLocation != NULL ) {
				free ( (*handle)->connections[i]->dataLocation );
			}
			free ( (*handle)->connections[i] );
		}
		
		free ( (*handle)->connections );
		free ( (*handle) );

		(*handle) = NULL;
	}
}

void LV2Instance_getControlPortValue(LV2Instance *handle, LV2Int32 index, float* value)
{
	if( handle != NULL && handle->plugin != NULL && handle->connections != NULL ) {
		if( index >= 0 && index < handle->plugin->portCount && handle->plugin->ports[index]->type == TYPE_CONTROL && handle->connections[index]->dataLocation != NULL ) {
			(*value) = *((float *) handle->connections[index]->dataLocation);
		}
	}
}

void LV2Instance_setControlPortValue(LV2Instance *handle, LV2Int32 index, float value) 
{
	if( handle != NULL && handle->plugin != NULL && handle->connections != NULL ) {
		if( index >= 0 && index < handle->plugin->portCount && handle->plugin->ports[index]->type == TYPE_CONTROL && handle->connections[index]->dataLocation != NULL ) {
			*((float *) handle->connections[index]->dataLocation) = value;
		}
	}
}

void LV2Instance_getConnectionBuffer(LV2Instance *handle, LV2Int32 index, void** buffer)
{
	if( handle != NULL && handle->plugin != NULL && handle->connections != NULL ) {
		if( index >= 0 && index < handle->plugin->portCount && handle->plugin->ports[index]->type == TYPE_EVENT && handle->connections[index]->dataLocation != NULL ) {
			(*buffer) = handle->connections[index]->dataLocation;
		}
	}
}

void LV2Instance_getWorkBuffer(LV2Instance *handle, LV2Int32 index, void** buffer)
{
	if( handle != NULL && handle->plugin != NULL && handle->connections != NULL ) {
		if( index >= 0 && index < handle->plugin->portCount && handle->plugin->ports[index]->type == TYPE_EVENT && handle->connections[index]->workBuffer != NULL ) {
			(*buffer) = handle->connections[index]->workBuffer;
		}
	}
}

void LV2Instance_setMidiMessages(LV2Instance *handle, unsigned char** messages, LV2Int32 length)
{
	if( handle != NULL && handle->plugin != NULL && handle->connections != NULL ) {
		LV2_PLUGIN_PORT_BUFFER_FOREACH(handle, TYPE_EVENT, FLOW_IN, workBuffer, connectionBuffer, {
			LV2_Atom_Sequence* seq = (LV2_Atom_Sequence *) workBuffer;
			if( seq->atom.size == 0 ) {
				lv2_atom_sequence_clear(seq);
			}
			
			struct {
				LV2_Atom atom;
				uint8_t  msg[3];
			} midiEvent;

			for(LV2Int32 m = 0 ; m < length ; m ++) {
				midiEvent.atom.type = handle->midiEventType;
				midiEvent.atom.size = 3;
				midiEvent.msg[0]    = messages[m][0];
				midiEvent.msg[1]    = messages[m][1];
				midiEvent.msg[2]    = messages[m][2];
				
				LV2_Atom_Event* event = lv2_atom_sequence_end(&seq->body, seq->atom.size);
				memcpy((&event->body), &midiEvent, sizeof(midiEvent));
				seq->atom.size += lv2_atom_pad_size(sizeof(LV2_Atom_Event) + event->body.size);
			}
		})
	}
}

void LV2Instance_processAudio(LV2Instance *handle, float** inputs, float** outputs)
{
	if( handle != NULL && handle->plugin != NULL && handle->connections != NULL ) {
		// copy inputs to lv2 buffer
		LV2Int32 inputsIndex = 0;
		LV2Int32 inputsLength = 0;
		LV2Plugin_getAudioInputPortCount(handle->plugin, &inputsLength);
		for (uint32_t i = 0; i < handle->plugin->portCount; i ++) {
			if( handle->plugin->ports[i]->type == TYPE_AUDIO && handle->plugin->ports[i]->flow == FLOW_IN ) {
				if( inputsIndex < inputsLength ) {
					float* sourceBuffer = (float*) inputs[inputsIndex];
					float* targetBuffer = (float*) handle->connections[i]->dataLocation;
					if( targetBuffer != NULL ) {
						for(int s = 0 ; s < handle->config->bufferSize; s ++) {
							targetBuffer[s] = sourceBuffer[s];
						}
					}
				}
				inputsIndex ++;
			}
		}
		
		// copy events to lv2 buffer
		LV2_PLUGIN_PORT_BUFFER_FOREACH(handle, TYPE_EVENT, FLOW_IN, workBuffer, connectionBuffer, {
			lv2_atom_sequence_clear((LV2_Atom_Sequence *)connectionBuffer);
			LV2_ATOM_SEQUENCE_FOREACH((LV2_Atom_Sequence *) workBuffer, ev) {
				lv2_atom_sequence_append_event((LV2_Atom_Sequence *)connectionBuffer, handle->config->eventBufferSize, ev);
			}
			lv2_atom_sequence_clear((LV2_Atom_Sequence *)workBuffer);
		})
		
		LV2_PLUGIN_PORT_BUFFER_FOREACH(handle, TYPE_EVENT, FLOW_OUT, workBuffer, connectionBuffer, {
			((LV2_Atom_Sequence *)connectionBuffer)->atom.size = handle->config->eventBufferSize;
		})
		
		// process lv2 buffers 
		lilv_instance_run(handle->lilvInstance, handle->config->bufferSize);

		// copy events from lv2 buffer
		LV2_PLUGIN_PORT_BUFFER_FOREACH(handle, TYPE_EVENT, FLOW_OUT, workBuffer, connectionBuffer, {
			LV2_ATOM_SEQUENCE_FOREACH((LV2_Atom_Sequence *) connectionBuffer, ev) {
				LV2_Atom_Sequence* seq = (LV2_Atom_Sequence *) workBuffer;
				if( seq->atom.size == 0 ) {
					lv2_atom_sequence_clear(seq);
				}
				lv2_atom_sequence_append_event(seq, handle->config->eventBufferSize, ev);
			}
		})

		// copy outputs from lv2 buffer
		LV2Int32 outputsIndex = 0;
		LV2Int32 outputsLength = 0;
		LV2Plugin_getAudioOutputPortCount(handle->plugin, &outputsLength);
		
		for (uint32_t i = 0; i < handle->plugin->portCount; i ++) {
			if( handle->plugin->ports[i]->type == TYPE_AUDIO && handle->plugin->ports[i]->flow == FLOW_OUT ) {
				if( outputsIndex < outputsLength ) {
					float* sourceBuffer = (float*) handle->connections[i]->dataLocation;
					float* targetBuffer = (float*) outputs[outputsIndex];
					if( sourceBuffer != NULL ) {
						for(int s = 0 ; s < handle->config->bufferSize; s ++) {
							targetBuffer[s] = sourceBuffer[s];
						}
					}
				}
				outputsIndex ++;
			}
		}
	}
}
