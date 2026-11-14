#include "instance.h"

#include "public.sdk/source/vst/hosting/hostclasses.h"
#include "pluginterfaces/base/funknownimpl.h"
#include "pluginterfaces/base/ibstream.h"
#include "pluginterfaces/vst/ivstmessage.h"
#include "pluginterfaces/vst/ivstnoteexpression.h"

#include <algorithm>
#include <cstring>

#ifdef _WIN32
#include <windows.h>
#endif

namespace TuxGuitar {

using namespace Steinberg::Vst;

namespace {

// what the controller tells the host goes to the instance
class Handler : public U::Implements<U::Directly<IComponentHandler>> {
public:
	explicit Handler(Instance* instance) : instance(instance) {}

	tresult PLUGIN_API beginEdit(ParamID id) override { return tell(Notification::kBeginEdit, id, 0.0); }
	tresult PLUGIN_API performEdit(ParamID id, ParamValue normalized) override { return tell(Notification::kPerformEdit, id, normalized); }
	tresult PLUGIN_API endEdit(ParamID id) override { return tell(Notification::kEndEdit, id, 0.0); }
	tresult PLUGIN_API restartComponent(int32 flags) override { return tell(Notification::kRestart, flags, 0.0); }

private:
	tresult tell(int32 kind, int32 id, double normalized) {
		instance->notify(kind, id, normalized);
		return kResultOk;
	}

	Instance* instance;
};

// a stream over bytes in memory
class ByteStream : public U::Implements<U::Directly<IBStream>> {
public:
	ByteStream() {}
	explicit ByteStream(const std::vector<char>& bytes) : bytes(bytes) {}

	tresult PLUGIN_API read(void* buffer, int32 count, int32* read) override {
		int32 available = (int32) std::max<int64>(0, (int64) bytes.size() - position);
		int32 done = std::min(count, available);
		if (done > 0) {
			std::memcpy(buffer, bytes.data() + position, (size_t) done);
		}
		position += done;
		if (read) {
			*read = done;
		}
		return kResultOk;
	}

	tresult PLUGIN_API write(void* buffer, int32 count, int32* written) override {
		if (position + count > (int64) bytes.size()) {
			bytes.resize((size_t) (position + count));
		}
		std::memcpy(bytes.data() + position, buffer, (size_t) count);
		position += count;
		if (written) {
			*written = count;
		}
		return kResultOk;
	}

	tresult PLUGIN_API seek(int64 pos, int32 mode, int64* result) override {
		int64 base = (mode == kIBSeekSet ? 0 : (mode == kIBSeekCur ? position : (int64) bytes.size()));
		position = std::max<int64>(0, base + pos);
		if (result) {
			*result = position;
		}
		return kResultOk;
	}

	tresult PLUGIN_API tell(int64* pos) override {
		if (pos) {
			*pos = position;
		}
		return kResultOk;
	}

