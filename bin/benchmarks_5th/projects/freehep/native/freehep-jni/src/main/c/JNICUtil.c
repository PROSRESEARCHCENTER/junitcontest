
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include "JNICUtil.h"

typedef jint (JNICALL *CreateJavaVM_t)(JavaVM **vm, void **env, void *vm_args);
typedef jint (JNICALL *GetDefaultJavaVMInitArgs_t)(void *vm_args);
typedef jint (JNICALL *GetCreatedJavaVMs_t)(JavaVM **vmBuf, jsize bufLen, jsize *nVMs);

static int JNICUtilDebug = -1;

static int JNICUtilVMwasDestroyed = JNI_FALSE;
static int JNICUtilVMwasCreatedByUs = JNI_FALSE;

#ifdef LINK_WITHOUT_JVM

#ifdef WIN32
#include "sys/JNICUtil-WIN32.h"
#endif

#ifdef Linux
#include "sys/JNICUtil-Linux.h"
#endif

#ifdef Darwin
#include "sys/JNICUtil-Darwin.h"
#endif

#ifdef SOLARIS2
#include "sys/JNICUtil-SUN.h"
#endif

DLHandle JNICloadLibraryFromPath(char* env) {
    char* jvmpath;
    DLHandle handle;
    
	jvmpath = getenv(env);
	if (jvmpath == NULL) return NULL;
	
	if (JNICdebug()) printf("Trying to find JVM at %s using %s.\n", jvmpath, env); 
    handle = JNICloadLibrary(jvmpath);
    if (handle != NULL) {
        if (JNICdebug()) printf("Loaded JVM from %s using %s.\n", jvmpath, env);
        return handle;
    }
    
    printf("Warning: Error loading JVM from %s using %s.\n", jvmpath, env);
    printf("%s", JNICgetLastError());
    return NULL;
}

DLHandle JNICloadLibraryFromAbsolutePaths() {
    char* jvmpath;
    char** paths;
    DLHandle handle;
    
    paths = JNICjvmAbsolutePaths;
    while (*paths != NULL) { 
        jvmpath = malloc(strlen(*paths)+sizeof(JNICseparator)+strlen(JNICjvmName)+1);
        strcpy(jvmpath, *paths);
        strcat(jvmpath, JNICseparator);
        strcat(jvmpath, JNICjvmName);

	    if (JNICdebug()) printf("Trying to find JVM at absolute path %s...\n", jvmpath); 
        handle = JNICloadLibrary(jvmpath);
        if (handle != NULL) {
            if (JNICdebug()) printf("Loaded JVM from %s.\n", jvmpath);
            free(jvmpath);
            return handle;
        }
        free(jvmpath);
        paths++;
    }
    return NULL;
}

DLHandle JNICloadLibraryFromHomePaths(char* env) {
    char* home;
    char* jvmpath;
    char** paths;
    DLHandle handle;
    
	home = getenv(env);
	if (home == NULL) return NULL;
	
    paths = JNICjvmHomePaths;
    while (*paths != NULL) { 
        jvmpath = malloc(strlen(home)+sizeof(JNICseparator)+strlen(*paths)+sizeof(JNICseparator)+strlen(JNICjvmName)+1);
        strcpy(jvmpath, home);
        strcat(jvmpath, JNICseparator);
        strcat(jvmpath, *paths);
        strcat(jvmpath, JNICseparator);
        strcat(jvmpath, JNICjvmName);

	    if (JNICdebug()) printf("Trying to find JVM at %s using %s...\n", jvmpath, env); 
        handle = JNICloadLibrary(jvmpath);
        if (handle != NULL) {
            if (JNICdebug()) printf("Loaded JVM from %s using %s.\n", jvmpath, env);
            free(jvmpath);
            return handle;
        }
        free(jvmpath);        
        paths++;
    }
    printf("Warning: Error loading JVM using %s.\n", env);
    printf("%s", JNICgetLastError());
    return NULL;
}

DLHandle JNICloadLibraryFromRegistry() {
    char* jvmpath;
    DLHandle handle;

    jvmpath = JNICgetRegistryJVMPath();
    if (jvmpath == NULL) return NULL;
    
    if (JNICdebug()) printf("Trying to find JVM at %s using Registry...\n", jvmpath); 
    handle = JNICloadLibrary(jvmpath);
    if (handle != NULL) {
        if (JNICdebug()) printf("Loaded JVM from %s using Registry.\n", jvmpath);
        return handle;
    }
    
    printf("Warning: Error loading JVM from %s using Registry.\n", jvmpath);
    printf("%s", JNICgetLastError());
    return NULL;
}

