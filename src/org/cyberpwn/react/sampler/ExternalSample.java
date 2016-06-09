package org.cyberpwn.react.sampler;

import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.object.ValueType;

public class ExternalSample extends Sample implements ExternallySamplable
{
	private final String plugin;
	
	public ExternalSample(SampleController sampleController, String cname, String plugin, ValueType type, String name, String description)
	{
		super(sampleController, cname, type, name, description, false);
		
		this.plugin = plugin;
		sampleController.registerExternalSample(this);
	}
	
	public String getPlugin()
	{
		return plugin;
	}
}
