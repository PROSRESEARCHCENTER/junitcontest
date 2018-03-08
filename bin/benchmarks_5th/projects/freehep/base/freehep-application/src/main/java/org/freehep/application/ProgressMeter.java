package org.freehep.application;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.freehep.util.images.ImageHandler;

/**
 * A progress meter designed to slot into a StatusBar.
 *
 * @see StatusBar
 * @author tonyj
 * @version $Id: ProgressMeter.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public class ProgressMeter extends JPanel {

    public ProgressMeter() {
        this(true);
    }

    public ProgressMeter(boolean showStopButton) {
        super(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        Icon icon = ImageHandler.getIcon("/toolbarButtonGraphics/general/Stop16.gif", getClass());
        m_stopButton = new JLabel(icon); // Note: We use a label since it takes much less space than a button
        m_stopButton.setEnabled(false);
        m_stopButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Stoppable stop = m_stop; // Thread safe
                if (stop != null) {
                    stop.stop();
                }
            }
        });
        m_meter.setBorderPainted(false);
        add(m_meter);
        add(m_stopButton);
        m_stopButton.setVisible(showStopButton);
        setAlignmentX(0.9f);
    }

    public void setShowStopButton(boolean showStopButton) {
        m_stopButton.setVisible(showStopButton);
    }

    /**
     * Set the model for the progress bar
     *
     * @param model The model, or null to clear the progress bar
     */
    public void setModel(BoundedRangeModel model) {
        if (m_model != null) {
            m_model.removeChangeListener(listener);
        }
        m_model = model;
        if (m_model != null) {
            m_model.addChangeListener(listener);
            setProgress(m_model);
        } else {
            m_realModel.setRangeProperties(0, 0, 0, 0, false);
        }
    }

    /**
     * Set a stoppable, which will be stopped if the user presses the stop
     * button
     *
     * @param stop The stoppable, or null to clear
     */
    public void setStoppable(Stoppable stop) {
        if (stop == null) {
            setModel(null);
            m_stopButton.setEnabled(false);
            m_stopButton.repaint();
            m_stop = stop;
        } else {
            m_stop = stop;
            setModel(stop.getModel());
            m_stopButton.setEnabled(true);
            m_stopButton.repaint();
        }
    }

    /**
     * Get the model attached to the progress bar
     */
    public BoundedRangeModel getModel() {
        return m_model;
    }

    public void setIndeterminate(boolean ind) {
        m_meter.setIndeterminate(ind);
    }

    public boolean isIndeterminate() {
        return m_meter.isIndeterminate();
    }

    public void setStopEnabled(boolean enabled) {
        m_stopButton.setEnabled(enabled);
    }

    public boolean getStopEnabled() {
        return m_stopButton.isEnabled();
    }

    private void setProgress(BoundedRangeModel model) {
        m_realModel.setRangeProperties(model.getValue(), model.getExtent(), model.getMinimum(), model.getMaximum(), model.getValueIsAdjusting());
    }
    private AsnycListener listener = new AsnycListener();
    private JLabel m_stopButton;
    private Stoppable m_stop;
    private BoundedRangeModel m_model;
    private DefaultBoundedRangeModel m_realModel = new DefaultBoundedRangeModel();
    private JProgressBar m_meter = new JProgressBar(m_realModel);

    private class AsnycListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            final BoundedRangeModel model = (BoundedRangeModel) e.getSource();
            if (SwingUtilities.isEventDispatchThread()) {
                setProgress(model);
            } else {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        setProgress(model);
                    }
                };
                SwingUtilities.invokeLater(run);
            }
        }
    }
}