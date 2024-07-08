package software.xdev.saveactions.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.serviceContainer.NonInjectable;
import com.intellij.util.xmlb.XmlSerializerUtil;


@State(name = "SaveActionSettings", storages = {@com.intellij.openapi.components.Storage("saveactions_settings.xml")})
@Service(Service.Level.PROJECT)
public final class Storage implements PersistentStateComponent<Storage>
{
	private boolean firstLaunch;
	private Set<Action> actions;
	private Set<String> exclusions;
	private Set<String> inclusions;
	private String configurationPath;
	private List<String> quickLists;
	
	@NonInjectable
	public Storage()
	{
		this.firstLaunch = true;
		this.actions = new HashSet<>();
		this.exclusions = new HashSet<>();
		this.inclusions = new HashSet<>();
		this.configurationPath = null;
		this.quickLists = new ArrayList<>();
	}
	
	@NonInjectable
	public Storage(final Storage storage)
	{
		this.firstLaunch = storage.firstLaunch;
		this.actions = new HashSet<>(storage.actions);
		this.exclusions = new HashSet<>(storage.exclusions);
		this.inclusions = new HashSet<>(storage.inclusions);
		this.configurationPath = storage.configurationPath;
		this.quickLists = new ArrayList<>(storage.quickLists);
	}
	
	@Override
	public Storage getState()
	{
		return this;
	}
	
	@Override
	public void loadState(@NotNull final Storage state)
	{
		this.firstLaunch = false;
		XmlSerializerUtil.copyBean(state, this);
		
		// Remove null values that might have been caused by non-parsable values
		this.actions.removeIf(Objects::isNull);
		this.exclusions.removeIf(Objects::isNull);
		this.inclusions.removeIf(Objects::isNull);
		this.quickLists.removeIf(Objects::isNull);
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
	
	public boolean isFirstLaunch()
	{
		return this.firstLaunch;
	}
	
	public void stopFirstLaunch()
	{
		this.firstLaunch = false;
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
	
	public void clear()
	{
		this.firstLaunch = true;
		this.actions.clear();
		this.exclusions.clear();
		this.inclusions.clear();
		this.configurationPath = null;
		this.quickLists.clear();
	}
}
