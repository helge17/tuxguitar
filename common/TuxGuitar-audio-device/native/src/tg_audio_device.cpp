// The JNI functions of app.tuxguitar.audio.device.TGPortAudio: PortAudio's devices and host APIs, and streams whose
// device callback calls the Java stream's callback(int frames) on the device's own thread. Samples cross in two direct
// buffers the Java stream owns, 32-bit float, planar: channel c from c * frames (frames: the stream's block). Each
// function is small; their behaviour is tested from Java (TestPortAudio).
#include <jni.h>
#include <portaudio.h>

#include <cstring>

namespace {

JavaVM* vm = nullptr;

struct Stream {
	PaStream* stream = nullptr;
	jobject target = nullptr;
	jmethodID callback = nullptr;
	float* input = nullptr;
	float* output = nullptr;
	int inputChannels = 0;
	int outputChannels = 0;
	unsigned long frames = 0;
};

// the device thread's JNIEnv: attached once, as a daemon, so it never keeps the JVM from exiting
JNIEnv* environment() {
	JNIEnv* env = nullptr;
	if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_8) == JNI_EDETACHED) {
		vm->AttachCurrentThreadAsDaemon(reinterpret_cast<void**>(&env), nullptr);
	}
	return env;
}

void copyIn(const Stream* stream, const void* input, unsigned long frames) {
	const float* const* channels = static_cast<const float* const*>(input);
	for (int c = 0; c < stream->inputChannels; c++) {
		float* to = stream->input + c * stream->frames;
		if (channels != nullptr) {
			std::memcpy(to, channels[c], frames * sizeof(float));
		} else {
			std::memset(to, 0, frames * sizeof(float));
		}
	}
}

void copyOut(const Stream* stream, void* output, unsigned long frames, bool silent) {
	float* const* channels = static_cast<float* const*>(output);
	for (int c = 0; c < stream->outputChannels && channels != nullptr; c++) {
		if (silent) {
			std::memset(channels[c], 0, frames * sizeof(float));
		} else {
			std::memcpy(channels[c], stream->output + c * stream->frames, frames * sizeof(float));
		}
	}
}

// the device asks for a block: the Java stream fills it; a Java exception leaves it silent and the stream running
int process(const void* input, void* output, unsigned long frames, const PaStreamCallbackTimeInfo*, PaStreamCallbackFlags, void* data) {
	Stream* stream = static_cast<Stream*>(data);
	unsigned long block = (frames < stream->frames ? frames : stream->frames);
	copyIn(stream, input, block);
	JNIEnv* env = environment();
	env->CallVoidMethod(stream->target, stream->callback, static_cast<jint>(block));
	bool failed = env->ExceptionCheck();
	if (failed) {
		env->ExceptionClear();
	}
	copyOut(stream, output, frames, failed || block < frames);
	return paContinue;
}

PaStreamParameters parameters(int device, int channels, bool input) {
	const PaDeviceInfo* info = Pa_GetDeviceInfo(device);
	PaStreamParameters parameters;
	parameters.device = device;
	parameters.channelCount = channels;
	parameters.sampleFormat = paFloat32 | paNonInterleaved;
	parameters.suggestedLatency = (info == nullptr ? 0 : (input ? info->defaultLowInputLatency : info->defaultLowOutputLatency));
	parameters.hostApiSpecificStreamInfo = nullptr;
	return parameters;
}

Stream* streamOf(jlong handle) {
	return reinterpret_cast<Stream*>(handle);
}

void release(JNIEnv* env, Stream* stream) {
	if (stream->target != nullptr) {
		env->DeleteGlobalRef(stream->target);
	}
	delete stream;
}

