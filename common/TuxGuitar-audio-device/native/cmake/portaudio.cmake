# PortAudio, pinned: release v19.7.0 of github.com/PortAudio/portaudio (commit
# 147dd722548358763a8b649b3e4b41dfffbcfbb6), downloaded as the GitHub archive of that commit and checked against its
# SHA256, so the build is exact and needs no git. Its licence is MIT-style ("Permission is hereby granted, free of
# charge ..."), which fits TuxGuitar's LGPL-2.1. Set TG_PORTAUDIO_DIR to a local copy of that release to build offline.

set(TG_PORTAUDIO_COMMIT "147dd722548358763a8b649b3e4b41dfffbcfbb6")
set(TG_PORTAUDIO_SHA256 "95457b809ce60d4d4790f84bb692e271f644e59d8adf96feb988c89ab52a506a")

if(NOT TG_PORTAUDIO_DIR)
	set(TG_PORTAUDIO_DIR "${CMAKE_BINARY_DIR}/portaudio")
	if(NOT EXISTS "${TG_PORTAUDIO_DIR}/.tg-${TG_PORTAUDIO_COMMIT}")
		set(archive "${CMAKE_BINARY_DIR}/portaudio-${TG_PORTAUDIO_COMMIT}.tar.gz")
		message(STATUS "PortAudio: downloading v19.7.0 (${TG_PORTAUDIO_COMMIT})")
		file(DOWNLOAD "https://github.com/PortAudio/portaudio/archive/${TG_PORTAUDIO_COMMIT}.tar.gz" "${archive}"
			EXPECTED_HASH SHA256=${TG_PORTAUDIO_SHA256} STATUS status TLS_VERIFY ON)
		list(GET status 0 code)
		if(NOT code EQUAL 0)
			message(FATAL_ERROR "PortAudio: could not download it (${status}). Set TG_PORTAUDIO_DIR to a local copy.")
		endif()
		file(REMOVE_RECURSE "${TG_PORTAUDIO_DIR}" "${CMAKE_BINARY_DIR}/portaudio-${TG_PORTAUDIO_COMMIT}")
		file(ARCHIVE_EXTRACT INPUT "${archive}" DESTINATION "${CMAKE_BINARY_DIR}")
		file(RENAME "${CMAKE_BINARY_DIR}/portaudio-${TG_PORTAUDIO_COMMIT}" "${TG_PORTAUDIO_DIR}")
		file(TOUCH "${TG_PORTAUDIO_DIR}/.tg-${TG_PORTAUDIO_COMMIT}")
		file(REMOVE "${archive}")
	endif()
endif()

file(READ "${TG_PORTAUDIO_DIR}/LICENSE.txt" TG_PORTAUDIO_LICENSE)
string(FIND "${TG_PORTAUDIO_LICENSE}" "Permission is hereby granted, free of charge" TG_PORTAUDIO_MIT)
if(TG_PORTAUDIO_MIT EQUAL -1)
	message(FATAL_ERROR "PortAudio: ${TG_PORTAUDIO_DIR}/LICENSE.txt is not its MIT-style licence")
endif()
