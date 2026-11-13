// Test Click: a click of +0.5 at the start of each note, delayed by its parameter Latency (0 by default), which it reports
#include "testplugin.h"
#include "public.sdk/source/main/pluginfactory.h"
#include "pluginterfaces/vst/ivstevents.h"

#include <algorithm>
#include <cmath>
#include <vector>

namespace TuxGuitar {

class TestClick : public TestPlugin {
public:
	TestClick() : TestPlugin(PluginSpec{true, 0, 2, true, {{1, u"Latency", 0.0, 100000.0, 0.0}}, {{u"Default", {0.0}}}}) {}

protected:
	uint32 latency() override { return (uint32) std::llround(value(1)); }

	void reset() override {
		now = 0;
		clicks.clear();
	}

	void render(float**, float** out, int32 frames, IEventList* events, ProcessContext*) override {
		int32 count = (events ? events->getEventCount() : 0);
		for (int32 i = 0; i < count; i++) {
			Event event;
			if (events->getEvent(i, event) == kResultOk && event.type == Event::kNoteOnEvent) {
				clicks.push_back(now + event.sampleOffset + latency());
			}
		}
		for (int32 c = 0; c < 2; c++) {
			std::fill(out[c], out[c] + frames, 0.f);
		}
		for (int64 click : clicks) {
			if (click >= now && click < now + frames) {
				out[0][click - now] = 0.5f;
				out[1][click - now] = 0.5f;
			}
		}
		now += frames;
		int64 end = now;
		clicks.erase(std::remove_if(clicks.begin(), clicks.end(), [end](int64 click) { return click < end; }), clicks.end());
	}

private:
	int64 now = 0;
	std::vector<int64> clicks;
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Click", TestClick, true, 0x7A1C0009, 0x5447434C, 0x49434B00, 0x00000009)
