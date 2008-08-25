CXX = g++

SDK = /Developer/SDKs/MacOSX10.4u.sdk
JAVA_FRAMEWORK = $(SDK)/System/Library/Frameworks/JavaVM.framework

INCLUDES = -I $(JAVA_FRAMEWORK)/Headers/

LIBRARIES = -framework CoreAudio -framework JavaVM -framework AudioUnit -framework AudioToolbox -framework Carbon

LIBRARY = libtuxguitar-coreaudio-jni.jnilib

FILENAME = org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI.cpp

all:	$(LIBRARY)

$(LIBRARY):	clean
	g++ $(INCLUDES) -isysroot $(SDK) -arch ppc $(LIBRARIES) -msse -dynamiclib -o ppc_$(LIBRARY) $(FILENAME)
	g++ $(INCLUDES) -isysroot $(SDK) -arch i386 $(LIBRARIES) -msse -dynamiclib -o intel_$(LIBRARY) $(FILENAME)
	lipo -create ppc_$(LIBRARY) intel_$(LIBRARY) -output $(LIBRARY)

clean:
	rm -f ppc_$(LIBRARY) intel_$(LIBRARY) $(LIBRARY)