public enum Qtype
{
    typeA(0x0001), typeNS(0x0002), typeMX(0x000f), typeCNAME(0x0005);
    
    private byte[] result;
    private int int_result;
    
    Qtype(int x)
    {
        result = new byte[2];
        result[0] = 0;
        result[1] = (byte)x;

        int_result = x;
    }
    
    public byte[] get_value()
    {
        return result;
    }

    public static Qtype get_type(int x)
    {
        for (int i=0; i<Qtype.values().length;i++){
            if (Qtype.values()[i].int_result == x){
                return Qtype.values()[i];
            }
        }
        return null;
    }
}
