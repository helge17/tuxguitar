// Test Latency: delays the signal by its parameter Latency, in samples (0 by default), and reports that latency
#include "effects.h"
#include "public.sdk/source/main/pluginfactory.h"

#include <cmath>

namespace TuxGuitar {

const double kMaxLatency = 100000.0;

class TestLatency : public TestPlugin {
public:
	TestLatency() : TestPlugin(PluginSpec{false, 2, 2, true, {{1, u"Latency", 0.0, kMaxLatency, 0.0}}, {{u"Default", {0.0}}}}) {
		history.resize(2, (int32) kMaxLatency);
	}

protected:
	uint32 latency() override { return (uint32) std::llround(value(1)); }

	void reset() override { history.clear(); }

	void render(float** in, float** out, int32 frames, IEventList*, ProcessContext*) override {
		uint32 delay = latency();
		for (int32 i = 0; i < frames; i++) {
			history.write(in, i);
			for (int32 c = 0; c < 2; c++) {
				out[c][i] = history.at(c, delay);
			}
		}
	}

private:
	History history;
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Latency", TestLatency, false, 0x7A1C0003, 0x5447414C, 0x41544500, 0x00000003)
