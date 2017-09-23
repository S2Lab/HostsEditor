package main;

public class HostItem implements Comparable<HostItem>
{
	public String url;
	public short[] ip;
	public String annotation;
	
	//  127.0.0.1 localhost
	public HostItem(String lineIn)
	{
		annotation=null;
		
		String lineTrim=lineIn.trim();
		if(lineTrim.startsWith("#")) // 这是一行注释
		{
			annotation=lineTrim;
			return;
		}
		
		int loc_space=getLocSpace(lineIn);
		
		ip=getIP(lineTrim.substring(0, loc_space));
		if(ip!=null)
			url=lineTrim.substring(loc_space+1);
		else
			annotation=new String("# "+lineTrim);
	}
	
	public String getIP()
	{
		if(ip==null)
			return "";
		else
			return new String(""+ip[0]+'.'+ip[1]+'.'+ip[2]+'.'+ip[3]);
	}
	public String getURL()
	{
		if(url==null)
			return "";
		else
			return url;
	}	
	public boolean isAnnotation()
	{
		return annotation!=null;
	}
	public String getAnnotation()
	{
		return annotation;
	}
	
	@Override
	public String toString()
	{
		return isAnnotation() ? getAnnotation() : getIP()+' '+getURL();
	}
	
	public boolean equalsIP(short[] ipIn)
	{
		if(isAnnotation())
			return false;
		if(ipIn.length!=4)
			return false;
		
		for(int i=0;i<4;i++)
			if(ipIn[i]!=ip[i])
				return false;
		
		return true;
	}
	public boolean equalsIP(HostItem hiIn)
	{
		if(isAnnotation())
			return false;
		for(short i=0;i<4;i++)
			if(ip[i]!=hiIn.ip[i])
				return false;
		return true;
	}
	public boolean equalsURL(String urlIn)
	{
		if(isAnnotation())
			return false;
		return url.equals(urlIn);
	}
	public boolean equalsURL(HostItem hiIn)
	{
		if(isAnnotation())
			return false;
		return url.equals(hiIn.url);
	}
	
	static public int getLocSpace(String lineIn)
	{
		String lineTrim=lineIn.trim();
		int loc_space=0;
		if(lineIn.indexOf(" ")>=0)
			loc_space=lineTrim.indexOf(" ");
		if(lineIn.indexOf("\t")>=0)
			loc_space=lineTrim.indexOf("\t");
		return loc_space;
	}
	
	static public short[] getIP(String lineIn)
	{
		try {
			
			
			short[] result=new short[4];
			short[] dots=new short[3];
			
			char[] array=lineIn.trim().toCharArray();
			
			short d=0;
			for(int i=0;i<array.length;i++)
			{
				if(array[i]=='.')
					dots[d++]=(short) i;
				if(d>3)
					return null;
			}
			if(d<2)
				return null;
			
			result[0]=Short.parseShort(lineIn.substring(0, dots[0]));
			result[1]=Short.parseShort(lineIn.substring(dots[0]+1,dots[1]));
			result[2]=Short.parseShort(lineIn.substring(dots[1]+1,dots[2]));
			result[3]=Short.parseShort(lineIn.substring(dots[2]+1));
			
			return result;
			
			
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int compareTo(HostItem o) {
		HostItem hiIn=(HostItem)o;
		
		if(!equalsIP(hiIn))
			return -1;
		if(!equalsURL(hiIn))
			return 1;
		return 0;
	}
}
