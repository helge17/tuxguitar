VST_SDK_HOME?=/home/julian/Documents/vst/VST_SDK/VST3_SDK/pluginterfaces/vst2.x/

RM?=rm -f
CFLAGS?=-I$(shell gcj -print-file-name=include/) -I$(VST_SDK_HOME) -fPIC -fpermissive
LDFLAGS?=
LDLIBS?=-ldl -lX11
LDPATH?=-L/usr/lib
LIBRARY_NAME?=tuxguitar-synth-vst-jni
LIBRARY_PREFIX?=lib
LIBRARY_SUFFIX?=.so
LIBRARY_OS?=_GNULinux

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_SUFFIX)
OBJECTS=\
	org_herac_tuxguitar_player_impl_midiport_vst_jni_VST.o\
	org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffect.o\
	org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTEffectUI$(LIBRARY_OS).o\
	org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPlugin.o\
	org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPluginLoader$(LIBRARY_OS).o\

%.o: %.c
	$(CXX) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CXX) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)
	
clean: 
	$(RM) $(OBJECTS) $(LIBRARY)
