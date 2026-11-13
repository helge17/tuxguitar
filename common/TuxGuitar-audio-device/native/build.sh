#!/bin/sh
# Builds the native audio backend, a JNI library over PortAudio (see CMakeLists.txt): build.sh <build directory>
# Needs CMake 3.25+, a C++17 compiler and a JDK on the PATH (Ninja when there, else make); ALSA's development files on
# Linux. Not verified yet on Linux or macOS.
set -e
SOURCE=$(cd "$(dirname "$0")" && pwd)
OUTPUT=${1:-"$SOURCE/../native-build"}

if ! command -v cmake >/dev/null 2>&1 || ! { command -v c++ >/dev/null 2>&1 || command -v g++ >/dev/null 2>&1 || command -v clang++ >/dev/null 2>&1; }; then
	echo "Audio backend native build: no C++ toolchain found. The audio backend needs CMake 3.25+ and a C++17 compiler." >&2
	exit 1
fi
if [ ! -f "$OUTPUT/CMakeCache.txt" ]; then
	if command -v ninja >/dev/null 2>&1; then
		cmake -S "$SOURCE" -B "$OUTPUT" -G Ninja -DCMAKE_BUILD_TYPE=Release
	else
		cmake -S "$SOURCE" -B "$OUTPUT" -DCMAKE_BUILD_TYPE=Release
	fi
fi
cmake --build "$OUTPUT"
