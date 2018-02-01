#include "VST.h"

VstIntPtr VSTCALLBACK VSTPluginCallback(AEffect *effect, VstInt32 opcode, VstInt32 index, VstIntPtr value, void *ptr, float opt)
{
	switch(opcode) {
		case audioMasterVersion:
			return 2400;
		case audioMasterAutomate:
			return 1;
		case audioMasterIdle:
			effect->dispatcher(effect, effEditIdle, 0, 0, 0, 0);
			return 1;
	}
	return 0;
}
