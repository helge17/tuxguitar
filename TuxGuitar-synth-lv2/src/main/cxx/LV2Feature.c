#include <stdlib.h>
#include <string.h>
#include "LV2.h"
#include "LV2Feature.h"
#include "LV2Instance.h"
#include "LV2Worker.h"
#include "LV2Logger.h"

void LV2Feature_malloc(LV2Feature **handle, LV2Config *config)
{
	(*handle) = (LV2Feature *) malloc(sizeof(LV2Feature));
	(*handle)->features = NULL;
	(*handle)->config = config;
	(*handle)->worker = NULL;
	(*handle)->uriMap = (LV2URILinkedMap*) malloc(sizeof(LV2URILinkedMap));
	(*handle)->uriMap->sequence = 1;
	(*handle)->uriMap->next = NULL;

	LV2Worker_malloc(&(*handle)->worker);
}

void LV2Feature_free(LV2Feature **handle)
{
	if( (*handle) != NULL ) {
		LV2Worker_free(&(*handle)->worker);

		if( (*handle)->uriMap != NULL) {
			LV2URILink* prev = NULL;
			LV2URILink* next = (*handle)->uriMap->next;
			while( next != NULL ) {
				prev = next;
				next = next->next;

				free( (void *) prev->uri );
				free( prev );
			}
			free ((*handle)->uriMap);
		}
		
		if( (*handle)->features != NULL ) {
			LV2_Feature** iterator = (*handle)->features;
			while((*iterator) != NULL ) {
				free ((*iterator)->data );
				free ((*iterator));
				
				iterator ++;
			}
			free ((*handle)->features);
		}

		free ((*handle));

		(*handle) = NULL;
	}
}

void LV2Feature_init(LV2Feature *handle, LV2Instance *instance)
{
	if( handle != NULL ) {
		// log required features: /////////////////////////////////////////////////////////////////////
		LilvNodes* requiredFeatures = lilv_plugin_get_required_features(instance->plugin->lilvPlugin);
		LV2Logger_log("LV2Feature_init -> Required features:\n");
		LILV_FOREACH(nodes, f, requiredFeatures) {
			LV2Logger_log("    %s\n", lilv_node_as_uri(lilv_nodes_get(requiredFeatures, f)));
		}
		lilv_nodes_free(requiredFeatures);
		///////////////////////////////////////////////////////////////////////////////////////////////

		/// Load default state ///
		

		LilvNode* state_threadSafeRestore = lilv_new_uri(instance->plugin->world->lilvWorld, LV2_STATE__threadSafeRestore);
		bool _has_state_interface = lilv_plugin_has_feature(instance->plugin->lilvPlugin, state_threadSafeRestore);
		lilv_node_free(state_threadSafeRestore);
		/*
		LilvNode* state_iface_uri = lilv_new_uri(instance->plugin->world->lilvWorld, LV2_STATE__interface);
		LilvNode* state_uri       = lilv_new_uri(instance->plugin->world->lilvWorld, LV2_STATE_URI);
		bool _has_state_interface = (
			lilv_plugin_has_feature(instance->plugin->lilvPlugin, state_uri) || 
			lilv_plugin_has_extension_data(instance->plugin->lilvPlugin, state_iface_uri));
			
		lilv_node_free(state_uri);
		lilv_node_free(state_iface_uri);
		*/
		const LV2_Feature* mapFeature = LV2Feature_getFeature(handle, LV2_URID__map);
		LilvState* state = lilv_state_new_from_world(
				instance->plugin->world->lilvWorld, 
				(LV2_URID_Map *) mapFeature->data, 
				lilv_plugin_get_uri(instance->plugin->lilvPlugin));

		if (state && _has_state_interface) {
			lilv_state_restore(state, instance->lilvInstance, NULL, NULL, 0, NULL);
		}
		
		/// start worker ///
		LV2Worker_start(handle->worker, instance);
	}
}

void LV2Feature_processAudio(LV2Feature *handle)
{
	if( handle != NULL ) {
		LV2Worker_processResponses(handle->worker);
	}
}

LV2_Options_Option* LV2Feature_createOption(LV2_URID key, LV2Int32 size, LV2_URID type, void* value)
{
	LV2_Options_Option* option = (LV2_Options_Option*) malloc(sizeof(LV2_Options_Option));
	option->context = LV2_OPTIONS_INSTANCE;
	option->subject = 0;
	option->key = key;
	option->size = size;
	option->type = type;
	option->value = value;
	
	return option;
}

