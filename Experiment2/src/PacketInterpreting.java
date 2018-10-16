import java.lang.StringBuilder;
import java.util.ArrayList;

public class PacketInterpreting
{
    private static byte[] m_respose;
    private static int m_answerPointer;

    private static boolean m_isAuthorative;
    private static int m_answerCount;
    private static int m_additionalCount;

    public static void interpretResults(byte[] payload) throws Exception
    {
        m_respose = payload;
        
        parseHeader(payload);
        
        m_answerPointer = 12 + name_width(12) + 4;
        parseAnswer(m_answerCount, true);
        parseAnswer(m_additionalCount, false);
    }

    private static void parseHeader(byte[] response) throws Exception
    {
    	// QR
        if ((response[2] >> 7 & 1) != 1)
        {
        	throw new Exception("Retrieved packed is not marked as a DNS response!");
        }
        
        // AA
        m_isAuthorative = (response[2] >> 2 & 1) == 1;
        
        // TC
        if ((response[2] >> 1 & 1) == 1) 
        {
        	Logger.logError("Recieved message was truncated!");
        }
        
     	// RA
        if ((response[3] >> 7 & 1) != 1) 
        {
        	Logger.logError("Recursive queries are not supported by the DNS server!");
        }
     	
        // RCODE
        switch (response[3] & 0xFF)
        {
			case 1:
	        	throw new Exception("Format error, the server was unable to interperet the query!");
			case 2:
	        	throw new Exception("Server failure, the name server was unable to process this query!");
			case 3:
				Logger.logError("Name error, the domain name referenced in the query does not exist!");
			case 4:
	        	throw new Exception("Not implemented, the name server does not support the requested kind of query!");
			case 5:
	        	throw new Exception("Refused, the name server refused to perform the requested operation for policy reasons!");
		}
        
        // ANCOUNT
        m_answerCount = (response[6] << 8) + response[7];

        // ARCOUNT
        m_additionalCount = (response[10] << 8) + response[11];
    }

    private static void parseAnswer(int count, boolean isAnswer)
    {
        String domain_name;
        String NAME;
        int pointer_count = m_answerPointer;
        int TTL;
        Qtype TYPE;
        int RDLENGTH;

        byte byte_0;
        byte byte_1;
        byte byte_2;
        byte byte_3;

        //is for answer section
        if (isAnswer){
            Logger.logAnswerSection(count);
        } else {    // is for additional section
            Logger.logAdditionalSection(count);
        }

        // no records found
        if (count == 0)
        {
            Logger.logNoRecords();
        }
        
        for (int i=0;i<count; i++) {
            ArrayList<String> answer = new ArrayList<>();
            domain_name = get_name_field(pointer_count);
            answer.add(domain_name);
            pointer_count = pointer_count + name_width(pointer_count);

            //TYPE field
            byte_0 = m_respose[pointer_count];
            byte_1 = m_respose[pointer_count + 1];
            TYPE = Qtype.getValue((short)((byte_0 << 8) + toUnsigned(byte_1)));
            answer.add(TYPE.toString());

            pointer_count = pointer_count + 2;

            //CLASS
            byte_0 = m_respose[pointer_count];
            byte_1 = m_respose[pointer_count + 1];
            if (((byte_0 << 8) + toUnsigned(byte_1)) != 0x0001) {
                //TODO: print an error
            }

            pointer_count = pointer_count + 2;

            //TTL
            byte_0 = m_respose[pointer_count];
            byte_1 = m_respose[pointer_count + 1];
            byte_2 = m_respose[pointer_count + 2];
            byte_3 = m_respose[pointer_count + 3];
            TTL = (byte_0 << 24) + (byte_1 << 16) + (byte_2 << 8) + toUnsigned(byte_3);
            answer.add(String.valueOf(TTL));

            pointer_count = pointer_count + 4;

            //RDLENGTH
            byte_0 = m_respose[pointer_count];
            byte_1 = m_respose[pointer_count + 1];
            RDLENGTH = (byte_0 << 8) + toUnsigned(byte_1);

            pointer_count = pointer_count + 2;

            //RDATA
            switch (TYPE)
            {
				case A:
	                byte_0 = m_respose[pointer_count];
	                byte_1 = m_respose[pointer_count + 1];
	                byte_2 = m_respose[pointer_count + 2];
	                byte_3 = m_respose[pointer_count + 3];

	                NAME = (get_ip_address(toUnsigned(byte_0), toUnsigned(byte_1), toUnsigned(byte_2), toUnsigned(byte_3)));
	                answer.add(NAME);
	                Logger.logAnswer(TYPE, NAME, TTL, m_isAuthorative);
	                pointer_count = pointer_count + 4;
					break;
					
				case NS:
				case CNAME:
	                NAME = (get_name_field(pointer_count));
	                answer.add(NAME);
	                Logger.logAnswer(TYPE, NAME, TTL, m_isAuthorative);
	                pointer_count = pointer_count + name_width(pointer_count);
					break;
					
				case MX:
	                byte_0 = m_respose[pointer_count];
	                byte_1 = m_respose[pointer_count + 1];

	                int preference = (byte_0 << 8) + toUnsigned(byte_1);

	                answer.add(String.valueOf(preference));
	                pointer_count = pointer_count + 2;
	                NAME = (get_name_field(pointer_count));
	                answer.add(NAME);
	                Logger.logMX(NAME, preference, TTL, m_isAuthorative);
	                pointer_count = pointer_count + name_width(pointer_count);
					break;
			}
        }
    }

