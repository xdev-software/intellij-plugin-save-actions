package software.xdev.saveactions.util.collections;

import java.util.LinkedHashMap;
import java.util.Map;


public class MaxSizedLinkedHashMap<K, V> extends LinkedHashMap<K, V>
{
	private final int maxSize;
	
	public MaxSizedLinkedHashMap(final int maxSize)
	{
		this.maxSize = maxSize;
	}
	
	@Override
	protected boolean removeEldestEntry(final Map.Entry<K, V> eldest)
	{
		return this.size() > this.maxSize;
	}
}
