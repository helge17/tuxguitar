CXX ?= g++

INCLUDES = -I ../../build-scripts/native-modules/common-include/

LIBRARIES = -framework CoreAudio -framework JavaRuntimeSupport -framework AudioUnit -framework AudioToolbox -framework CoreServices

LIBRARY = libtuxguitar-audiounit-jni.jnilib

FILENAME = app_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI.cpp

all:	$(LIBRARY)

$(LIBRARY):	clean
	$(CXX) $(INCLUDES) -arch x86_64 $(LIBRARIES) -msse -bundle -o intel64_$(LIBRARY) $(FILENAME)
	$(CXX) $(INCLUDES) -arch arm64 $(LIBRARIES) -bundle -o arm64_$(LIBRARY) $(FILENAME)
	lipo -create intel64_$(LIBRARY) arm64_$(LIBRARY) -output $(LIBRARY)

clean:
	rm -f intel64_$(LIBRARY) arm64_$(LIBRARY) $(LIBRARY)
