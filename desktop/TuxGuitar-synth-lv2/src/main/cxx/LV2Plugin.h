#ifndef _Included_LV2Plugin
#define _Included_LV2Plugin

void LV2Plugin_malloc(LV2Plugin **handle, LV2World* world, const LilvPlugin* lilvPlugin);

void LV2Plugin_free(LV2Plugin **handle);

void LV2Plugin_getUri(LV2Plugin *handle, const char** value);

void LV2Plugin_getName(LV2Plugin *handle, const char** value);

void LV2Plugin_getCategory(LV2Plugin *handle, const char** value);

void LV2Plugin_getPortIndex(LV2Plugin *handle, LV2Int32* index, const char* symbol);

void LV2Plugin_getPortCount(LV2Plugin *handle, LV2PortType portType, LV2PortFlow portFlow, LV2Int32* count);

void LV2Plugin_getAudioInputPortCount(LV2Plugin *handle, LV2Int32* count);

void LV2Plugin_getAudioOutputPortCount(LV2Plugin *handle, LV2Int32* count);

void LV2Plugin_getMidiInputPortCount(LV2Plugin *handle, LV2Int32* count);

void LV2Plugin_getNextPortIndex(LV2Plugin *handle, LV2PortType portType, LV2PortFlow portFlow, LV2Int32* index);

#define LV2_PLUGIN_PORT_INDEX_FOREACH(handle, portType, portFlow, portIndex, block) \
	{\
        LV2Int32 (portIndex) = -1;\
        LV2Plugin_getNextPortIndex(handle, portType, portFlow, &(portIndex));\
        while( (portIndex) >= 0 ) {\
            block;\
            LV2Plugin_getNextPortIndex(handle, portType, portFlow, &(portIndex));\
        }\
    }\
    
#endif

