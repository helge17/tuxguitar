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

			LV2Feature_getFeature((*handle), LV2_INSTANCE_ACCESS_URI)->data = NULL;

			LV2_Feature** iterator = (*handle)->features;
			while((*iterator) != NULL ) {
				if( (*iterator)->data != NULL ) {
					free ((*iterator)->data );
				}
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
		
		/// Load data access handle ///
		LV2_Extension_Data_Feature* dataAccess = (LV2_Extension_Data_Feature *) LV2Feature_getFeature(handle, LV2_DATA_ACCESS_URI)->data;
		dataAccess->data_access = lilv_instance_get_descriptor(instance->lilvInstance)->extension_data;

		/// Load instance access handle ///
		LV2Feature_getFeature(handle, LV2_INSTANCE_ACCESS_URI)->data = lilv_instance_get_handle(instance->lilvInstance);

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

			// unmap feature
			LV2_URID_Unmap* unmap = (LV2_URID_Unmap *) malloc(sizeof(LV2_URID_Unmap));
			unmap->handle = handle;
			unmap->unmap = LV2Feature_unmap;
			
			LV2_Feature* unmapFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			unmapFeature->URI = LV2_URID__unmap;
			unmapFeature->data = unmap;

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

			// data-access feature
			LV2_Extension_Data_Feature* dataAccess = (LV2_Extension_Data_Feature *) malloc(sizeof(LV2_Extension_Data_Feature));
			dataAccess->data_access = NULL;

			LV2_Feature* dataAccessFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			dataAccessFeature->URI = LV2_DATA_ACCESS_URI;
			dataAccessFeature->data = dataAccess;

			// instance-access feature
			LV2_Feature* instanceAccessFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			instanceAccessFeature->URI = LV2_INSTANCE_ACCESS_URI;
			instanceAccessFeature->data = NULL;

			// ui parent feature
			LV2_Feature* uiParentFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			uiParentFeature->URI = LV2_UI__parent;
			uiParentFeature->data = NULL;

			// idle feature
			LV2_Feature* idleFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			idleFeature->URI = LV2_UI__idleInterface;
			idleFeature->data = NULL;

			// feature list
			LV2Int32 index = 0;
			handle->features = (LV2_Feature**) malloc((sizeof(LV2_Feature *) * 13));
			handle->features[index ++] = loadDefaultStateFeature;
			handle->features[index ++] = powerOf2BlockLengthFeature;
			handle->features[index ++] = fixedBlockLengthFeature;
			handle->features[index ++] = boundedBlockLengthFeature;
			handle->features[index ++] = mapFeature;
			handle->features[index ++] = unmapFeature;
			handle->features[index ++] = scheduleFeature;
			handle->features[index ++] = optionsFeature;
			handle->features[index ++] = dataAccessFeature;
			handle->features[index ++] = instanceAccessFeature;
			handle->features[index ++] = uiParentFeature;
			handle->features[index ++] = idleFeature;
			handle->features[index ++] = NULL;
		}
		return handle->features;
	}
	return NULL;
}

LV2_Feature* LV2Feature_getFeature(LV2Feature *handle, const char* uri)
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

const char* LV2Feature_unmap(LV2_URID_Unmap_Handle unmapHandle, LV2_URID urid)
{
	LV2Feature *handle = (LV2Feature *) unmapHandle;
	if( handle != NULL && handle->uriMap != NULL) {
		LV2URILink* next = handle->uriMap->next;
		while( next != NULL ) {
			if( next->id == urid ) {
				return next->uri;
			}
			next = next->next;
		}
	}
	return NULL;
}