// What the test effects share: a ring of past input samples, per channel.
#pragma once

#include "testplugin.h"

#include <vector>

namespace TuxGuitar {

// the last samples that came in, per channel; at(c, d) is the sample d samples before the newest one written
class History {
public:
	void resize(int32 channels, int32 length) {
		size_t size = 1;
		while (size < (size_t) length + 1) {
			size <<= 1;
		}
		rings.assign(channels, std::vector<float>(size, 0.f));
		mask = size - 1;
		position = 0;
	}

	void clear() {
		for (std::vector<float>& ring : rings) {
			std::fill(ring.begin(), ring.end(), 0.f);
		}
	}

	// the next sample of every channel; then at(c, 0) is that sample
	void write(float** in, int32 frame) {
		position = (position + 1) & mask;
		for (size_t c = 0; c < rings.size(); c++) {
			rings[c][position] = in[c][frame];
		}
	}

	float at(int32 channel, size_t delay) const { return rings[channel][(position - delay) & mask]; }

	size_t capacity() const { return mask; }

private:
	std::vector<std::vector<float>> rings;
	size_t mask = 0;
	size_t position = 0;
};

}  // namespace TuxGuitar
