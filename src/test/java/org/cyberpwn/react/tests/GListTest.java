package org.cyberpwn.react.tests;

import org.cyberpwn.react.util.GList;
import org.junit.Assert;
import org.junit.Test;

public class GListTest
{
	@Test
	public void testConstruction()
	{
		X.x(getClass().getSimpleName(), "CONSTRUCTION");
		Assert.assertEquals("This, Is, A, Test", new GList<String>().qadd("This").qadd("Is").qadd("A").qadd("Test").toString(", "));
	}
	
	@Test
	public void testReverse()
	{
		X.x(getClass().getSimpleName(), "REVERSALS");
		Assert.assertEquals("Test, A, Is, This", new GList<String>().qadd("This").qadd("Is").qadd("A").qadd("Test").reverse().toString(", "));
	}
	
	@Test
	public void testPop()
	{
		X.x(getClass().getSimpleName(), "POPPING");
		GList<String> test = new GList<String>().qadd("This").qadd("Is").qadd("A").qadd("Test");
		String popped = test.pop();
		Assert.assertEquals("Is, A, Test", test.toString(", "));
		Assert.assertEquals("This", popped);
	}
}
