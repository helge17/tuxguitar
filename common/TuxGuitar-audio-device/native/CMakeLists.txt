# TuxGuitar's native audio backend: a JNI library (tuxguitar-audio-device) over PortAudio, built statically into it.
#
#   src/  the JNI functions of app.tuxguitar.audio.device.TGPortAudio
#
# Host APIs: on Windows ASIO (unless -DTG_AUDIO_ASIO=OFF; see cmake/asiosdk.cmake for its GPLv3 licensing), WASAPI,
# DirectSound and MME; Core Audio on macOS; ALSA and JACK (when found) on Linux. Output, under the build directory:
# lib/tuxguitar-audio-device.dll (Windows), lib/libtuxguitar-audio-device.so (Linux), .dylib (macOS).
# Built by build.cmd / build.sh, which Maven runs before the tests of TuxGuitar-audio-device and TuxGuitar-audio.
# Verified on Windows (MSVC); the Linux and macOS paths are written but not verified yet.
cmake_minimum_required(VERSION 3.25)

project(tuxguitar_audio_device VERSION 1.0.0 LANGUAGES C CXX)

include(cmake/portaudio.cmake)

option(TG_AUDIO_ASIO "Build the ASIO host API (Windows; the build is then distributed under GPLv3)" ON)
set(PA_USE_ASIO OFF CACHE BOOL "" FORCE)
if(WIN32 AND TG_AUDIO_ASIO)
	include(cmake/asiosdk.cmake)
	set(ASIOSDK_ROOT_DIR "${TG_ASIO_SDK_DIR}" CACHE PATH "" FORCE)
	set(ASIOSDK_INCLUDE_DIR "${TG_ASIO_SDK_DIR}/common" CACHE PATH "" FORCE)
	set(PA_USE_ASIO ON CACHE BOOL "" FORCE)
endif()

set(PA_BUILD_SHARED OFF CACHE BOOL "" FORCE)
set(PA_BUILD_STATIC ON CACHE BOOL "" FORCE)
set(PA_DISABLE_INSTALL ON CACHE BOOL "" FORCE)
set(PA_USE_WDMKS OFF CACHE BOOL "" FORCE)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)
# PortAudio 19.7.0 asks for CMake 2.8, which CMake 4 no longer emulates
set(CMAKE_POLICY_VERSION_MINIMUM 3.5)
add_subdirectory("${TG_PORTAUDIO_DIR}" portaudio EXCLUDE_FROM_ALL)

find_package(JNI REQUIRED)

add_library(tuxguitar-audio-device SHARED src/tg_audio_device.cpp)
target_compile_features(tuxguitar-audio-device PRIVATE cxx_std_17)
target_include_directories(tuxguitar-audio-device PRIVATE ${JNI_INCLUDE_DIRS} "${TG_PORTAUDIO_DIR}/include")
target_link_libraries(tuxguitar-audio-device PRIVATE portaudio_static)
set_target_properties(tuxguitar-audio-device PROPERTIES
	RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
	LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib")
