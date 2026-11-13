# The ASIO SDK (Windows), pinned: version 2.3.4 of 2025-10-15, the archive www.steinberg.net/asiosdk links to,
# checked against its SHA256. Steinberg offers it under the proprietary Steinberg ASIO License or, alternatively, under
# the GNU General Public License version 3. TuxGuitar takes the GPLv3 option: a build with ASIO is distributed under
# GPLv3 (see the module's README). The configuration checks that the SDK's licence offers that option. Only common/,
# host/ and LICENSE.txt are extracted. Set TG_ASIO_SDK_DIR to a local copy of that SDK (the folder holding common/ and
# host/) to build offline; configure with -DTG_AUDIO_ASIO=OFF to build without ASIO.

set(TG_ASIO_SDK_ARCHIVE "ASIO-SDK_2.3.4_2025-10-15.zip")
set(TG_ASIO_SDK_SHA256 "d5ebf0c20dd2c5f43771fd0c1418f4b361bf52434ee670097cfa6b3a335e2eca")

if(NOT TG_ASIO_SDK_DIR)
	set(TG_ASIO_SDK_DIR "${CMAKE_BINARY_DIR}/asiosdk/ASIOSDK")
	if(NOT EXISTS "${TG_ASIO_SDK_DIR}/.tg-${TG_ASIO_SDK_SHA256}")
		set(archive "${CMAKE_BINARY_DIR}/${TG_ASIO_SDK_ARCHIVE}")
		message(STATUS "ASIO SDK: downloading ${TG_ASIO_SDK_ARCHIVE}")
		file(DOWNLOAD "https://download.steinberg.net/sdk_downloads/${TG_ASIO_SDK_ARCHIVE}" "${archive}"
			EXPECTED_HASH SHA256=${TG_ASIO_SDK_SHA256} STATUS status TLS_VERIFY ON)
		list(GET status 0 code)
		if(NOT code EQUAL 0)
			message(FATAL_ERROR "ASIO SDK: could not download it (${status}). Set TG_ASIO_SDK_DIR to a local copy, or configure with -DTG_AUDIO_ASIO=OFF.")
		endif()
		file(REMOVE_RECURSE "${CMAKE_BINARY_DIR}/asiosdk")
		file(ARCHIVE_EXTRACT INPUT "${archive}" DESTINATION "${CMAKE_BINARY_DIR}/asiosdk"
			PATTERNS "ASIOSDK/common/*" "ASIOSDK/host/*" "ASIOSDK/LICENSE.txt")
		file(TOUCH "${TG_ASIO_SDK_DIR}/.tg-${TG_ASIO_SDK_SHA256}")
		file(REMOVE "${archive}")
	endif()
endif()

file(READ "${TG_ASIO_SDK_DIR}/LICENSE.txt" TG_ASIO_SDK_LICENSE)
string(FIND "${TG_ASIO_SDK_LICENSE}" "or alternatively under the terms of the General Public License (GPL) Version 3" TG_ASIO_SDK_GPL)
string(FIND "${TG_ASIO_SDK_LICENSE}" "b) General Public License (GPL) Version 3" TG_ASIO_SDK_GPL_OPTION)
if(TG_ASIO_SDK_GPL EQUAL -1 OR TG_ASIO_SDK_GPL_OPTION EQUAL -1)
	message(FATAL_ERROR "ASIO SDK: ${TG_ASIO_SDK_DIR}/LICENSE.txt does not offer the GPLv3 option TuxGuitar's build takes. Configure with -DTG_AUDIO_ASIO=OFF to build without ASIO.")
endif()
message(STATUS "ASIO SDK: under its GPLv3 option; this build of the audio backend is distributed under GPLv3")
