package software.xdev.saveactions.ui.java;

import static software.xdev.saveactions.model.ActionType.java;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.intellij.ui.IdeBorderFactory;

import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import software.xdev.saveactions.model.Action;


public class InspectionPanel
{
	private static final String TEXT_TITLE_INSPECTIONS = "Java Inspection and Quick Fix";
	
	private final Map<Action, JCheckBox> checkboxes;
	
	public InspectionPanel(final Map<Action, JCheckBox> checkboxes)
	{
		this.checkboxes = checkboxes;
	}
	
	public JPanel getPanel()
	{
		final JPanel panel = new JPanel();
		if(!SaveActionsServiceManager.getService().isJavaAvailable())
		{
			return panel;
		}
		panel.setBorder(IdeBorderFactory.createTitledBorder(TEXT_TITLE_INSPECTIONS));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		Action.stream(java).map(this.checkboxes::get).forEach(panel::add);
		panel.add(Box.createHorizontalGlue());
		panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
		return panel;
	}
}
