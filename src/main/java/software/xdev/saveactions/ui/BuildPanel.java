package software.xdev.saveactions.ui;

import static java.text.MessageFormat.format;
import static java.util.Comparator.comparing;
import static software.xdev.saveactions.model.Action.compile;
import static software.xdev.saveactions.model.Action.executeAction;
import static software.xdev.saveactions.model.Action.reload;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

import com.intellij.openapi.actionSystem.ex.QuickList;
import com.intellij.openapi.actionSystem.ex.QuickListsManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.IdeBorderFactory;

import software.xdev.saveactions.core.service.SaveActionsServiceManager;
import software.xdev.saveactions.model.Action;


class BuildPanel
{
	private static final String TEXT_TITLE_ACTIONS = "Build Actions";
	private static final int QUICK_LIST_MAX_DESCRIPTION_LENGTH = 100;
	
	private final List<String> quickLists;
	private final List<QuickListWrapper> quickListElements;
	private final ListComboBoxModel<QuickListWrapper> quickListModel;
	private final JPanel panel;
	
	BuildPanel(final Map<Action, JCheckBox> checkboxes, final List<String> quickLists)
	{
		this.quickLists = quickLists;
		this.quickListElements = new ArrayList<>();
		this.quickListModel = new ListComboBoxModel<>(this.quickListElements);
		this.quickListModel.addListDataListener(this.getListDataListener(quickLists));
		this.panel = new JPanel();
		this.panel.setBorder(IdeBorderFactory.createTitledBorder(TEXT_TITLE_ACTIONS));
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));
		if(SaveActionsServiceManager.getService().isCompilingAvailable())
		{
			this.panel.add(this.wrap(checkboxes.get(compile), null));
			this.panel.add(this.wrap(checkboxes.get(reload), null));
		}
		@SuppressWarnings("unchecked")
		final JComboBox<QuickListWrapper> comboBox = new ComboBox<>(this.quickListModel);
		this.panel.add(this.wrap(checkboxes.get(executeAction), comboBox));
		this.panel.add(Box.createHorizontalGlue());
		this.panel.setMinimumSize(new Dimension(Short.MAX_VALUE, 0));
	}
	
	JPanel getPanel()
	{
		return this.panel;
	}
	
	void update()
	{
		final List<QuickListWrapper> quickListWrappers = Arrays
			.stream(QuickListsManager.getInstance().getAllQuickLists())
			.map(QuickListWrapper::new)
			.sorted(comparing(QuickListWrapper::toString))
			.toList();
		this.quickListElements.clear();
		this.quickListElements.addAll(quickListWrappers);
		if(!this.quickLists.isEmpty())
		{
			final String selectedItem = this.quickLists.get(0);
			this.quickListElements.stream()
				.filter(wrapper -> wrapper.hasId(selectedItem))
				.findFirst()
				.ifPresent(this.quickListModel::setSelectedItem);
		}
	}
	
	@SuppressWarnings("checkstyle:MagicNumber")
	private JComponent wrap(final JCheckBox checkBox, @SuppressWarnings("rawtypes") final JComboBox comboBox)
	{
		final JPanel wrapper = new JPanel();
		wrapper.setLayout(new BorderLayout());
		wrapper.add(checkBox, BorderLayout.WEST);
		if(comboBox != null)
		{
			checkBox.addChangeListener(e -> {
				comboBox.setEnabled(checkBox.isSelected());
				this.update();
			});
			comboBox.setEnabled(checkBox.isSelected());
			wrapper.add(comboBox, BorderLayout.SOUTH);
		}
		wrapper.setMaximumSize(new Dimension(3000, 100));
		return wrapper;
	}
	
	private static final class QuickListWrapper
	{
		private final QuickList quickList;
		
		private QuickListWrapper(final QuickList quickList)
		{
			this.quickList = quickList;
		}
		
		private boolean hasId(final String id)
		{
			return this.getId().equals(id);
		}
		
		private String getId()
		{
			return this.quickList.hashCode() + "";
		}
		
		@Override
		public String toString()
		{
			final String name = this.quickList.getName();
			String description = this.quickList.getDescription();
			if(description == null)
			{
				return name;
			}
			if(description.length() > QUICK_LIST_MAX_DESCRIPTION_LENGTH)
			{
				description = description.substring(0, QUICK_LIST_MAX_DESCRIPTION_LENGTH);
				description = description + "...";
			}
			return format("{0} ({1})", name, description);
		}
	}
	
	private ListDataListener getListDataListener(final List<String> quickLists)
	{
		return new ListDataListener()
		{
			
			@Override
			public void intervalAdded(final ListDataEvent ignored)
			{
				// not used.
			}
			
			@Override
			public void intervalRemoved(final ListDataEvent ignored)
			{
				// not used.
			}
			
			@Override
			public void contentsChanged(final ListDataEvent e)
			{
				@SuppressWarnings("rawtypes")
				final ListComboBoxModel source = (ListComboBoxModel)e.getSource();
				final QuickListWrapper selectedItem = (QuickListWrapper)source.getSelectedItem();
				if(selectedItem == null)
				{
					return;
				}
				quickLists.clear();
				quickLists.add(selectedItem.getId());
			}
		};
	}
}
