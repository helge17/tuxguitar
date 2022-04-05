#include <stdlib.h>
#include <string.h>
#include "LV2.h"
#include "LV2Feature.h"
#include "LV2Logger.h"

void LV2Feature_malloc(LV2Feature **handle)
{
	(*handle) = (LV2Feature *) malloc(sizeof(LV2Feature));
	(*handle)->features == NULL;
	(*handle)->uriMap = (LV2URILinkedMap*) malloc(sizeof(LV2URILinkedMap));
	(*handle)->uriMap->sequence = 1;
	(*handle)->uriMap->next = NULL;
}

void LV2Feature_free(LV2Feature **handle)
{
	if( (*handle) != NULL ) {
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

const LV2_Feature* const* LV2Feature_getFeatures(LV2Feature *handle)
{
	if( handle != NULL ) {
		if( handle->features == NULL ) {
			// map feature
			LV2_URID_Map* map = (LV2_URID_Map *) malloc(sizeof(LV2_URID_Map));
			map->handle = handle;
			map->map = LV2Feature_map;
			
			LV2_Feature* mapFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			mapFeature->URI = LV2_URID__map;
			mapFeature->data = map;

			// schedule feature
			LV2_Worker_Schedule* schedule = (LV2_Worker_Schedule *) malloc(sizeof(LV2_Worker_Schedule));
			schedule->handle = handle;
			schedule->schedule_work = LV2Feature_schedule_work;

			LV2_Feature* scheduleFeature = (LV2_Feature *) malloc(sizeof(LV2_Feature));
			scheduleFeature->URI = LV2_WORKER__schedule;
			scheduleFeature->data = schedule;

			// feature list
			handle->features = (LV2_Feature**) malloc((sizeof(LV2_Feature) * 2));
			handle->features[0] = mapFeature;
			handle->features[1] = scheduleFeature;
			handle->features[2] = NULL;
		}
		return handle->features;
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

LV2_Worker_Status LV2Feature_schedule_work(LV2_Worker_Schedule_Handle handle, uint32_t size, const void *data)
{
	return LV2_WORKER_SUCCESS;
}