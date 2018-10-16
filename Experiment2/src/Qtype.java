public enum Qtype
{
    A(0x0001), NS(0x0002), MX(0x000F), CNAME(0x0005);
    
    private short m_value;
    
    Qtype(int value)
    {
    	m_value = (short)value;
    }
    
    public short value()
    {
        return m_value;
    }
    
    public static Qtype getValue(short value)
    {
        for (Qtype t : Qtype.values())
        {
            if (t.m_value == value)
            {
            	return t;
            }
        }
        throw new IllegalArgumentException("QType unknown for value: " + value);
    }
}