const PaDeviceInfo* device(jint index) {
	return (index >= 0 && index < Pa_GetDeviceCount() ? Pa_GetDeviceInfo(index) : nullptr);
}

}

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* javaVm, void*) {
	vm = javaVm;
	return JNI_VERSION_1_8;
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nInitialize(JNIEnv*, jclass) {
	return Pa_Initialize();
}

JNIEXPORT void JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nTerminate(JNIEnv*, jclass) {
	Pa_Terminate();
}

JNIEXPORT jstring JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nErrorText(JNIEnv* env, jclass, jint error) {
	return env->NewStringUTF(Pa_GetErrorText(error));
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nHostApiCount(JNIEnv*, jclass) {
	return Pa_GetHostApiCount();
}

JNIEXPORT jstring JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nHostApiName(JNIEnv* env, jclass, jint index) {
	const PaHostApiInfo* info = Pa_GetHostApiInfo(index);
	return env->NewStringUTF(info == nullptr ? "" : info->name);
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nDeviceCount(JNIEnv*, jclass) {
	return Pa_GetDeviceCount();
}

JNIEXPORT jstring JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nDeviceName(JNIEnv* env, jclass, jint index) {
	const PaDeviceInfo* info = device(index);
	return env->NewStringUTF(info == nullptr ? "" : info->name);
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nDeviceHostApi(JNIEnv*, jclass, jint index) {
	const PaDeviceInfo* info = device(index);
	return (info == nullptr ? -1 : info->hostApi);
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nDeviceInputs(JNIEnv*, jclass, jint index) {
	const PaDeviceInfo* info = device(index);
	return (info == nullptr ? 0 : info->maxInputChannels);
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nDeviceOutputs(JNIEnv*, jclass, jint index) {
	const PaDeviceInfo* info = device(index);
	return (info == nullptr ? 0 : info->maxOutputChannels);
}

JNIEXPORT jdouble JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nDeviceSampleRate(JNIEnv*, jclass, jint index) {
	const PaDeviceInfo* info = device(index);
	return (info == nullptr ? 0 : info->defaultSampleRate);
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nDefaultInput(JNIEnv*, jclass) {
	return Pa_GetDefaultInputDevice();
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nDefaultOutput(JNIEnv*, jclass) {
	return Pa_GetDefaultOutputDevice();
}

// a stream of the channels of each device (none: no device), not started; its handle, or a PortAudio error (< 0)
JNIEXPORT jlong JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nOpen(JNIEnv* env, jclass, jobject target, jint inputDevice,
		jint inputChannels, jint outputDevice, jint outputChannels, jdouble sampleRate, jint frames, jobject input, jobject output) {
	Stream* stream = new Stream();
	stream->target = env->NewGlobalRef(target);
	stream->callback = env->GetMethodID(env->GetObjectClass(target), "callback", "(I)V");
	stream->input = (input != nullptr ? static_cast<float*>(env->GetDirectBufferAddress(input)) : nullptr);
	stream->output = static_cast<float*>(env->GetDirectBufferAddress(output));
	stream->inputChannels = inputChannels;
	stream->outputChannels = outputChannels;
	stream->frames = static_cast<unsigned long>(frames);
	PaStreamParameters in = parameters(inputDevice, inputChannels, true);
	PaStreamParameters out = parameters(outputDevice, outputChannels, false);
	PaError error = Pa_OpenStream(&stream->stream, (inputChannels > 0 ? &in : nullptr), (outputChannels > 0 ? &out : nullptr),
		sampleRate, stream->frames, paNoFlag, process, stream);
	if (error != paNoError) {
		release(env, stream);
		return error;
	}
	return reinterpret_cast<jlong>(stream);
}

JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nStart(JNIEnv*, jclass, jlong handle) {
	return Pa_StartStream(streamOf(handle)->stream);
}

// stops the device after its last callback returned, closes it and frees the stream
JNIEXPORT jint JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nClose(JNIEnv* env, jclass, jlong handle) {
	Stream* stream = streamOf(handle);
	if (Pa_IsStreamActive(stream->stream) == 1) {
		Pa_StopStream(stream->stream);
	}
	PaError error = Pa_CloseStream(stream->stream);
	release(env, stream);
	return error;
}

JNIEXPORT jdouble JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nSampleRate(JNIEnv*, jclass, jlong handle) {
	return Pa_GetStreamInfo(streamOf(handle)->stream)->sampleRate;
}

JNIEXPORT jdouble JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nInputLatency(JNIEnv*, jclass, jlong handle) {
	return Pa_GetStreamInfo(streamOf(handle)->stream)->inputLatency;
}

JNIEXPORT jdouble JNICALL Java_app_tuxguitar_audio_device_TGPortAudio_nOutputLatency(JNIEnv*, jclass, jlong handle) {
	return Pa_GetStreamInfo(streamOf(handle)->stream)->outputLatency;
}

}
