/*
 * @(#)jni_md.h	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Modified for usage on WIN32 with g++ compiler
 * @author Mark Donszelmann
 * @version $Id: jni_md.h 9658 2004-01-23 22:50:31Z duns $
 */

#ifndef _JAVASOFT_JNI_MD_H_
#define _JAVASOFT_JNI_MD_H_

#define JNIEXPORT 
#define JNIIMPORT
#define JNICALL __stdcall

typedef long jint;
typedef long jlong;
typedef signed char jbyte;

#endif /* !_JAVASOFT_JNI_MD_H_ */
