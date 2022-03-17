#ifndef _Included_VSTEffect
#define _Included_VSTEffect

#include "VST.h"

void VSTEffect_malloc(VSTEffectHandle **handle, VSTPluginHandle *plugin);

void VSTEffect_delete(VSTEffectHandle **handle);

void VSTEffect_openEffect(VSTEffectHandle *handle);

void VSTEffect_closeEffect(VSTEffectHandle *handle);

void VSTEffect_setActive(VSTEffectHandle *handle, int value);

void VSTEffect_setUpdated(VSTEffectHandle *handle, bool value);

void VSTEffect_getUpdated(VSTEffectHandle *handle, bool *value);

void VSTEffect_getNumParams(VSTEffectHandle *handle, int *value);

void VSTEffect_getNumInputs(VSTEffectHandle *handle, int *value);

void VSTEffect_getNumOutputs(VSTEffectHandle *handle, int *value);

void VSTEffect_setBlockSize(VSTEffectHandle *handle, int value);

void VSTEffect_setSampleRate(VSTEffectHandle *handle, float value);

void VSTEffect_setParameter(VSTEffectHandle *handle, int index, float value);

void VSTEffect_getParameter(VSTEffectHandle *handle, int index, float *value);

void VSTEffect_getParameterName(VSTEffectHandle *handle, int index, const char* value);

void VSTEffect_getParameterLabel(VSTEffectHandle *handle, int index, const char* value);

void VSTEffect_setChunk(VSTEffectHandle *handle, int length, char** value);

void VSTEffect_getChunk(VSTEffectHandle *handle, int* length, char** value);

void VSTEffect_beginSetProgram(VSTEffectHandle *handle);

void VSTEffect_endSetProgram(VSTEffectHandle *handle);

void VSTEffect_sendMessages(VSTEffectHandle *handle, unsigned char** messages, int length);

void VSTEffect_sendProcessReplacing(VSTEffectHandle *handle, float** inputs, float** outputs, int blockSize);

#endif /* _Included_VSTEffect */
