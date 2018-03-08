package hep.aida.ref.tree;

import hep.aida.ref.AidaUtils;

import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * A path is responsible for tokenizing string paths, and dealing with
 * special conventions such as "." and "..".
 * @author tonyj
 * @version $Id: Path.java 8584 2006-08-10 23:06:37Z duns $
 */
class Path
{ 
   private final static char separatorChar = '/';
   private final static String separatorString = new String(new char[] { separatorChar });
   private Stack stack;
   /**
    * Create an empty Path
    */
   Path()
   {
      stack = new Stack();
   }
   /**
    * Create an new path from a start point and a relative or absolute path.
    */
   Path(Path start, String path) {
       int pos = 0;
       if (path == null) path="";
       int l = path.length();
       if (l>0 && path.charAt(0) == separatorChar) {
           stack = new Stack();
           pos++;
       }
       else stack = (Stack) start.stack.clone();
       
       StringTokenizer tokenizer = new StringTokenizer(path, "/");
       while (tokenizer.hasMoreTokens()) {
           String token = tokenizer.nextToken();
           while (token.endsWith("\\")) {
               token = token.substring(0, token.length()-1) + "/";
               if (tokenizer.hasMoreTokens()) token = token  + tokenizer.nextToken();
           }
           if      (token.equals(".")) continue;
           else if (token.equals("")) continue;
           else if (token.equals("..")) {
               if (stack.isEmpty()) throw new IllegalArgumentException("Hit rock bottom");
               stack.pop();
           }
           else stack.push(token);
       }
       
      /*
      for (;pos<l;)
   {
         int next = path.indexOf(separatorChar,pos);
         if (next < 0) next = path.length();
       
         String token = path.substring(pos,next);
         pos = next+1;
         if      (token.equals(".")) continue;
         else if (token.equals("")) continue;
      else if (token.equals(".."))
         {
            if (stack.isEmpty()) throw new IllegalArgumentException("Hit rock bottom");
            stack.pop();
         }
         else stack.push(token);
      }
       */
   }
   public String toString()
   {
      StringBuffer b = new StringBuffer();
      b.append(separatorChar);
      if (!stack.isEmpty())
      {
         for (int i=0;;)
         {
            String token = AidaUtils.modifyName((String) stack.get(i)); 
            b.append(token);
            if (++i == stack.size()) break;
            b.append(separatorChar);
         }
      }
      return b.toString();
   }
   public String toString(int start, int stop)
   {
      if (start < 0 || start > stop || stop > size())
          throw new IllegalArgumentException("PATH: Wrong START or STOP points:  "+start+"   "+stop);
      StringBuffer b = new StringBuffer();
      b.append(separatorChar);
      if (!stack.isEmpty())
      {
         for (int i=start; i<size();)
         {
            String token = AidaUtils.modifyName((String) stack.get(i)); 
            b.append(token);
            if (++i >= stop) break;
            b.append(separatorChar);
         }
      }
      return b.toString();
   }
   Path parent()
   {
      return new Path(this,"..");
   }
   String getName()
   {
      return stack.isEmpty() ? separatorString : AidaUtils.modifyName((String) stack.peek());
   }
   Iterator iterator()
   {
      return stack.iterator();
   }
   int size()
   {
      return stack.size();
   }
   String[] toArray()
   {
      String[] result = new String[stack.size()];
      stack.copyInto(result);
      return result;
   }
   String[] toArray(int depth)
   {
      String[] result = new String[depth];
      for (int i=0; i<depth; i++) result[i] = (String) stack.get(i);
      return result;
   }
   String[] toArray(String item)
   {
      String[] result = new String[stack.size()+1];
      stack.copyInto(result);
      result[stack.size()] = item;
      return result;
   }
   
   public static void main(String[] args) {
       String currentString = "/Dir0";
       String extraString = "Hist/dir1";
       
       Path current = new Path(null, currentString);
       Path full = new Path(current, extraString);
       
       System.out.println("Current="+current.toString()+",  extra="+extraString+", full="+full.toString());
   }
}
