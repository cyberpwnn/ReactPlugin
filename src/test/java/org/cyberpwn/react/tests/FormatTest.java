package org.cyberpwn.react.tests;

import org.cyberpwn.react.util.F;
import org.junit.Assert;
import org.junit.Test;

public class FormatTest
{
	@Test
	public void testDoubles()
	{
		X.x(getClass().getSimpleName(), "DOUBLES");
		Assert.assertEquals("0", F.f(0.123231, 0));
		Assert.assertEquals("11.422", F.f(11.42155445, 3));
		Assert.assertEquals("3.8", F.f(3.8345345345, 1));
		Assert.assertEquals("-0.14", F.f(-0.14323231, 2));
		Assert.assertEquals("1024.4421", F.f(1024.4421426118766, 4));
		Assert.assertEquals("-8192.382", F.f(-8192.381764546, 3));
	}
	
	@Test
	public void testMemory()
	{
		X.x(getClass().getSimpleName(), "MEMORY");
		Assert.assertEquals("3 MB", F.mem(3));
		Assert.assertEquals("3 GB", F.mem(1024 * 3));
		Assert.assertEquals("1.5 GB", F.mem(1024 + 512));
	}
	
	@Test
	public void testPercents()
	{
		X.x(getClass().getSimpleName(), "PERCENTS");
		Assert.assertEquals("0%", F.pc(0.003231, 0));
		Assert.assertEquals("0.32%", F.pc(0.003231, 2));
		Assert.assertEquals("-312.55%", F.pc(-3.125474, 2));
	}
	
	@Test
	public void testIntegers()
	{
		X.x(getClass().getSimpleName(), "INTEGERS");
		Assert.assertEquals("0", F.f(0));
		Assert.assertEquals("11,435,123", F.f(11435123));
		Assert.assertEquals("-1,321", F.f(-1321));
	}
}
