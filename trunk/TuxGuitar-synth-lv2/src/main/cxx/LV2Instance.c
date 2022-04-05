#include <stdlib.h>
#include <string.h>
#include "LV2.h"
#include "LV2Plugin.h"
#include "LV2Feature.h"
#include "LV2Logger.h"

void LV2Instance_malloc(LV2Instance **handle, LV2Plugin* plugin, LV2Feature* feature, LV2Int32 bufferSize)
{
	(*handle) = NULL;
	if( plugin != NULL && plugin->lilvPlugin != NULL ) {
		(*handle) = (LV2Instance *) malloc(sizeof(LV2Instance));
		(*handle)->plugin = plugin;
		(*handle)->lilvInstance = lilv_plugin_instantiate((*handle)->plugin->lilvPlugin, 44100.00, LV2Feature_getFeatures(feature));
		(*handle)->connections = (LV2PortConnection **) malloc(sizeof(LV2PortConnection) * ((*handle)->plugin->portCount));
		(*handle)->bufferSize = (uint32_t) bufferSize;
		(*handle)->midiEventType = LV2Feature_map(feature, LV2_MIDI__MidiEvent);

		for (uint32_t i = 0; i < (*handle)->plugin->portCount; i ++) {
			(*handle)->connections[i] = (LV2PortConnection *) malloc(sizeof(LV2PortConnection));
			(*handle)->connections[i]->dataLocation = NULL;
			
			if( (*handle)->plugin->ports[i]->type == TYPE_CONTROL ) {
				(*handle)->connections[i]->dataLocation = malloc(sizeof(float));
				
				LilvNode* defaultValue = NULL;
				lilv_port_get_range((*handle)->plugin->lilvPlugin, (*handle)->plugin->ports[i]->lilvPort, &defaultValue, NULL, NULL);
				*((float *) (*handle)->connections[i]->dataLocation) = lilv_node_as_float(defaultValue);
				lilv_node_free(defaultValue);
			}
			else if( (*handle)->plugin->ports[i]->type == TYPE_AUDIO ) {
				(*handle)->connections[i]->dataLocation = malloc(sizeof(float) * (*handle)->bufferSize);
				
				for(int s = 0 ; s < (*handle)->bufferSize; s ++) {
					((float *) (*handle)->connections[i]->dataLocation)[s] = 0.0f;
				}
			}
			else if( (*handle)->plugin->ports[i]->type == TYPE_MIDI ) {
				(*handle)->connections[i]->dataLocation = malloc(sizeof(LV2_Atom_Sequence));
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

void LV2Instance_setMidiMessages(LV2Instance *handle, unsigned char** messages, LV2Int32 length)
{
	if( handle != NULL && handle->plugin != NULL && handle->connections != NULL ) {
		for (LV2Int32 i = 0; i < handle->plugin->portCount; i ++) {
			if( handle->plugin->ports[i]->type == TYPE_MIDI && handle->plugin->ports[i]->flow == FLOW_IN ) {
				LV2Int32 offset = 0;
				LV2_Atom_Sequence* lv2_Atom_Sequence = (LV2_Atom_Sequence*) handle->connections[i]->dataLocation;
				lv2_Atom_Sequence->atom.size = 0;
				for(LV2Int32 m = 0 ; m < length ; m ++) {

					LV2_Atom_Event* lv2_Atom_Event = (LV2_Atom_Event*)((char*)LV2_ATOM_CONTENTS(LV2_Atom_Sequence, lv2_Atom_Sequence) + lv2_Atom_Sequence->atom.size);

					lv2_Atom_Event->time.frames = 0;
					lv2_Atom_Event->body.type = handle->midiEventType;
					lv2_Atom_Event->body.size = 4;
					memcpy(LV2_ATOM_BODY(&lv2_Atom_Event->body), messages[m], 4);

					lv2_Atom_Sequence->atom.size += ((sizeof(LV2_Atom_Event) + 4) + 7) & (~7);
				}
			}
		}
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
					for(int s = 0 ; s < handle->bufferSize; s ++) {
						targetBuffer[s] = sourceBuffer[s];
					}
				}
				inputsIndex ++;
			}
		}
		
		// process lv2 buffers 
		lilv_instance_run(handle->lilvInstance, handle->bufferSize);
		
		// copy outputs from lv2 buffer
		LV2Int32 outputsIndex = 0;
		LV2Int32 outputsLength = 0;
		LV2Plugin_getAudioOutputPortCount(handle->plugin, &outputsLength);
		for (uint32_t i = 0; i < handle->plugin->portCount; i ++) {
			if( handle->plugin->ports[i]->type == TYPE_AUDIO && handle->plugin->ports[i]->flow == FLOW_OUT ) {
				if( outputsIndex < outputsLength ) {
					float* sourceBuffer = (float*) handle->connections[i]->dataLocation;
					float* targetBuffer = (float*) outputs[outputsIndex];
					for(int s = 0 ; s < handle->bufferSize; s ++) {
						targetBuffer[s] = sourceBuffer[s];
					}
				}
				outputsIndex ++;
			}
		}
	}
}
