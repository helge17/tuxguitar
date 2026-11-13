// One VST3 plugin loaded in the helper: its component, audio processor and edit controller, set up for 32-bit float
// blocks of up to the maximum block size agreed when it opens, exchanging its audio with TuxGuitar through a shared
// file mapping (SharedBlocks) and keeping what its controller tells the host (edits, restarts) for TuxGuitar.
#pragma once

#include "shared.h"

#include "public.sdk/source/vst/hosting/eventlist.h"
#include "public.sdk/source/vst/hosting/module.h"
#include "public.sdk/source/vst/hosting/parameterchanges.h"
#include "public.sdk/source/vst/hosting/processdata.h"
#include "pluginterfaces/gui/iplugview.h"
#include "pluginterfaces/vst/ivstaudioprocessor.h"
#include "pluginterfaces/vst/ivstcomponent.h"
#include "pluginterfaces/vst/ivsteditcontroller.h"
#include "pluginterfaces/vst/ivstprocesscontext.h"

#include <memory>
#include <string>
#include <vector>

namespace TuxGuitar {

using namespace Steinberg;

// what the controller told the host
struct Notification {
	enum Kind { kBeginEdit = 1, kPerformEdit = 2, kEndEdit = 3, kRestart = 4 };
	int32 kind;
	int32 id;     // the parameter, or the restart flags
	double plain; // the parameter's plain value
};

// a note event of a block, at a sample of it
struct NoteEvent {
	enum Type { kNoteOn = 1, kNoteOff = 2, kTuning = 3 };
	int32 type;
	int32 offset;
	int32 pitch;
	int32 noteId;
	float value;  // velocity 0..1, or the tuning in semitones
};

// what the song is doing at the block's first sample
struct Transport {
	bool playing;
	double tempo;
	int32 numerator;
	int32 denominator;
	double quarters;
	int64 samples;
};

struct ParameterInfo {
	int32 id;
	std::string title;
	double plain;
	int32 flags;
};

class Instance {
public:
	static std::unique_ptr<Instance> open(VST3::Hosting::Module::Ptr module, const VST3::UID& classId, FUnknown* host, double sampleRate,
	                                      int32 maxBlock, const std::string& shared, std::string& error);
	~Instance();

	int32 inputs() const { return inputChannels; }
	int32 outputs() const { return outputChannels; }
	int32 latency() const;
	int32 tail() const;
	bool hasEditor();
	std::vector<ParameterInfo> parameters();

	void process(int32 frames, const Transport& transport, const std::vector<NoteEvent>& events);
	double setParameter(int32 id, double plain);
	double parameter(int32 id);
	std::vector<char> componentState();
	std::vector<char> controllerState();
	void setState(const std::vector<char>& component, const std::vector<char>& controller);
	void reset();
	bool openEditor();
	void closeEditor();
	// sends the controller a message with the integer and binary attributes; answers its binary attribute "report"
	std::vector<char> message(const std::string& id, const std::vector<std::pair<std::string, int64>>& integers,
	                          const std::vector<std::pair<std::string, std::vector<char>>>& binaries);
	std::vector<Notification> takeNotifications();

	// what the controller's component handler hears
	void notify(int32 kind, int32 id, double normalized);

private:
	Instance(VST3::Hosting::Module::Ptr module, int32 maxBlock, const std::string& shared);
	bool load(const VST3::UID& classId, FUnknown* host, std::string& error);
	bool loadController(FUnknown* host);
	void setupBuses();
	bool start(double sampleRate, std::string& error);
	void stop();
	void copyIn(int32 frames);
	void copyOut(int32 frames);
	void addEvents(const std::vector<NoteEvent>& events);
	void setContext(int32 frames, const Transport& transport);

	VST3::Hosting::Module::Ptr module;
	int32 maxBlock;
	double sampleRate = 44100.0;
	SharedBlocks shared;
	IPtr<Vst::IComponent> component;
	IPtr<Vst::IAudioProcessor> processor;
	IPtr<Vst::IEditController> controller;
	bool separateController = false;
	IPtr<Vst::IComponentHandler> handler;
	IPtr<IPlugView> view;
	void* window = nullptr;
	int32 inputChannels = 0;
	int32 outputChannels = 0;
	Vst::HostProcessData data;
	Vst::ProcessContext context = {};
	Vst::EventList eventList;
	Vst::ParameterChanges changes;
	Vst::ParameterChanges outputChanges;
	std::vector<Notification> notifications;
	bool active = false;
};

}  // namespace TuxGuitar
