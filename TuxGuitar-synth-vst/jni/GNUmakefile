JAVA_HOME=/opt/local/applications/java/jdk1.8.0_111
VST_SDK_HOME=/home/julian/Downloads/VST_SDK/VST3_SDK

CFLAGS?=-I$(JAVA_HOME)/include -I$(VST_SDK_HOME)/public.sdk/source/vst2.x
CFLAGS+=-fPIC -fpermissive

LDFLAGS?=
LDLIBS?=-ldl -ljsig -lX11
LDPATH?=-L/usr/lib -L$(JAVA_HOME)/jre/lib/amd64/
LIBRARY_PREFIX?=lib
LIBRARY_NAME?=tuxguitar-vst-jni
LIBRARY_EXTENSION?=.so
LIBRARY_OS?=_GNULinux


LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_EXTENSION)
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
	rm -f $(OBJECTS) $(LIBRARY)
