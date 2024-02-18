SDK = /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk
JAVA_FRAMEWORK = $(SDK)/System/Library/Frameworks/JavaRuntimeSupport.framework

INCLUDES = -I $(JAVA_FRAMEWORK)/Headers/ -I ../../../desktop/build-scripts/native-modules/common-include/

LIBRARIES = -framework CoreAudio -framework JavaRuntimeSupport -framework AudioUnit -framework AudioToolbox -framework CoreServices

LIBRARY = libtuxguitar-coreaudio-jni.jnilib

FILENAME = org_herac_tuxguitar_player_impl_midiport_coreaudio_MidiReceiverJNI.cpp

all:	$(LIBRARY)

$(LIBRARY):	clean
	g++ $(INCLUDES) -isysroot $(SDK) -arch x86_64 $(LIBRARIES) -msse -bundle -o intel_$(LIBRARY) $(FILENAME)
	# TODO ARM binary: g++ ...
	# TODO universal binary: lipo -create intel_$(LIBRARY) arm_$(LIBRARY) -output $(LIBRARY)

clean:
	rm -f intel_$(LIBRARY) arm_$(LIBRARY) $(LIBRARY)