    private static String get_ip_address(int one, int two, int three, int four){
        StringBuilder ip_address = new StringBuilder();
        ip_address.append(one);
        ip_address.append(".");
        ip_address.append(two);
        ip_address.append(".");
        ip_address.append(three);
        ip_address.append(".");
        ip_address.append(four);

        return ip_address.toString();
    }

    private static String get_name_field(int pointer_count){
        StringBuilder domain_name = new StringBuilder();
        byte byte_0;
        byte byte_1;

        while(pointer_count <= m_respose.length && m_respose[pointer_count] != 0){
            if (isCompressed(m_respose[pointer_count])){
                byte_0 = m_respose[pointer_count];
                byte_1 = m_respose[pointer_count + 1];
                byte_0 &= ~(3 << 6);        //invert the 1s
                pointer_count = (byte_0 << 8) + byte_1;        //find the pointed number
                continue;
            }
            int count = m_respose[pointer_count];

            for (int i=1;i<(count+1);i++){
                domain_name.append((char)m_respose[pointer_count+i]);
            }
            domain_name.append(".");
            pointer_count = pointer_count + count + 1;
            //as long as no pointer was used, count + 1 ( 3 w w w  = 3 + 1 )
        }
        domain_name.deleteCharAt(domain_name.length() -1);
        return domain_name.toString();
    }

    private static int name_width(int pointer_count){
        int name_bytecount = 0;
        boolean add_0_for_end = true;

        while(pointer_count <= m_respose.length && m_respose[pointer_count] != 0){
            if (isCompressed(m_respose[pointer_count])){
                name_bytecount = name_bytecount + 2;
                add_0_for_end = false;
                break;
            }
            int count = m_respose[pointer_count];
            pointer_count = pointer_count + count + 1;
            //as long as no pointer was used, count + 1 ( 3 w w w  = 3 + 1 )
            name_bytecount = name_bytecount + count + 1;
        }
        if(add_0_for_end){
            name_bytecount ++;
        }

        return name_bytecount;
    }
    
    private static int toUnsigned(byte signed)
    {
        return (signed & 0xFF);
    }

    private static boolean isCompressed(byte response)
    {
    	return ((response >> 6) & 3) == 3;
    }
}
