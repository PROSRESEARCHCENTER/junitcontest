package org.freehep.webutil.tree;

import java.util.List;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public interface TreeNode
{
   public boolean isLeaf();
   public List children();
   public String getHref();
   public String getTarget();
   public String getLabel();
   public Icon getIcon();
   public String getTitle();
   public boolean isExpanded();
   public String processHref(String href);
}
