import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Responsible for formatting the packet
public class Packet_Formatting
{
    
    public static byte[] return_result;
    
    public static void main(String[] args)
    {
        // Format Packet Header
        byte[] header_section = header(false);

        // Format Packet Question
        byte[] questions_section = questions("www.work.com", Qtype.typeNS);
        // Qtype.typeNS));
        
        return_result = new byte[header_section.length + questions_section.length];
        System.arraycopy(header_section, 0, return_result, 0, header_section.length);
        System.arraycopy(questions_section, 0, return_result, header_section.length, questions_section.length);
//        System.out.println("sent package: ");
//        for(byte x: return_result){
//            System.out.println(x);
//         }
    }
    
    public static byte[] createRequest(Qtype queryType, String domainName)
    {
        byte[] header_section = header(false);
        byte[] questions_section = questions(domainName, queryType);
        
        return_result = new byte[header_section.length + questions_section.length];
        System.arraycopy(header_section, 0, return_result, 0, header_section.length);
        System.arraycopy(questions_section, 0, return_result, header_section.length, questions_section.length);

        return return_result;
    }
    
    public static byte[] header(boolean TC_truncate)
    {
        ArrayList<Byte> result_Byte = new ArrayList<>();
        
        // ID
        byte[] id = new byte[2];
        Random random_num = new Random();
        random_num.nextBytes(id);


        add_array_to_list(id, result_Byte);
        
        // QR - OPCODE - AA - TC - RD
        byte chunk_1 = 0;
        
        // TC
        if (TC_truncate)
        {
            chunk_1 |= 0b00000010;
        }
        // RD
        chunk_1 |= 1;
        
        result_Byte.add(chunk_1);
        
        // RA-Z-RCODE
        byte chunk_2 = 0;
        
        result_Byte.add(chunk_2);
        
        // QDCOUNT
        byte[] QDCOUNT = new byte[2];
        QDCOUNT = assign_bytes((byte) 0, (byte) 1);
        
        add_array_to_list(QDCOUNT, result_Byte);
        
        // ANCOUNT
        byte[] ANCOUNT = new byte[2];
        ANCOUNT = assign_bytes((byte) 0, (byte) 0);
        
        add_array_to_list(ANCOUNT, result_Byte);
        
        // NSCOUNT
        byte[] NSCOUNT = new byte[2];
        NSCOUNT = assign_bytes((byte) 0, (byte) 0);
        
        add_array_to_list(NSCOUNT, result_Byte);
        
        // ARCOUNT
        byte[] ARCOUNT = new byte[2];
        NSCOUNT = assign_bytes((byte) 0, (byte) 0);
        add_array_to_list(ARCOUNT, result_Byte);
        
        return (convert_to_byte_array(result_Byte));
    }
    
    public static byte[] questions(String domain_name, Qtype type)
    {
        ArrayList<Byte> result_Byte = new ArrayList<>();
        
        // QNAME
        ArrayList<Byte> QNAME_Byte = new ArrayList<>();
        
        String[] labels = domain_name.split("\\.");
        
        if (domain_name.length() > 62 - labels.length)
        {
            // TODO: handle cases where domain name > 63
        }
        for (int i = 0; i < labels.length; i++)
        {
            Byte label_length = (byte) labels[i].length();
            //System.out.println("labelLength : " + label_length);
            QNAME_Byte.add(label_length);
            for (int j = 0; j < labels[i].length(); j++)
            {
                QNAME_Byte.add((byte) labels[i].charAt(j));
            }
        }
        QNAME_Byte.add((byte) 0);
        
        byte[] QNAME = convert_to_byte_array(QNAME_Byte);
        add_array_to_list(QNAME, result_Byte);
        
        // QTYPE
        byte[] QTYPE = type.get_value();
        add_array_to_list(QTYPE, result_Byte);
        
        // QCLASS
        byte[] QCLASS = new byte[2];
        QCLASS[0] = 0;
        QCLASS[1] = 1;
        add_array_to_list(QCLASS, result_Byte);
        
        return (convert_to_byte_array(result_Byte));
    }
    
    public static byte[] assign_bytes(byte up_half, byte low_half)
    {
        byte[] result = new byte[2];
        result[0] = up_half;
        result[1] = low_half;
        return result;
    }
    
    public static void add_array_to_list(byte[] byteArray, ArrayList<Byte> arrayList_byte)
    {
        for (int i = 0; i < byteArray.length; i++)
        {
            arrayList_byte.add(byteArray[i]);
        }
    }
    
    public static byte[] convert_to_byte_array(ArrayList<Byte> arrayList_byte)
    {
        byte[] result = new byte[arrayList_byte.size()];
        for (int i = 0; i < arrayList_byte.size(); i++)
        {
            result[i] = arrayList_byte.get(i);
        }
        return result;
    }
}
