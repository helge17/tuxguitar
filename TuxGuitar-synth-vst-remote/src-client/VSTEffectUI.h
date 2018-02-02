#ifndef _Included_VSTEffectUI
#define _Included_VSTEffectUI

#include "VST.h"

void VSTEffectUI_malloc(VSTEffectHandle *effect);

void VSTEffectUI_delete(VSTEffectHandle *effect);

void VSTEffectUI_process(VSTEffectHandle *effect);

void VSTEffectUI_openEditor(VSTEffectHandle *effect);

void VSTEffectUI_closeEditor(VSTEffectHandle *effect);

void VSTEffectUI_isEditorOpen(VSTEffectHandle *effect, bool *value);

void VSTEffectUI_isEditorAvailable(VSTEffectHandle *effect, bool *value);

#endif /* _Included_VSTEffectUI */
