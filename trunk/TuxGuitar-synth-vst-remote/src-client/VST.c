#include "VST.h"
#include "VSTEffect.h"

VstIntPtr VSTCALLBACK VSTPluginCallback(AEffect *effect, VstInt32 opcode, VstInt32 index, VstIntPtr value, void *ptr, float opt)
{
	switch(opcode) {
		case audioMasterVersion: {
			return 2400;
		}
		case audioMasterAutomate: {
			VSTEffectHandle *handle = NULL;
			memcpy(&handle, &effect->resvd1, sizeof(handle));
			VSTEffect_setUpdated(handle, true);
			
			return 1;
		}
		case audioMasterIdle: {
			effect->dispatcher(effect, effEditIdle, 0, 0, 0, 0);
			return 1;
		}
	}
	return 0;
}
