package gr.sr.dressmeEngine.xml.dataHolder;

import java.util.ArrayList;
import java.util.List;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.dressmeEngine.data.DressMeHatData;

public final class DressMeHatHolder extends AbstractHolder
{
	private static final DressMeHatHolder _instance = new DressMeHatHolder();
	
	public static DressMeHatHolder getInstance()
	{
		return _instance;
	}
	
	private final List<DressMeHatData> _hat = new ArrayList<>();
	
	public void addHat(DressMeHatData hat)
	{
		_hat.add(hat);
	}
	
	public List<DressMeHatData> getAllHats()
	{
		return _hat;
	}
	
	public DressMeHatData getHat(int id)
	{
		for (DressMeHatData hat : _hat)
		{
			if (hat.getId() == id)
			{
				return hat;
			}
		}
		
		return null;
	}
	
	@Override
	public int size()
	{
		return _hat.size();
	}
	
	@Override
	public void clear()
	{
		_hat.clear();
	}
}
