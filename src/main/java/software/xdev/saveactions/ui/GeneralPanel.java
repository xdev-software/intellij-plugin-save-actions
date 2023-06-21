package software.xdev.saveactions.ui;

import software.xdev.saveactions.model.Action;
import com.intellij.ui.IdeBorderFactory;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.Map;

import static software.xdev.saveactions.model.Action.activate;
import static software.xdev.saveactions.model.Action.activateOnBatch;
import static software.xdev.saveactions.model.Action.activateOnShortcut;
import static software.xdev.saveactions.model.Action.noActionIfCompileErrors;

class GeneralPanel {

    private static final String TEXT_TITLE_ACTIONS = "General";

    private final Map<Action, JCheckBox> checkboxes;

    GeneralPanel(Map<Action, JCheckBox> checkboxes) {
        this.checkboxes = checkboxes;
    }

    JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(IdeBorderFactory.createTitledBorder(TEXT_TITLE_ACTIONS));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(checkboxes.get(activate));
        panel.add(checkboxes.get(activateOnShortcut));
        panel.add(checkboxes.get(activateOnBatch));
        panel.add(checkboxes.get(noActionIfCompileErrors));
        panel.add(Box.createHorizontalGlue());
        panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
        return panel;
    }

}
