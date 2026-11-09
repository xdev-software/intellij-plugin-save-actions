package software.xdev.saveactions.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SuppressWarnings("PMD.UseEnumCollections") // Set must be nullable!
public class Storage
{
	// Must use a set that supports nullable values!
	// For example EnumSet will crash when encountering an unknown/null value
	private Set<Action> actions;
	private Set<String> exclusions;
	private Set<String> inclusions;
	private String configurationPath;
	private List<String> quickLists;
	
	public Storage()
	{
		this.actions = new HashSet<>();
		this.exclusions = new HashSet<>();
		this.inclusions = new HashSet<>();
		this.configurationPath = null;
		this.quickLists = new ArrayList<>();
	}
	
	public Storage(final Storage storage)
	{
		this.actions = new HashSet<>(storage.actions);
		this.exclusions = new HashSet<>(storage.exclusions);
		this.inclusions = new HashSet<>(storage.inclusions);
		this.configurationPath = storage.configurationPath;
		this.quickLists = new ArrayList<>(storage.quickLists);
	}
	
	public Set<Action> getActions()
	{
		return this.actions;
	}
	
	public void setActions(final Set<Action> actions)
	{
		this.actions = actions;
	}
	
	public Set<String> getExclusions()
	{
		return this.exclusions;
	}
	
	public void setExclusions(final Set<String> exclusions)
	{
		this.exclusions = exclusions;
	}
	
	public boolean isEnabled(final Action action)
	{
		return this.actions.contains(action);
	}
	
	public void setEnabled(final Action action, final boolean enable)
	{
		if(enable)
		{
			this.actions.add(action);
		}
		else
		{
			this.actions.remove(action);
		}
	}
	
	public Set<String> getInclusions()
	{
		return this.inclusions;
	}
	
	public void setInclusions(final Set<String> inclusions)
	{
		this.inclusions = inclusions;
	}
	
	public String getConfigurationPath()
	{
		return this.configurationPath;
	}
	
	public void setConfigurationPath(final String configurationPath)
	{
		this.configurationPath = configurationPath;
	}
	
	public List<String> getQuickLists()
	{
		return this.quickLists;
	}
	
	public void setQuickLists(final List<String> quickLists)
	{
		this.quickLists = quickLists;
	}
}
