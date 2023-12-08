#ifndef _Included_LV2World
#define _Included_LV2World

void LV2World_malloc(LV2World** handle);

void LV2World_free(LV2World** handle);

void LV2World_getAllPlugins(LV2World* handle, LV2Plugin ***plugins, LV2Int32* count);

void LV2World_getPluginByURI(LV2World* handle, LV2Plugin **plugin, const char* uri);

#endif
