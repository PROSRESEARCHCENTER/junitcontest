package org.freehep.util;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version comparisons.
 * <p>
 * Note that instance methods {@link #versionNumberCompare versionNumberCompare(String, String)}
 * and {@link #compare compare(Object, Object)} work with
 * older FreeHEP style versions in <tt>N.N.N.N</tt> format. Static methods of this class and the
 * {@link Version} class expect file names to conform to maven-like standard format:
 * <pre>
 * {name}-{major}[.{minor}][.{incremental}][-{qualifier}][-{build}]
 * </pre>
 * Major, minor, incremental versions and build number should only contain digits.
 * The qualifier can contain any characters.
 * Underscore can be used as a separator instead of a hyphen everywhere.
 * The qualifier can also be separated from the rest of the version string  by a dot instead 
 * of a hyphen, but this is discouraged due to greater chances of ambiguity.
 * <p>
 * You can use {@link #getComparator()} method to obtain a String {@link Comparator} instance that
 * works with version strings in standard format.
 * <p>
 * Note that splitting file name into artifact name and version can be
 * ambiguous if the version string does not contain minor version. The algorithm
 * will try to put as much of the original string as possible into the version,
 * so "core-util-5-3", for example, will be interpreted as artifact "core-util"
 * version 5.0.0 build 3, not as artifact "core-util-5" version 3.0.0.
 * 
 * @author Tony Johnson
 * @author onoprien
 * @version $Id: VersionComparator.java 16217 2014-11-29 20:17:55Z onoprien $
 */
public class VersionComparator implements Comparator {
  
//  public enum Format {
//    /** Standard format. */
//    STANDARD, 
//    /** Loose format. */
//    LOOSE}
  
// -- Private parts : ----------------------------------------------------------

  private static String[] special = {"SNAPSHOT", "alpha", "beta", "rc"};
  private static String pattern = "\\.+";
  
  /** Extracts version data from file names (without extension) in standard format. */
  private static Pattern pFileName = Pattern.compile("^(.+?)[-_](\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:[-_.](.+?))??(?:[-_.](\\d+))?$");
  /** Extracts version data from version strings in standard format. */
  private static Pattern pVersion = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:[-_.](.+?))??(?:[-_.](\\d+))?$");
  /** Matches qualifiers ending with what can be interpreted as build number. */
  private static Pattern pTail = Pattern.compile("(.+[-_.])?\\d+$");

//  /** Extracts version data from file names (without extension) in standard format. */
//  private static Pattern pFileName = Pattern.compile("^(.+?)[-_](\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:[-_.]([^-_]+?))??(?:[-_.](\\d+))?$");
//  /** Extracts version data from version strings in standard format. */
//  private static Pattern pVersion = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:[-_.]([^-_]+?))??(?:[-_.](\\d+))?$");


// -- Static methods that work with standard format version strings : ----------

  /**
   * Strips version from a file name.
   * 
   * @param fileName File name without extension in <pre>
   *        {name}-{major}[.{minor}][.{incremental}][-{qualifier}][-{build}]</pre>format.
   * @return <pre>{name}</pre>
   * @throws IllegalArgumentException if the file name is not in the required format.
   */
  static public String stripVersion(String fileName) {
    Matcher m = pFileName.matcher(fileName);
    if (m.matches()) {
      return m.group(1);
    } else {
      throw new IllegalArgumentException("Unknown version format: "+ fileName);
    }
  }
  
//  static public String stripVersion(String fileName, Format format) {
//    switch (format) {
//      case LOOSE:
//        try {
//          return stripVersion(fileName);
//        } catch (IllegalArgumentException x) {
//          Matcher m = pFileNameLoose.matcher(fileName);
//          if (m.matches()) {
//            return m.group(1);
//          } else {
//            throw new IllegalArgumentException("Unknown version format: " + fileName);
//          }
//        }
//      case STANDARD:
//      default:
//        return stripVersion(fileName);
//    }
//  }

  /**
   * Strips artifact name from a file name, leaving only version string.
   * 
   * @param fileName File name without extension in <pre>
   *        {name}-{major}[.{minor}][.{incremental}][-{qualifier}][-{build}]</pre>format.
   * @return <pre>{major}[.{minor}][.{incremental}][-{qualifier}][-{build}]</pre>
   * @throws IllegalArgumentException if the file name is not in the required format.
   */
  static public String stripName(String fileName) {
    Matcher m = pFileName.matcher(fileName);
    if (m.matches()) {
      int i = m.group(1).length();
      return fileName.substring(i+1);
    } else {
      throw new IllegalArgumentException("Unknown version format: "+ fileName);
    }
  }

  /**
   * Extracts version from a file name.
   * @param fileName File name without extension in <pre>
   *        {name}-{major}[.{minor}][.{incremental}][-{qualifier}][-{build}]</pre>format.
   * @throws IllegalArgumentException if the file name is not in the required format.
   */
  static public Version getVersionFromFileName(String fileName) {
    Matcher m = pFileName.matcher(fileName);
    if (m.matches()) {
      try {
        return new Version(m.group(2), m.group(3), m.group(4), m.group(5), m.group(6));
      } catch (Exception x) {
        throw new IllegalArgumentException("Unknown version format: "+ fileName);
      }
    } else {
      throw new IllegalArgumentException("Unknown version format: "+ fileName);
    }
  }

  /**
   * Parses a string that contain version number.
   * @param version Version string in <pre>
   *        {major}[.{minor}][.{incremental}][-{qualifier}][-{build}]</pre>format.
   * @throws IllegalArgumentException if the argument is not in the required format.
   */
  static public Version getVersion(String version) {
    Matcher m = pVersion.matcher(version);
    if (m.matches()) {
      try {
        return new Version(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5));
      } catch (Exception x) {
        throw new IllegalArgumentException("Unknown version format: "+ version);
      }
    } else {
      throw new IllegalArgumentException("Unknown version format: "+ version);
    }
  }

  /**
   * Compares versions specified by the two arguments.
   * The version strings should be in the standard format.
   * If minor version, incremental version, or the build number are not present in the argument,
   * they are assumed to be zero. A version without qualifier is assumed to be newer than 
   * a version with a qualifier and the same major, minor, and incremental version numbers. 
   * Qualifiers are compared as strings. 
   * 
   * @return A positive, zero, or negative value if the first version is newer than, equal, or
   *         older than the second version, respectively.
   * @throws IllegalArgumentException if any of the version strings is not in the required format.
   */
  static public int compareVersion(String version1, String version2) {
    return getVersion(version1).compareTo(getVersion(version2));
  }
  
  /**
   * Returns a comparator that orders version strings using the algorithm implemented by the
   * static {@link #compareVersion} method. If one of the strings is not in the standard format, it will be considered
   * to be bigger than the other. If both strings are not in the standard format, they are 
   * compared as simple strings ignoring case.
   */
  static public Comparator<String> getComparator() {
    return new Comparator<String>() {
      public int compare(String version1, String version2) {
        try {
          return compareVersion(version1, version2);
        } catch (IllegalArgumentException x) {
          try {
            getVersion(version1);
            return -1;
          } catch (IllegalArgumentException xx) {
            try {
              getVersion(version2);
              return 1;
            } catch (IllegalArgumentException xxx) {
              return String.CASE_INSENSITIVE_ORDER.compare(version1, version2);
            }
          }
        }
      }
    };
  }
  
  
// -- Methods that work with version strings in old format : -------------------

  /**
   * Compares two version numbers of the form 1.2.3.4
   *
   * @return >0 if v1>v2, <0 if v1<v2 or 0 if v1=v2
   * @deprecated The older FreeHEP style version format should no longer be used since
   *             it is incompatible with modern version handling tools.
   */
  @Deprecated
  public int versionNumberCompare(String v1, String v2) throws NumberFormatException {
    String[] t1 = replaceSpecials(v1).split(pattern);
    String[] t2 = replaceSpecials(v2).split(pattern);
    int maxLength = Math.max(t1.length, t2.length);
    int i = 0;
    for (; i < maxLength; i++) {
      int i1 = i < t1.length ? Integer.parseInt(t1[i]) : 0;
      int i2 = i < t2.length ? Integer.parseInt(t2[i]) : 0;

      if (i1 == i2) {
        continue;
      }
      return i1 - i2;
    }
    return 0;
  }

  private String replaceSpecials(String in) {
    in = in.replaceAll("-", ".");
    for (int i = 0; i < special.length; i++) {
      int j = -special.length + i;
      in = in.replaceAll(special[i], "." + j + ".");
    }
    return in;
  }

  /**
   * @deprecated The older FreeHEP style version format should no longer be used since
   *             it is incompatible with modern version handling tools.
   */
  @Deprecated
  @Override
  public int compare(Object obj, Object obj1) {
    return versionNumberCompare(obj.toString(), obj1.toString());
  }

  
// -- Version class : ----------------------------------------------------------

  /**
   * Encapsulates version number and provides access to its components.
   */
  final static public class Version implements Comparable<Version> {
    
    final private int major;
    final private int minor;
    final private int incremental;
    final private String qualifier;
    final private int build;
    
    Version(String major, String minor, String incremental, String qualifier, String build) {
      this.major = Integer.parseInt(major);
      this.minor = minor == null ? 0 : Integer.parseInt(minor);
      this.incremental = incremental == null ? 0 : Integer.parseInt(incremental);
      this.qualifier = qualifier;
      this.build = build == null ? 0 : Integer.parseInt(build);
    }
    
    Version(int major, int minor, int incremental, String qualifier, int build) {
      this.major = major;
      this.minor = minor;
      this.incremental = incremental;
      this.qualifier = qualifier;
      this.build = build;
    }
    
    /** Returns major version. */
    public int major() {return major;}
    
    /** Returns minor version (or 0 if there is no minor version). */
    public int minor() {return minor;}
    
    /** Returns incremental version (or 0 if there is no incremental version). */
    public int incremental() {return incremental;}
    
    /** Returns qualifier (or <tt>null</tt> if there is no qualifier. */
    public String qualifier() {return qualifier;}
    
    /** Returns build number (or 0 if there is no build number). */
    public int build() {return build;}

    @Override
    /** 
     * Returns positive, zero, or negative value if this version is newer than, equal, or
     * older than the other version, respectively.
     */
    public int compareTo(Version other) {
      
      if (major != other.major) return major - other.major;
      if (minor != other.minor) return minor - other.minor;
      if (incremental != other.incremental) return incremental - other.incremental;
   
      if (qualifier == null) {
        if (other.qualifier != null) return 1;
      } else {
        if (other.qualifier == null) {
          return -1;
        } else {
          int out = qualifier.compareTo(other.qualifier);
          if (out != 0) return out;
        }
      }

      return build - other.build;
    }

    @Override
    public int hashCode() {
      int hash = (build << 24) | (incremental << 16) | (minor << 8) | major; 
      return qualifier == null ? hash : hash ^ qualifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (! (obj instanceof Version)) return false;
      Version other = (Version)obj;
      return ( major == other.major && minor == other.minor && incremental == other.incremental && build == other.build &&
              ((qualifier == null && other.qualifier == null) || (qualifier != null && qualifier.equals(other.qualifier))) );
    }

    /**
     * Returns a canonical string representation of this version in <pre>
     * {major}.{minor}.{incremental}[-{qualifier}][-{build}]</pre>format.
     */
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder(String.valueOf(major));
      sb.append('.').append(String.valueOf(minor)).append('.').append(String.valueOf(incremental));
      boolean needBuild = build != 0;
      if (qualifier != null) {
        sb.append('-').append(qualifier);
        needBuild = needBuild || pTail.matcher(qualifier).matches();
      }
      if (needBuild) {
        sb.append('-').append(String.valueOf(build));
      }
      return sb.toString();
    }
    
  }
}