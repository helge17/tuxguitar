#include <stdlib.h>
#include "org_herac_tuxguitar_player_impl_midiport_vst_jni_VST.h"

VstIntPtr VSTCALLBACK VSTPluginCallback(AEffect *effect, VstInt32 opcode, VstInt32 index, VstIntPtr value, void *ptr, float opt)
{
	switch(opcode) {
		case audioMasterVersion:
			return 2400;
		case audioMasterAutomate:
			return 1;
	}
	return 0;
}
