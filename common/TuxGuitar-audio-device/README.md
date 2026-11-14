# TuxGuitar-audio-device: the native audio backend

The audio devices TuxGuitar plays and records through: listed per host API (`TGAudioBackend.getDevices`), opened as
streams (`TGAudioBackend.open`) at the device's sample rate, with input channels 1 to N of the input device, whose
callback the device runs on its own thread in blocks of a fixed number of frames, reporting the input and output
latency of the stream. The implementation is `TGPortAudio`, a JNI library (`native/`) over
[PortAudio](https://www.portaudio.com) v19.7.0, built statically into it.

Host APIs: ASIO, WASAPI, DirectSound and MME on Windows (verified); Core Audio on macOS and ALSA (and JACK when found)
on Linux (written, not verified yet).

## Building

Maven runs `native/build.cmd` (Windows) or `native/build.sh` before the tests; CMake downloads PortAudio and, on
Windows, the ASIO SDK, each pinned by archive and SHA256 (`native/cmake/`), into `native-build/` (git-ignored, built
once). Without a C++ toolchain the build stops, saying what to install. No binary is committed.

## Licensing

- PortAudio: MIT-style licence (checked at configure time), which fits TuxGuitar's LGPL-2.1.
- ASIO SDK 2.3.4 (Steinberg): offered under the proprietary Steinberg ASIO License or, alternatively, under the GNU
  General Public License version 3. TuxGuitar's build takes the GPLv3 option, and the configuration checks that the
  SDK's `LICENSE.txt` offers it. **Builds with ASIO are distributed under GPLv3.** Configure with `-DTG_AUDIO_ASIO=OFF`
  for a build without ASIO, which stays under TuxGuitar's licence. "ASIO" is a trademark of Steinberg Media
  Technologies GmbH.
