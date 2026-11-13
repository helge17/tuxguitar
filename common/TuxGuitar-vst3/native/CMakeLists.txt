# TuxGuitar's VST3 host process and the test plugins the audio features play.
#
#   host/          tuxguitar-vst3-host, the helper process that runs VST3 plugins apart from TuxGuitar
#   test-plugins/  the plugins of the README of the audio features ("the VST3 folders hold the test plugins"), and
#                  Old Amp, a stand-in for a VST 2 plugin
#
# Outputs, under the build directory: bin/tuxguitar-vst3-host[.exe], VST3/<plugin>.vst3 (bundles), vst2/Old Amp.<ext>.
# Built by build.cmd / build.sh, which Maven runs before the tests of TuxGuitar-vst3 and TuxGuitar-audio.
# Verified on Windows (MSVC); the Linux and macOS paths follow the VST3 SDK's own CMake and are not verified yet.
cmake_minimum_required(VERSION 3.25)

set(SMTG_ENABLE_VSTGUI_SUPPORT OFF CACHE BOOL "" FORCE)
set(SMTG_ENABLE_VST3_PLUGIN_EXAMPLES OFF CACHE BOOL "" FORCE)
set(SMTG_ENABLE_VST3_HOSTING_EXAMPLES OFF CACHE BOOL "" FORCE)
set(SMTG_RUN_VST_VALIDATOR OFF CACHE BOOL "" FORCE)
set(SMTG_CREATE_MODULE_INFO OFF CACHE BOOL "" FORCE)
set(SMTG_CREATE_PLUGIN_LINK OFF CACHE BOOL "" FORCE)
set(SMTG_ENABLE_TARGET_VARS_LOG OFF CACHE BOOL "" FORCE)

include(cmake/vst3sdk.cmake)

set(SDK_ROOT "${TG_VST3_SDK_DIR}")
set(public_sdk_SOURCE_DIR "${SDK_ROOT}/public.sdk")
set(pluginterfaces_SOURCE_DIR "${SDK_ROOT}/pluginterfaces")
list(APPEND CMAKE_MODULE_PATH "${SDK_ROOT}/cmake/modules")
include(SMTG_VST3_SDK)

project(tuxguitar_vst3 VERSION 1.0.0 LANGUAGES C CXX)

smtg_setup_platform_toolset()
smtg_setup_symbol_visibility()

smtg_create_lib_base_target()
smtg_create_pluginterfaces_target()
smtg_create_public_sdk_common_target()
smtg_create_public_sdk_target()
smtg_create_public_sdk_hosting_target()

# the helper process
set(TG_HOST_SOURCES host/main.cpp host/wire.cpp host/shared.cpp host/instance.cpp host/sockets.cpp)
if(SMTG_WIN)
	list(APPEND TG_HOST_SOURCES "${public_sdk_SOURCE_DIR}/source/vst/hosting/module_win32.cpp")
elseif(SMTG_MAC)
	list(APPEND TG_HOST_SOURCES "${public_sdk_SOURCE_DIR}/source/vst/hosting/module_mac.mm")
else()
	list(APPEND TG_HOST_SOURCES "${public_sdk_SOURCE_DIR}/source/vst/hosting/module_linux.cpp")
endif()
add_executable(tuxguitar-vst3-host ${TG_HOST_SOURCES})
target_link_libraries(tuxguitar-vst3-host PRIVATE sdk_hosting)
set_target_properties(tuxguitar-vst3-host PROPERTIES RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin")
if(SMTG_WIN)
	target_link_libraries(tuxguitar-vst3-host PRIVATE iphlpapi ws2_32)
	target_compile_definitions(tuxguitar-vst3-host PRIVATE NOMINMAX)
elseif(SMTG_MAC)
	target_link_libraries(tuxguitar-vst3-host PRIVATE "-framework CoreFoundation" "-framework Foundation")
else()
	target_link_libraries(tuxguitar-vst3-host PRIVATE dl pthread stdc++fs)
endif()

# the test plugins, each a bundle of its own
option(TG_VST3_TEST_PLUGINS "Build the test plugins of the audio features" ON)
if(TG_VST3_TEST_PLUGINS)
	add_library(tg_test_plugin STATIC test-plugins/testplugin.cpp "${public_sdk_SOURCE_DIR}/source/vst/vstsinglecomponenteffect.cpp")
	target_link_libraries(tg_test_plugin PUBLIC sdk)
	set_target_properties(tg_test_plugin PROPERTIES POSITION_INDEPENDENT_CODE ON)

	function(tg_add_test_plugin target name source)
		smtg_add_vst3plugin(${target} PACKAGE_NAME "${name}" SOURCES_LIST test-plugins/${source})
		target_link_libraries(${target} PRIVATE tg_test_plugin)
	endfunction()

	tg_add_test_plugin(tg_test_gain "Test Gain" test_gain.cpp)
	tg_add_test_plugin(tg_test_clip "Test Clip 0.5" test_clip.cpp)
	tg_add_test_plugin(tg_test_latency "Test Latency" test_latency.cpp)
	tg_add_test_plugin(tg_test_echo "Test Echo" test_echo.cpp)
	tg_add_test_plugin(tg_test_drone "Test Drone" test_drone.cpp)
	tg_add_test_plugin(tg_test_stereo "Test Stereo" test_stereo.cpp)
	tg_add_test_plugin(tg_test_tempo "Test Tempo" test_tempo.cpp)
	tg_add_test_plugin(tg_test_sine "Test Sine" test_sine.cpp)
	tg_add_test_plugin(tg_test_click "Test Click" test_click.cpp)

	add_library(tg_old_amp MODULE test-plugins/old_amp.c)
	set_target_properties(tg_old_amp PROPERTIES OUTPUT_NAME "Old Amp" PREFIX "" LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/vst2")
endif()
