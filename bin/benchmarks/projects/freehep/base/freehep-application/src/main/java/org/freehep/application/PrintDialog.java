package org.freehep.application;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.freehep.application.studio.Studio;

class PrintDialog extends JOptionPane {

    private JButton cancel = new JButton("Cancel");
    private JButton page = new JButton("Page Setup...");
    private JButton preview = new JButton("Print Preview...");
    private JButton ok = new JButton("OK");
    private JButton setup = new JButton("Printer Setup...");
    private JDialog dlg;
    private PrintHelper printable;
    private ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Object source = e.getSource();
                if (source == ok) {
                    setValue(source);
                } else if (source == cancel) {
                    setValue(source);
                } else if (source == page) {
                    printable.pageDialog(PrintDialog.this);
                } else if (source == setup) {
                    printable.printDialog(PrintDialog.this);
                } else if (source == preview) {
                    printable.printPreview(PrintDialog.this);
                } else {
                    enableButtons();
                }
            } catch (PrinterException x) {
                Application.error(PrintDialog.this, "Print Error", x);
            }
        }
    };

    PrintDialog(Studio app, PrintHelper printable) {
        this.printable = printable;

        JPanel xx = new PrintOptions(printable);
        setMessage(xx);

        JButton[] buttons = {ok, preview, setup, page, cancel};
        setOptions(buttons);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].addActionListener(al);
        }

        enableButtons();
    }

    int showDialog(Component parent) {
        dlg = createDialog(parent, "Plot Page Print Setup");
        dlg.setVisible(true);
        if (value == null) {
            return CLOSED_OPTION;
        } else if (value == ok) {
            return OK_OPTION;
        } else {
            return CANCEL_OPTION;
        }
    }

    private void enableButtons() {
    }
}
