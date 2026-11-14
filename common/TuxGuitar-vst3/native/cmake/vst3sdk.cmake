# The VST3 SDK, pinned: release v3.8.1_build_84 of github.com/steinbergmedia/vst3sdk
# (commit 3cdf9ca5d1f5b1b21e0a86832aa4abe55607bd96), MIT-licensed. That repository holds its parts as git submodules;
# each part used here is downloaded as the GitHub archive of the commit the release pins, and checked against its
# SHA256, so the build is exact and needs no git. Only `source/` of public.sdk is extracted: its samples hold paths
# too long for Windows. Set TG_VST3_SDK_DIR to a directory holding base, cmake, pluginterfaces and public.sdk to build
# from a local copy instead (offline).

set(TG_VST3_SDK_PARTS
	"base|vst3_base|fcf9da0bd27a16f7f03773a3a39822f28f5c8477|3753d45a79b5e324c2843d74a18c8f1117fe0b1118fa9312658aecf6360ec603"
	"cmake|vst3_cmake|054c9143cbb8d47fc4694e473f2ee3b4d951a8f5|d8bddb3d87d66b5078e803d271ef82ba51bc74f941e0ca8256e7145950db409e"
	"pluginterfaces|vst3_pluginterfaces|4f547e8e102b47de4a8b8aaf343c73b700786372|ee70777eebb8450fc04fae2a6dd452f723d12bfb8222af443b8c1f8a9259089a"
	"public.sdk|vst3_public_sdk|586dc5e6c8012c3e4b01c79389375cbe96bdb1da|71319491b771068b8e2c5374c22830a339677e9d54c4cd33af4a0aa210e4c838")

function(tg_fetch_vst3_sdk_part destination part repository commit sha256)
	set(directory "${destination}/${part}")
	if(EXISTS "${directory}/.tg-${commit}")
		return()
	endif()
	set(archive "${destination}/${repository}-${commit}.tar.gz")
	message(STATUS "VST3 SDK: downloading ${repository} at ${commit}")
	file(DOWNLOAD "https://github.com/steinbergmedia/${repository}/archive/${commit}.tar.gz" "${archive}"
		EXPECTED_HASH SHA256=${sha256} STATUS status TLS_VERIFY ON)
	list(GET status 0 code)
	if(NOT code EQUAL 0)
		message(FATAL_ERROR "VST3 SDK: could not download ${repository} (${status}). Set TG_VST3_SDK_DIR to a local copy.")
	endif()
	set(patterns "*")
	if(part STREQUAL "public.sdk")
		set(patterns "${repository}-${commit}/source/*" "${repository}-${commit}/LICENSE.txt")
	endif()
	file(REMOVE_RECURSE "${directory}" "${destination}/${repository}-${commit}")
	file(ARCHIVE_EXTRACT INPUT "${archive}" DESTINATION "${destination}" PATTERNS ${patterns})
	file(RENAME "${destination}/${repository}-${commit}" "${directory}")
	file(TOUCH "${directory}/.tg-${commit}")
	file(REMOVE "${archive}")
endfunction()

if(NOT TG_VST3_SDK_DIR)
	set(TG_VST3_SDK_DIR "${CMAKE_BINARY_DIR}/vst3sdk")
	foreach(entry IN LISTS TG_VST3_SDK_PARTS)
		string(REPLACE "|" ";" fields "${entry}")
		tg_fetch_vst3_sdk_part("${TG_VST3_SDK_DIR}" ${fields})
	endforeach()
endif()

# every part says it is MIT-licensed, which fits TuxGuitar's LGPL-2.1
foreach(part base cmake pluginterfaces public.sdk)
	file(STRINGS "${TG_VST3_SDK_DIR}/${part}/LICENSE.txt" TG_VST3_SDK_LICENSE LIMIT_COUNT 1)
	if(NOT TG_VST3_SDK_LICENSE STREQUAL "MIT License")
		message(FATAL_ERROR "VST3 SDK: ${TG_VST3_SDK_DIR}/${part}/LICENSE.txt is not the MIT License (${TG_VST3_SDK_LICENSE})")
	endif()
endforeach()
