package software.xdev.saveactions.ui;

import static software.xdev.saveactions.model.Action.activate;
import static software.xdev.saveactions.model.Action.activateOnBatch;
import static software.xdev.saveactions.model.Action.activateOnShortcut;
import static software.xdev.saveactions.model.Action.compile;
import static software.xdev.saveactions.model.Action.customUnqualifiedStaticMemberAccess;
import static software.xdev.saveactions.model.Action.reformat;
import static software.xdev.saveactions.model.Action.reformatChangedCode;
import static software.xdev.saveactions.model.Action.reload;
import static software.xdev.saveactions.model.Action.unqualifiedStaticMemberAccess;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;

import software.xdev.saveactions.model.Action;
import software.xdev.saveactions.model.PersistentStorageState;
import software.xdev.saveactions.model.Storage;
import software.xdev.saveactions.model.java.EpfStorage;
import software.xdev.saveactions.ui.java.IdeSupportPanel;
import software.xdev.saveactions.ui.java.InspectionPanel;


public class Configuration implements Configurable
{
	public static final int BOX_LAYOUT_MAX_WIDTH = 3000;
	public static final int BOX_LAYOUT_MAX_HEIGHT = 100;
	
	private static final String TEXT_DISPLAY_NAME = "Save Actions";
	
	private final Project project;
	
	private final Set<String> exclusions = new HashSet<>();
	private final Set<String> inclusions = new HashSet<>();
	private final List<String> quickLists = new ArrayList<>();
	private final Map<Action, JCheckBox> checkboxes = new EnumMap<>(Action.class);
	private final ActionListener checkboxActionListener = this::updateCheckboxEnabled;
	
	private BuildPanel buildPanel;
	private FileMaskPanel fileMasksExclusionPanel;
	private FileMaskPanel fileMasksInclusionPanel;
	private IdeSupportPanel ideSupport;
	
	public Configuration(final Project project)
	{
		this.project = project;
	}
	
	private Storage getStorage()
	{
		return this.project.getService(PersistentStorageState.class).getState();
	}
	
	@Nullable
	@Override
	public JComponent createComponent()
	{
		final JPanel panel = this.initComponent(this.getStorage());
		this.initActionListeners();
		return panel;
	}
	
	private void initActionListeners()
	{
		for(final Map.Entry<Action, JCheckBox> checkbox : this.checkboxes.entrySet())
		{
			checkbox.getValue().addActionListener(this.checkboxActionListener);
		}
	}
	
	@Override
	public boolean isModified()
	{
		final Storage storage = this.getStorage();
		for(final Map.Entry<Action, JCheckBox> checkbox : this.checkboxes.entrySet())
		{
			if(storage.isEnabled(checkbox.getKey()) != checkbox.getValue().isSelected())
			{
				return true;
			}
		}
		if(storage.getConfigurationPath() != null
			&& !storage.getConfigurationPath().equals(this.ideSupport.getPath()))
		{
			return true;
		}
		return !storage.getExclusions().equals(this.exclusions)
			|| !storage.getInclusions().equals(this.inclusions)
			|| !storage.getQuickLists().equals(this.quickLists);
	}
	
	@Override
	public void apply()
	{
		final Storage storage = this.getStorage();
		for(final Map.Entry<Action, JCheckBox> checkbox : this.checkboxes.entrySet())
		{
			storage.setEnabled(checkbox.getKey(), checkbox.getValue().isSelected());
		}
		storage.setExclusions(new HashSet<>(this.exclusions));
		storage.setInclusions(new HashSet<>(this.inclusions));
		storage.setQuickLists(new ArrayList<>(this.quickLists));
		storage.setConfigurationPath(this.ideSupport.getPath());
		final Storage efpStorage = EpfStorage.INSTANCE.getStorageOrDefault(this.ideSupport.getPath(), storage);
		this.updateSelectedStateOfCheckboxes(efpStorage.getActions());
		this.updateCheckboxEnabled(null);
	}
	
	@Override
	public void reset()
	{
		final Storage storage = this.getStorage();
		this.updateSelectedStateOfCheckboxes(storage.getActions());
		this.updateCheckboxEnabled(null);
		this.updateExclusions(storage);
		this.updateInclusions(storage);
		this.updateQuickLists(storage);
		this.ideSupport.setPath(storage.getConfigurationPath());
	}
	
	private void updateSelectedStateOfCheckboxes(final Set<Action> selectedActions)
	{
		for(final Map.Entry<Action, JCheckBox> checkbox : this.checkboxes.entrySet())
		{
			final boolean isSelected = selectedActions.contains(checkbox.getKey());
			checkbox.getValue().setSelected(isSelected);
		}
	}
	
	@Override
	public void disposeUIResources()
	{
		this.checkboxes.clear();
		this.exclusions.clear();
		this.inclusions.clear();
		this.quickLists.clear();
		this.buildPanel = null;
		this.fileMasksInclusionPanel = null;
		this.fileMasksExclusionPanel = null;
		this.ideSupport = null;
	}
	
	@Nls
	@Override
	public String getDisplayName()
	{
		return TEXT_DISPLAY_NAME;
	}
	
