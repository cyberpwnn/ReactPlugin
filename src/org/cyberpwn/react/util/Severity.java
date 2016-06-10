package org.cyberpwn.react.util;

public enum Severity
{
	POSSIBLE, NOTABLE, PROBLEMATIC, SERIOUS;
	
	public static GList<Severity> topDown()
	{
		return new GList<Severity>().qadd(Severity.SERIOUS).qadd(Severity.PROBLEMATIC).qadd(Severity.NOTABLE).qadd(Severity.POSSIBLE);
	}
}
