// Test Drone: passes the signal and adds a 100 Hz tone at 0.1 forever; its tail is infinite
#include "testplugin.h"
#include "public.sdk/source/main/pluginfactory.h"

#include <cmath>

namespace TuxGuitar {

class TestDrone : public TestPlugin {
public:
	TestDrone() : TestPlugin(PluginSpec{false, 2, 2, true, {}, {{u"Default", {}}}}) {}

protected:
	uint32 tail() override { return kInfiniteTail; }

	void reset() override { sample = 0; }

	void render(float** in, float** out, int32 frames, IEventList*, ProcessContext*) override {
		const double kTwoPi = 6.283185307179586;
		for (int32 i = 0; i < frames; i++, sample++) {
			float tone = (float) (0.1 * std::sin(kTwoPi * 100.0 * (double) sample / sampleRate()));
			for (int32 c = 0; c < 2; c++) {
				out[c][i] = in[c][i] + tone;
			}
		}
	}

private:
	int64 sample = 0;
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Drone", TestDrone, false, 0x7A1C0005, 0x54474452, 0x4F4E4500, 0x00000005)
