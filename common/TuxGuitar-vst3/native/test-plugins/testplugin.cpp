#include "testplugin.h"

#include "pluginterfaces/base/ibstream.h"
#include "public.sdk/source/common/pluginview.h"
#include "public.sdk/source/main/pluginfactory.h"
#include "pluginterfaces/gui/iplugview.h"
#include "pluginterfaces/vst/ivstparameterchanges.h"

#include <cmath>
#include <cstdlib>
#include <cstring>

namespace TuxGuitar {

namespace {

// an editor with nothing on it: enough for a host to open it in a window of its own
class TestView : public CPluginView {
public:
	TestView() : CPluginView(nullptr) {
		ViewRect size(0, 0, 200, 100);
		setRect(size);
	}

	tresult PLUGIN_API isPlatformTypeSupported(FIDString type) SMTG_OVERRIDE {
		bool supported = (std::strcmp(type, kPlatformTypeHWND) == 0 || std::strcmp(type, kPlatformTypeX11EmbedWindowID) == 0 ||
		                  std::strcmp(type, kPlatformTypeNSView) == 0);
		return (supported ? kResultTrue : kResultFalse);
	}
};

SpeakerArrangement arrangement(int32 channels) {
	return (channels == 2 ? SpeakerArr::kStereo : SpeakerArr::kMono);
}

}  // namespace

TestPlugin::TestPlugin(PluginSpec spec) : spec(spec), preset(0), reportedLatency(0) {
	for (const ParamSpec& param : this->spec.params) {
		this->values.push_back(param.def);
	}
}

tresult PLUGIN_API TestPlugin::initialize(FUnknown* context) {
	tresult result = SingleComponentEffect::initialize(context);
	if (result != kResultOk) {
		return result;
	}
	if (spec.instrument) {
		addEventInput(u"Events", 16);
	} else {
		addAudioInput(u"Input", arrangement(spec.inputs));
	}
	addAudioOutput(u"Output", arrangement(spec.outputs));
	for (const ParamSpec& param : spec.params) {
		parameters.addParameter(new RangeParameter(param.title, param.id, nullptr, param.min, param.max, param.def, 0, ParameterInfo::kCanAutomate));
	}
	StringListParameter* presets = new StringListParameter(u"Preset", kPresetParam, nullptr, ParameterInfo::kCanAutomate | ParameterInfo::kIsList | ParameterInfo::kIsProgramChange);
	for (const PresetSpec& entry : spec.presets) {
		presets->appendString(entry.name);
	}
	parameters.addParameter(presets);
	reportedLatency = latency();
	return kResultOk;
}

tresult PLUGIN_API TestPlugin::setActive(TBool state) {
	if (state) {
		reset();
	}
	return kResultOk;
}

tresult PLUGIN_API TestPlugin::setupProcessing(ProcessSetup& setup) {
	tresult result = SingleComponentEffect::setupProcessing(setup);
	reset();
	return result;
}

tresult PLUGIN_API TestPlugin::canProcessSampleSize(int32 size) {
	return (size == kSample32 ? kResultTrue : kResultFalse);
}

tresult PLUGIN_API TestPlugin::setBusArrangements(SpeakerArrangement* inputs, int32 numIns, SpeakerArrangement* outputs, int32 numOuts) {
	bool same = (numOuts == 1 && outputs[0] == arrangement(spec.outputs));
	if (!spec.instrument) {
		same = same && (numIns == 1 && inputs[0] == arrangement(spec.inputs));
	}
	return (same ? kResultTrue : kResultFalse);
}

tresult PLUGIN_API TestPlugin::process(ProcessData& data) {
	applyChanges(data.inputParameterChanges);
	if (data.numSamples > 0 && data.numOutputs > 0) {
		float** in = (spec.instrument || data.numInputs == 0 ? nullptr : data.inputs[0].channelBuffers32);
		render(in, data.outputs[0].channelBuffers32, data.numSamples, data.inputEvents, data.processContext);
		data.outputs[0].silenceFlags = 0;
	}
	checkLatency();
	return kResultOk;
}

// a change of latency is told to the host, which asks for the new one
void TestPlugin::checkLatency() {
	if (latency() != reportedLatency && componentHandler) {
		reportedLatency = latency();
		componentHandler->restartComponent(kLatencyChanged);
	}
}

// the last value of each change in the block, from its start
void TestPlugin::applyChanges(IParameterChanges* changes) {
	int32 count = (changes ? changes->getParameterCount() : 0);
	for (int32 i = 0; i < count; i++) {
		IParamValueQueue* queue = changes->getParameterData(i);
		ParamValue value;
		int32 offset;
		if (queue && queue->getPoint(queue->getPointCount() - 1, offset, value) == kResultTrue) {
			setParamNormalized(queue->getParameterId(), value);
		}
	}
}

tresult PLUGIN_API TestPlugin::setParamNormalized(ParamID id, ParamValue value) {
	tresult result = SingleComponentEffect::setParamNormalized(id, value);
	Parameter* parameter = getParameterObject(id);
	if (id == kPresetParam && parameter) {
		applyPreset((int32) parameter->toPlain(value));
	} else if (parameter) {
		setValue(id, parameter->toPlain(value));
	}
	checkLatency();
	return result;
}

void TestPlugin::applyPreset(int32 index) {
	if (index < 0 || index >= (int32) spec.presets.size()) {
		return;
	}
	preset = index;
	const std::vector<double>& presetValues = spec.presets[index].values;
	for (size_t i = 0; i < spec.params.size() && i < presetValues.size(); i++) {
		ParamID id = spec.params[i].id;
		SingleComponentEffect::setParamNormalized(id, getParameterObject(id)->toNormalized(presetValues[i]));
		values[i] = presetValues[i];
	}
}

void TestPlugin::setValue(ParamID id, double plain) {
	int32 index = indexOf(id);
	if (index >= 0) {
		values[index] = plain;
	}
}

int32 TestPlugin::indexOf(ParamID id) const {
	for (size_t i = 0; i < spec.params.size(); i++) {
		if (spec.params[i].id == id) {
			return (int32) i;
		}
	}
	return -1;
}

double TestPlugin::value(ParamID id) const {
	int32 index = indexOf(id);
	return (index >= 0 ? values[index] : 0.0);
}

// the state: "TGTP", the preset, each parameter's plain value, then zeros up to 1 KB
tresult PLUGIN_API TestPlugin::getState(IBStream* state) {
	char bytes[kStateBytes] = {0};
	std::memcpy(bytes, "TGTP", 4);
	std::memcpy(bytes + 4, &preset, sizeof(int32));
	for (size_t i = 0; i < values.size(); i++) {
		std::memcpy(bytes + 8 + i * sizeof(double), &values[i], sizeof(double));
	}
	int32 written = 0;
	return state->write(bytes, kStateBytes, &written);
}

tresult PLUGIN_API TestPlugin::setState(IBStream* state) {
	char bytes[kStateBytes] = {0};
	int32 read = 0;
	if (state->read(bytes, kStateBytes, &read) != kResultOk || read != kStateBytes || std::memcmp(bytes, "TGTP", 4) != 0) {
		return kResultFalse;
	}
	std::memcpy(&preset, bytes + 4, sizeof(int32));
	for (size_t i = 0; i < values.size(); i++) {
		std::memcpy(&values[i], bytes + 8 + i * sizeof(double), sizeof(double));
		ParamID id = spec.params[i].id;
		SingleComponentEffect::setParamNormalized(id, getParameterObject(id)->toNormalized(values[i]));
	}
	Parameter* presets = getParameterObject(kPresetParam);
	SingleComponentEffect::setParamNormalized(kPresetParam, presets->toNormalized(preset));
	return kResultOk;
}

IPlugView* PLUGIN_API TestPlugin::createView(FIDString name) {
	if (spec.editor && name && std::strcmp(name, ViewType::kEditor) == 0) {
		return new TestView();
	}
	return nullptr;
}

// the test messages play the plugin's own editor: "TG.gesture" moves a parameter through values in one gesture,
// "TG.preset" chooses a preset; "TG.report" answers with what the plugin was told; "TG.crash" takes its process down, as
// a plugin that crashes does
tresult PLUGIN_API TestPlugin::notify(IMessage* message) {
	FIDString id = message->getMessageID();
	IAttributeList* attributes = message->getAttributes();
	if (std::strcmp(id, "TG.gesture") == 0) {
		int64 param = 0;
		const void* data = nullptr;
		uint32 size = 0;
		attributes->getInt("id", param);
		attributes->getBinary("values", data, size);
		std::vector<double> plain(size / sizeof(double));
		if (size > 0) {
			std::memcpy(plain.data(), data, plain.size() * sizeof(double));
		}
		gesture((ParamID) param, plain);
		return kResultOk;
	}
	if (std::strcmp(id, "TG.preset") == 0) {
		int64 index = 0;
		attributes->getInt("index", index);
		choosePreset((int32) index);
		return kResultOk;
	}
	if (std::strcmp(id, "TG.crash") == 0) {
		std::_Exit(70);
	}
	if (std::strcmp(id, "TG.report") == 0) {
		std::vector<char> bytes = report();
		return attributes->setBinary("report", bytes.data(), (uint32) bytes.size());
	}
	return SingleComponentEffect::notify(message);
}

tresult PLUGIN_API TestPlugin::queryInterface(const TUID _iid, void** obj) {
	DEF_INTERFACE(IConnectionPoint)
	return SingleComponentEffect::queryInterface(_iid, obj);
}

void TestPlugin::gesture(ParamID id, const std::vector<double>& plain) {
	beginEdit(id);
	for (double value : plain) {
		edit(id, value);
	}
	endEdit(id);
}

// as the editor's preset menu does it: the preset and every parameter it sets, in one gesture
void TestPlugin::choosePreset(int32 index) {
	beginEdit(kPresetParam);
	for (const ParamSpec& param : spec.params) {
		beginEdit(param.id);
	}
	edit(kPresetParam, index);
	for (const ParamSpec& param : spec.params) {
		performEdit(param.id, getParamNormalized(param.id));
	}
	for (const ParamSpec& param : spec.params) {
		endEdit(param.id);
	}
	endEdit(kPresetParam);
}

void TestPlugin::edit(ParamID id, double plain) {
	ParamValue normalized = getParameterObject(id)->toNormalized(plain);
	setParamNormalized(id, normalized);
	performEdit(id, normalized);
}

}  // namespace TuxGuitar
