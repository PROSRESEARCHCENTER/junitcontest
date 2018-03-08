package jas.plot;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class EditableLabel extends JComponent implements JASPlotMouseListener, HasPopupItems {

    public EditableLabel(final String text, final String prefix, final int align) {
        sourceText = text;
        this.prefix = prefix;
        converter = new LabelTextConverter();
        setLayout(new BorderLayout());
        label = new JLabel(converter.convert(text), align);
        label.setVerticalAlignment(JLabel.TOP);
        label.setForeground(getForeground());
        add(label, BorderLayout.CENTER);
    }

    public EditableLabel(final String text, final String prefix) {
        this(text, prefix, JLabel.CENTER);
    }

    public void addActionListener(ActionListener al) {
        if (listener == null) {
            listener = new Vector();
        }
        listener.addElement(al);
    }

    public void removeActionListener(ActionListener al) {
        listener.removeElement(al);
    }

    protected void fireActionPerformed() {
        if (listener != null) {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_FIRST, null);
            Enumeration e = listener.elements();
            while (e.hasMoreElements()) {
                ((ActionListener) e.nextElement()).actionPerformed(event);
            }
        }
    }

    void edit() {
        textField = new JTextField(sourceText);
        textField.setFont(getFont());
        textField.setBorder(null);
        textField.setHorizontalAlignment(label.getHorizontalAlignment());
        textField.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent a) {
                finishTextEdit();
            }
        });
        textField.addFocusListener(new FocusAdapter() {

            public void focusLost(final FocusEvent fe) {
                finishTextEdit();
            }
        });
        remove(label);
        add(textField, BorderLayout.CENTER);
        textField.requestFocus();
        revalidate();
        textField.getCaret().setVisible(true);
    }

    public void mouseEventNotify(final MouseEvent me) {
        if (me.getID() == MouseEvent.MOUSE_CLICKED && textField == null && me.getClickCount() == 2) {
            edit();
        }
    }

    private void finishTextEdit() {
        if (textField != null) {
            JTextField text = textField;
            textField = null; // protect against recursive call

            setText(text.getText());
            remove(text);
            add(label, BorderLayout.CENTER);
            fireActionPerformed();
            revalidate();
            repaint();
        }
    }

    public void addNotify() {
        super.addNotify();
        label.setFont(getFont());
    }

    public void setFont(final Font f) {
        super.setFont(f);
        label.setFont(f);
        label.revalidate();
    }

    public String getText() {
        return label.getText();
    }

    public void setText(final String text) {
        sourceText = text;
        label.setText(converter.convert(text));
    }

    public void modifyPopupMenu(final JPopupMenu menu, final Component source) {
        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        }
        JMenuItem editMenuItem = new JMenuItem("Edit " + prefix + " Text") {

            final protected void fireActionPerformed(final ActionEvent e) {
                edit();
            }
        };
        if (showFontMenuItem) {
            menu.add(new FontMenuItem(this, prefix));
        }
        menu.add(editMenuItem);
    }

    public boolean isShowFontMenuItem() {
        return showFontMenuItem;
    }

    public void setShowFontMenuItem(boolean show) {
        showFontMenuItem = show;
    }

    public void paint(Graphics g) {
        if (g instanceof Graphics2D && isRotated) {
            Graphics2D g2d = ((Graphics2D) g);
            g2d.rotate(-Math.PI / 2.);
            g2d.translate(-getHeight(), 0);
        }
        super.paint(g);
    }

    public void setRotated(boolean isRotated) {
        this.isRotated = isRotated;
    }

    public boolean isRotated() {
        return isRotated;
    }
    private Vector listener;
    private boolean showFontMenuItem = true;
    private final LabelTextConverter converter;
    private String sourceText;
    private JLabel label;
    private JTextField textField;
    private String prefix;
    private boolean isRotated = false;
}
