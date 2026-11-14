// Test Gain: multiplies the signal by its parameter Gain (2.0 by default); its preset Loud sets Gain to 4.0
#include "testplugin.h"
#include "public.sdk/source/main/pluginfactory.h"

namespace TuxGuitar {

class TestGain : public TestPlugin {
public:
	TestGain() : TestPlugin(PluginSpec{false, 2, 2, true, {{1, u"Gain", 0.0, 16.0, 2.0}}, {{u"Default", {2.0}}, {u"Loud", {4.0}}}}) {}

protected:
	void render(float** in, float** out, int32 frames, IEventList*, ProcessContext*) override {
		float gain = (float) value(1);
		for (int32 c = 0; c < 2; c++) {
			for (int32 i = 0; i < frames; i++) {
				out[c][i] = in[c][i] * gain;
			}
		}
	}
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Gain", TestGain, false, 0x7A1C0001, 0x54474741, 0x494E0000, 0x00000001)
