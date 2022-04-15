#ifndef _Included_LV2Instance
#define _Included_LV2Instance

struct LV2InstanceImpl {
	LilvInstance* lilvInstance;
	LV2Plugin* plugin;
	LV2PortConnection** connections;
	LV2Config *config;
	LV2_URID atomChunkType;
	LV2_URID atomSequenceType;
	LV2_URID midiEventType;
	pthread_t* thread;
};

void LV2Instance_malloc(LV2Instance **handle, LV2Plugin* plugin, LV2Feature* feature, LV2Config *config);

void LV2Instance_free(LV2Instance **handle);

void LV2Instance_getControlPortValue(LV2Instance *handle, LV2Int32 index, float *value);

void LV2Instance_setControlPortValue(LV2Instance *handle, LV2Int32 index, float value);

void LV2Instance_getConnectionBuffer(LV2Instance *handle, LV2Int32 index, void** sequence);

void LV2Instance_getWorkBuffer(LV2Instance *handle, LV2Int32 index, void** sequence);

void LV2Instance_setMidiMessages(LV2Instance *handle, unsigned char** messages, LV2Int32 length);

void LV2Instance_processAudio(LV2Instance *handle, float** inputs, float** outputs);

#define LV2_PLUGIN_PORT_BUFFER_FOREACH(handle, portType, portFlow, workBuffer, connectionBuffer, block)\
	LV2_PLUGIN_PORT_INDEX_FOREACH(handle->plugin, portType, portFlow, portIndex, {\
		void *(workBuffer) = NULL;\
		void *(connectionBuffer) = NULL;\
		LV2Instance_getWorkBuffer(handle, portIndex, &(workBuffer));\
		LV2Instance_getConnectionBuffer(handle, portIndex, &(connectionBuffer));\
		block;\
	})\

#endif
