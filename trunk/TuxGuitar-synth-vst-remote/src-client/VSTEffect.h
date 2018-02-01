#ifndef _Included_VSTEffect
#define _Included_VSTEffect

#include "VST.h"

void VSTEffect_malloc(JNIEffect **handle, JNIPlugin *plugin);

void VSTEffect_delete(JNIEffect **handle);

void VSTEffect_openEffect(JNIEffect *handle);

void VSTEffect_closeEffect(JNIEffect *handle);

void VSTEffect_setActive(JNIEffect *handle, int value);

void VSTEffect_getNumParams(JNIEffect *handle, int *value);

void VSTEffect_getNumInputs(JNIEffect *handle, int *value);

void VSTEffect_getNumOutputs(JNIEffect *handle, int *value);

void VSTEffect_setBlockSize(JNIEffect *handle, int value);

void VSTEffect_setSampleRate(JNIEffect *handle, float value);

void VSTEffect_setParameter(JNIEffect *handle, int index, float value);

void VSTEffect_getParameter(JNIEffect *handle, int index, float *value);

void VSTEffect_getParameterName(JNIEffect *handle, int index, const char* value);

void VSTEffect_getParameterLabel(JNIEffect *handle, int index, const char* value);

void VSTEffect_sendMessages(JNIEffect *handle, unsigned char** messages, int length);

void VSTEffect_sendProcessReplacing(JNIEffect *handle, float** inputs, float** outputs, int blockSize);

#endif /* _Included_VSTEffect */
