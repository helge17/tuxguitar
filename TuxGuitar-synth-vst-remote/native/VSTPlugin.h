#ifndef _Included_VSTPlugin
#define _Included_VSTPlugin

#include "VST.h"

void VSTPlugin_malloc(JNIPlugin **handle, const char *libraryPath);

void VSTPlugin_delete(JNIPlugin **handle);

#endif /* _Included_VSTPlugin */
