package org.freehep.swing;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * Combo box with an ability to disable some of its items.
 * Disabled items are grayed out and cannot be selected.
 * Adding or removing items to this combo box enables all items.
 *
 * @author onoprien
 */
public class JComboBoxWithDisabledItems<E> extends JComboBox<E> {
  
// -- Private parts : ----------------------------------------------------------
 
  private boolean[] disabledIndices;
  
// -- Construction and setters : -----------------------------------------------
 
  public JComboBoxWithDisabledItems() {
   super.setRenderer(new Renderer());
  }
  
  /**
   * Specifies a set of disabled items for this combo box.
   * Items not listed are enabled.
   * Calling this method with no parameters enables all items.
   * @param items Items to be disabled.
   */
  public void setDisabled(E... items) {
    if (items.length == 0) {
      disabledIndices = null;
    } else {
      List<E> itemList = Arrays.asList(items);
      int n = this.getItemCount();
      disabledIndices = new boolean[n];
      for (int i = 0; i < n; i++) {
        disabledIndices[i] = itemList.contains(getItemAt(i));
      }
    }
  }
  
  /**
   * Specifies a set of disabled items for this combo box.
   * Items not in the specified set are enabled.
   * Calling this method with an empty set enables all items.
   * @param items Items to be disabled.
   */
  public void setDisabled(Set<? extends E> items) {
    if (items.isEmpty()) {
      disabledIndices = null;
    } else {
      int n = this.getItemCount();
      disabledIndices = new boolean[n];
      for (int i = 0; i < n; i++) {
        disabledIndices[i] = items.contains(getItemAt(i));
      }
    }
  }


// -- Handling selection : -----------------------------------------------------
 
  @Override
  public void setSelectedIndex(int index) {
    if (isItemEnabled(index)) {
      super.setSelectedIndex(index);
    }
  }
  
  
// -- Adding/Removing elements : -----------------------------------------------

  @Override
  public void removeAllItems() {
    disabledIndices = null;
    super.removeAllItems();
  }

  @Override
  public void removeItemAt(int anIndex) {
    disabledIndices = null;
    super.removeItemAt(anIndex);
//    if (disabledIndices != null && disabledIndices.length > getItemCount()) {
//      int n = disabledIndices.length - 1;
//      boolean[] temp = new boolean[n];
//      System.arraycopy(disabledIndices, 0, temp, 0, anIndex);
//      if (anIndex < n) {
//        System.arraycopy(disabledIndices, anIndex + 1, temp, anIndex, n - anIndex);
//      }
//      disabledIndices = temp;
//    }
  }

  @Override
  public void removeItem(Object anObject) {
    disabledIndices = null;
    super.removeItem(anObject);
  }

  @Override
  public void insertItemAt(E item, int index) {
    disabledIndices = null;
    super.insertItemAt(item, index);
  }

  @Override
  public void addItem(E item) {
    disabledIndices = null;
    super.addItem(item);
  }

  
  
// -- Customized renderer : ----------------------------------------------------
 
  private class Renderer extends BasicComboBoxRenderer {
 
   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
 
    if (isSelected) {
     setBackground(list.getSelectionBackground());
     setForeground(list.getSelectionForeground());
    } else {
     setBackground(list.getBackground());
     setForeground(list.getForeground());
    }
    if (!isItemEnabled(index)) {
     setBackground(list.getBackground());
     setForeground(UIManager.getColor("Label.disabledForeground"));
    }
    setFont(list.getFont());
    setText((value == null) ? "" : value.toString());
    return this;
   }
  }
  
  
// -- Local methods : ----------------------------------------------------------
  
  private boolean isItemEnabled(int index) {
    return disabledIndices == null || index<0 || index>=disabledIndices.length || !disabledIndices[index];
  }

// -- Example of use : ---------------------------------------------------------

  public static void main(String... args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("ComboBox With Disabled Items Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(panel);
        
        JComboBoxWithDisabledItems<String> comboBox = new JComboBoxWithDisabledItems<>();
        comboBox.addItem("One");
        comboBox.addItem("Two");
        comboBox.addItem("Three");
        comboBox.addItem("Four");
        comboBox.setDisabled("Two", "Four");
        
        panel.add(comboBox);
        frame.pack();
        frame.setVisible(true);        
      }
    });
  }

}

