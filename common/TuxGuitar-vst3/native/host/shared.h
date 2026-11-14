// The audio of one plugin instance, shared with TuxGuitar: a file TuxGuitar created and sized, mapped by both. It holds
// the input block, then the output block, each two channels of the maximum block size, 32-bit float, planar,
// little-endian (the byte order of every platform TuxGuitar runs on).
#pragma once

#include <cstddef>
#include <string>

namespace TuxGuitar {

class SharedBlocks {
public:
	SharedBlocks(const std::string& path, int maxBlock);
	~SharedBlocks();
	SharedBlocks(const SharedBlocks&) = delete;
	SharedBlocks& operator=(const SharedBlocks&) = delete;

	bool isOpen() const { return data != nullptr; }
	float* input(int channel) const { return data + (size_t) channel * maxBlock; }
	float* output(int channel) const { return data + (size_t) (2 + channel) * maxBlock; }

	static size_t bytes(int maxBlock) { return (size_t) 4 * maxBlock * sizeof(float); }

private:
	float* data = nullptr;
	int maxBlock;
#ifdef _WIN32
	void* file = nullptr;
	void* mapping = nullptr;
#endif
};

}  // namespace TuxGuitar
