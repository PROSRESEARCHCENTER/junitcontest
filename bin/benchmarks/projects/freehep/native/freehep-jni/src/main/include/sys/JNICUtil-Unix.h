
#include <jni.h>

#include <dlfcn.h>

#define DLHandle void*
#define DLSymbol void*

char* JNICseparator = "/";

char* JNICgetRegistryJVMPath() {
    return NULL;
}

DLHandle JNICloadLibrary(char* name) {
    return dlopen(name, RTLD_GLOBAL | RTLD_NOW);
}

DLSymbol JNICgetSymbol(DLHandle handle, char* name) {
    return dlsym(handle, name);
}

char* JNICgetLastError() {
    char* error;
    error = dlerror();
    return (error != NULL) ? error : "";
}
