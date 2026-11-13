// Test Clip 0.5: clips the signal to plus or minus its parameter Ceiling (0.5 by default); no editor of its own
#include "testplugin.h"
#include "public.sdk/source/main/pluginfactory.h"

#include <algorithm>

namespace TuxGuitar {

class TestClip : public TestPlugin {
public:
	TestClip() : TestPlugin(PluginSpec{false, 2, 2, false, {{1, u"Ceiling", 0.0, 1.0, 0.5}}, {{u"Default", {0.5}}}}) {}

protected:
	void render(float** in, float** out, int32 frames, IEventList*, ProcessContext*) override {
		float ceiling = (float) value(1);
		for (int32 c = 0; c < 2; c++) {
			for (int32 i = 0; i < frames; i++) {
				out[c][i] = std::max(-ceiling, std::min(ceiling, in[c][i]));
			}
		}
	}
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Clip 0.5", TestClip, false, 0x7A1C0002, 0x54474341, 0x4C495000, 0x00000002)
