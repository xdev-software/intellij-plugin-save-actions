package software.xdev.saveactions.ui;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;


abstract class FileMaskPanel extends JPanel
{
	private final SortedListModel patternModels = new SortedListModel();
	
	private final JBList<String> patternList;
	
	private final JPanel patternPanel;
	
	private final String textAddMessage;
	
	private final String textAddTitle;
	
	private final String textEditMessage;
	
	private final String textEditTitle;
	
	FileMaskPanel(
		final Set<String> patterns, final String textEmpty, final String textTitle, final String textAddMessage,
		final String textAddTitle, final String textEditMessage, final String textEditTitle)
	{
		this.textAddMessage = textAddMessage;
		this.textAddTitle = textAddTitle;
		this.textEditMessage = textEditMessage;
		this.textEditTitle = textEditTitle;
		this.patternList = new JBList<>(this.patternModels);
		this.patternList.setEmptyText(textEmpty);
		this.patternPanel = ToolbarDecorator.createDecorator(this.patternList)
			.setAddAction(this.getAddActionButtonRunnable(patterns))
			.setRemoveAction(this.getRemoveActionButtonRunnable(patterns))
			.setEditAction(this.getEditActionButtonRunnable(patterns))
			.disableUpDownActions()
			.createPanel();
		this.patternPanel.setBorder(IdeBorderFactory.createTitledBorder(textTitle));
	}
	
	private AnActionButtonRunnable getEditActionButtonRunnable(final Set<String> patterns)
	{
		return actionButton -> {
			final String oldValue = this.patternList.getSelectedValue();
			final String pattern = Messages.showInputDialog(
				this.textEditMessage, this.textEditTitle, null, oldValue, this.getRegexInputValidator());
			if(pattern != null && !pattern.equals(oldValue))
			{
				patterns.remove(oldValue);
				this.patternModels.removeElement(oldValue);
				if(patterns.add(pattern))
				{
					this.patternModels.addElementSorted(pattern);
				}
			}
		};
	}
	
	JPanel getPanel()
	{
		return this.patternPanel;
	}
	
	void update(final Set<String> patterns)
	{
		this.patternModels.clear();
		this.patternModels.addAllSorted(patterns);
	}
	
	@NotNull
	private AnActionButtonRunnable getRemoveActionButtonRunnable(final Set<String> patterns)
	{
		return actionButton -> {
			for(final String selectedValue : this.patternList.getSelectedValuesList())
			{
				patterns.remove(selectedValue);
				this.patternModels.removeElement(selectedValue);
			}
		};
	}
	
	@NotNull
	private AnActionButtonRunnable getAddActionButtonRunnable(final Set<String> patterns)
	{
		return actionButton -> {
			final String pattern = Messages.showInputDialog(
				this.textAddMessage, this.textAddTitle, null, null, this.getRegexInputValidator());
			if(pattern != null && (patterns.add(pattern)))
			{
				this.patternModels.addElementSorted(pattern);
			}
		};
	}
	
	@NotNull
	private InputValidator getRegexInputValidator()
	{
		return new InputValidator()
		{
			@Override
			public boolean checkInput(final String string)
			{
				if(string == null || string.trim().isEmpty())
				{
					// do not allow null or blank entries
					return false;
				}
				try
				{
					Pattern.compile(string);
					return true;
				}
				catch(final PatternSyntaxException e)
				{
					return false;
				}
			}
			
			@Override
			public boolean canClose(final String s)
			{
				return true;
			}
		};
	}
	
	private static final class SortedListModel extends DefaultListModel<String>
	{
		private void addElementSorted(final String element)
		{
			final Enumeration<?> modelElements = this.elements();
			int index = 0;
			while(modelElements.hasMoreElements())
			{
				final String modelElement = (String)modelElements.nextElement();
				if(0 < modelElement.compareTo(element))
				{
					this.add(index, element);
					return;
				}
				index++;
			}
			this.addElement(element);
		}
		
		private void addAllSorted(final Collection<String> elements)
		{
			for(final String element : elements)
			{
				this.addElementSorted(element);
			}
		}
	}
}
