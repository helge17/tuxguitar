// tuxguitar-vst3-host: runs VST3 plugins apart from TuxGuitar, so a plugin that crashes takes only this process down.
// TuxGuitar starts it and talks to it over its standard input and output (no network port, no named pipe): one request
// at a time, each answered by one reply. Audio goes through a file both map (see SharedBlocks).
//
// A request is an operation code and its fields; a reply is a status (0 done, 1 failed, then the reason), the
// operation's fields, then what the plugins' controllers told the host since the last reply (edits, restarts).
// The operations and their fields are in TGVst3Protocol.java, the other side of this file.
#include "instance.h"
#include "sockets.h"
#include "wire.h"

#include "public.sdk/source/vst/hosting/hostclasses.h"

#include <cstdio>
#include <map>

#ifdef _WIN32
#include <windows.h>
#define tg_pid() ((int64_t) GetCurrentProcessId())
#else
#include <unistd.h>
#define tg_pid() ((int64_t) getpid())
#endif

namespace TuxGuitar {

const int32_t kProtocolVersion = 2;

enum Operation {
	kHello = 1,
	kScan = 2,
	kOpen = 3,
	kClose = 4,
	kProcess = 5,
	kSetParameter = 6,
	kGetParameter = 7,
	kGetState = 8,
	kSetState = 9,
	kReset = 10,
	kListeners = 11,
	kOpenEditor = 12,
	kCloseEditor = 13,
	kMessage = 14,
};

struct Failure {
	std::string reason;
};

class Host {
public:
	explicit Host(Wire wire) : wire(wire), application(owned(new Vst::HostApplication())) {}

	int run() {
		try {
			int32_t operation;
			while (wire.readInt(operation)) {
				answer(operation);
			}
		} catch (const WireClosed&) {
		}
		instances.clear();
		return 0;
	}

private:
	void answer(int32_t operation) {
		try {
			wire.writeInt(0);
			dispatch(operation);
			writeNotifications();
		} catch (const Failure& failure) {
			discardAndFail(failure.reason);
		} catch (const std::exception& exception) {
			discardAndFail(exception.what());
		}
		wire.flush();
	}

	void discardAndFail(const std::string& reason) {
		wire.discard();
		wire.writeInt(1);
		wire.writeString(reason);
		writeNotifications();
	}

	void dispatch(int32_t operation) {
		switch (operation) {
			case kHello: return hello();
			case kScan: return scan();
			case kOpen: return open();
			case kClose: return close();
			case kProcess: return process();
			case kSetParameter: return setParameter();
			case kGetParameter: return getParameter();
			case kGetState: return getState();
			case kSetState: return setState();
			case kReset: return instance().reset();
			case kListeners: return wire.writeInt(networkListeners(wire.readLong()));
			case kOpenEditor: return wire.writeInt(instance().openEditor() ? 1 : 0);
			case kCloseEditor: return instance().closeEditor();
			case kMessage: return message();
			default: throw Failure{"unknown operation " + std::to_string(operation)};
		}
	}

	void hello() {
		wire.writeInt(kProtocolVersion);
		wire.writeLong(tg_pid());
	}

	VST3::Hosting::Module::Ptr module(const std::string& path) {
		auto known = modules.find(path);
		if (known != modules.end()) {
			return known->second;
		}
		std::string error;
		VST3::Hosting::Module::Ptr loaded = VST3::Hosting::Module::create(path, error);
		if (!loaded) {
			throw Failure{"not a VST3 module: " + path + " (" + error + ")"};
		}
		modules[path] = loaded;
		return loaded;
	}

	// the audio processors of a module: class id, name, and whether it is an instrument
	void scan() {
		VST3::Hosting::Module::Ptr scanned = module(wire.readString());
		std::vector<VST3::Hosting::ClassInfo> processors;
		for (const VST3::Hosting::ClassInfo& info : scanned->getFactory().classInfos()) {
			if (info.category() == kVstAudioEffectClass) {
				processors.push_back(info);
			}
		}
		wire.writeInt((int32_t) processors.size());
		for (const VST3::Hosting::ClassInfo& info : processors) {
			wire.writeString(info.ID().toString());
			wire.writeString(info.name());
			wire.writeInt(info.subCategoriesString().find("Instrument") != std::string::npos ? 1 : 0);
		}
	}

	void open() {
		std::string path = wire.readString();
		std::string classId = wire.readString();
		double sampleRate = wire.readDouble();
		int32_t maxBlock = wire.readInt();
		std::string shared = wire.readString();
		auto uid = VST3::UID::fromString(classId);
		if (!uid) {
			throw Failure{"not a class id: " + classId};
		}
		std::string error;
		std::unique_ptr<Instance> opened = Instance::open(module(path), *uid, application, sampleRate, maxBlock, shared, error);
		if (!opened) {
			throw Failure{error};
		}
		int32_t id = next++;
		wire.writeInt(id);
		wire.writeInt(opened->inputs());
		wire.writeInt(opened->outputs());
		wire.writeInt(opened->hasEditor() ? 1 : 0);
		writeParameters(*opened);
		writeTiming(*opened);
		instances[id] = std::move(opened);
	}

