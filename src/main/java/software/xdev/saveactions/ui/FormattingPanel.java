package software.xdev.saveactions.ui;

import software.xdev.saveactions.model.Action;
import com.intellij.ui.IdeBorderFactory;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.Map;

import static software.xdev.saveactions.model.Action.organizeImports;
import static software.xdev.saveactions.model.Action.rearrange;
import static software.xdev.saveactions.model.Action.reformat;
import static software.xdev.saveactions.model.Action.reformatChangedCode;

class FormattingPanel {

    private static final String TEXT_TITLE_ACTIONS = "Formatting Actions";

    private final Map<Action, JCheckBox> checkboxes;

    FormattingPanel(Map<Action, JCheckBox> checkboxes) {
        this.checkboxes = checkboxes;
    }

    JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(IdeBorderFactory.createTitledBorder(TEXT_TITLE_ACTIONS));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(checkboxes.get(organizeImports));
        panel.add(checkboxes.get(reformat));
        panel.add(checkboxes.get(reformatChangedCode));
        panel.add(checkboxes.get(rearrange));
        panel.add(Box.createHorizontalGlue());
        panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
        return panel;
    }

}
