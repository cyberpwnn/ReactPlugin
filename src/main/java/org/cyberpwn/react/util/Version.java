package org.cyberpwn.react.util;

/**
 * Version
 * <p>
 * Version container
 *
 * @author Maxim Van de Wynckel (Maximvdw)
 * @version 1.0
 * @project BasePlugin
 * @site http://www.mvdw-software.be/
 */
public class Version implements Comparable<Version>
{
	/**
	 * Major minor and release
	 */
	private short major, minor, release;
	/**
	 * Alpha or beta release
	 */
	private boolean alpha, beta;
	
	/**
	 * Create version
	 */
	public Version()
	{
		
	}
	
	/**
	 * Create version from string
	 *
	 * @param version
	 *            Version string
	 */
	public Version(String version)
	{
		String[] data = version.split("\\.");
		if(data.length == 3)
		{
			if(NumberUtils.isInteger(data[0]))
			{
				major = (short) Integer.parseInt(data[0]);
			}
			if(major == 0)
			{
				alpha = true;
			}
			if(NumberUtils.isInteger(data[1]))
			{
				minor = (short) Integer.parseInt(data[1]);
			}
			if(data[2].contains("b"))
			{
				beta = true;
			}
			if(NumberUtils.isInteger(data[2].replace("b", "")))
			{
				release = (short) Integer.parseInt(data[2].replace("b", ""));
			}
		}
	}
	
	/**
	 * Is beta release
	 *
	 * @return Beta release
	 */
	public boolean isBeta()
	{
		return beta;
	}
	
	/**
	 * Is alpha release
	 *
	 * @return Alpha
	 */
	public boolean isAlpha()
	{
		return alpha;
	}
	
	/**
	 * Get major version
	 *
	 * @return Major
	 */
	public short getMajor()
	{
		return major;
	}
	
	/**
	 * Set major version
	 *
	 * @param major
	 *            Major
	 * @return Version
	 */
	public Version setMajor(short major)
	{
		this.major = major;
		return this;
	}
	
	/**
	 * Get minor version
	 *
	 * @return Minor
	 */
	public short getMinor()
	{
		return minor;
	}
	
	/**
	 * Set minor version
	 *
	 * @param minor
	 *            Minor
	 * @return Version
	 */
	public Version setMinor(short minor)
	{
		this.minor = minor;
		return this;
	}
	
	/**
	 * Get release
	 *
	 * @return release
	 */
	public short getRelease()
	{
		return release;
	}
	
	/**
	 * Set release version
	 *
	 * @param release
	 *            Release
	 * @return Version
	 */
	public Version setRelease(short release)
	{
		this.release = release;
		return this;
	}
	
	public int compare(Version otherVersion)
	{
		return compareTo(otherVersion);
	}
	
	/**
	 * Version to string
	 */
	@Override
	public String toString()
	{
		String version = major + "." + minor + "." + release;
		if(isBeta())
		{
			version += "b";
		}
		return version;
	}
	
	@Override
	public int compareTo(Version otherVersion)
	{
		if(otherVersion.getMajor() > getMajor())
		{
			return 1;
		}
		else if(otherVersion.getMajor() < getMajor())
		{
			return -1;
		}
		else
		{
			if(otherVersion.getMinor() > getMinor())
			{
				return 1;
			}
			else if(otherVersion.getMinor() < getMinor())
			{
				return -1;
			}
			else
			{
				if(otherVersion.getRelease() > getRelease())
				{
					return 1;
				}
				else if(otherVersion.getRelease() < getRelease())
				{
					return -1;
				}
				else
				{
					if(otherVersion.isBeta() == isBeta())
					{
						return 0;
					}
					else
					{
						if(otherVersion.isBeta())
						{
							return -1;
						}
						else
						{
							return 1;
						}
					}
				}
			}
		}
	}
}
