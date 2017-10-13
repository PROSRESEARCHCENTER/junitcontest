// AIDAJNI made by using SWIG
// 
// @author Mark Donszelmann
// @version $Id: AIDAJNI.swg 9052 2006-09-29 23:30:11Z duns $
 
%module(directors="1") AIDA

%{
#include <vector>
#include <map>
#include <string>

#include <AIDA/AIDA.h>
#include <AIDAJNIUtil.h>

// For tests
#include <CAIDA.h>
%}

%import "std_except.i"
%import "std_string.i"
%import "std_vector.i"
%apply const std::string & {std::string &};

%typemap(javainterfaces) SWIGTYPE "hep.aida.$javaclassname"

%typemap(javainterfaces) AIDA::IAnalysisFactory ""
%typemap(javabase) AIDA::IAnalysisFactory "hep.aida.$javaclassname"

// FIXME list abstract classes
//%typemap(javaclassmodifiers)	Histogram "public abstract class"

%typemap(javaimports) SWIGTYPE %{ 
import hep.aida.jni.AIDAJNIUtil;
%}

%pragma(java) jniclassimports=%{
import hep.aida.jni.AIDAJNIUtil;
%}

// Base proxy classes
%typemap(javabody) SWIGTYPE %{
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public $javaclassname(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(hep.aida.$javaclassname obj) {
    if (obj instanceof $javaclassname) {
      return (obj == null) ? 0 : (($javaclassname)obj).swigCPtr;
    } else {
      long cPtr = $moduleJNI.new_$javaclassname();
      // FIXME, memory leak if Java class gets finalized, since C++ director is not freed.
      $moduleJNI.$javaclassname_director_connect(obj, cPtr, true, true);
      return cPtr;
    }
  }
%}

// Derived proxy classes
%typemap(javabody_derived) SWIGTYPE %{
  private long swigCPtr;

  public $javaclassname(long cPtr, boolean cMemoryOwn) {
    super($imclassname.SWIG$javaclassnameUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(hep.aida.$javaclassname obj) {
    if (obj instanceof $javaclassname) {
      return (obj == null) ? 0 : (($javaclassname)obj).swigCPtr;
    } else {
      long cPtr = $moduleJNI.new_$javaclassname();
      // FIXME, memory leak if Java class gets finalized, since C++ director is not freed.
      $moduleJNI.$javaclassname_director_connect(obj, cPtr, true, true);
      return cPtr;
    }
  }
%}

%typemap(javadirectorout) SWIGTYPE *, SWIGTYPE (CLASS::*), SWIGTYPE &, const SWIGTYPE & "$javaclassname.getCPtr((hep.aida.$javaclassname)$javacall)"

// char **
%typemap(directorin, descriptor="[Ljava/lang/String;") char ** "std::cerr << \"Need conversion from char** to [Ljava/lang/Class;\" << std::endl;"
%typemap(in) char ** (jint size) {
    int i = 0;
    size = jenv->GetArrayLength($input);
    $1 = (char **) malloc((size+1)*sizeof(char *));
    /* make a copy of each string */
    for (i = 0; i<size; i++) {
        jstring j_string = (jstring)jenv->GetObjectArrayElement($input, i);
        const char * c_string = jenv->GetStringUTFChars(j_string, 0);
        $1[i] = malloc(strlen((c_string)+1)*sizeof(const char *));
        strcpy($1[i], c_string);
        jenv->ReleaseStringUTFChars(j_string, c_string);
        jenv->DeleteLocalRef(j_string);
    }
    $1[i] = 0;
}
%typemap(freearg) char ** {
    int i;
    for (i=0; i<size$argnum-1; i++)
      free($1[i]);
    free($1);
}
%typemap(out) char ** {
    int i;
    int len=0;
    jstring temp_string;
    const jclass clazz = jenv->FindClass("java/lang/String");

    while ($1[len]) len++;    
    jresult = jenv->NewObjectArray(len, clazz, NULL);
    /* exception checking omitted */

    for (i=0; i<len; i++) {
      temp_string = jenv->NewStringUTF(*result++);
      jenv->SetObjectArrayElement(jresult, i, temp_string);
      jenv->DeleteLocalRef(temp_string);
    }
}
%typemap(jni) char ** "jobjectArray"
%typemap(jtype) char ** "String[]"
%typemap(jstype) char ** "String[]"
%typemap(javain) char ** "$javainput"
%typemap(javaout) char ** {
    return $jnicall;
  }


// int64
%typemap(javadirectorin) int64 "$jniinput"
%typemap(javadirectorout) int64 "$javacall"
%typemap(javain) int64 "$javainput"
%typemap(javaout) int64 {
    return $jnicall;
  }
%typemap(jni) int64 "jlong"
%typemap(jtype) int64 "long"
%typemap(jstype) int64 "long"

// byte
%typemap(javadirectorin) byte "$jniinput"
%typemap(javadirectorout) byte "$javacall"
%typemap(javain) byte "$javainput"
%typemap(javaout) byte {
    return $jnicall;
  }
%typemap(jni) byte "jbyte"
%typemap(jtype) byte "byte"
%typemap(jstype) byte "byte"


// int[]
// FIXME, real conversion should still be implemented
%typemap(javadirectorin) std::vector<int>, std::vector<int> & "$jniinput"
%typemap(javadirectorout) std::vector<int>, std::vector<int> & "$javacall"
%typemap(javain) std::vector<int>, std::vector<int> & "$javainput"
%typemap(javaout) std::vector<int>, std::vector<int> & {
    return $jnicall;
  }
%typemap(jni) std::vector<int>, std::vector<int> & "jintArray"
%typemap(jtype) std::vector<int>, std::vector<int> & "int[]"
%typemap(jstype) std::vector<int>, std::vector<int> & "int[]"
%typemap(directorin, descriptor="[I") std::vector<int>, std::vector<int> & "std::cerr << \"Need conversion from vector to [I\" << std::endl;"


// double[]
// FIXME, real conversion should still be implemented
%typemap(javadirectorin) std::vector<double>, std::vector<double> &, std::vector<double> & "$jniinput"
%typemap(javadirectorout) std::vector<double>, std::vector<double> & "$javacall"
%typemap(javain) std::vector<double>, std::vector<double> & "$javainput"
%typemap(javaout) std::vector<double>, std::vector<double> & {
    return $jnicall;
  }
%typemap(jni) std::vector<double>, std::vector<double> & "jdoubleArray"
%typemap(jtype) std::vector<double>, std::vector<double> & "double[]"
%typemap(jstype) std::vector<double>, std::vector<double> & "double[]"
%typemap(directorin, descriptor="[D") std::vector<double>, std::vector<double> & "std::cerr << \"Need conversion from vector to [D\" << std::endl;"


// float[]
// FIXME, real conversion should still be implemented
%typemap(javadirectorin) std::vector<float>, std::vector<float> & "$jniinput"
%typemap(javadirectorout) std::vector<float>, std::vector<float> & "$javacall"
%typemap(javain) std::vector<float>, std::vector<float> & "$javainput"
%typemap(javaout) std::vector<float>, std::vector<float> & {
    return $jnicall;
  }
%typemap(jni) std::vector<float>, std::vector<float> & "jfloatArray"
%typemap(jtype) std::vector<float>, std::vector<float> & "float[]"
%typemap(jstype) std::vector<float>, std::vector<float> & "float[]"
%typemap(directorin, descriptor="[F") std::vector<float>, std::vector<float> & "std::cerr << \"Need conversion from vector to [F\" << std::endl;"


// String[]
%typemap(javadirectorin) std::vector<std::string> &, std::vector<std::string> "AIDAJNIUtil.toStringArray($jniinput)"
%typemap(javadirectorout) std::vector<std::string> &, std::vector<std::string> "AIDAJNIUtil.toPtr($javacall)"
%typemap(javain) std::vector<std::string> &, std::vector<std::string> "AIDAJNIUtil.toPtr($javainput)"
%typemap(javaout) std::vector<std::string> &, std::vector<std::string> {
    return AIDAJNIUtil.toStringArray($jnicall);
  }
%typemap(jni) std::vector<std::string> &, std::vector<std::string> "jlong"
%typemap(jtype) std::vector<std::string> &, std::vector<std::string> "long"
%typemap(jstype) std::vector<std::string> &, std::vector<std::string> "String[]"
%typemap(directorin, descriptor="[Ljava/lang/String;") std::vector<std::string>, std::vector<std::string> & "std::cerr << \"Need conversion from vector to [LJava/lang/String;\" << std::endl;"


// Class[] (input)
// FIXME, real conversion should still be implemented
//%typemap(javadirectorin) std::vector<std::string>  & columnType "$jniinput"
//%typemap(javain) std::vector<std::string>  & columnType "$javainput"
//%typemap(jni) std::vector<std::string>  & columnType "jlong"
//%typemap(jtype) std::vector<std::string>  & columnType "long"
//%typemap(jstype) std::vector<std::string>  & columnType "Class[]"


// StringObject (return)
%typemap(javadirectorout) StringObject "$javacall.toString()"
%typemap(javaout) StringObject {
    return $jnicall;
  }
%typemap(jni) StringObject "jobject"
%typemap(jtype) StringObject "String"
%typemap(jstype) StringObject "Object"


// StringClass (return)
%typemap(javadirectorout) StringClass "AIDAJNIUtil.convert($javacall)"
%typemap(javaout) StringClass {
    return AIDAJNIUtil.convert($jnicall);
  }
//%typemap(out) StringClass {
//		$result = Java_hep_aida_jni_convert(jenv, result);
//  }
%typemap(jni) StringClass "jstring"
%typemap(jtype) StringClass "String"
%typemap(jstype) StringClass "Class"
%typemap(directorin, descriptor="Ljava/lang/Class;") StringClass "std::cerr << \"Need conversion from String to Ljava/lang/Class;\" << std::endl;"


// StringClassArray (return)
%typemap(javadirectorin) StringClassArray, StringClassArray & "AIDAJNIUtil.toClassArray($jniinput)"
%typemap(javadirectorout) StringClassArray, StringClassArray & "AIDAJNIUtil.toPtr($javacall)"
%typemap(javaout) StringClassArray, StringClassArray & {
    return AIDAJNIUtil.toClassArray($jnicall);
  }
%typemap(javain) StringClassArray, StringClassArray & "AIDAJNIUtil.toPtr($javainput)"
%typemap(jni) StringClassArray, StringClassArray & "jlong"
%typemap(jtype) StringClassArray, StringClassArray & "long"
%typemap(jstype) StringClassArray, StringClassArray & "Class[]"
%typemap(directorin, descriptor="[Ljava/lang/Class;") StringClassArray, StringClassArray & "std::cerr << \"Need conversion from vector to [Ljava/lang/Class;\" << std::endl;"


// java.io.OutputStream
// FIXME, real conversion should still be implemented, null used to be $jniinput, 0 $javainput
%typemap(javadirectorin) std::ostream & "null"
%typemap(javadirectorout) std::ostream & "$javacall"
%typemap(javain) std::ostream & "0"
%typemap(javaout) std::ostream & {
    return $jnicall;
  }
%typemap(jni) std::ostream & "jlong"
%typemap(jtype) std::ostream & "long"
%typemap(jstype) std::ostream & "java.io.OutputStream"
%typemap(directorin, descriptor="Ljava/io/OutputStream;") std::ostream & "std::cerr << \"Need conversion from std::ostream to Ljava/io/OutputStream;\" << std::endl;"


// IBaseStyle[]
 // FIXME, real conversion should still be implemented
%typemap(javadirectorin) std::vector<AIDA::IBaseStyle *> "$jniinput"
%typemap(javadirectorout) std::vector<AIDA::IBaseStyle *> "$javacall"
%typemap(javain) std::vector<AIDA::IBaseStyle *> "$javainput"
%typemap(javaout) std::vector<AIDA::IBaseStyle *> {
    return $jnicall;
  }
%typemap(jni) std::vector<AIDA::IBaseStyle *> "jlong"
%typemap(jtype) std::vector<AIDA::IBaseStyle *> "long"
%typemap(jstype) std::vector<AIDA::IBaseStyle *> "hep.aida.IBaseStyle[]"
   
   
// IEvaluator[]
// FIXME, real conversion should still be implemented
%typemap(javadirectorin) std::vector<AIDA::IEvaluator *> & "AIDAJNIUtil.toIEvaluatorArray($jniinput)"
%typemap(javadirectorout) std::vector<AIDA::IEvaluator *> & "AIDAJNIUtil.toPtr($javacall)"
%typemap(javain) std::vector<AIDA::IEvaluator *> & "AIDAJNIUtil.toPtr($javainput)"
%typemap(javaout) std::vector<AIDA::IEvaluator *> & {
    return AIDAJNIUtil.toIEvaluatorArray($jnicall);
  }
%typemap(jni) std::vector<AIDA::IEvaluator *> & "jlong"
%typemap(jtype) std::vector<AIDA::IEvaluator *> & "long"
%typemap(jstype) std::vector<AIDA::IEvaluator *> & "hep.aida.IEvaluator[]"
%typemap(directorin, descriptor="[Lhep/aida/IEvaluator;") std::vector<AIDA::IEvaluator *> & "std::cerr << \"Need conversion from vector to [Lhep/aida/IEvaluator;\" << std::endl;"


// ITuple[]
// FIXME, real conversion should still be implemented
%typemap(javadirectorin) std::vector<AIDA::ITuple *> & "AIDAJNIUtil.toITupleArray($jniinput)"
%typemap(javadirectorout) std::vector<AIDA::ITuple *> & "AIDAJNIUtil.toPtr($javacall)"
%typemap(javain) std::vector<AIDA::ITuple *> & "AIDAJNIUtil.toPtr($javainput)"
%typemap(javaout) std::vector<AIDA::ITuple *> & {
    return AIDAJNIUtil.toITupleArray($jnicall);
  }
%typemap(jni) std::vector<AIDA::ITuple *> & "jlong"
%typemap(jtype) std::vector<AIDA::ITuple *> & "long"
%typemap(jstype) std::vector<AIDA::ITuple *> & "hep.aida.ITuple[]"
%typemap(directorin, descriptor="[Lhep/aida/IEvaluator;") std::vector<AIDA::ITuple *> & "std::cerr << \"Need conversion from vector to [Lhep/aida/ITuple;\" << std::endl;"
   
%ignore AIDA_TYPES_H;   
%ignore AIDA_INT32_FORMAT;
%ignore AIDA_INT64_FORMAT;
   
%ignore AIDA_IANALYSISFACTORY_H;
%ignore AIDA::IAnalysisFactory::createPlotterFactory;
%typemap(jni) AIDA::IAnalysisFactory &, AIDA::IAnalysisFactory * "jlong"
%typemap(jtype) AIDA::IAnalysisFactory &, AIDA::IAnalysisFactory * "long"
%typemap(jstype) AIDA::IAnalysisFactory &, AIDA::IAnalysisFactory * "hep.aida.IAnalysisFactory"
%typemap(directorin, descriptor="Lhep/aida/IAnalysisFactory;") AIDA::IAnalysisFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IAnalysisFactory;") AIDA::IAnalysisFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%typemap(javabody) AIDA::IAnalysisFactory %{
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public $javaclassname(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(hep.aida.$javaclassname obj) {
    if (obj instanceof $javaclassname) {
      return (obj == null) ? 0 : (($javaclassname)obj).swigCPtr;
    } else {
      long cPtr = $moduleJNI.new_$javaclassname();
      // FIXME, memory leak if Java class gets finalized, since C++ director is not freed.
      $moduleJNI.$javaclassname_director_connect(obj, cPtr, true, true);
      return cPtr;
    }
  }
  
  public hep.aida.IPlotterFactory createPlotterFactory() {
    throw new RuntimeException("IAnalysisFactory.createPlotterFactory() not implemented in C++");
  }
%}


%ignore AIDA_IANNOTATION_H;
%typemap(jni) AIDA::IAnnotation & "jlong"
%typemap(jtype) AIDA::IAnnotation & "long"
%typemap(jstype) AIDA::IAnnotation & "hep.aida.IAnnotation"
%typemap(directorin, descriptor="Lhep/aida/IAnnotation;") AIDA::IAnnotation & " *($&1_ltype)&$input = ($1_ltype) &$1; "
//%typemap(jstype) AIDA::IAnnotation "hep.aida.IAnnotation"

%ignore AIDA_IAXIS_H;
%typemap(jni) AIDA::IAxis & "jlong"
%typemap(jtype) AIDA::IAxis & "long"
%typemap(jstype) AIDA::IAxis & "hep.aida.IAxis"
%typemap(directorin, descriptor="Lhep/aida/IAxis;") AIDA::IAxis & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IAXISSTYLE_H;
%typemap(jni) AIDA::IAxisStyle &, AIDA::IAxisStyle * "jlong"
%typemap(jtype) AIDA::IAxisStyle &,  AIDA::IAxisStyle * "long"
%typemap(jstype) AIDA::IAxisStyle &,  AIDA::IAxisStyle * "hep.aida.IAxisStyle"
%typemap(directorin, descriptor="Lhep/aida/IAxisStyle;") AIDA::IAxisStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IAxisStyle;") AIDA::IAxisStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IBASEHISTOGRAM_H;
%ignore AIDA::IBaseHistogram::annotation();
%typemap(jni) AIDA::IBaseHistogram &, AIDA::IBaseHistogram * "jlong"
%typemap(jtype) AIDA::IBaseHistogram &, AIDA::IBaseHistogram * "long"
%typemap(jstype) AIDA::IBaseHistogram &, AIDA::IBaseHistogram * "hep.aida.IBaseHistogram"
%typemap(directorin, descriptor="Lhep/aida/IBaseHistogram;") AIDA::IBaseHistogram & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IBaseHistogram;") AIDA::IBaseHistogram * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IBASESTYLE_H;
%typemap(jni) AIDA::IBaseStyle &, AIDA::IBaseStyle * "jlong"
%typemap(jtype) AIDA::IBaseStyle &, AIDA::IBaseStyle * "long"
%typemap(jstype) AIDA::IBaseStyle &, AIDA::IBaseStyle * "hep.aida.IBaseStyle"
%typemap(directorin, descriptor="Lhep/aida/IBaseStyle;") AIDA::IBaseStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IBaseStyle;") AIDA::IBaseStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IBASETUPLECOLUMN_H;
%typemap(jni) AIDA::IBaseTupleColumn &, AIDA::IBaseTupleColumn * "jlong"
%typemap(jtype) AIDA::IBaseTupleColumn &, AIDA::IBaseTupleColumn * "long"
%typemap(jstype) AIDA::IBaseTupleColumn &, AIDA::IBaseTupleColumn * "hep.aida.IBaseTupleColumn"
%typemap(directorin, descriptor="Lhep/aida/IBaseTupleColumn;") AIDA::IBaseTupleColumn & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IBaseTupleColumn;") AIDA::IBaseTupleColumn * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IBORDERSTYLE_H;
%typemap(jni) AIDA::IBorderStyle & "jlong"
%typemap(jtype) AIDA::IBorderStyle & "long"
%typemap(jstype) AIDA::IBorderStyle & "hep.aida.IBorderStyle"
%typemap(directorin, descriptor="Lhep/aida/IBorderStyle;") AIDA::IBorderStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IBOXSTYLE_H;
%typemap(jni) AIDA::IBoxStyle & "jlong"
%typemap(jtype) AIDA::IBoxStyle & "long"
%typemap(jstype) AIDA::IBoxStyle & "hep.aida.IBoxStyle"
%typemap(directorin, descriptor="Lhep/aida/IBoxStyle;") AIDA::IBoxStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IBRUSHSTYLE_H;
%typemap(jni) AIDA::IBrushStyle & "jlong"
%typemap(jtype) AIDA::IBrushStyle & "long"
%typemap(jstype) AIDA::IBrushStyle & "hep.aida.IBrushStyle"
%typemap(directorin, descriptor="Lhep/aida/IBrushStyle;") AIDA::IBrushStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_ICLOUD_H;
%typemap(jni) AIDA::ICloud & "jlong"
%typemap(jtype) AIDA::ICloud & "long"
%typemap(jstype) AIDA::ICloud & "hep.aida.ICloud"
%typemap(directorin, descriptor="Lhep/aida/ICloud;") AIDA::ICloud & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_ICLOUD1D_H;
%typemap(jni) AIDA::ICloud1D &, AIDA::ICloud1D * "jlong"
%typemap(jtype) AIDA::ICloud1D &, AIDA::ICloud1D * "long"
%typemap(jstype) AIDA::ICloud1D &, AIDA::ICloud1D * "hep.aida.ICloud1D"
%typemap(directorin, descriptor="Lhep/aida/ICloud1D;") AIDA::ICloud1D & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ICloud1D;") AIDA::ICloud1D * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ICLOUD2D_H;
%typemap(jni) AIDA::ICloud2D &, AIDA::ICloud2D * "jlong"
%typemap(jtype) AIDA::ICloud2D &, AIDA::ICloud2D * "long"
%typemap(jstype) AIDA::ICloud2D &, AIDA::ICloud2D * "hep.aida.ICloud2D"
%typemap(directorin, descriptor="Lhep/aida/ICloud2D;") AIDA::ICloud2D & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ICloud2D;") AIDA::ICloud2D * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ICLOUD3D_H;
%typemap(jni) AIDA::ICloud3D &, AIDA::ICloud3D * "jlong"
%typemap(jtype) AIDA::ICloud3D &, AIDA::ICloud3D * "long"
%typemap(jstype) AIDA::ICloud3D &, AIDA::ICloud3D * "hep.aida.ICloud3D"
%typemap(directorin, descriptor="Lhep/aida/ICloud3D;") AIDA::ICloud3D & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ICloud3D;") AIDA::ICloud3D * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ICONSTANTS_H;

%ignore AIDA_IDATAPOINT_H;
%ignore AIDA::IDataPoint::coordinate(int);
%typemap(jni) AIDA::IDataPoint &, AIDA::IDataPoint * "jlong"
%typemap(jtype) AIDA::IDataPoint &, AIDA::IDataPoint * "long"
%typemap(jstype) AIDA::IDataPoint &, AIDA::IDataPoint * "hep.aida.IDataPoint"
%typemap(directorin, descriptor="Lhep/aida/IDataPoint;") AIDA::IDataPoint & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IDataPoint;") AIDA::IDataPoint * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IDATAPOINTSET_H;
%ignore AIDA::IDataPointSet::annotation();
%ignore AIDA::IDataPointSet::point(int);
%typemap(jni) AIDA::IDataPointSet &, AIDA::IDataPointSet * "jlong"
%typemap(jtype) AIDA::IDataPointSet &, AIDA::IDataPointSet * "long"
%typemap(jstype) AIDA::IDataPointSet &, AIDA::IDataPointSet * "hep.aida.IDataPointSet"
%typemap(directorin, descriptor="Lhep/aida/IDataPointSet;") AIDA::IDataPointSet & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IDataPointSet;") AIDA::IDataPointSet * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IDATAPOINTSETFACTORY_H;
%typemap(jni) AIDA::IDataPointSetFactory &, AIDA::IDataPointSetFactory * "jlong"
%typemap(jtype) AIDA::IDataPointSetFactory &, AIDA::IDataPointSetFactory * "long"
%typemap(jstype) AIDA::IDataPointSetFactory &, AIDA::IDataPointSetFactory * "hep.aida.IDataPointSetFactory"
%typemap(directorin, descriptor="Lhep/aida/IDataPointSetFactory;") AIDA::IDataPointSetFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IDataPointSetFactory;") AIDA::IDataPointSetFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IDATASTYLE_H;
%typemap(jni) AIDA::IDataStyle &, AIDA::IDataStyle * "jlong"
%typemap(jtype) AIDA::IDataStyle &, AIDA::IDataStyle * "long"
%typemap(jstype) AIDA::IDataStyle &, AIDA::IDataStyle * "hep.aida.IDataStyle"
%typemap(directorin, descriptor="Lhep/aida/IDataStyle;") AIDA::IDataStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IDataStyle;") AIDA::IDataStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IEVALUATOR_H;
%typemap(jni) AIDA::IEvaluator &, AIDA::IEvaluator * "jlong"
%typemap(jtype) AIDA::IEvaluator &, AIDA::IEvaluator * "long"
%typemap(jstype) AIDA::IEvaluator &, AIDA::IEvaluator * "hep.aida.IEvaluator"
%typemap(directorin, descriptor="Lhep/aida/IEvaluator;") AIDA::IEvaluator & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IEvaluator;") AIDA::IEvaluator * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFILLSTYLE_H;
%typemap(jni) AIDA::IFillStyle &, AIDA::IFillStyle * "jlong"
%typemap(jtype) AIDA::IFillStyle &, AIDA::IFillStyle * "long"
%typemap(jstype) AIDA::IFillStyle &, AIDA::IFillStyle * "hep.aida.IFillStyle"
%typemap(directorin, descriptor="Lhep/aida/IFillStyle;") AIDA::IFillStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFillStyle;") AIDA::IFillStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFILTER_H;
%typemap(jni) AIDA::IFilter &, AIDA::IFilter * "jlong"
%typemap(jtype) AIDA::IFilter &, AIDA::IFilter * "long"
%typemap(jstype) AIDA::IFilter &, AIDA::IFilter * "hep.aida.IFilter"
%typemap(directorin, descriptor="Lhep/aida/IFilter;") AIDA::IFilter & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFilter;") AIDA::IFilter * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFITDATA_H;
%typemap(jni) AIDA::IFitData &, AIDA::IFitData * "jlong"
%typemap(jtype) AIDA::IFitData &, AIDA::IFitData * "long"
%typemap(jstype) AIDA::IFitData &, AIDA::IFitData * "hep.aida.IFitData"
%typemap(directorin, descriptor="Lhep/aida/IFitData;") AIDA::IFitData & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFilter;") AIDA::IFitData * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFITFACTORY_H;
%typemap(jni) AIDA::IFitFactory &, AIDA::IFitFactory * "jlong"
%typemap(jtype) AIDA::IFitFactory &, AIDA::IFitFactory * "long"
%typemap(jstype) AIDA::IFitFactory &, AIDA::IFitFactory * "hep.aida.IFitFactory"
%typemap(directorin, descriptor="Lhep/aida/IFitFactory;") AIDA::IFitFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFitFactory;") AIDA::IFitFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFITPARMETERSETTINGS_H;
%typemap(jni) AIDA::IFitParameterSettings &, AIDA::IFitParameterSettings * "jlong"
%typemap(jtype) AIDA::IFitParameterSettings &, AIDA::IFitParameterSettings * "long"
%typemap(jstype) AIDA::IFitParameterSettings &, AIDA::IFitParameterSettings * "hep.aida.IFitParameterSettings"
%typemap(directorin, descriptor="Lhep/aida/IFitParameterSettings;") AIDA::IFitParameterSettings & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFitParameterSettings;") AIDA::IFitParameterSettings * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFITRESULT_H;
%typemap(jni) AIDA::IFitResult &, AIDA::IFitResult * "jlong"
%typemap(jtype) AIDA::IFitResult &, AIDA::IFitResult * "long"
%typemap(jstype) AIDA::IFitResult &, AIDA::IFitResult * "hep.aida.IFitResult"
%typemap(directorin, descriptor="Lhep/aida/IFitResult;") AIDA::IFitResult & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFitResult;") AIDA::IFitResult * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFITTER_H;
%typemap(jni) AIDA::IFitter &, AIDA::IFitter * "jlong"
%typemap(jtype) AIDA::IFitter &, AIDA::IFitter * "long"
%typemap(jstype) AIDA::IFitter &, AIDA::IFitter * "hep.aida.IFitter"
%typemap(directorin, descriptor="Lhep/aida/IFitter;") AIDA::IFitter & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFilter;") AIDA::IFitter * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFUNCTION_H;
%ignore AIDA::IFunction::annotation();
%typemap(jni) AIDA::IFunction &, AIDA::IFunction * "jlong"
%typemap(jtype) AIDA::IFunction &, AIDA::IFunction * "long"
%typemap(jstype) AIDA::IFunction &, AIDA::IFunction * "hep.aida.IFunction"
%typemap(directorin, descriptor="Lhep/aida/IFunction;") AIDA::IFunction & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFunction;") AIDA::IFunction * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFUNCTIONCATALOG_H;
%typemap(jni) AIDA::IFunctionCatalog &, AIDA::IFunctionCatalog * "jlong"
%typemap(jtype) AIDA::IFunctionCatalog &, AIDA::IFunctionCatalog * "long"
%typemap(jstype) AIDA::IFunctionCatalog &, AIDA::IFunctionCatalog * "hep.aida.IFunctionCatalog"
%typemap(directorin, descriptor="Lhep/aida/IFunctionCatalog;") AIDA::IFunctionCatalog & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFunctionCatalog;") AIDA::IFunctionCatalog * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IFUNCTIONFACTORY_H;
%typemap(jni) AIDA::IFunctionFactory &, AIDA::IFunctionFactory * "jlong"
%typemap(jtype) AIDA::IFunctionFactory &, AIDA::IFunctionFactory * "long"
%typemap(jstype) AIDA::IFunctionFactory &, AIDA::IFunctionFactory * "hep.aida.IFunctionFactory"
%typemap(directorin, descriptor="Lhep/aida/IFunctionFactory;") AIDA::IFunctionFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IFunctionFactory;") AIDA::IFunctionFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IGENERICFACTORY_H;
%typemap(jni) AIDA::IGenericFactory &, AIDA::IGenericFactory * "jlong"
%typemap(jtype) AIDA::IGenericFactory &, AIDA::IGenericFactory * "long"
%typemap(jstype) AIDA::IGenericFactory &, AIDA::IGenericFactory * "hep.aida.IGenericFactory"
%typemap(directorin, descriptor="Lhep/aida/IGenericFactory;") AIDA::IGenericFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IGenericFactory;") AIDA::IGenericFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IGRIDSTYLE_H;
%typemap(jni) AIDA::IGridStyle & "jlong"
%typemap(jtype) AIDA::IGridStyle & "long"
%typemap(jstype) AIDA::IGridStyle & "hep.aida.IGridStyle"
%typemap(directorin, descriptor="Lhep/aida/IGridStyle;") AIDA::IGridStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IHISTOGRAM_H;
%typemap(jni) AIDA::IHistogram & "jlong"
%typemap(jtype) AIDA::IHistogram & "long"
%typemap(jstype) AIDA::IHistogram & "hep.aida.IHistogram"
%typemap(directorin, descriptor="Lhep/aida/IHistogram;") AIDA::IHistogram & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IHISTOGRAM1D_H;
%typemap(jni) AIDA::IHistogram1D &, AIDA::IHistogram1D * "jlong"
%typemap(jtype) AIDA::IHistogram1D &, AIDA::IHistogram1D * "long"
%typemap(jstype) AIDA::IHistogram1D &, AIDA::IHistogram1D * "hep.aida.IHistogram1D"
%typemap(directorin, descriptor="Lhep/aida/IHistogram1D;") AIDA::IHistogram1D & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IHistogram1D;") AIDA::IHistogram1D * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IHISTOGRAM2D_H;
%typemap(jni) AIDA::IHistogram2D &, AIDA::IHistogram2D * "jlong"
%typemap(jtype) AIDA::IHistogram2D &, AIDA::IHistogram2D * "long"
%typemap(jstype) AIDA::IHistogram2D &, AIDA::IHistogram2D * "hep.aida.IHistogram2D"
%typemap(directorin, descriptor="Lhep/aida/IHistogram2D;") AIDA::IHistogram2D & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IHistogram2D;") AIDA::IHistogram2D * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IHISTOGRAM3D_H;
%typemap(jni) AIDA::IHistogram3D &, AIDA::IHistogram3D * "jlong"
%typemap(jtype) AIDA::IHistogram3D &, AIDA::IHistogram3D * "long"
%typemap(jstype) AIDA::IHistogram3D &, AIDA::IHistogram3D * "hep.aida.IHistogram3D"
%typemap(directorin, descriptor="Lhep/aida/IHistogram3D;") AIDA::IHistogram3D & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IHistogram3D;") AIDA::IHistogram3D * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IHISTOGRAMFACTORY_H;
%typemap(jni) AIDA::IHistogramFactory &, AIDA::IHistogramFactory * "jlong"
%typemap(jtype) AIDA::IHistogramFactory &, AIDA::IHistogramFactory * "long"
%typemap(jstype) AIDA::IHistogramFactory &, AIDA::IHistogramFactory * "hep.aida.IHistogramFactory"
%typemap(directorin, descriptor="Lhep/aida/IHistogramFactory;") AIDA::IHistogramFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IHistogramFactory;") AIDA::IHistogramFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IINFO_H;
%typemap(jni) AIDA::IInfo & "jlong"
%typemap(jtype) AIDA::IInfo & "long"
%typemap(jstype) AIDA::IInfo & "hep.aida.IInfo"
%typemap(directorin, descriptor="Lhep/aida/IInfo;") AIDA::IInfo & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IINFOSTYLE_H;
%typemap(jni) AIDA::IInfoStyle & "jlong"
%typemap(jtype) AIDA::IInfoStyle & "long"
%typemap(jstype) AIDA::IInfoStyle & "hep.aida.IInfoStyle"
%typemap(directorin, descriptor="Lhep/aida/IInfoStyle;") AIDA::IInfoStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_ILINESTYLE_H;
%typemap(jni) AIDA::ILineStyle &, AIDA::ILineStyle * "jlong"
%typemap(jtype) AIDA::ILineStyle &, AIDA::ILineStyle * "long"
%typemap(jstype) AIDA::ILineStyle &, AIDA::ILineStyle * "hep.aida.ILineStyle"
%typemap(directorin, descriptor="Lhep/aida/ILineStyle;") AIDA::ILineStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ILineStyle;") AIDA::ILineStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ILEGENDBOXSTYLE_H;
%typemap(jni) AIDA::ILegendBoxStyle & "jlong"
%typemap(jtype) AIDA::ILegendBoxStyle & "long"
%typemap(jstype) AIDA::ILegendBoxStyle & "hep.aida.ILegendBoxStyle"
%typemap(directorin, descriptor="Lhep/aida/ILegendBoxStyle;") AIDA::ILegendBoxStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IMANAGEDOBJECT_H;
%typemap(jni) AIDA::IManagedObject &, AIDA::IManagedObject * "jlong"
%typemap(jtype) AIDA::IManagedObject &, AIDA::IManagedObject * "long"
%typemap(jstype) AIDA::IManagedObject &, AIDA::IManagedObject * "hep.aida.IManagedObject"
%typemap(directorin, descriptor="Lhep/aida/IManagedObject;") AIDA::IManagedObject & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IManagedObject;") AIDA::IManagedObject * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IMARKERSTYLE_H;
%typemap(jni) AIDA::IMarkerStyle &, AIDA::IMarkerStyle * "jlong"
%typemap(jtype) AIDA::IMarkerStyle &, AIDA::IMarkerStyle * "long"
%typemap(jstype) AIDA::IMarkerStyle &, AIDA::IMarkerStyle * "hep.aida.IMarkerStyle"
%typemap(directorin, descriptor="Lhep/aida/IMarkerStyle;") AIDA::IMarkerStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IMarkerStyle;") AIDA::IMarkerStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IMEASUREMENT_H;
%typemap(jni) AIDA::IMeasurement &, AIDA::IMeasurement * "jlong"
%typemap(jtype) AIDA::IMeasurement &, AIDA::IMeasurement * "long"
%typemap(jstype) AIDA::IMeasurement &, AIDA::IMeasurement * "hep.aida.IMeasurement"
%typemap(directorin, descriptor="Lhep/aida/IMeasurement;") AIDA::IMeasurement & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IMeasurement;") AIDA::IMeasurement * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IMODELFUNCTION_H;
%typemap(jni) AIDA::IModelFunction & "jlong"
%typemap(jtype) AIDA::IModelFunction & "long"
%typemap(jstype) AIDA::IModelFunction & "hep.aida.IModelFunction"
%typemap(directorin, descriptor="Lhep/aida/IModelFunction;") AIDA::IModelFunction & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IPLOTTABLE_H;
%typemap(jni) AIDA::IPlottable & "jlong"
%typemap(jtype) AIDA::IPlottable & "long"
%typemap(jstype) AIDA::IPlottable & "hep.aida.IPlottable"
%typemap(directorin, descriptor="Lhep/aida/IPlottable;") AIDA::IPlottable & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IPLOTTER_H;
%typemap(jni) AIDA::IPlotter &, AIDA::IPlotter * "jlong"
%typemap(jtype) AIDA::IPlotter &, AIDA::IPlotter * "long"
%typemap(jstype) AIDA::IPlotter &, AIDA::IPlotter * "hep.aida.IPlotter"
%typemap(directorin, descriptor="Lhep/aida/IPlotter;") AIDA::IPlotter & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IPlotter;") AIDA::IPlotter * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IPLOTTERFACORY_H;
%typemap(jni) AIDA::IPlotterFactory &, AIDA::IPlotterFactory * "jlong"
%typemap(jtype) AIDA::IPlotterFactory &, AIDA::IPlotterFactory * "long"
%typemap(jstype) AIDA::IPlotterFactory &, AIDA::IPlotterFactory * "hep.aida.IPlotterFactory"
%typemap(directorin, descriptor="Lhep/aida/IPlotterFactory;") AIDA::IPlotterFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IPlotterFactory;") AIDA::IPlotterFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "
%typemap(javadirectorout) AIDA::IPlotterFactory * "IPlotterFactory.getCPtr(self.createPlotterFactory())"

%ignore AIDA_IPLOTTERLAYOUT_H;
%typemap(jni) AIDA::IPlotterLayout & "jlong"
%typemap(jtype) AIDA::IPlotterLayout & "long"
%typemap(jstype) AIDA::IPlotterLayout & "hep.aida.IPlotterLayout"
%typemap(directorin, descriptor="Lhep/aida/IPlotterLayout;") AIDA::IPlotterLayout & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IPLOTTERREGION_H;
%typemap(jni) AIDA::IPlotterRegion &, AIDA::IPlotterRegion * "jlong"
%typemap(jtype) AIDA::IPlotterRegion &, AIDA::IPlotterRegion * "long"
%typemap(jstype) AIDA::IPlotterRegion &, AIDA::IPlotterRegion * "hep.aida.IPlotterRegion"
%typemap(directorin, descriptor="Lhep/aida/IPlotterRegion;") AIDA::IPlotterRegion & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IPlotterRegion;") AIDA::IPlotterRegion * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IPLOTTERSTYLE_H;
%typemap(jni) AIDA::IPlotterStyle &, AIDA::IPlotterStyle * "jlong"
%typemap(jtype) AIDA::IPlotterStyle &, AIDA::IPlotterStyle * "long"
%typemap(jstype) AIDA::IPlotterStyle &, AIDA::IPlotterStyle * "hep.aida.IPlotterStyle"
%typemap(directorin, descriptor="Lhep/aida/IPlotterStyle;") AIDA::IPlotterStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IPlotterStyle;") AIDA::IPlotterStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IPROFILE_H;
%typemap(jni) AIDA::IProfile & "jlong"
%typemap(jtype) AIDA::IProfile & "long"
%typemap(jstype) AIDA::IProfile & "hep.aida.IProfile"
%typemap(directorin, descriptor="Lhep/aida/IProfile;") AIDA::IProfile & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_IPROFILE1D_H;
%typemap(jni) AIDA::IProfile1D &, AIDA::IProfile1D * "jlong"
%typemap(jtype) AIDA::IProfile1D &, AIDA::IProfile1D * "long"
%typemap(jstype) AIDA::IProfile1D &, AIDA::IProfile1D * "hep.aida.IProfile1D"
%typemap(directorin, descriptor="Lhep/aida/IProfile1D;") AIDA::IProfile1D & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IProfile1D;") AIDA::IProfile1D * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IPROFILE2D_H;
%typemap(jni) AIDA::IProfile2D &, AIDA::IProfile2D * "jlong"
%typemap(jtype) AIDA::IProfile2D &, AIDA::IProfile2D * "long"
%typemap(jstype) AIDA::IProfile2D &, AIDA::IProfile2D * "hep.aida.IProfile2D"
%typemap(directorin, descriptor="Lhep/aida/IProfile2D;") AIDA::IProfile2D & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/IProfile2D;") AIDA::IProfile2D * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_IRANGESET_H;
%typemap(jni) AIDA::IRangeSet & "jlong"
%typemap(jtype) AIDA::IRangeSet & "long"
%typemap(jstype) AIDA::IRangeSet & "hep.aida.IRangeSet"
%typemap(directorin, descriptor="Lhep/aida/IRangeSet;") AIDA::IRangeSet & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_ISTATISTICSBOXSTYLE_H;
%typemap(jni) AIDA::IStatisticsBoxStyle & "jlong"
%typemap(jtype) AIDA::IStatisticsBoxStyle & "long"
%typemap(jstype) AIDA::IStatisticsBoxStyle & "hep.aida.IStatisticsBoxStyle"
%typemap(directorin, descriptor="Lhep/aida/IStatisticsBoxStyle;") AIDA::IStatisticsBoxStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "

%ignore AIDA_ITEXTSTYLE_H;
%typemap(jni) AIDA::ITextStyle &, AIDA::ITextStyle * "jlong"
%typemap(jtype) AIDA::ITextStyle &, AIDA::ITextStyle * "long"
%typemap(jstype) AIDA::ITextStyle &, AIDA::ITextStyle * "hep.aida.ITextStyle"
%typemap(directorin, descriptor="Lhep/aida/ITextStyle;") AIDA::ITextStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ITextStyle;") AIDA::ITextStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ITITLESTYLE_H;
%typemap(jni) AIDA::ITitleStyle &, AIDA::ITitleStyle * "jlong"
%typemap(jtype) AIDA::ITitleStyle &, AIDA::ITitleStyle * "long"
%typemap(jstype) AIDA::ITitleStyle &, AIDA::ITitleStyle * "hep.aida.ITitleStyle"
%typemap(directorin, descriptor="Lhep/aida/ITitleStyle;") AIDA::ITitleStyle & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ITitleStyle;") AIDA::ITitleStyle * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ITREE_H;
%ignore AIDA::ITree::ls;
%typemap(jni) AIDA::ITree &, AIDA::ITree * "jlong"
%typemap(jtype) AIDA::ITree &, AIDA::ITree * "long"
%typemap(jstype) AIDA::ITree &, AIDA::ITree * "hep.aida.ITree"
%typemap(directorin, descriptor="Lhep/aida/ITree;") AIDA::ITree & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ITree;") AIDA::ITree * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%typemap(javabody) AIDA::ITree %{
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public $javaclassname(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(hep.aida.$javaclassname obj) {
    if (obj instanceof $javaclassname) {
      return (obj == null) ? 0 : (($javaclassname)obj).swigCPtr;
    } else {
      long cPtr = $moduleJNI.new_$javaclassname();
      // FIXME, memory leak if Java class gets finalized, since C++ director is not freed.
      $moduleJNI.$javaclassname_director_connect(obj, cPtr, true, true);
      return cPtr;
    }
  }

  public void ls() throws IllegalArgumentException {
    throw new RuntimeException("ITree.ls() not implemented in C++");
  }

  public void ls(String arg0) throws IllegalArgumentException {
    throw new RuntimeException("ITree.ls(String) not implemented in C++");  
  }
  
  public void ls(String arg0, boolean arg1) throws IllegalArgumentException {
    throw new RuntimeException("ITree.ls(String, boolean) not implemented in C++");
  }

  public void ls(String arg0, boolean arg1, java.io.OutputStream arg2)
		throws IllegalArgumentException {
    throw new RuntimeException("ITree.ls(String, boolean, OutputStream) not implemented in C++");
  }
%}

// Extra only for aida 3.0.0 since after these are RuntimeExceptions in Java
// needs some thinking %typemap(javadirectorin) AIDA::ITree::close "xxx"

%ignore AIDA_ITREEFACTORY_H;
%typemap(jni) AIDA::ITreeFactory &, AIDA::ITreeFactory * "jlong"
%typemap(jtype) AIDA::ITreeFactory &, AIDA::ITreeFactory * "long"
%typemap(jstype) AIDA::ITreeFactory &, AIDA::ITreeFactory * "hep.aida.ITreeFactory"
%typemap(directorin, descriptor="Lhep/aida/ITreeFactory;") AIDA::ITreeFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ITreeFactory;") AIDA::ITreeFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ITUPLE_H;
%ignore AIDA::ITuple::annotation();
%ignore AIDA::ITuple::getTuple(int);
//%rename columnTypes columnTypesStringArray;

%typemap(jni) AIDA::ITuple &, AIDA::ITuple * "jlong"
%typemap(jtype) AIDA::ITuple &, AIDA::ITuple * "long"
%typemap(jstype) AIDA::ITuple &, AIDA::ITuple * "hep.aida.ITuple"
%typemap(directorin, descriptor="Lhep/aida/ITuple;") AIDA::ITuple & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ITuple;") AIDA::ITuple * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%typemap(javabody) AIDA::ITuple %{
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public $javaclassname(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(hep.aida.$javaclassname obj) {
    if (obj instanceof $javaclassname) {
      return (obj == null) ? 0 : (($javaclassname)obj).swigCPtr;
    } else {
      long cPtr = $moduleJNI.new_$javaclassname();
      // FIXME, memory leak if Java class gets finalized, since C++ director is not freed.
      $moduleJNI.$javaclassname_director_connect(obj, cPtr, true, true);
      return cPtr;
    }
  }
  
  public void fill(int arg0, byte arg1) throws IllegalArgumentException {
    throw new RuntimeException("ITuple.fill(int, byte) not implemented in C++");
  }

  public byte getByte(int arg0) throws ClassCastException {
    throw new RuntimeException("byte ITuple.getByte(int) not implemented in C++");
  }
%}

%ignore AIDA_ITUPLECOLUMN_H;
%typemap(jni) AIDA::ITupleColumn &, AIDA::ITupleColumn * "jlong"
%typemap(jtype) AIDA::ITupleColumn &, AIDA::ITupleColumn * "long"
%typemap(jstype) AIDA::ITupleColumn &, AIDA::ITupleColumn * "hep.aida.ITupleColumn"
%typemap(directorin, descriptor="Lhep/aida/ITupleColumn;") AIDA::ITupleColumn & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ITupleColumn;") AIDA::ITupleColumn * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ITUPLEENTRY_H;
%typemap(jni) AIDA::ITupleEntry &, AIDA::ITupleEntry * "jlong"
%typemap(jtype) AIDA::ITupleEntry &, AIDA::ITupleEntry * "long"
%typemap(jstype) AIDA::ITupleEntry &, AIDA::ITupleEntry * "Object"
%typemap(javain) AIDA::ITupleEntry &, AIDA::ITupleEntry * "ITupleEntry.getCPtr((hep.aida.ITupleEntry)$javainput)"
%typemap(directorin, descriptor="Lhep/aida/ITupleEntry;") AIDA::ITupleEntry & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ITupleEntry;") AIDA::ITupleEntry * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore AIDA_ITUPLEFACTORY_H;
%typemap(jni) AIDA::ITupleFactory &, AIDA::ITupleFactory * "jlong"
%typemap(jtype) AIDA::ITupleFactory &, AIDA::ITupleFactory * "long"
%typemap(jstype) AIDA::ITupleFactory &, AIDA::ITupleFactory * "hep.aida.ITupleFactory"
%typemap(directorin, descriptor="Lhep/aida/ITupleFactory;") AIDA::ITupleFactory & " *($&1_ltype)&$input = ($1_ltype) &$1; "
%typemap(directorin, descriptor="Lhep/aida/ITupleFactory;") AIDA::ITupleFactory * " *(($&1_ltype)&$input) = ($1_ltype) $1; "

%ignore cast(const std::string & className) const;

%feature("director") IAnalysisFactory;
%feature("director") IAnnotation;
%feature("director") IAxis;
%feature("director") IAxisStyle;
%feature("director") IBaseHistogram;
%feature("director") IBaseStyle;
%feature("director") IBaseTupleColumn;
%feature("director") IBorderStyle;
%feature("director") IBoxStyle;
%feature("director") IBrushStyle;
%feature("director") ICloud;
%feature("director") ICloud1D;
%feature("director") ICloud2D;
%feature("director") ICloud3D;
%feature("director") IConstants;
%feature("director") IDataPoint;
%feature("director") IDataPointSet;
%feature("director") IDataPointSetFactory;
%feature("director") IDataStyle;
%feature("director") IEvaluator;
%feature("director") IFillStyle;
%feature("director") IFilter;
%feature("director") IFitData;
%feature("director") IFitFactory;
%feature("director") IFitParameterSettings;
%feature("director") IFitResult;
%feature("director") IFitter;
%feature("director") IFunction;
%feature("director") IFunctionCatalog;
%feature("director") IFunctionFactory;
%feature("director") IGenericFactory;
%feature("director") IGridStyle;
%feature("director") IHistogram;
%feature("director") IHistogram1D;
%feature("director") IHistogram2D;
%feature("director") IHistogram3D;
%feature("director") IHistogramFactory;
%feature("director") IInfo;
%feature("director") IInfoStyle;
%feature("director") ILegendBoxStyle;
%feature("director") ILineStyle;
%feature("director") IManagedObject;
%feature("director") IMarkerStyle;
%feature("director") IMeasurement;
%feature("director") IModelFunction;
%feature("director") IPlottable;
%feature("director") IPlotter;
%feature("director") IPlotterFactory;
%feature("director") IPlotterLayout;
%feature("director") IPlotterRegion;
%feature("director") IPlotterStyle;
%feature("director") IProfile;
%feature("director") IProfile1D;
%feature("director") IProfile2D;
%feature("director") IRangeSet;
%feature("director") IStatisticsBoxStyle;
%feature("director") ITextStyle;
%feature("director") ITitleStyle;
%feature("director") ITree;
%feature("director") ITreeFactory;
%feature("director") ITuple;
%feature("director") ITupleColumn;
%feature("director") ITupleEntry;
%feature("director") ITupleFactory;


// %feature("javadowncast") IHistogram1D;

%include "AIDA/Types.h"

// misc classes
%include "AIDA/IAnnotation.h" 
%include "AIDA/IConstants.h"
//// %include "AIDA/IGenericFactory.h"
%include "AIDA/IManagedObject.h"

// histograms
%include "AIDA/IAxis.h" 
%include "AIDA/IBaseHistogram.h"
%include "AIDA/IHistogram.h" 
%include "AIDA/IHistogram1D.h" 
%include "AIDA/IHistogram2D.h" 
%include "AIDA/IHistogram3D.h" 
%include "AIDA/ICloud.h"
%include "AIDA/ICloud1D.h"
%include "AIDA/ICloud2D.h"
%include "AIDA/ICloud3D.h"
%include "AIDA/IProfile.h"
%include "AIDA/IProfile1D.h"
%include "AIDA/IProfile2D.h"
%include "AIDA/IHistogramFactory.h"

// datapoints
%include "AIDA/IDataPoint.h"
%include "AIDA/IDataPointSet.h"
%include "AIDA/IDataPointSetFactory.h"

// tree
%include "AIDA/ITree.h" 
%include "AIDA/ITreeFactory.h"

// tuple
//// %include "AIDA/IBaseTupleColumn.h" 
%include "AIDA/IFilter.h"
%include "AIDA/ITupleEntry.h" 
%include "AIDA/IEvaluator.h"
%include "AIDA/ITuple.h" 
//// %include "AIDA/ITupleColumn.h" 
%include "AIDA/ITupleFactory.h" 

// functions
%include "AIDA/IMeasurement.h"
%include "AIDA/IRangeSet.h"
%include "AIDA/IFitData.h"
%include "AIDA/IFunction.h"
%include "AIDA/IFitParameterSettings.h"
%include "AIDA/IFitResult.h"
%include "AIDA/IFitter.h"
%include "AIDA/IFitFactory.h"
%include "AIDA/IModelFunction.h"
%include "AIDA/IFunctionCatalog.h"
%include "AIDA/IFunctionFactory.h"


// styles
%include "AIDA/IBaseStyle.h"
%include "AIDA/IBrushStyle.h"
%include "AIDA/ILineStyle.h"
%include "AIDA/ITextStyle.h"
%include "AIDA/IAxisStyle.h"
////%include "AIDA/IBorderStyle.h"
%include "AIDA/IFillStyle.h"
////%include "AIDA/IBoxStyle.h"
%include "AIDA/IMarkerStyle.h"
%include "AIDA/IDataStyle.h"
%include "AIDA/IInfoStyle.h"
////%include "AIDA/ILegendBoxStyle.h"
%include "AIDA/ITitleStyle.h"
////%include "AIDA/IStatisticsBoxStyle.h"
////%include "AIDA/IGridStyle.h"
%include "AIDA/IPlotterStyle.h"
%include "AIDA/IInfo.h"

// plotter
////%include "AIDA/IPlottable.h"
%include "AIDA/IPlotterLayout.h"
%include "AIDA/IPlotterRegion.h"
%include "AIDA/IPlotter.h"
%include "AIDA/IPlotterFactory.h"

// grand factory
%include "AIDA/IAnalysisFactory.h" 

// Bootstrap for test
AIDA::IHistogram1D* getTestHistogram();
 