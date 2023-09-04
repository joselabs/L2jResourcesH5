package gr.sr.dressmeEngine.xml.dataParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilderFactory;

import l2r.Config;

import gr.sr.data.xml.AbstractFileParser;
import gr.sr.dressmeEngine.data.DressMeShieldData;
import gr.sr.dressmeEngine.xml.dataHolder.DressMeShieldHolder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public final class DressMeShieldParser extends AbstractFileParser<DressMeShieldHolder>
{
	private final String SHIELD_FILE_PATH = Config.DATAPACK_ROOT + "/data/xml/sunrise/dressme/shield.xml";
	
	private static final DressMeShieldParser _instance = new DressMeShieldParser();
	
	public static DressMeShieldParser getInstance()
	{
		return _instance;
	}
	
	private DressMeShieldParser()
	{
		super(DressMeShieldHolder.getInstance());
	}
	
	@Override
	public File getXMLFile()
	{
		return new File(SHIELD_FILE_PATH);
	}
	
	@Override
	protected void readData()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		
		File file = getXMLFile();
		
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);
			
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if (n.getNodeName().equalsIgnoreCase("list"))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equalsIgnoreCase("shield"))
						{
							int number = Integer.parseInt(d.getAttributes().getNamedItem("number").getNodeValue());
							int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
							String name = d.getAttributes().getNamedItem("name").getNodeValue();
							int itemId = 0;
							long itemCount = 0;
							
							for (Node price = d.getFirstChild(); price != null; price = price.getNextSibling())
							{
								if ("price".equalsIgnoreCase(price.getNodeName()))
								{
									itemId = Integer.parseInt(price.getAttributes().getNamedItem("id").getNodeValue());
									itemCount = Long.parseLong(price.getAttributes().getNamedItem("count").getNodeValue());
								}
							}
							
							getHolder().addShield(new DressMeShieldData(number, id, name, itemId, itemCount));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.warn(getClass().getSimpleName() + ": Error: " + e);
		}
	}
}