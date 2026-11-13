// Test Hang: passes the signal, and hangs once: the block holding the sample its parameter "Hang at" counts to (samples
// processed since it was activated; never by default) does not come back for "Hang for" milliseconds (forever by
// default, until its process is ended), as a plugin stuck in its process call does
#include "testplugin.h"
#include "public.sdk/source/main/pluginfactory.h"

#include <chrono>
#include <thread>

namespace TuxGuitar {

const double kNever = 1e12;
const double kForever = 1e9;

class TestHang : public TestPlugin {
public:
	TestHang()
	: TestPlugin(PluginSpec{false, 2, 2, true, {{1, u"Hang at", 0.0, kNever, kNever}, {2, u"Hang for", 0.0, kForever, kForever}},
	                        {{u"Default", {kNever, kForever}}}}) {}

protected:
	void reset() override {
		processed = 0;
		hung = false;
	}

	void render(float** in, float** out, int32 frames, IEventList*, ProcessContext*) override {
		for (int32 c = 0; c < 2; c++) {
			for (int32 i = 0; i < frames; i++) {
				out[c][i] = in[c][i];
			}
		}
		if (!hung && (double) (processed + frames) > value(1)) {
			hung = true;
			std::this_thread::sleep_for(std::chrono::milliseconds((int64) value(2)));
		}
		processed += frames;
	}

private:
	int64 processed = 0;
	bool hung = false;
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Hang", TestHang, false, 0x7A1C000A, 0x54474841, 0x4E470000, 0x0000000A)