DLSymbol JNICfindSymbol(DLHandle handle, char* name) {
    DLSymbol symbol;
    
    if (JNICdebug()) printf("Looking for symbol %s...\n", name); 
    symbol = JNICgetSymbol(handle, name);
    if (symbol == NULL) {
        printf("Error: cannot find JNI interface %s.\n", name);
        printf("%s", JNICgetLastError());
    } else {
        if (JNICdebug()) printf("Found symbol %s.\n", name);
    }
    return symbol;
}

#endif /* LINK_WITHOUT_JVM */

int JNICexceptions(JNIEnv *env) {
    if ((*env)->ExceptionOccurred(env) != NULL) {
        if (JNICdebug()) (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        return 1;
    }
    return 0;
}

void JNICcheckExceptions(JNIEnv *env) {
    if ((*env)->ExceptionOccurred(env) != NULL) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }
}


int JNICcreateJVM(JNIEnv **env, JavaVM **jvm) {
#ifdef LINK_WITHOUT_JVM
    DLHandle handle = NULL;
#endif

    CreateJavaVM_t CreateJavaVM = NULL;
    GetDefaultJavaVMInitArgs_t GetDefaultJavaVMInitArgs = NULL;
    GetCreatedJavaVMs_t GetCreatedJavaVMs = NULL;
    
	int nOptions = 0;
    char *classpathOption;
    char *classpathOptionKey;
    char *classpath;
    char *verboseOption;
    char *verboseOptionKey;
    char *verbose;
    char *args;
    JavaVMOption* options;
    JavaVMInitArgs vm_args;
    int i;
    jint rc;
    jsize nSize = 1;
    jsize nVms;
    jint nStatus;


#ifdef LINK_WITHOUT_JVM
    /*  Look for jvm.dll or jvm.so */
    if (JNICdebug()) printf("Looking for JVM...\n");
    
    /* Load from JVM_PATH */
    if (handle == NULL) handle = JNICloadLibraryFromPath("JVM_PATH");

    /* Load from JRE_HOME */
    if (handle == NULL) handle = JNICloadLibraryFromHomePaths("JRE_HOME");
        
    /* Load from JDK_HOME */
    if (handle == NULL) handle = JNICloadLibraryFromHomePaths("JDK_HOME");
            
    /* Load from JAVA_HOME */
    if (handle == NULL) handle = JNICloadLibraryFromHomePaths("JAVA_HOME");
            
    /* Load from Absolute Paths */
    if (handle == NULL) handle = JNICloadLibraryFromAbsolutePaths();
            
    /* Load from Registry */
    if (handle == NULL) handle = JNICloadLibraryFromRegistry();
    
    /* No other options */
    if (handle == NULL) {
        printf("Could not find JVM");
        return -2;
    }

    /* Load all symbols */
    if ((CreateJavaVM = (CreateJavaVM_t)JNICfindSymbol(handle, "JNI_CreateJavaVM")) == NULL) return -2;
    if ((GetDefaultJavaVMInitArgs = (GetDefaultJavaVMInitArgs_t)JNICfindSymbol(handle, "JNI_GetDefaultJavaVMInitArgs")) == NULL) return -2;
    if ((GetCreatedJavaVMs = (GetCreatedJavaVMs_t)JNICfindSymbol(handle, "JNI_GetCreatedJavaVMs")) == NULL) return -2;
#else
    CreateJavaVM = JNI_CreateJavaVM;
    GetDefaultJavaVMInitArgs = JNI_GetDefaultJavaVMInitArgs;
    GetCreatedJavaVMs = JNI_GetCreatedJavaVMs;
    if (JNICdebug()) printf("Loaded linked in JVM.\n");
#endif /* LINK_WITHOUT_JVM */

    
/*   NOTE: one can only create one JVM per process, so we check if there
           is already one there */
    if (JNICdebug()) printf("Looking for already loaded JVMs...\n");
    nStatus = GetCreatedJavaVMs(jvm, nSize, &nVms);
    if ((nStatus == JNI_OK) && (nVms > 0)) {
        if (JNICdebug()) printf("Number of jvm's returned: %d", (int)nVms);
        (**jvm)->GetEnv(*jvm, (void **)env, JNI_VERSION_1_4);
        if (JNICdebug()) printf("Using already loaded JVM.\n");
        return nStatus;
    } else {
        if (JNICdebug()) printf("None found.\n");
    }

    if (JNICUtilVMwasDestroyed) {
        printf("Cannot re-create JVM in JDK 1.4 and below.\n");
        printf("    You should not have destroyed it.\n");
        printf("or  You should set the enviroment variable: DO_NOT_DESTROY_JVM\n");
        return -1;
    }

    /* check for classpath */
	classpath = getenv("JVM_CLASSPATH");
    if (classpath == NULL) {
        classpath = getenv("CLASSPATH");
        if (JNICdebug() && (classpath != NULL)) printf("Using CLASSPATH.\n");        
    } else {
        if (JNICdebug()) printf("Using JVM_CLASSPATH.\n");        
    }

    classpathOption = NULL;
    if (classpath != NULL) {
        classpathOptionKey = "-Djava.class.path=";
	    classpathOption = malloc(strlen(classpathOptionKey)+strlen(classpath)+1);
	    strcpy(classpathOption,classpathOptionKey);
	    strcat(classpathOption,classpath);
	    nOptions++;
        if (JNICdebug()) printf("classpathOption: %s\n", classpathOption);
	}

    /* check for verbose */
	verboseOption = NULL;
	verbose = getenv("JVM_VERBOSE");
	if (verbose != NULL) {
	    verboseOptionKey = "-verbose";
	    if (!strcmp(verbose,"on")) {
	        verboseOption = malloc(strlen(verboseOptionKey)+1);
	        strcpy(verboseOption,verboseOptionKey);
	    } else {
	        verboseOption = malloc(strlen(verboseOptionKey)+1+strlen(verbose));
	        strcpy(verboseOption,verboseOptionKey);
	        strcat(verboseOption,":");
	        strcat(verboseOption,verbose);
	    }
	    nOptions++;
        if (JNICdebug()) printf("verboseOption: %s\n", verboseOption);
	}

	/* check for args */
	args = getenv("JVM_ARGS");
	if (args != NULL) {
        nOptions++;
        if (JNICdebug()) printf("JVM_ARGS: %s\n", args);
	}

	/* create list */
	options = NULL;
	if (nOptions >= 0) {
	    options = malloc(nOptions*sizeof(JavaVMOption));
	    i=0;
	    if (classpathOption != NULL) {
	        options[i].optionString = classpathOption;
	        i++;
	    }
	    if (verboseOption != NULL) {
	        options[i].optionString = verboseOption;
	        i++;
        }
        if (args != NULL) {
            options[i].optionString = args;
            i++;
        }
    }

    if (JNICdebug()) printf("Starting JVM...");
    /* create VM init args */
	vm_args.version = JNI_VERSION_1_4;
	vm_args.options = options;
	vm_args.nOptions = nOptions;
	vm_args.ignoreUnrecognized = JNI_TRUE;

    /* create VM */
	rc = CreateJavaVM(jvm, (void **)env, &vm_args);

    /* freeing memory */
	free(options);
	free(classpathOption);
	free(verboseOption);

    JNICUtilVMwasCreatedByUs = JNI_TRUE;

	/* checking return code */
	if (rc < 0) {
        if (JNICdebug()) printf("Failed to start JVM, errorcode: %d.\n", (int)rc);
	    return rc;
	} else {
        if (JNICdebug()) printf("JVM Created.\n");
        if (JNICdebug() && (classpath == NULL)) printf("Warning JVM_CLASSPATH or CLASSPATH was NOT set.\n");
	    return (classpath != NULL) ? 0 : 1;
	}
}

