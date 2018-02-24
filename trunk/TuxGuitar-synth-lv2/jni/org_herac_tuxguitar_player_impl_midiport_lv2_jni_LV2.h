#ifndef _Included_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2
#define _Included_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2

#include <lilv.h>

typedef enum {
	TYPE_CONTROL,
	TYPE_AUDIO_IN,
	TYPE_AUDIO_OUT,
	TYPE_UNKNOWN
} LV2PortType;

typedef struct {
	LilvWorld* lilvWorld;
} LV2World;

typedef struct {
	LV2World* world;
	LilvPlugin* lilvPlugin;
} LV2Plugin;

typedef struct {
	LV2PortType type;
	const LilvPort* lilvPort;
	void* connection;
} LV2Port;

typedef struct {
	LilvInstance* lilvInstance;
	LV2Plugin* plugin;
	LV2Port** ports;
	uint32_t portCount;
	uint32_t bufferSize;
} LV2Instance;

#endif /* _Included_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2 */
