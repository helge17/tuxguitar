#include "shared.h"

#ifdef _WIN32
#include <windows.h>
#else
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#endif

#include <vector>

namespace TuxGuitar {

#ifdef _WIN32

static std::wstring widen(const std::string& utf8) {
	int size = MultiByteToWideChar(CP_UTF8, 0, utf8.c_str(), -1, nullptr, 0);
	std::vector<wchar_t> wide((size_t) (size > 0 ? size : 1));
	MultiByteToWideChar(CP_UTF8, 0, utf8.c_str(), -1, wide.data(), size);
	return std::wstring(wide.data());
}

SharedBlocks::SharedBlocks(const std::string& path, int maxBlock) : maxBlock(maxBlock) {
	HANDLE handle = CreateFileW(widen(path).c_str(), GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE | FILE_SHARE_DELETE,
	                            nullptr, OPEN_EXISTING, FILE_ATTRIBUTE_TEMPORARY, nullptr);
	if (handle == INVALID_HANDLE_VALUE) {
		return;
	}
	file = handle;
	mapping = CreateFileMappingW(handle, nullptr, PAGE_READWRITE, 0, (DWORD) bytes(maxBlock), nullptr);
	if (mapping) {
		data = static_cast<float*>(MapViewOfFile(mapping, FILE_MAP_ALL_ACCESS, 0, 0, bytes(maxBlock)));
	}
}

SharedBlocks::~SharedBlocks() {
	if (data) {
		UnmapViewOfFile(data);
	}
	if (mapping) {
		CloseHandle(mapping);
	}
	if (file) {
		CloseHandle(file);
	}
}

#else

SharedBlocks::SharedBlocks(const std::string& path, int maxBlock) : maxBlock(maxBlock) {
	int handle = open(path.c_str(), O_RDWR);
	if (handle < 0) {
		return;
	}
	void* mapped = mmap(nullptr, bytes(maxBlock), PROT_READ | PROT_WRITE, MAP_SHARED, handle, 0);
	close(handle);
	data = (mapped == MAP_FAILED ? nullptr : static_cast<float*>(mapped));
}

SharedBlocks::~SharedBlocks() {
	if (data) {
		munmap(data, bytes(maxBlock));
	}
}

#endif

}  // namespace TuxGuitar