void JNICdestroyJVM(JavaVM *jvm) {
    char *doNotDestroyJVM = NULL;

	doNotDestroyJVM = getenv("DO_NOT_DESTROY_JVM");
	if (doNotDestroyJVM != NULL) return;

    if (jvm == NULL) return;

    if (!JNICUtilVMwasCreatedByUs) {
        if (JNICdebug()) printf("Trying to destroy a JVM which was not ours.\n");
        return;
    }

    if (JNICdebug()) printf("Destroying JVM.\n");
	(*jvm)->DestroyJavaVM(jvm);
	jvm = NULL;

	JNICUtilVMwasDestroyed = JNI_TRUE;
}

void JNICfatal(char* msg, ...) {
    char* p = "Fatal Error: ";
    char* s = malloc(strlen(p)+strlen(msg)+1);
    va_list v;

    va_start(v, msg);
    strcpy(s,p);
    strcat(s,msg);
    strcat(s,"\n");
    vprintf(s, v);
    free(s);
    va_end(v);
    exit(1);
}

void JNICerror(char* msg, ...) {
    char* p = "Error: ";
    char* s = malloc(strlen(p)+strlen(msg)+1);
    va_list v;

    va_start(v, msg);
    strcpy(s,p);
    strcat(s,msg);
    strcat(s,"\n");
    vprintf(s, v);
    va_end(v);
    free(s);
}

int JNICdebug() {
    char *showDebug = NULL;
    
    if (JNICUtilDebug < 0) {

	    showDebug = getenv("JNI_SHOW_DEBUG");
	    JNICUtilDebug = (showDebug == NULL) ? 0 : 1;
    }
    return JNICUtilDebug;
}