const LV2_Feature* const* LV2Feature_getFeatures(LV2Feature *handle)
{
	if( handle != NULL ) {
		if( handle->features == NULL ) {
			LV2_Feature* loadDefaultStateFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			loadDefaultStateFeature->URI = LV2_STATE__loadDefaultState;
			loadDefaultStateFeature->data = NULL;

			LV2_Feature* powerOf2BlockLengthFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			powerOf2BlockLengthFeature->URI = LV2_BUF_SIZE__powerOf2BlockLength;
			powerOf2BlockLengthFeature->data = NULL;

			LV2_Feature* fixedBlockLengthFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			fixedBlockLengthFeature->URI = LV2_BUF_SIZE__fixedBlockLength;
			fixedBlockLengthFeature->data = NULL;

			LV2_Feature* boundedBlockLengthFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			boundedBlockLengthFeature->URI = LV2_BUF_SIZE__boundedBlockLength;
			boundedBlockLengthFeature->data = NULL;

			// map feature
			LV2_URID_Map* map = (LV2_URID_Map *) malloc(sizeof(LV2_URID_Map));
			map->handle = handle;
			map->map = LV2Feature_map;
			
			LV2_Feature* mapFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			mapFeature->URI = LV2_URID__map;
			mapFeature->data = map;

			// schedule feature
			LV2_Worker_Schedule* schedule = (LV2_Worker_Schedule *) malloc(sizeof(LV2_Worker_Schedule));
			schedule->handle = handle->worker;
			schedule->schedule_work = LV2Worker_schedule;

			LV2_Feature* scheduleFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			scheduleFeature->URI = LV2_WORKER__schedule;
			scheduleFeature->data = schedule;
			
			// options feature
			LV2_URID atomIntURID = LV2Feature_map(handle, LV2_ATOM__Int);
			LV2_URID atomFloatURID = LV2Feature_map(handle, LV2_ATOM__Float);

			const LV2_Options_Option options[6] = {
				{ LV2_OPTIONS_INSTANCE, 0, LV2Feature_map(handle, LV2_PARAMETERS__sampleRate), sizeof(LV2Float64), atomFloatURID, &(handle->config->sampleRate)},
				{ LV2_OPTIONS_INSTANCE, 0, LV2Feature_map(handle, LV2_BUF_SIZE__maxBlockLength), sizeof(LV2Int32), atomIntURID, &(handle->config->bufferSize)},
				{ LV2_OPTIONS_INSTANCE, 0, LV2Feature_map(handle, LV2_BUF_SIZE__minBlockLength), sizeof(LV2Int32), atomIntURID, &(handle->config->bufferSize)},
				{ LV2_OPTIONS_INSTANCE, 0, LV2Feature_map(handle, LV2_BUF_SIZE__fixedBlockLength), sizeof(LV2Int32), atomIntURID, &(handle->config->bufferSize)},
				{ LV2_OPTIONS_INSTANCE, 0, LV2Feature_map(handle, LV2_BUF_SIZE__nominalBlockLength), sizeof(LV2Int32), atomIntURID, &(handle->config->bufferSize)},
				{ LV2_OPTIONS_INSTANCE, 0, 0, 0, 0, NULL }
			};

			LV2_Feature* optionsFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			optionsFeature->URI = LV2_OPTIONS__options;
			optionsFeature->data = malloc(sizeof(options));
			memcpy(optionsFeature->data, options, sizeof(options));

			// feature list
			LV2Int32 index = 0;
			handle->features = (LV2_Feature**) malloc((sizeof(LV2_Feature *) * 8));
			handle->features[index ++] = loadDefaultStateFeature;
			handle->features[index ++] = powerOf2BlockLengthFeature;
			handle->features[index ++] = fixedBlockLengthFeature;
			handle->features[index ++] = boundedBlockLengthFeature;
			handle->features[index ++] = mapFeature;
			handle->features[index ++] = scheduleFeature;
			handle->features[index ++] = optionsFeature;
			handle->features[index ++] = NULL;
		}
		return handle->features;
	}
	return NULL;
}

const LV2_Feature* LV2Feature_getFeature(LV2Feature *handle, const char* uri)
{
	if( handle != NULL && handle->features != NULL) {
		LV2_Feature** iterator = handle->features;
		while((*iterator) != NULL ) {
			if( strcmp((*iterator)->URI, uri) == 0 ) {
				return (*iterator);
			}

			iterator ++;
		}
	}
	return NULL;
}

LV2_URID LV2Feature_map(LV2_URID_Map_Handle mapHandle, const char* uri)
{
	LV2Feature *handle = (LV2Feature *) mapHandle;
	if( handle != NULL && handle->uriMap != NULL) {
		LV2URILink* prev = NULL;
		LV2URILink* next = handle->uriMap->next;
		while( next != NULL ) {
			if( strcmp(next->uri, uri) == 0 ) {
				return next->id;
			}
			prev = next;
			next = next->next;
		}

		char* targetUri = (char*) malloc(sizeof(char) * (strlen(uri) + 1));
		strcpy(targetUri, uri);

		next = (LV2URILink*) malloc(sizeof(LV2URILink));
		next->id = (handle->uriMap->sequence ++);
		next->uri = targetUri;
		next->next = NULL;

		if( prev != NULL ) {
			prev->next = next;
		} else {
			handle->uriMap->next = next;
		}
		return next->id;
	}
	return 0;
}