	const std::vector<char>& data() const { return bytes; }

private:
	std::vector<char> bytes;
	int64 position = 0;
};

int32 mainChannels(IComponent* component, BusDirection direction) {
	BusInfo info = {};
	if (component->getBusCount(kAudio, direction) > 0 && component->getBusInfo(kAudio, direction, 0, info) == kResultOk) {
		return info.channelCount;
	}
	return 0;
}

}  // namespace

Instance::Instance(VST3::Hosting::Module::Ptr module, int32 maxBlock, const std::string& sharedPath)
    : module(module), maxBlock(maxBlock), shared(sharedPath, maxBlock), eventList(1024), changes(256), outputChanges(256) {}

std::unique_ptr<Instance> Instance::open(VST3::Hosting::Module::Ptr module, const VST3::UID& classId, FUnknown* host, double sampleRate,
                                         int32 maxBlock, const std::string& sharedPath, std::string& error) {
	std::unique_ptr<Instance> instance(new Instance(module, maxBlock, sharedPath));
	if (!instance->shared.isOpen()) {
		error = "cannot map the shared blocks " + sharedPath;
		return nullptr;
	}
	if (!instance->load(classId, host, error)) {
		return nullptr;
	}
	instance->setupBuses();
	if (!instance->start(sampleRate, error)) {
		return nullptr;
	}
	return instance;
}

bool Instance::load(const VST3::UID& classId, FUnknown* host, std::string& error) {
	component = module->getFactory().createInstance<IComponent>(classId);
	if (!component || component->initialize(host) != kResultOk) {
		error = "the plugin's component did not start";
		component = nullptr;
		return false;
	}
	processor = U::cast<IAudioProcessor>(component);
	controller = U::cast<IEditController>(component);
	if (!processor || (!controller && !loadController(host))) {
		error = "the plugin has no audio processor or no edit controller";
		return false;
	}
	handler = owned(new Handler(this));
	controller->setComponentHandler(handler);
	return true;
}

// a controller apart from the component: created, connected to it, and given its state
bool Instance::loadController(FUnknown* host) {
	TUID cid;
	if (component->getControllerClassId(cid) != kResultTrue) {
		return false;
	}
	controller = module->getFactory().createInstance<IEditController>(VST3::UID::fromTUID(cid));
	if (!controller || controller->initialize(host) != kResultOk) {
		controller = nullptr;
		return false;
	}
	separateController = true;
	IPtr<IConnectionPoint> fromComponent = U::cast<IConnectionPoint>(component);
	IPtr<IConnectionPoint> fromController = U::cast<IConnectionPoint>(controller);
	if (fromComponent && fromController) {
		fromComponent->connect(fromController);
		fromController->connect(fromComponent);
	}
	ByteStream state;
	component->getState(&state);
	ByteStream copy(state.data());
	controller->setComponentState(&copy);
	return true;
}

// the main audio buses and the main event input are on; side chains are off
void Instance::setupBuses() {
	for (BusDirection direction : {kInput, kOutput}) {
		int32 count = component->getBusCount(kAudio, direction);
		for (int32 i = 0; i < count; i++) {
			component->activateBus(kAudio, direction, i, i == 0);
		}
	}
	if (component->getBusCount(kEvent, kInput) > 0) {
		component->activateBus(kEvent, kInput, 0, true);
	}
	inputChannels = mainChannels(component, kInput);
	outputChannels = mainChannels(component, kOutput);
}

bool Instance::start(double rate, std::string& error) {
	sampleRate = rate;
	ProcessSetup setup = {kRealtime, kSample32, maxBlock, rate};
	if (processor->setupProcessing(setup) != kResultOk || !data.prepare(*component, maxBlock, kSample32)) {
		error = "the plugin refused to process 32-bit float blocks of " + std::to_string(maxBlock) + " samples";
		return false;
	}
	data.inputEvents = &eventList;
	data.inputParameterChanges = &changes;
	data.outputParameterChanges = &outputChanges;
	data.processContext = &context;
	component->setActive(true);
	processor->setProcessing(true);
	active = true;
	return true;
}

void Instance::stop() {
	if (active) {
		processor->setProcessing(false);
		component->setActive(false);
		active = false;
	}
}

Instance::~Instance() {
	closeEditor();
	if (component) {
		stop();
	}
	if (separateController) {
		IPtr<IConnectionPoint> fromComponent = U::cast<IConnectionPoint>(component);
		IPtr<IConnectionPoint> fromController = U::cast<IConnectionPoint>(controller);
		if (fromComponent && fromController) {
			fromComponent->disconnect(fromController);
			fromController->disconnect(fromComponent);
		}
	}
	if (controller) {
		controller->setComponentHandler(nullptr);
		if (separateController) {
			controller->terminate();
		}
	}
	if (component) {
		component->terminate();
	}
}

int32 Instance::latency() const { return (int32) processor->getLatencySamples(); }

// kInfiniteTail comes out as -1
int32 Instance::tail() const { return (int32) processor->getTailSamples(); }

void Instance::process(int32 frames, const Transport& transport, const std::vector<NoteEvent>& events) {
	frames = std::max(0, std::min(frames, maxBlock));
	copyIn(frames);
	addEvents(events);
	setContext(frames, transport);
	data.numSamples = frames;
	processor->process(data);
	copyOut(frames);
	eventList.clear();
	changes.clearQueue();
	outputChanges.clearQueue();
}

// a mono input takes the mean of the two sides; a stereo one each side; any other channel silence
void Instance::copyIn(int32 frames) {
	if (data.numInputs == 0) {
		return;
	}
	AudioBusBuffers& bus = data.inputs[0];
	for (int32 c = 0; c < bus.numChannels; c++) {
		float* to = bus.channelBuffers32[c];
		for (int32 i = 0; i < frames; i++) {
			float left = shared.input(0)[i];
			float right = shared.input(1)[i];
			to[i] = (bus.numChannels == 1 ? 0.5f * (left + right) : (c < 2 ? (c == 0 ? left : right) : 0.f));
		}
	}
}

// a mono output sounds on both sides
void Instance::copyOut(int32 frames) {
	int32 channels = (data.numOutputs > 0 ? data.outputs[0].numChannels : 0);
	for (int32 c = 0; c < 2; c++) {
		float* to = shared.output(c);
		if (channels == 0) {
			std::fill(to, to + frames, 0.f);
		} else {
			std::memcpy(to, data.outputs[0].channelBuffers32[std::min(c, channels - 1)], sizeof(float) * (size_t) frames);
		}
	}
}

void Instance::addEvents(const std::vector<NoteEvent>& events) {
	for (const NoteEvent& note : events) {
		Event event = {};
		event.sampleOffset = note.offset;
		event.flags = Event::kIsLive;
		if (note.type == NoteEvent::kNoteOn) {
			event.type = Event::kNoteOnEvent;
			event.noteOn = {0, (int16) note.pitch, 0.f, note.value, 0, note.noteId};
		} else if (note.type == NoteEvent::kNoteOff) {
			event.type = Event::kNoteOffEvent;
			event.noteOff = {0, (int16) note.pitch, note.value, note.noteId, 0.f};
		} else {
			event.type = Event::kNoteExpressionValueEvent;
			event.noteExpressionValue = {kTuningTypeID, note.noteId, 0.5 + note.value / 240.0};
		}
		eventList.addEvent(event);
	}
}

void Instance::setContext(int32 frames, const Transport& transport) {
	context.state = ProcessContext::kTempoValid | ProcessContext::kTimeSigValid | ProcessContext::kProjectTimeMusicValid |
	                ProcessContext::kContTimeValid | (transport.playing ? ProcessContext::kPlaying : 0);
	context.sampleRate = sampleRate;
	context.projectTimeSamples = transport.samples;
	context.tempo = transport.tempo;
	context.timeSigNumerator = transport.numerator;
	context.timeSigDenominator = transport.denominator;
	context.projectTimeMusic = transport.quarters;
	context.continousTimeSamples += frames;  // (sic: the SDK spells it so)
}

std::vector<ParameterInfo> Instance::parameters() {
	std::vector<ParameterInfo> infos;
	int32 count = controller->getParameterCount();
	for (int32 i = 0; i < count; i++) {
		Vst::ParameterInfo info = {};
		if (controller->getParameterInfo(i, info) == kResultOk) {
			std::string title;
			for (int k = 0; k < 128 && info.title[k] != 0; k++) {
				title.push_back(info.title[k] < 128 ? (char) info.title[k] : '?');
			}
			double minimum = controller->normalizedParamToPlain(info.id, 0.0);
			double maximum = controller->normalizedParamToPlain(info.id, 1.0);
			infos.push_back(ParameterInfo{(int32) info.id, title, parameter((int32) info.id), minimum, maximum, info.stepCount, info.flags});
		}
	}
	return infos;
}

double Instance::parameter(int32 id) {
	return controller->normalizedParamToPlain((ParamID) id, controller->getParamNormalized((ParamID) id));
}

// on the controller now, and on the processor from the start of the next block
double Instance::setParameter(int32 id, double plain) {
	ParamValue normalized = controller->plainParamToNormalized((ParamID) id, plain);
	controller->setParamNormalized((ParamID) id, normalized);
	int32 index = 0;
	IParamValueQueue* queue = changes.addParameterData((ParamID) id, index);
	if (queue) {
		queue->addPoint(0, normalized, index);
	}
	return parameter(id);
}

std::vector<char> Instance::componentState() {
	ByteStream state;
	component->getState(&state);
	return state.data();
}

std::vector<char> Instance::controllerState() {
	ByteStream state;
	if (separateController) {
		controller->getState(&state);
	}
	return state.data();
}

void Instance::setState(const std::vector<char>& componentBytes, const std::vector<char>& controllerBytes) {
	ByteStream state(componentBytes);
	component->setState(&state);
	if (separateController) {
		ByteStream copy(componentBytes);
		controller->setComponentState(&copy);
		if (!controllerBytes.empty()) {
			ByteStream own(controllerBytes);
			controller->setState(&own);
		}
	}
}

// what still sounds (tails, voices) is dropped: the plugin starts again
void Instance::reset() {
	stop();
	component->setActive(true);
	processor->setProcessing(true);
	active = true;
}

bool Instance::hasEditor() {
	IPtr<IPlugView> editor = owned(controller->createView(ViewType::kEditor));
	return editor != nullptr;
}

#ifdef _WIN32
bool Instance::openEditor() {
	closeEditor();
	view = owned(controller->createView(ViewType::kEditor));
	if (!view || view->isPlatformTypeSupported(kPlatformTypeHWND) != kResultTrue) {
		view = nullptr;
		return false;
	}
	ViewRect size = {0, 0, 400, 300};
	view->getSize(&size);
	window = CreateWindowExW(0, L"STATIC", L"", WS_OVERLAPPEDWINDOW, CW_USEDEFAULT, CW_USEDEFAULT, size.getWidth(), size.getHeight(), nullptr,
	                         nullptr, GetModuleHandleW(nullptr), nullptr);
	if (!window || view->attached(window, kPlatformTypeHWND) != kResultOk) {
		closeEditor();
		return false;
	}
	return true;
}

void Instance::closeEditor() {
	if (view && window) {
		view->removed();
	}
	view = nullptr;
	if (window) {
		DestroyWindow((HWND) window);
		window = nullptr;
	}
}
#else
// editor windows need the platform's window system: only Windows so far
bool Instance::openEditor() { return false; }

void Instance::closeEditor() { view = nullptr; }
#endif

std::vector<char> Instance::message(const std::string& id, const std::vector<std::pair<std::string, int64>>& integers,
                                    const std::vector<std::pair<std::string, std::vector<char>>>& binaries) {
	IPtr<IConnectionPoint> connection = U::cast<IConnectionPoint>(controller);
	if (!connection) {
		return std::vector<char>();
	}
	IPtr<IMessage> message = owned(new HostMessage());
	message->setMessageID(id.c_str());
	IAttributeList* attributes = message->getAttributes();
	for (const auto& entry : integers) {
		attributes->setInt(entry.first.c_str(), entry.second);
	}
	for (const auto& entry : binaries) {
		attributes->setBinary(entry.first.c_str(), entry.second.data(), (uint32) entry.second.size());
	}
	connection->notify(message);
	const void* report = nullptr;
	uint32 size = 0;
	if (attributes->getBinary("report", report, size) == kResultOk && report) {
		const char* bytes = static_cast<const char*>(report);
		return std::vector<char>(bytes, bytes + size);
	}
	return std::vector<char>();
}

void Instance::notify(int32 kind, int32 id, double normalized) {
	double plain = (kind == Notification::kPerformEdit ? controller->normalizedParamToPlain((ParamID) id, normalized) : 0.0);
	notifications.push_back(Notification{kind, id, plain});
	if (kind == Notification::kPerformEdit && separateController) {
		int32 index = 0;
		IParamValueQueue* queue = changes.addParameterData((ParamID) id, index);
		if (queue) {
			queue->addPoint(0, normalized, index);
		}
	}
}

std::vector<Notification> Instance::takeNotifications() {
	std::vector<Notification> taken;
	taken.swap(notifications);
	return taken;
}

}  // namespace TuxGuitar
