
char* JNICjvmName = "libjvm.so";

char* JNICjvmHomePaths[] = {
    "lib/i386/client",
    "lib/i386/server",
    "jre/lib/i386/client",
    "jre/lib/i386/server",
    NULL
};

char* JNICjvmAbsolutePaths[] = {
    NULL
};

#include "JNICUtil-Unix.h"
