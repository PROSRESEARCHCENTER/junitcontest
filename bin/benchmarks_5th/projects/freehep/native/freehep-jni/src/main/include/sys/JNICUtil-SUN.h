
char* JNICjvmName = "libjvm.so";

char* JNICjvmHomePaths[] = {
    "lib/sparc/client",
    "lib/sparc/server",
    "jre/lib/sparc/client",
    "jre/lib/sparc/server",
    NULL
};

char* JNICjvmAbsolutePaths[] = {
    NULL
};

#include "JNICUtil-Unix.h"
