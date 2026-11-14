// Test Stereo: mono in, stereo out: the signal on the left, half of it on the right
#include "testplugin.h"
#include "public.sdk/source/main/pluginfactory.h"

namespace TuxGuitar {

class TestStereo : public TestPlugin {
public:
	TestStereo() : TestPlugin(PluginSpec{false, 1, 2, true, {}, {{u"Default", {}}}}) {}

protected:
	void render(float** in, float** out, int32 frames, IEventList*, ProcessContext*) override {
		for (int32 i = 0; i < frames; i++) {
			float sample = in[0][i];
			out[0][i] = sample;
			out[1][i] = 0.5f * sample;
		}
	}
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Stereo", TestStereo, false, 0x7A1C0006, 0x54475354, 0x4552454F, 0x00000006)
