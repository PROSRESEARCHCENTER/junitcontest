package hep.io.root.util;

import hep.io.root.RootClass;
import hep.io.root.RootMember;
import hep.io.root.RootObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


/**
 * An adaptor which converts a RootObject to a TreeModel.
 * This allows the super classes and member variables of
 * the RootObject to be viewed in a JTree. If member variables
 * are themselves RootObjects, or arrays, they can be
 * browsed in turn by "drilling down" in the tree.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootObjectTreeModel.java 8584 2006-08-10 23:06:37Z duns $
 */

/*
 * Implementation notes:
 * Since there are not already existing objects which represent all the nodes
 * in the tree, we use the TreeNode approach to building the TreeModel.
 * Consecutive requests for the same node may result in different objects, so
 * this will only work when the JTree caches the nodes, i.e. when "largeModel"
 * has not been set on the JTree.
 */
public class RootObjectTreeModel extends DefaultTreeModel
{
   /**
    * Create the RootObjectTreeModel
    * @param top The RootObject to appear at the "root" of the tree
    */
   public RootObjectTreeModel(Object top, String name)
   {
      super(RootObjectTreeNode.getNodeForValue(null, top, name, 0));
   }

   protected RootObjectTreeModel(TreeNode node)
   {
      super(node);
   }

   protected static int getIndex(TreeNode child)
   {
      return ((RootObjectTreeNode) child).index;
   }

   protected static TreeNode getNodeForChild(TreeNode parent, Object child, String name, int index)
   {
      return RootObjectTreeNode.getNodeForValue(parent, child, name, index);
   }

   static abstract class RootObjectTreeNode implements TreeNode
   {
      private String tooltip;
      private TreeNode parent;
      private int index;

      RootObjectTreeNode(TreeNode parent, int index)
      {
         this.parent = parent;
         this.index = index;
      }

      public boolean getAllowsChildren()
      {
         return true;
      }

      public TreeNode getChildAt(int p1)
      {
         throw new InternalError("getChildAt() called");
      }

      public int getChildCount()
      {
         return 0;
      }

      public int getIndex(TreeNode child)
      {
         return ((RootObjectTreeNode) child).index;
      }

      public boolean isLeaf()
      {
         return false;
      }

      public TreeNode getParent()
      {
         return parent;
      }

      public Enumeration children()
      {
         return new Enumeration()
            {
               private int n = getChildCount();
               private int i = 0;

               public boolean hasMoreElements()
               {
                  return i < n;
               }

               public Object nextElement()
               {
                  return getChildAt(i++);
               }
            };
      }

      public boolean equals(Object obj)
      {
         if (obj instanceof RootObjectTreeNode)
         {
            RootObjectTreeNode other = (RootObjectTreeNode) obj;
            if (this.index != other.index)
               return false;
            if (this.parent == null)
               return other.parent == null;
            return this.parent.equals(other.parent);
         }
         return false;
      }

      public int hashCode()
      {
         return index + ((parent == null) ? 0 : parent.hashCode());
      }

      public String toString()
      {
         return description();
      }

      static RootObjectTreeNode getNodeForValue(TreeNode parent, Object value, String name, int index)
      {
         if (value == null)
            return new RootSimpleValue(parent, "null", name, index);
         else if (value instanceof List)
            return new RootListNode(parent, (List) value, name, index);
         else if (value instanceof Map)
             return new RootMapNode(parent, (Map) value, name, index);
         else if (value.getClass().isArray())
            return new RootArrayNode(parent, value, name, index);
         else if (value instanceof RootObject)
            return new RootObjectNode(parent, (RootObject) value, name, index);
         else
            return new RootSimpleValue(parent, value, name, index);
      }

      void setToolTip(String value)
      {
         tooltip = value;
      }

      abstract String description();

      String toolTip()
      {
         return tooltip;
      }
   }

   static class RootArrayNode extends RootObjectTreeNode
   {
      private Object array;
      private String name;

