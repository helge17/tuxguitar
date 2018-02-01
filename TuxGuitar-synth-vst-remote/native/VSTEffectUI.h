#ifndef _Included_VSTEffectUI
#define _Included_VSTEffectUI

#include "VST.h"

void VSTEffectUI_malloc(JNIEffect *effect);

void VSTEffectUI_delete(JNIEffect *effect);

void VSTEffectUI_process(JNIEffect *effect);

void VSTEffectUI_openEditor(JNIEffect *effect);

void VSTEffectUI_closeEditor(JNIEffect *effect);

void VSTEffectUI_isEditorOpen(JNIEffect *effect, bool *value);

void VSTEffectUI_isEditorAvailable(JNIEffect *effect, bool *value);

#endif /* _Included_VSTEffectUI */
