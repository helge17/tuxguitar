// Test Tempo: passes the signal and records the tempo, time signature, play state and musical position it is given,
// one record per block, which its report gives back
#include "testplugin.h"
#include "public.sdk/source/main/pluginfactory.h"
#include "pluginterfaces/vst/ivstprocesscontext.h"

#include <cstring>

namespace TuxGuitar {

#pragma pack(push, 1)
// what one block was told; -1 (or 0 for the position) where the host told nothing
struct TempoRecord {
	double tempo;
	int32 numerator;
	int32 denominator;
	int32 playing;
	double position;  // quarter notes
	int64 sample;     // project time in samples
};
#pragma pack(pop)

class TestTempo : public TestPlugin {
public:
	TestTempo() : TestPlugin(PluginSpec{false, 2, 2, true, {}, {{u"Default", {}}}}) {}

protected:
	void render(float** in, float** out, int32 frames, IEventList*, ProcessContext* context) override {
		for (int32 c = 0; c < 2; c++) {
			for (int32 i = 0; i < frames; i++) {
				out[c][i] = in[c][i];
			}
		}
		records.push_back(record(context));
	}

	std::vector<char> report() override {
		std::vector<char> bytes(records.size() * sizeof(TempoRecord));
		if (!records.empty()) {
			std::memcpy(bytes.data(), records.data(), bytes.size());
		}
		return bytes;
	}

private:
	static TempoRecord record(ProcessContext* context) {
		TempoRecord told = {-1.0, -1, -1, -1, 0.0, 0};
		if (context) {
			uint32 state = context->state;
			told.tempo = ((state & ProcessContext::kTempoValid) ? context->tempo : -1.0);
			told.numerator = ((state & ProcessContext::kTimeSigValid) ? context->timeSigNumerator : -1);
			told.denominator = ((state & ProcessContext::kTimeSigValid) ? context->timeSigDenominator : -1);
			told.playing = ((state & ProcessContext::kPlaying) ? 1 : 0);
			told.position = ((state & ProcessContext::kProjectTimeMusicValid) ? context->projectTimeMusic : 0.0);
			told.sample = context->projectTimeSamples;
		}
		return told;
	}

	std::vector<TempoRecord> records;
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Tempo", TestTempo, false, 0x7A1C0007, 0x54475445, 0x4D504F00, 0x00000007)