      RootArrayNode(TreeNode parent, Object array, String name, int index)
      {
         super(parent, index);
         this.array = array;
         this.name = name;
      }

      public TreeNode getChildAt(int index)
      {
         return getNodeForValue(this, Array.get(array, index), "[" + index + "]", index);
      }

      public int getChildCount()
      {
         return Array.getLength(array);
      }

      String description()
      {
         return name + " (Array)";
      }
   }

   static class RootListNode extends RootObjectTreeNode
   {
      private List list;
      private String name;

      RootListNode(TreeNode parent, List list, String name, int index)
      {
         super(parent, index);
         this.list = list;
         this.name = name;
      }

      public TreeNode getChildAt(int index)
      {
         return getNodeForValue(this, list.get(index), "[" + index + "]", index);
      }

      public int getChildCount()
      {
         return list.size();
      }

      String description()
      {
         return name + " (List)";
      }
   }
   
   static class RootMapNode extends RootObjectTreeNode
   {
      private List list;
      private String name;

      RootMapNode(TreeNode parent, Map map, String name, int index)
      {
         super(parent, index);
         this.list = new ArrayList(map.entrySet());
         this.name = name;
      }

      public TreeNode getChildAt(int index)
      {
         return new RootMapEntryNode(this, (Map.Entry) list.get(index), "[" + index + "]", index);
      }

      public int getChildCount()
      {
         return list.size();
      }

      String description()
      {
         return name + " (Map)";
      }
   }
   static class RootMapEntryNode extends RootObjectTreeNode
   {
      private Map.Entry entry;
      private String name;

      RootMapEntryNode(TreeNode parent, Map.Entry entry, String name, int index)
      {
         super(parent, index);
         this.entry = entry;
         this.name = name;
      }

      public TreeNode getChildAt(int index)
      {
         if (index == 0) return getNodeForValue(this, entry.getKey (), "key", index);
         else           return getNodeForValue(this, entry.getValue(), "value", index);
      }

      public int getChildCount()
      {
         return 2;
      }

      String description()
      {
         return name + " (MapEntry)";
      }
   }

   static class RootObjectNode extends RootSubObject
   {
      private String name;

      RootObjectNode(TreeNode parent, RootObject obj, String name, int index)
      {
         super(parent, obj, obj.getRootClass(), index);
         this.name = name;
      }

      String description()
      {
         return name + " (" + super.description() + ")";
      }
   }

   static class RootSimpleValue extends RootObjectTreeNode
   {
      private Object value;
      private String name;

      RootSimpleValue(TreeNode parent, Object value, String name, int index)
      {
         super(parent, index);
         this.value = value;
         this.name = name;
      }

      public boolean isLeaf()
      {
         return true;
      }

      String description()
      {
         return name + " = " + value;
      }
   }

   static class RootSubObject extends RootObjectTreeNode
   {
      private RootClass klass;
      private RootObject obj;

      RootSubObject(TreeNode parent, RootObject obj, RootClass klass, int index)
      {
         super(parent, index);
         this.obj = obj;
         this.klass = klass;
      }

      public TreeNode getChildAt(int index)
      {
         RootClass[] superClasses = klass.getSuperClasses();
         if (index < superClasses.length)
         {
            return new RootSubObject(this, obj, superClasses[index], index);
         }
         else
         {
            RootMember member = klass.getMembers()[index - superClasses.length];

            // TODO: Some better way to tell if object is composite
            Object value = null;
            try
            {
               value = member.getValue(obj);
            }
            catch (Exception x)
            {
               x.printStackTrace();
               value = x.getMessage(); 
            }
            RootObjectTreeNode node = getNodeForValue(this, value, member.getName(), index);
            node.setToolTip(member.getComment());
            return node;
         }
      }

      public int getChildCount()
      {
         int n = klass.getSuperClasses().length;
         n += klass.getMembers().length;
         return n;
      }

      String description()
      {
         return "Class " + klass.getClassName();
      }
   }
}
