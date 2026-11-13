#include "sockets.h"

#ifdef _WIN32
#include <winsock2.h>
#include <ws2tcpip.h>
#include <iphlpapi.h>
#include <windows.h>

#include <vector>

namespace TuxGuitar {

namespace {

// the rows of one of the system's tables of sockets, each with its owning process
template <typename Table, typename Get>
int countOwned(Get get, ULONG family, int tableClass, int64_t pid) {
	ULONG size = 0;
	get(nullptr, &size, FALSE, family, tableClass, 0);
	std::vector<char> buffer(size + 1024);
	size = (ULONG) buffer.size();
	if (get(buffer.data(), &size, FALSE, family, tableClass, 0) != NO_ERROR) {
		return -1;
	}
	const Table* table = reinterpret_cast<const Table*>(buffer.data());
	int count = 0;
	for (DWORD i = 0; i < table->dwNumEntries; i++) {
		count += ((int64_t) table->table[i].dwOwningPid == pid ? 1 : 0);
	}
	return count;
}

DWORD tcp(PVOID table, PDWORD size, BOOL order, ULONG family, int tableClass, ULONG reserved) {
	return GetExtendedTcpTable(table, size, order, family, (TCP_TABLE_CLASS) tableClass, reserved);
}

DWORD udp(PVOID table, PDWORD size, BOOL order, ULONG family, int tableClass, ULONG reserved) {
	return GetExtendedUdpTable(table, size, order, family, (UDP_TABLE_CLASS) tableClass, reserved);
}

}  // namespace

int networkListeners(int64_t pid) {
	int counts[] = {
		countOwned<MIB_TCPTABLE_OWNER_PID>(tcp, AF_INET, TCP_TABLE_OWNER_PID_LISTENER, pid),
		countOwned<MIB_TCP6TABLE_OWNER_PID>(tcp, AF_INET6, TCP_TABLE_OWNER_PID_LISTENER, pid),
		countOwned<MIB_UDPTABLE_OWNER_PID>(udp, AF_INET, UDP_TABLE_OWNER_PID, pid),
		countOwned<MIB_UDP6TABLE_OWNER_PID>(udp, AF_INET6, UDP_TABLE_OWNER_PID, pid),
	};
	int total = 0;
	for (int count : counts) {
		if (count < 0) {
			return -1;
		}
		total += count;
	}
	return total;
}

}  // namespace TuxGuitar

#else

namespace TuxGuitar {

// not known yet on this platform
int networkListeners(int64_t) { return -1; }

}  // namespace TuxGuitar

#endif
