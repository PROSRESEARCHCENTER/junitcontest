package org.freehep.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.freehep.util.VersionComparator.Version;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author onoprien
 */
public class VersionComparatorTest {
  
  static VersionComparator vc;
  static ArrayList<TestCase> tests;
  
  public VersionComparatorTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    
    vc = new VersionComparator();
    
    tests = new ArrayList<>(50); // test cases in increasing version order
    
    tests.add(new TestCase("freehep-jminuit-jdk1.4-1.0", "freehep-jminuit-jdk1.4", new Version(1, 0, 0, null , 0)));
    tests.add(new TestCase("jas-jython-1.2.6-SNAPSHOT", "jas-jython", new Version(1, 2, 6, "SNAPSHOT", 0)));
    tests.add(new TestCase("jas-jython-1.2.6-beta.46", "jas-jython", new Version(1, 2, 6, "beta", 46)));
    tests.add(new TestCase("jre8-1.2.6_5", "jre8", new Version(1, 2, 6, null, 5)));
    tests.add(new TestCase("jre-1.8.0_20-b26", "jre", new Version(1, 8, 0, "20-b26", 0)));
    tests.add(new TestCase("openide-lookup-1.9-patched-1.0", "openide-lookup", new Version(1, 9, 0, "patched-1", 0)));
    tests.add(new TestCase("javahelp-2.0.02", "javahelp", new Version(2, 0, 2, null, 0)));
    tests.add(new TestCase("hib-com-ann-4.0.Inter.Test.1.3", "hib-com-ann", new Version(4, 0, 0, "Inter.Test.1", 3)));
    tests.add(new TestCase("hib-com-ann-4.0.1.Final", "hib-com-ann", new Version(4, 0, 1, "Final", 0)));
    tests.add(new TestCase("core-util-5-3", "core-util", new Version(5, 0, 0, null, 3)));
    tests.add(new TestCase("optimizers-20020927.0.0.0.0", "optimizers", new Version(20020927, 0, 0, "0", 0)));
    tests.add(new TestCase("optimizers-20020927", "optimizers", new Version(20020927, 0, 0, null, 0)));
    
  }

  /**
   * Test of stripVersion method, of class VersionComparator.
   */
  @Test
  public void testStripVersion() {
    for (TestCase test : tests) {
      assertEquals("for "+ test.fileName, test.name, VersionComparator.stripVersion(test.fileName));
    }
  }

  /**
   * Test of getVersionFromFileName method, of class VersionComparator.
   */
  @Test
  public void testGetVersionFromFileName() {
    for (TestCase test : tests) {
      assertEquals("for "+ test.fileName, test.version, VersionComparator.getVersionFromFileName(test.fileName));
    }
  }

  /**
   * Test of getVersion and stripName methods, of class VersionComparator.
   */
  @Test
  public void testGetVersion() {
    for (TestCase test : tests) {
      assertEquals("for "+ test.fileName, test.version, VersionComparator.getVersion(VersionComparator.stripName(test.fileName)));
    }
  }

  /**
   * Test of compareVersion method, of class VersionComparator.
   */
  @Test
  public void testCompareVersion() {
    int n = tests.size();
    ArrayList<String> v = new ArrayList<>(n);
    for (TestCase test : tests) {
      v.add(VersionComparator.stripName(test.fileName));
    }
    for (int i=0; i<n; i++) {
      String vCanonI = tests.get(i).version.toString();
      assertTrue(v.get(i)+" == "+ vCanonI, VersionComparator.compareVersion(v.get(i), vCanonI) == 0);
      for (int j=0; j<n; j++) {
        String vCanonJ = tests.get(j).version.toString();
        if (i < j) {
          assertTrue(v.get(i)+" <= "+ vCanonJ, VersionComparator.compareVersion(v.get(i), vCanonJ) <= 0);
        } else if (i > j) {
          assertTrue(v.get(i)+" >= "+ vCanonJ, VersionComparator.compareVersion(v.get(i), vCanonJ) >= 0);
        }
      }
    }
  }

  /**
   * Test of getComparator method, of class VersionComparator.
   */
  @Test
  public void testGetComparator() {
    int n = tests.size();
    String[] vOriginal = new String[n];
    for (int i=0; i<n; i++) {
      vOriginal[i] = VersionComparator.stripName(tests.get(i).fileName);
    }
    ArrayList<String> vCopy = new ArrayList<>(Arrays.asList(vOriginal));
    Collections.shuffle(vCopy);
    Collections.sort(vCopy, VersionComparator.getComparator());
    assertArrayEquals(vOriginal, vCopy.toArray());
  }

  /**
   * Test of versionNumberCompare method, of class VersionComparator.
   */
  @Test
  public void testVersionNumberCompare() {
    assertTrue(vc.compare("1", "2") < 0);
    assertTrue(vc.compare("2", "1") > 0);
    assertTrue(vc.compare("2", "2") == 0);
    assertTrue(vc.compare("2.1", "2.1") == 0);
    assertTrue(vc.compare("2.....1", "2.1") == 0);
    assertTrue(vc.compare("2.1.0", "2.1") == 0); // Correct?
    assertTrue(vc.compare("2.1.1", "2.1") > 0);
    assertTrue(vc.compare("2.1.1", "2.1.2") < 0);
    assertTrue(vc.compare("2.1.1rc1", "2.1.1") < 0);
    assertTrue(vc.compare("2.1.1rc1", "2.1.1rc2") < 0);
    assertTrue(vc.compare("2.1.1rc1", "2.1.1rc1") == 0);
    assertTrue(vc.compare("2.1.1beta1", "2.1.1rc1") < 0);
    assertTrue(vc.compare("2.1.1alpha1", "2.1.1beta1") < 0);
    assertTrue(vc.compare("2.0.4-SNAPSHOT", "2.0.4") < 0);
  }
  
  static class TestCase {
    String fileName;
    String name;
    Version version;
    TestCase(String fileName, String name, Version version) {
      this.fileName = fileName;
      this.name = name;
      this.version = version;
    }
  }
  
}
