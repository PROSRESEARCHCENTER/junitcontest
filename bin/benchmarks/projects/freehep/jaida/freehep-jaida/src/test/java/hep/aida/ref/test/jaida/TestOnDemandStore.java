package hep.aida.ref.test.jaida;

import hep.aida.IAnalysisFactory;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.dev.IDevTree;
import hep.aida.dev.IOnDemandStore;
import hep.aida.dev.IStore;
import hep.aida.dev.IStoreFactory;
import hep.aida.ref.tree.TreeObjectAlreadyExistException;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.freehep.util.FreeHEPLookup;

/**
 *
 * @author tonyj
 * @version $Id: TestOnDemandStore.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TestOnDemandStore extends TestCase
{
   private ITree tree;
   private IStoreFactory storeFactory;
   
   public TestOnDemandStore(String name)
   {
      super(name);
   }
   public void testGoodPath()
   {
      IManagedObject obj = tree.find("f0/f1/f2/f3/1");
      assertTrue(obj instanceof MyObject);
   }
   public void testBadPath()
   {
      try
      {
         IManagedObject obj = tree.find("f0/f1/f6/f3/1");
         assertTrue(false); // Should not get here
      }
      catch (IllegalArgumentException x) { }
   }
   public void testAcrossMountPoint() throws Exception
   {
      IAnalysisFactory af = IAnalysisFactory.create();
      ITree top = af.createTreeFactory().create();
      top.mount("x", tree, "/");
      //top.ls("/", true);
      IManagedObject obj = top.find("/x/f3/f2/f1/f0/3");
      //System.out.println("Found Object: "+obj.name());
      assertTrue(obj instanceof MyObject);
      top.close();
   }
   public void testLS()
   {
      String[] names = tree.listObjectNames("/",true);
      int nNodes = 0;
      int nFolders = 1;
      for (int level=0; level<=MAX_DEPTH; level++)
      {
         nNodes += nFolders * (level == MAX_DEPTH ? DIR_SIZE : DIR_SIZE+DIR_SIZE);
         nFolders *= DIR_SIZE;
      }
      assertEquals(nNodes,names.length);
   }
   protected void tearDown() throws java.lang.Exception
   {
      tree.close();
      FreeHEPLookup.instance().remove(storeFactory);
      super.tearDown();
   }
   
   protected void setUp() throws java.lang.Exception
   {
      super.setUp();
      storeFactory = new MyStoreFactory();
      FreeHEPLookup.instance().add(storeFactory);
      
      IAnalysisFactory af = IAnalysisFactory.create();
      tree = af.createTreeFactory().create("Test Store","test");
   }

   private class MyStoreFactory implements IStoreFactory
   {
      public IStore createStore()
      {
         return new MyOnDemandStore();
      }
      public String description()
      {
         return "Test Store";
      }
      public boolean supportsType(String type)
      {
         return "test".equals(type);
      }
   }
   private class MyOnDemandStore implements IOnDemandStore
   {
      
      public void close() throws IOException
      {
      }
      
      public void commit(IDevTree tree, Map options) throws IOException
      {
         throw new UnsupportedOperationException();
      }
      
      public boolean isReadOnly()
      {
         return true;
      }
      
      public void read(IDevTree tree, String path) throws IllegalArgumentException, IOException
      {
         StringTokenizer st = new StringTokenizer(path,"/");
         int depth = st.countTokens();
         // Comment out next line to see weird path passed in (begins with three ///'s)
         //System.out.println("path="+path+",  count="+depth);
         // It is the stores responsibilty to make sure this folder should exist
         if (depth > MAX_DEPTH) throw new IllegalArgumentException("Bad path: "+path);
         while (st.hasMoreTokens()) 
         {
            String token = st.nextToken();
            if (token.charAt(0) != 'f') throw new IllegalArgumentException("Bad path: "+path);
            char n = token.charAt(1);
            if (n < '0' || n > '3') throw new IllegalArgumentException("Bad path: "+path);
         }
         createDummyFolder(tree,path,depth < MAX_DEPTH);
         tree.hasBeenFilled(path);
      }
      
      public void read(IDevTree tree, Map options, boolean readOnly, boolean createNew) throws IOException
      {
         createDummyFolder(tree,"/",true);
         tree.hasBeenFilled("/");
      }
      private void createDummyFolder(IDevTree tree, String path, boolean withSubFolders)
      {
         for (int i=0; i<DIR_SIZE; i++)
         {
             //try {
                tree.add(path, new MyObject(i));
             //} catch(TreeFolderDoesNotExistException tfe) {
             //    tree.mkdirs(path);
             //    tree.add(path, new MyObject(i));
             //} catch(TreeObjectAlreadyExistException toe) {
             //    System.out.println("WARNING: "+toe.getMessage());
             //}
         }
         if (!path.endsWith("/")) path += "/";
         if (withSubFolders)
         {
            for (int i=0; i<DIR_SIZE; i++)
            {
                try {
                   tree.mkdirs(path + "f"+i);
                } catch(TreeObjectAlreadyExistException toe) {
                   System.out.println("WARNING: "+toe.getMessage());
                }
            }
         }
      }
   }
   private class MyObject implements IManagedObject
   {
      private int index;
      MyObject(int i)
      {
         index = i;
      }
      public String name()
      {
         return String.valueOf(index);
      }
      
      public String type() {
          return "myType";
      }
      
   }
   private final static int MAX_DEPTH = 4;
   private final static int DIR_SIZE = 4;
   
   public static void main(String[] args) {
       TestOnDemandStore test = new TestOnDemandStore("Test IOnDemandStore");
       
       try {
          test.setUp();
          test.testGoodPath();
          //test.testBadPath();
          test.testAcrossMountPoint();
          
          System.out.println("\nRoot directory:");
          test.tree.ls("/");
       
          System.out.println("\nDirectory: /f1");
          test.tree.ls("/f1");
       
          System.out.println("\nDirectory: /f1/f3/f1");
          test.tree.ls("/f1/f3/f1");
       
          System.out.println("\nDirectory: /f2/f3/f1");
          test.tree.ls("/f2/f3/f1");
       
          System.out.println("\nDirectory: /f3/f2/f1/f0");
          test.tree.ls("/f3/f2/f1/f0");
       
          test.tearDown();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}
