# See pom.xml for variable definitions

OBJECTS=\
	../cxx/LV2Logger.o\
	../cxx/LV2World.o\
	../cxx/LV2Plugin.o\
	app_tuxguitar_player_impl_midiport_lv2_jni_LV2World.o\
	app_tuxguitar_player_impl_midiport_lv2_jni_LV2Plugin.o

%.o: %.c
	$(CXX) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CXX) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean:
	$(RM) $(OBJECTS) $(LIBRARY)
