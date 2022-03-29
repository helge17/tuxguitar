#include <stdlib.h>
#include <string.h>
#include "LV2.h"
#include "LV2World.h"
#include "LV2Plugin.h"

void LV2World_malloc(LV2World** handle)
{
	(*handle) = (LV2World *) malloc(sizeof(LV2World));
	(*handle)->lilvWorld = lilv_world_new();
	
	lilv_world_load_all((*handle)->lilvWorld);
}

void LV2World_free(LV2World** handle)
{
	if( (*handle) != NULL) {
		if( (*handle)->lilvWorld != NULL ){
			lilv_world_free((*handle)->lilvWorld);
			(*handle)->lilvWorld = NULL;
		}
		free ( (*handle) );

		(*handle) = NULL;
	}
}

void LV2World_getAllPlugins(LV2World* handle, LV2Plugin ***plugins, LV2Int32* count) 
{
	(*count) = 0;

	if( handle != NULL && handle->lilvWorld != NULL ) {
		const LilvPlugins* lilvPlugins = lilv_world_get_all_plugins(handle->lilvWorld);
		if( lilvPlugins ) {
			(*count) = lilv_plugins_size(lilvPlugins);
			(*plugins) = (LV2Plugin **) malloc(sizeof(LV2Plugin*) * (*count));
			
			LV2Int32 index = 0;
			LILV_FOREACH(plugins, itr, lilvPlugins) {
				const LilvPlugin *lilvPlugin = lilv_plugins_get(lilvPlugins, itr);
				
				LV2Plugin_malloc(&(*plugins)[index ++], handle, lilvPlugin);
			}
		}
	}
}

void LV2World_getPluginByURI(LV2World* handle, LV2Plugin **plugin, const char* uri)
{
	(*plugin) = NULL;

	if( handle != NULL && handle->lilvWorld != NULL ) {
		const LilvPlugins* lilvPlugins = lilv_world_get_all_plugins(handle->lilvWorld);
		if( lilvPlugins ) {
			const LilvPlugin *lilvPlugin = lilv_plugins_get_by_uri(lilvPlugins, lilv_new_uri(handle->lilvWorld, uri));

			LV2Plugin_malloc(plugin, handle, lilvPlugin);
		}
	}	
}