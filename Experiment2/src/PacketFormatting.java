import java.util.Random;
import java.io.*;

public class PacketFormatting
{
	private static final int MAX_DOMAIN_LENGTH 	= 253;
	private static final int MAX_LABEL_LENGTH 	= 63;
	
	private static final Random m_random = new Random();

    public static byte[] createRequest(String domainName, Qtype type) throws IOException
    {
    	ByteArrayOutputStream payload = new ByteArrayOutputStream();
    	DataOutputStream query = new DataOutputStream(payload);
    	
    	// write header
    	query.writeShort((short)m_random.nextInt()); // get random ID
    	query.writeShort(0x0100); // set recursion bit
    	query.writeShort(1); // one query
    	query.writeShort(0);
    	query.writeShort(0);
    	query.writeShort(0);

    	// write questions
    	writeQuestion(query, domainName, type);
    	
    	return payload.toByteArray();
    }
    
    private static void writeQuestion(DataOutputStream query, String domainName, Qtype type) throws IOException
    {
        if (domainName.length() > MAX_DOMAIN_LENGTH)
        {
        	throw new IOException("The domain name \"" + domainName + "\" is too long for DNS (" + MAX_DOMAIN_LENGTH + " max).");
        }
        
        for (String label : domainName.split("\\."))
        {
            if (label.length() > MAX_LABEL_LENGTH)
            {
            	throw new IOException("The label \"" + label + "\" is too long for DNS (" + MAX_LABEL_LENGTH + " max).");
            }
            query.writeByte(label.length());
            query.write(label.getBytes("UTF-8"));
        }
        query.writeByte(0);
        query.writeShort(type.value());
        query.writeShort(0x0001);
    }
}