	@Nullable
	@Override
	public String getHelpTopic()
	{
		return null;
	}
	
	private JPanel initComponent(final Storage storage)
	{
		for(final Action action : Action.values())
		{
			this.checkboxes.put(action, new JCheckBox(action.getText()));
		}
		final GeneralPanel generalPanel = new GeneralPanel(this.checkboxes);
		final FormattingPanel formattingPanel = new FormattingPanel(this.checkboxes);
		this.buildPanel = new BuildPanel(this.checkboxes, this.quickLists);
		final InspectionPanel inspectionPanel = new InspectionPanel(this.checkboxes);
		this.fileMasksInclusionPanel = new FileMaskInclusionPanel(this.inclusions);
		this.fileMasksExclusionPanel = new FileMaskExclusionPanel(this.exclusions);
		this.ideSupport = new IdeSupportPanel();
		return this.initRootPanel(
			generalPanel.getPanel(),
			formattingPanel.getPanel(),
			this.buildPanel.getPanel(),
			inspectionPanel.getPanel(),
			this.fileMasksInclusionPanel.getPanel(),
			this.fileMasksExclusionPanel.getPanel(),
			this.ideSupport.getPanel(storage.getConfigurationPath())
		);
	}
	
	@SuppressWarnings("checkstyle:MagicNumber")
	private JPanel initRootPanel(
		final JPanel general,
		final JPanel actions,
		final JPanel build,
		final JPanel inspections,
		final JPanel fileMasksInclusions,
		final JPanel fileMasksExclusions,
		final JPanel ideSupport)
	{
		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		
		c.gridy = 0;
		panel.add(general, c);
		c.gridy = 1;
		panel.add(actions, c);
		c.gridy = 2;
		panel.add(build, c);
		c.gridy = 3;
		panel.add(inspections, c);
		
		final JPanel fileMaskPanel = new JPanel();
		fileMaskPanel.setLayout(new BoxLayout(fileMaskPanel, BoxLayout.LINE_AXIS));
		fileMaskPanel.add(fileMasksInclusions);
		fileMaskPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		fileMaskPanel.add(fileMasksExclusions);
		c.gridy = 4;
		panel.add(fileMaskPanel, c);
		
		c.gridy = 5;
		panel.add(ideSupport, c);
		
		c.gridy = 6;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		final JPanel filler = new JPanel();
		filler.setOpaque(false);
		panel.add(filler, c);
		
		return panel;
	}
	
	private void updateInclusions(final Storage storage)
	{
		this.inclusions.clear();
		this.inclusions.addAll(storage.getInclusions());
		this.fileMasksInclusionPanel.update(this.inclusions);
	}
	
	private void updateExclusions(final Storage storage)
	{
		this.exclusions.clear();
		this.exclusions.addAll(storage.getExclusions());
		this.fileMasksExclusionPanel.update(this.exclusions);
	}
	
	private void updateQuickLists(final Storage storage)
	{
		this.quickLists.clear();
		this.quickLists.addAll(storage.getQuickLists());
		this.buildPanel.update();
	}
	
	private void updateCheckboxEnabled(final ActionEvent event)
	{
		this.updateCheckboxEnabledIfActiveSelected();
		this.updateCheckboxGroupExclusive(event, reformat, reformatChangedCode);
		this.updateCheckboxGroupExclusive(event, compile, reload);
		this.updateCheckboxGroupExclusive(event, unqualifiedStaticMemberAccess, customUnqualifiedStaticMemberAccess);
	}
	
	private void updateCheckboxEnabledIfActiveSelected()
	{
		for(final Map.Entry<Action, JCheckBox> checkbox : this.checkboxes.entrySet())
		{
			final Action currentCheckBoxKey = checkbox.getKey();
			if(!activate.equals(currentCheckBoxKey)
				&& !activateOnShortcut.equals(currentCheckBoxKey)
				&& !activateOnBatch.equals(currentCheckBoxKey))
			{
				checkbox.getValue().setEnabled(this.isActiveSelected());
			}
		}
	}
	
	private void updateCheckboxGroupExclusive(final ActionEvent event, final Action checkbox1, final Action checkbox2)
	{
		if(event == null || !(event.getSource() instanceof final JCheckBox thisCheckbox))
		{
			return;
		}
		if(thisCheckbox.isSelected())
		{
			if(thisCheckbox == this.checkboxes.get(checkbox1))
			{
				this.checkboxes.get(checkbox2).setSelected(false);
			}
			else if(thisCheckbox == this.checkboxes.get(checkbox2))
			{
				this.checkboxes.get(checkbox1).setSelected(false);
			}
		}
	}
	
	private boolean isActiveSelected()
	{
		final boolean activateIsSelected = this.checkboxes.get(activate).isSelected();
		final boolean activateShortcutIsSelected = this.checkboxes.get(activateOnShortcut).isSelected();
		final boolean activateBatchIsSelected = this.checkboxes.get(activateOnBatch).isSelected();
		return activateIsSelected || activateShortcutIsSelected || activateBatchIsSelected;
	}
}
