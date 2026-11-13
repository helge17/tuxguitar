#include "wire.h"

#include <cstring>
#include <fcntl.h>

#ifdef _WIN32
#include <io.h>
#include <windows.h>
#define tg_dup _dup
#define tg_dup2 _dup2
#define tg_read _read
#define tg_write _write
#define tg_open _open
#define TG_NULL_DEVICE "NUL"
#else
#include <unistd.h>
#define tg_dup dup
#define tg_dup2 dup2
#define tg_read read
#define tg_write write
#define tg_open open
#define TG_NULL_DEVICE "/dev/null"
#endif

namespace TuxGuitar {

Wire Wire::claimStandardStreams() {
#ifdef _WIN32
	_setmode(0, _O_BINARY);
	_setmode(1, _O_BINARY);
#endif
	int input = tg_dup(0);
	int output = tg_dup(1);
	int none = tg_open(TG_NULL_DEVICE, O_RDONLY);
	tg_dup2(none, 0);
	tg_dup2(2, 1);
#ifdef _WIN32
	SetStdHandle(STD_OUTPUT_HANDLE, GetStdHandle(STD_ERROR_HANDLE));
	SetStdHandle(STD_INPUT_HANDLE, (HANDLE) _get_osfhandle(none));
#endif
	return Wire(input, output);
}

void Wire::readFully(void* data, size_t size) {
	char* bytes = static_cast<char*>(data);
	while (size > 0) {
		int count = (int) tg_read(input, bytes, (unsigned int) size);
		if (count <= 0) {
			throw WireClosed();
		}
		bytes += count;
		size -= (size_t) count;
	}
}

uint64_t Wire::get(int bytes) {
	unsigned char buffer[8];
	readFully(buffer, (size_t) bytes);
	uint64_t bits = 0;
	for (int i = 0; i < bytes; i++) {
		bits = (bits << 8) | buffer[i];
	}
	return bits;
}

bool Wire::readInt(int32_t& value) {
	unsigned char first;
	if (tg_read(input, &first, 1) != 1) {
		return false;
	}
	value = (int32_t) (((uint32_t) first << 24) | (uint32_t) get(3));
	return true;
}

int32_t Wire::readInt() { return (int32_t) (uint32_t) get(4); }

int64_t Wire::readLong() { return (int64_t) get(8); }

float Wire::readFloat() {
	uint32_t bits = (uint32_t) get(4);
	float value;
	std::memcpy(&value, &bits, 4);
	return value;
}

double Wire::readDouble() {
	uint64_t bits = get(8);
	double value;
	std::memcpy(&value, &bits, 8);
	return value;
}

std::string Wire::readString() {
	std::vector<char> bytes = readBytes();
	return std::string(bytes.begin(), bytes.end());
}

std::vector<char> Wire::readBytes() {
	int32_t size = readInt();
	std::vector<char> bytes((size_t) (size > 0 ? size : 0));
	if (!bytes.empty()) {
		readFully(bytes.data(), bytes.size());
	}
	return bytes;
}

void Wire::put(uint64_t bits, int bytes) {
	for (int i = bytes - 1; i >= 0; i--) {
		pending.push_back((unsigned char) (bits >> (8 * i)));
	}
}

void Wire::writeInt(int32_t value) { put((uint32_t) value, 4); }

void Wire::writeLong(int64_t value) { put((uint64_t) value, 8); }

void Wire::writeFloat(float value) {
	uint32_t bits;
	std::memcpy(&bits, &value, 4);
	put(bits, 4);
}

void Wire::writeDouble(double value) {
	uint64_t bits;
	std::memcpy(&bits, &value, 8);
	put(bits, 8);
}

void Wire::writeString(const std::string& value) { writeBytes(value.data(), value.size()); }

void Wire::writeBytes(const void* data, size_t size) {
	writeInt((int32_t) size);
	const unsigned char* bytes = static_cast<const unsigned char*>(data);
	pending.insert(pending.end(), bytes, bytes + size);
}

void Wire::flush() {
	size_t written = 0;
	while (written < pending.size()) {
		int count = (int) tg_write(output, pending.data() + written, (unsigned int) (pending.size() - written));
		if (count <= 0) {
			throw WireClosed();
		}
		written += (size_t) count;
	}
	pending.clear();
}

}  // namespace TuxGuitar
