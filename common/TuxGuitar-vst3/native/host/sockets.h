// Whether a process listens on the network: its listening TCP sockets and its UDP sockets
#pragma once

#include <cstdint>

namespace TuxGuitar {

// how many network sockets the process listens or receives on; -1 when the platform cannot tell
int networkListeners(int64_t pid);

}  // namespace TuxGuitar
