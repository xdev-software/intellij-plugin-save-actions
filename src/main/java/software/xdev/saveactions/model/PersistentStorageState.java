package software.xdev.saveactions.model;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;


@State(name = "SaveActionSettings", storages = {@com.intellij.openapi.components.Storage("saveactions_settings.xml")})
@Service(Service.Level.PROJECT)
public final class PersistentStorageState implements PersistentStateComponent<Storage>
{
	private Storage storage;
	
	@NonNull
	@Override
	public Storage getState()
	{
		if(this.storage == null)
		{
			this.init();
		}
		return this.storage;
	}
	
	private synchronized void init()
	{
		if(this.storage == null)
		{
			this.storage = new Storage();
		}
	}
	
	@Override
	public void loadState(@NotNull final Storage state)
	{
		this.storage = state;
		
		// Remove null values that might have been caused by non-parsable values
		this.storage.getActions().removeIf(Objects::isNull);
		this.storage.getExclusions().removeIf(Objects::isNull);
		this.storage.getInclusions().removeIf(Objects::isNull);
		this.storage.getQuickLists().removeIf(Objects::isNull);
	}
	
	public void clear()
	{
		this.storage = null;
	}
}
