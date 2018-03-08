package org.freehep.application.mdi;

import java.util.EventListener;

public interface PageListener extends EventListener {

    void pageChanged(PageEvent e);
}
