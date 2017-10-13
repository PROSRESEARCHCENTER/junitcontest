
#include <jni.h>

#include <windows.h>

#define JRE_KEY "Software\\JavaSoft\\Java Runtime Environment"

char* JNICseparator = "\\";

char* JNICjvmName = "jvm.dll";

char* JNICjvmHomePaths[] = {
    "bin\\client",
    "bin\\server",
    "jre\\bin\\client",
    "jre\\bin\\server",
    NULL
};

char* JNICjvmAbsolutePaths[] = {
    NULL
};

#define DLHandle HMODULE
#define DLSymbol FARPROC

char JNICerrorString[MAX_PATH];
char JNICjvmPath[MAX_PATH];

jboolean JNICgetStringFromRegistry(HKEY key, const char *name, char *buf, jint bufsize) {
    DWORD type, size;
	if (debug) printf("Looking for key %s\n",name);

    if ((RegQueryValueEx(key, name, 0, &type, 0, &size) == 0)
		&& (type == REG_SZ)
		&& (size < (unsigned int)bufsize)) {
		if (RegQueryValueEx(key, name, 0, 0,(unsigned char*) buf, &size) == 0) {
			return JNI_TRUE;
		}
    }
    return JNI_FALSE;
}

jboolean JNICgetPublicJVMPath(char *buf, jint bufsize) {
    HKEY key, subkey;
    char version[MAX_PATH];
	if (debug) printf("GetPublicJVMPath \n");

    /* Find the current version of the JRE */
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, JRE_KEY, 0, KEY_READ, &key) != 0) {
		printf("Error opening registry key '%s'\n", JRE_KEY);
		return JNI_FALSE;
    }

    if (!JNICgetStringFromRegistry(key, "CurrentVersion",version, sizeof(version))) {
		printf("Failed reading value of registry key:\n\t%s\\CurrentVersion", JRE_KEY);
		RegCloseKey(key);
		return JNI_FALSE;
    }


    /* Find directory where the current version is installed. */
    if (RegOpenKeyEx(key, version, 0, KEY_READ, &subkey) != 0) {
		printf("Error opening registry key '%s\\%s'", JRE_KEY, version);
		RegCloseKey(key);
		return JNI_FALSE;
    }

    if (!JNICgetStringFromRegistry(subkey, "RuntimeLib", buf, bufsize)) {
		printf("Failed reading value of registry key:\n\t%s\\%s\\RuntimeLib\n", JRE_KEY, version);
		RegCloseKey(key);
		RegCloseKey(subkey);
		return JNI_FALSE;
    }

    if (debug) {
		char micro[MAX_PATH];
		if (!JNICgetStringFromRegistry(subkey, "MicroVersion", micro,sizeof(micro))) {
			printf("Warning: Can't read MicroVersion\n");
			micro[0] = '\0';
		}
		printf("Using JRE version major.minor.micro = %s.%s\n", version, micro);
		printf("Using public VM at %s\n",buf);
    }

    RegCloseKey(key);
    RegCloseKey(subkey);
    return JNI_TRUE;
}

char* JNICgetRegistryJVMPath() {
    jboolean rc = JNICgetPublicJVMPath(JNICjvmPath, sizeof(JNICjvmPath));
    return rc ? JNICjvmPath : NULL;
}

DLHandle JNICloadLibrary(char* name) {
    return LoadLibrary(name);
}

DLSymbol JNICgetSymbol(DLHandle handle, char* name) {
    return GetProcAddress(handle, name);
}

char* JNICgetLastError() {
    unsigned int error;
    
    error = GetLastError();    
    if (FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
                      NULL, error, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), 
                      JNICerrorString, sizeof(JNICerrorString), NULL)) {
        return JNICerrorString;
    }
    
    sprintf(JNICerrorString, "Error Code %u", error);
    return JNICerrorString;
}
