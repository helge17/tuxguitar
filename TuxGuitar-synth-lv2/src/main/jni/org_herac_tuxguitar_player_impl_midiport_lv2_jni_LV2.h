#ifndef _Included_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2
#define _Included_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2

#include "../cxx/LV2.h"
#include "../cxx/LV2World.h"
#include "../cxx/LV2Plugin.h"
/*
#include <lilv.h>

#include <lv2/lv2plug.in/ns/ext/atom/atom.h>
#include <lv2/lv2plug.in/ns/ext/midi/midi.h>
#include <lv2/lv2plug.in/ns/extensions/ui/ui.h>
#include <lv2/instance-access/instance-access.h>

typedef enum {
	TYPE_CONTROL,
	TYPE_AUDIO_IN,
	TYPE_AUDIO_OUT,
	TYPE_MIDI_IN,
	TYPE_UNKNOWN
} LV2PortType;

typedef struct {
	LV2PortType type;
	const LilvPort* lilvPort;
	void* connection;
} LV2Port;

typedef struct {
	LilvWorld* lilvWorld;
} LV2World;

typedef struct {
	LV2World* world;
	LilvPlugin* lilvPlugin;
	LV2Port** ports;
	uint32_t portCount;
} LV2Plugin;

typedef struct {
	LilvInstance* lilvInstance;
	LV2Plugin* plugin;
	LV2Port** ports;
	uint32_t portCount;
	uint32_t bufferSize;
	
	pthread_t* thread;
} LV2Instance;
*/
#endif /* _Included_org_herac_tuxguitar_player_impl_midiport_lv2_jni_LV2 */
