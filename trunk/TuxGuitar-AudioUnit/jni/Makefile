CXX = g++

SDK = /Developer/SDKs/MacOSX10.5.sdk
SDK64 = /Developer/SDKs/MacOSX10.5.sdk
JAVA_FRAMEWORK = $(SDK)/System/Library/Frameworks/JavaVM.framework
JAVA_FRAMEWORK64 = $(SDK64)/System/Library/Frameworks/JavaVM.framework

INCLUDES = -I $(JAVA_FRAMEWORK)/Headers/
INCLUDES64 = -I $(JAVA_FRAMEWORK64)/Headers/

LIBRARIES = -framework CoreAudio -framework JavaVM -framework AudioUnit -framework AudioToolbox -framework CoreServices

LIBRARY = libtuxguitar-audiounit-jni.jnilib

FILENAME = org_herac_tuxguitar_player_impl_midiport_audiounit_MidiReceiverJNI.cpp

all:	$(LIBRARY)

$(LIBRARY):	clean
	g++ $(INCLUDES) -isysroot $(SDK) -arch ppc $(LIBRARIES) -msse -bundle -o ppc_$(LIBRARY) $(FILENAME)
	g++ $(INCLUDES) -isysroot $(SDK) -arch i386 $(LIBRARIES) -msse -bundle -o intel_$(LIBRARY) $(FILENAME)
	g++ $(INCLUDES64) -isysroot $(SDK64) -arch x86_64 $(LIBRARIES) -msse -bundle -o intel64_$(LIBRARY) $(FILENAME)
	lipo -create ppc_$(LIBRARY) intel_$(LIBRARY) intel64_$(LIBRARY) -output $(LIBRARY)

clean:
	rm -f ppc_$(LIBRARY) intel_$(LIBRARY) intel64_$(LIBRARY) $(LIBRARY)