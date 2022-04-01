#ifndef _Included_LV2UI
#define _Included_LV2UI

#include <suil.h>

void LV2UI_malloc(LV2UI **handle, LV2Feature *feature, LV2Instance *instance, pthread_mutex_t *lock);

void LV2UI_free(LV2UI **handle);

void LV2UI_isAvailable(LV2UI *handle, bool* available);

void LV2UI_isOpen(LV2UI *handle, bool *open);

void LV2UI_open(LV2UI *handle);

void LV2UI_close(LV2UI *handle);

void LV2UI_isUpdated(LV2UI *handle, bool *updated);

void LV2UI_setUpdated(LV2UI *handle, bool updated);

void LV2UI_setControlPortValue(LV2UI *handle, LV2Int32 index, float value);

void LV2UI_setControlPortValues(LV2UI *handle, LV2PortFlow flow);

void LV2UI_processAudio(LV2UI *handle);

void LV2UI_process(LV2UI *handle);

#endif
