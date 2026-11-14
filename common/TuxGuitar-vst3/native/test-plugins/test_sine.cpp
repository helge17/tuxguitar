// Test Sine: a sine at each note's pitch, peaking at 0.5 x velocity / 127, following per-note tuning (note expression)
#include "testplugin.h"
#include "public.sdk/source/main/pluginfactory.h"
#include "pluginterfaces/vst/ivstevents.h"
#include "pluginterfaces/vst/ivstnoteexpression.h"

#include <algorithm>
#include <cmath>
#include <vector>

namespace TuxGuitar {

struct Voice {
	int32 id;
	int16 pitch;
	double tuning;  // semitones
	double phase;
	float peak;
};

class TestSine : public TestPlugin {
public:
	TestSine() : TestPlugin(PluginSpec{true, 0, 2, true, {}, {{u"Default", {}}}}) {}

protected:
	void reset() override { voices.clear(); }

	// sample for sample: each event applies from its offset
	void render(float**, float** out, int32 frames, IEventList* events, ProcessContext*) override {
		int32 from = 0;
		int32 count = (events ? events->getEventCount() : 0);
		for (int32 i = 0; i < count; i++) {
			Event event;
			if (events->getEvent(i, event) == kResultOk) {
				int32 to = std::max(from, std::min(frames, event.sampleOffset));
				sound(out, from, to);
				apply(event);
				from = to;
			}
		}
		sound(out, from, frames);
	}

private:
	void apply(const Event& event) {
		if (event.type == Event::kNoteOnEvent) {
			int32 id = (event.noteOn.noteId >= 0 ? event.noteOn.noteId : event.noteOn.pitch);
			voices.push_back(Voice{id, event.noteOn.pitch, 0.0, 0.0, 0.5f * event.noteOn.velocity});
		} else if (event.type == Event::kNoteOffEvent) {
			int32 id = (event.noteOff.noteId >= 0 ? event.noteOff.noteId : event.noteOff.pitch);
			voices.erase(std::remove_if(voices.begin(), voices.end(), [id](const Voice& voice) { return voice.id == id; }), voices.end());
		} else if (event.type == Event::kNoteExpressionValueEvent && event.noteExpressionValue.typeId == kTuningTypeID) {
			for (Voice& voice : voices) {
				if (voice.id == event.noteExpressionValue.noteId) {
					voice.tuning = 240.0 * (event.noteExpressionValue.value - 0.5);
				}
			}
		}
	}

	void sound(float** out, int32 from, int32 to) {
		const double kTwoPi = 6.283185307179586;
		for (int32 i = from; i < to; i++) {
			out[0][i] = 0.f;
			out[1][i] = 0.f;
		}
		for (Voice& voice : voices) {
			double step = kTwoPi * 440.0 * std::pow(2.0, (voice.pitch + voice.tuning - 69.0) / 12.0) / sampleRate();
			for (int32 i = from; i < to; i++) {
				float sample = voice.peak * (float) std::sin(voice.phase);
				out[0][i] += sample;
				out[1][i] += sample;
				voice.phase += step;
			}
		}
	}

	std::vector<Voice> voices;
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Sine", TestSine, true, 0x7A1C0008, 0x54475349, 0x4E450000, 0x00000008)
