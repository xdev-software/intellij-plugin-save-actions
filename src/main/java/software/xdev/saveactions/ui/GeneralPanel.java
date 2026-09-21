package software.xdev.saveactions.ui;

import static software.xdev.saveactions.model.Action.activate;
import static software.xdev.saveactions.model.Action.activateOnBatch;
import static software.xdev.saveactions.model.Action.activateOnShortcut;
import static software.xdev.saveactions.model.Action.noActionIfCompileErrors;
import static software.xdev.saveactions.model.Action.processAsync;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.intellij.ui.IdeBorderFactory;

import software.xdev.saveactions.model.Action;


class GeneralPanel
{
	private static final String TEXT_TITLE_ACTIONS = "General";
	
	private final Map<Action, JCheckBox> checkboxes;
	
	GeneralPanel(final Map<Action, JCheckBox> checkboxes)
	{
		this.checkboxes = checkboxes;
	}
	
	JPanel getPanel()
	{
		final JPanel panel = new JPanel();
		panel.setBorder(IdeBorderFactory.createTitledBorder(TEXT_TITLE_ACTIONS));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(this.checkboxes.get(activate));
		panel.add(this.checkboxes.get(activateOnShortcut));
		panel.add(this.checkboxes.get(activateOnBatch));
		panel.add(this.checkboxes.get(noActionIfCompileErrors));
		panel.add(this.checkboxes.get(processAsync));
		panel.add(Box.createHorizontalGlue());
		panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
		return panel;
	}
}