	void writeParameters(Instance& opened) {
		std::vector<ParameterInfo> parameters = opened.parameters();
		wire.writeInt((int32_t) parameters.size());
		for (const ParameterInfo& parameter : parameters) {
			wire.writeInt(parameter.id);
			wire.writeString(parameter.title);
			wire.writeDouble(parameter.plain);
			wire.writeDouble(parameter.minimum);
			wire.writeDouble(parameter.maximum);
			wire.writeInt(parameter.steps);
			wire.writeInt(parameter.flags);
		}
	}

	void writeTiming(Instance& timed) {
		wire.writeInt(timed.latency());
		wire.writeInt(timed.tail());
	}

	Instance& instance() { return instance(wire.readInt()); }

	Instance& instance(int32_t id) {
		auto found = instances.find(id);
		if (found == instances.end()) {
			throw Failure{"no plugin " + std::to_string(id)};
		}
		return *found->second;
	}

	void close() {
		int32_t id = wire.readInt();
		collect(instance(id));
		instances.erase(id);
	}

	void process() {
		int32_t id = wire.readInt();
		int32_t frames = wire.readInt();
		Transport transport = readTransport();
		std::vector<NoteEvent> events = readEvents();
		Instance& processed = instance(id);
		processed.process(frames, transport, events);
		writeTiming(processed);
	}

	Transport readTransport() {
		Transport transport;
		transport.playing = (wire.readInt() != 0);
		transport.tempo = wire.readDouble();
		transport.numerator = wire.readInt();
		transport.denominator = wire.readInt();
		transport.quarters = wire.readDouble();
		transport.samples = wire.readLong();
		return transport;
	}

	std::vector<NoteEvent> readEvents() {
		std::vector<NoteEvent> events((size_t) std::max(0, wire.readInt()));
		for (NoteEvent& event : events) {
			event.type = wire.readInt();
			event.offset = wire.readInt();
			event.pitch = wire.readInt();
			event.noteId = wire.readInt();
			event.value = wire.readFloat();
		}
		return events;
	}

	void setParameter() {
		Instance& changed = instance();
		int32_t id = wire.readInt();
		wire.writeDouble(changed.setParameter(id, wire.readDouble()));
		writeTiming(changed);
	}

	void getParameter() {
		Instance& read = instance();
		wire.writeDouble(read.parameter(wire.readInt()));
	}

	void getState() {
		Instance& saved = instance();
		std::vector<char> component = saved.componentState();
		std::vector<char> controller = saved.controllerState();
		wire.writeBytes(component.data(), component.size());
		wire.writeBytes(controller.data(), controller.size());
	}

	void setState() {
		Instance& restored = instance();
		std::vector<char> component = wire.readBytes();
		std::vector<char> controller = wire.readBytes();
		restored.setState(component, controller);
		writeTiming(restored);
	}

	// a message to the plugin's controller, with integer and binary attributes; the reply holds its report
	void message() {
		Instance& told = instance();
		std::string id = wire.readString();
		std::vector<std::pair<std::string, int64>> integers((size_t) std::max(0, wire.readInt()));
		for (auto& entry : integers) {
			entry.first = wire.readString();
			entry.second = wire.readLong();
		}
		std::vector<std::pair<std::string, std::vector<char>>> binaries((size_t) std::max(0, wire.readInt()));
		for (auto& entry : binaries) {
			entry.first = wire.readString();
			entry.second = wire.readBytes();
		}
		std::vector<char> report = told.message(id, integers, binaries);
		wire.writeBytes(report.data(), report.size());
		writeTiming(told);
	}

	void collect(Instance& told) {
		for (const Notification& notification : told.takeNotifications()) {
			pending.push_back(std::make_pair(idOf(told), notification));
		}
	}

	int32_t idOf(const Instance& told) const {
		for (const auto& entry : instances) {
			if (entry.second.get() == &told) {
				return entry.first;
			}
		}
		return 0;
	}

	void writeNotifications() {
		for (auto& entry : instances) {
			collect(*entry.second);
		}
		wire.writeInt((int32_t) pending.size());
		for (const auto& entry : pending) {
			wire.writeInt(entry.first);
			wire.writeInt(entry.second.kind);
			wire.writeInt(entry.second.id);
			wire.writeDouble(entry.second.plain);
		}
		pending.clear();
	}

	Wire wire;
	IPtr<Vst::HostApplication> application;
	std::map<std::string, VST3::Hosting::Module::Ptr> modules;
	std::map<int32_t, std::unique_ptr<Instance>> instances;
	std::vector<std::pair<int32_t, Notification>> pending;
	int32_t next = 1;
};

}  // namespace TuxGuitar

int main() {
	TuxGuitar::Host host(TuxGuitar::Wire::claimStandardStreams());
	return host.run();
}
