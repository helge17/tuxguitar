#include <stdlib.h>
#include <string.h>
#include "LV2.h"
#include "LV2Plugin.h"

void LV2Plugin_malloc(LV2Plugin **handle, LV2World* world, const LilvPlugin* lilvPlugin)
{
	(*handle) = NULL;
	if( world != NULL && lilvPlugin != NULL ) {
		(*handle) = (LV2Plugin *) malloc(sizeof(LV2Plugin));
		(*handle)->world = world;
		(*handle)->lilvPlugin = lilvPlugin;

		(*handle)->portCount = lilv_plugin_get_num_ports((*handle)->lilvPlugin);
		(*handle)->ports = (LV2Port **) malloc(sizeof(LV2Port *) * ((*handle)->portCount));

		for (uint32_t i = 0; i < (*handle)->portCount; i ++) {
			(*handle)->ports[i] = (LV2Port *) malloc(sizeof(LV2Port));
			(*handle)->ports[i]->lilvPort = lilv_plugin_get_port_by_index((*handle)->lilvPlugin, i);
		}

		LilvNode* lv2_InputPort   = lilv_new_uri((*handle)->world->lilvWorld, LV2_CORE__InputPort);
		LilvNode* lv2_OutputPort  = lilv_new_uri((*handle)->world->lilvWorld, LV2_CORE__OutputPort);
		LilvNode* lv2_ControlPort = lilv_new_uri((*handle)->world->lilvWorld, LV2_CORE__ControlPort);
		LilvNode* lv2_AudioPort   = lilv_new_uri((*handle)->world->lilvWorld, LV2_CORE__AudioPort);
		LilvNode* lv2_AtomPort    = lilv_new_uri((*handle)->world->lilvWorld, LV2_ATOM__AtomPort);
		LilvNode* lv2_MidiEvent   = lilv_new_uri((*handle)->world->lilvWorld, LV2_MIDI__MidiEvent);
		
		for (uint32_t i = 0; i < (*handle)->portCount; i ++) {
			(*handle)->ports[i] = (LV2Port *) malloc(sizeof(LV2Port));
			(*handle)->ports[i]->lilvPort = lilv_plugin_get_port_by_index((*handle)->lilvPlugin, i);
			(*handle)->ports[i]->type = TYPE_UNKNOWN;
			(*handle)->ports[i]->flow = FLOW_UNKNOWN;

			// LV2PortFlow
			if( lilv_port_is_a((*handle)->lilvPlugin, (*handle)->ports[i]->lilvPort, lv2_InputPort)) {
				(*handle)->ports[i]->flow = FLOW_IN;
			}
			else if( lilv_port_is_a((*handle)->lilvPlugin, (*handle)->ports[i]->lilvPort, lv2_OutputPort)) {
				(*handle)->ports[i]->flow = FLOW_OUT;
			}

			// LV2PortType
			if( lilv_port_is_a((*handle)->lilvPlugin, (*handle)->ports[i]->lilvPort, lv2_ControlPort)) {
				(*handle)->ports[i]->type = TYPE_CONTROL;
			}
			else if( lilv_port_is_a((*handle)->lilvPlugin, (*handle)->ports[i]->lilvPort, lv2_AudioPort)) {
				(*handle)->ports[i]->type = TYPE_AUDIO;
			}
			else if( lilv_port_is_a((*handle)->lilvPlugin, (*handle)->ports[i]->lilvPort, lv2_AtomPort)) {
				if( lilv_port_supports_event((*handle)->lilvPlugin, (*handle)->ports[i]->lilvPort, lv2_MidiEvent)) {
					(*handle)->ports[i]->type = TYPE_MIDI;
				}
			}
		}

		lilv_node_free(lv2_InputPort);
		lilv_node_free(lv2_OutputPort);		
		lilv_node_free(lv2_ControlPort);
		lilv_node_free(lv2_AudioPort);
		lilv_node_free(lv2_AtomPort);
		lilv_node_free(lv2_MidiEvent);
	}
}

void LV2Plugin_free(LV2Plugin **handle)
{
	if( (*handle) != NULL ){
		(*handle)->world = NULL;
		(*handle)->lilvPlugin = NULL;
		
		free ( (*handle) );
		
		(*handle) = NULL;
	}
}

void LV2Plugin_getUri(LV2Plugin *handle, const char** value)
{
	(*value) = NULL;
	
	if( handle != NULL && handle->lilvPlugin != NULL ){
		const LilvNode* lilvNode = lilv_plugin_get_uri(handle->lilvPlugin);
		if( lilvNode != NULL ) {
			(*value) = lilv_node_as_string(lilvNode);
		}
	}
}

void LV2Plugin_getName(LV2Plugin *handle, const char** value)
{
	(*value) = NULL;

	if( handle != NULL && handle->lilvPlugin != NULL ){
		LilvNode* lilvNode = lilv_plugin_get_name(handle->lilvPlugin);
		if( lilvNode != NULL ) {
			(*value) = lilv_node_as_string(lilvNode);

			lilv_node_free(lilvNode);
		}
	}
}

void LV2Plugin_getPortIndex(LV2Plugin *handle, LV2Int32* index, const char* symbol) {
	(*index) = -1;
	
	for (LV2Int32 i = 0; i < handle->portCount; ++i) {
		const LilvNode* port_sym = lilv_port_get_symbol(handle->lilvPlugin, handle->ports[i]->lilvPort);

		if (!strcmp(lilv_node_as_string(port_sym), symbol)) {
			(*index) = i;
		}
	}
}

void LV2Plugin_getPortCount(LV2Plugin *handle, LV2PortType portType, LV2PortFlow portFlow, LV2Int32* count)
{
	(*count) = 0;
	
	if( handle != NULL && handle->ports != NULL ) {
		for (uint32_t i = 0; i < handle->portCount; i ++) {
			if( handle->ports[i]->type == portType && handle->ports[i]->flow == portFlow ) {
				(*count) ++;
			}
		}
	}
}

void LV2Plugin_getAudioInputPortCount(LV2Plugin *handle, LV2Int32* count) 
{
	LV2Plugin_getPortCount(handle, TYPE_AUDIO, FLOW_IN, count);
}

void LV2Plugin_getAudioOutputPortCount(LV2Plugin *handle, LV2Int32* count) 
{
	LV2Plugin_getPortCount(handle, TYPE_AUDIO, FLOW_OUT, count);
}

void LV2Plugin_getMidiInputPortCount(LV2Plugin *handle, LV2Int32* count) 
{
	LV2Plugin_getPortCount(handle, TYPE_MIDI, FLOW_IN, count);
}
