// Test Echo: adds one copy of the signal at half its level, 0.3 s later; its tail is those 0.3 s
#include "effects.h"
#include "public.sdk/source/main/pluginfactory.h"

#include <cmath>

namespace TuxGuitar {

class TestEcho : public TestPlugin {
public:
	TestEcho() : TestPlugin(PluginSpec{false, 2, 2, true, {}, {{u"Default", {}}}}) {}

protected:
	uint32 tail() override { return (uint32) std::llround(0.3 * sampleRate()); }

	void reset() override { history.resize(2, (int32) tail()); }

	void render(float** in, float** out, int32 frames, IEventList*, ProcessContext*) override {
		uint32 delay = tail();
		for (int32 i = 0; i < frames; i++) {
			history.write(in, i);
			for (int32 c = 0; c < 2; c++) {
				out[c][i] = in[c][i] + 0.5f * history.at(c, delay);
			}
		}
	}

private:
	History history;
};

}  // namespace TuxGuitar

using namespace TuxGuitar;
TG_TEST_PLUGIN_FACTORY("Test Echo", TestEcho, false, 0x7A1C0004, 0x54474543, 0x484F0000, 0x00000004)
