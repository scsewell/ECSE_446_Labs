public enum Qtype
{
    typeA(0x0001), typeNS(0x0002), typeMX(0x000f);
    
    private byte[] result;
    
    Qtype(int x)
    {
        result = new byte[2];
        result[0] = 0;
        result[1] = (byte)x;
    }
    
    public byte[] get_value()
    {
        return result;
    }
}
