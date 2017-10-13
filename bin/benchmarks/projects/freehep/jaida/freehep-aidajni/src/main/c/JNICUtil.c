
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

/* Apple only */
/* #include <pthread.h> */

#include "JNICUtil.h"

static int JNICUtilDebug = -1;

static int JNICUtilVMwasDestroyed = JNI_FALSE;
static int JNICUtilVMwasCreatedByUs = JNI_FALSE;

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
    
/*   NOTE: one can only create one JVM per process, so we check if there
           is already one there */
    if (JNICdebug()) printf("Looking for already loaded JVMs...\n");
/* FIXME, this call does NOT seem to work (Bus Error) on MacOSX i386, so we ignore existing JVMs */
#if defined(__i386__) && defined(__APPLE__)
    nStatus = JNI_OK;
    nVms = 0;
    nSize = 0;
#else 
    nStatus = JNI_GetCreatedJavaVMs(jvm, nSize, &nVms);
#endif
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

#if 0
	/* Apple only */
	/* create a new pthread copying the stack size of the primordial pthread, see simpleJavaLauncher on apple dev */
	struct rlimit limit;
	size_t stack_size = 0;
	int rca = getrlimit(RLIMIT_STACK, &limit);
	if (rca == 0) {
		if (limit.rlim_cur != 0LL) {
			stack_size = (size_t)limit.rlim_cur;
		}
	}
	
	pthread_attr_t thread_attr;
	pthread_attr_init(&thread_attr);
	pthread_attr_setscope(&thread_attr, PTHREAD_SCOPE_SYSTEM);
	pthread_attr_setdetachstate(&thread_attr, PTHREAD_CREATE_DETACHED);
	if (stack_size > 0) {
		pthread_attr_setstacksize(&thread_attr, stack_size);
	}
	
	/* Start the thread that we will start the JVM on. */
	pthread_t vmthread;
	pthread_create(&vmthread, &thread_attr, JNI_CreateJavaVM, (void**)env, &vm_args);
	pthread_attr_destroy(&thread_attr);
	
    /* Create a a sourceContext to be used by our source that makes */
    /* sure the CFRunLoop doesn't exit right away */
	CFRunLoopSourceContext sourceContext;
    sourceContext.version = 0;
    sourceContext.info = NULL;
    sourceContext.retain = NULL;
    sourceContext.release = NULL;
    sourceContext.copyDescription = NULL;
    sourceContext.equal = NULL;
    sourceContext.hash = NULL;
    sourceContext.schedule = NULL;
    sourceContext.cancel = NULL;
    sourceContext.perform = &sourceCallBack;
    
    /* Create the Source from the sourceContext */
    CFRunLoopSourceRef sourceRef = CFRunLoopSourceCreate (NULL, 0, &sourceContext);
    
    /* Use the constant kCFRunLoopCommonModes to add the source to the set of objects */ 
    /* monitored by all the common modes */
    CFRunLoopAddSource (CFRunLoopGetCurrent(),sourceRef,kCFRunLoopCommonModes); 

    /* Park this thread in the runloop */
    CFRunLoopRun();
#endif
    
    if (JNICdebug()) printf("Starting JVM...");
    /* create VM init args */
	vm_args.version = JNI_VERSION_1_4;
	vm_args.options = options;
	vm_args.nOptions = nOptions;
	vm_args.ignoreUnrecognized = JNI_TRUE;

    /* create VM */
	rc = JNI_CreateJavaVM(jvm, (void **)env, &vm_args);

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
