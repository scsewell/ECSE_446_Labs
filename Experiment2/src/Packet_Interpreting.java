import static java.util.Arrays.copyOfRange;
import java.lang.StringBuilder;

public class Packet_Interpreting {

    public static byte[] full_received_packet;
    public static int current_answer_pointer;

    public static boolean AA;       //1: name server is authority for a domain name in question section
    public static String error;
    public static boolean isTruncated;
    public static String isRecursiveSupported;
    public static String RCODE_message;
    public static int ANCOUNT;
    public static int ARCOUNT;

    public static void main(String[] args, byte[] received_packet) {
        //Check Packet Header
        parse_Header(received_packet);
        //Format Packet Answer
        for (int i=0;i<ANCOUNT; i++){
            byte[] answer_packet = copyOfRange(received_packet,32, received_packet.length);
//            parse_Answer(answer_packet);
        }
        //Format Packet Authority - ignore
        //Format Packet Additional
    }

    public static void interpret_results(byte[] received_packet){
        //Check Packet Header
        full_received_packet = received_packet;
        parse_Header(received_packet);
        //count Question Section
        current_answer_pointer = 12 + name_width(12) + 1 + 4;
        //Format Packet Answer
        for (int i=0;i<ANCOUNT; i++){
            parse_Answer();
        }
        //Format Packet Authority - ignore
        //Format Packet Additional
    }

    public static void parse_Header(byte[] response) {
        //check if it's an answer
        if (!isResponse(response)){
            //TODO: return some kind of error?
        }
        //retrieve AA code
        AA = (response[2] >> 1 & 1) == 1;

        //retrieve isTruncated
        isTruncated = (response[2] >> 2 & 1) == 1;

        //retrieve RA
        set_RecursiveMessage((response[3] >> 7 & 1 ) == 1);

        //retrieve RCODE
        int RCODE = response[3] & 0b00001111;
        set_RCODE_message(RCODE);

        //retrieve ANCOUNT
        ANCOUNT = (response[6] << 8 ) + response[7];
        System.out.println(ANCOUNT);

        //retrieve ARCOUNT
        ARCOUNT = (response[10] << 8) + response[11];
        System.out.println(ARCOUNT);
    }

    public static void parse_Answer(){
        String domain_name;
        String NAME;
        int pointer_count = current_answer_pointer;
        int TTL;
        Qtype TYPE;
        int RDLENGTH;

        byte byte_0;
        byte byte_1;
        byte byte_2;
        byte byte_3;

        domain_name = get_name_field(pointer_count);
        pointer_count = current_answer_pointer + name_width(pointer_count);

        //TYPE field
        byte_0 = full_received_packet[pointer_count];
        byte_1 = full_received_packet[pointer_count + 1];
        TYPE = (Qtype.get_type((byte_0 << 8)+ to_unsigned(byte_1)));

        pointer_count = pointer_count + 2;

        //CLASS
        byte_0 = full_received_packet[pointer_count];
        byte_1 = full_received_packet[pointer_count +1];
        if (((byte_0 << 8)+ to_unsigned(byte_1)) != 0x0001){
            //TODO: print an error
        }

        pointer_count = pointer_count + 2;

        //TTL
        byte_0 = full_received_packet[pointer_count];
        byte_1 = full_received_packet[pointer_count +1];
        byte_2 = full_received_packet[pointer_count +2];
        byte_3 = full_received_packet[pointer_count +3];
        TTL = ((byte_0 << 24) + (byte_1 << 16) + (byte_2 << 8) + to_unsigned(byte_3));

        pointer_count = pointer_count + 4;

        //RDLENGTH
        byte_0 = full_received_packet[pointer_count];
        byte_1 = full_received_packet[pointer_count +1];
        RDLENGTH = (byte_0 << 8)+ to_unsigned(byte_1);

        pointer_count = pointer_count + 2;

        //RDATA
        if (TYPE == Qtype.typeA){
            byte_0 = full_received_packet[pointer_count];
            byte_1 = full_received_packet[pointer_count +1];
            byte_2 = full_received_packet[pointer_count +2];
            byte_3 = full_received_packet[pointer_count +3];

            get_ip_address(to_unsigned(byte_0),to_unsigned(byte_1),to_unsigned(byte_2),to_unsigned(byte_3));
        }
        if (TYPE == Qtype.typeNS || TYPE == Qtype.typeCNAME){
            NAME = get_name_field(pointer_count);
            pointer_count = current_answer_pointer + name_width(pointer_count);
        }
        if (TYPE == Qtype.typeMX){

        }
    }

    public static String get_ip_address(int one, int two, int three, int four){
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

    public static String get_name_field(int pointer_count){
        StringBuilder domain_name = new StringBuilder();
        byte byte_0;
        byte byte_1;

        while(pointer_count <= full_received_packet.length && full_received_packet[pointer_count] != 0){
            if (isCompressed(full_received_packet[pointer_count])){
                byte_0 = full_received_packet[pointer_count];
                byte_1 = full_received_packet[pointer_count + 1];
                byte_0 &= ~(3 << 6);        //invert the 1s
                pointer_count = (byte_0 << 8) + byte_1;        //find the pointed number
                continue;
            }
            int count = full_received_packet[pointer_count];

            for (int i=1;i<(count+1);i++){
                domain_name.append((char)full_received_packet[pointer_count+i]);
            }
            domain_name.append(".");
            pointer_count = pointer_count + count + 1;
            //as long as no pointer was used, count + 1 ( 3 w w w  = 3 + 1 )
        }
        domain_name.deleteCharAt(domain_name.length() -1);
        return domain_name.toString();
    }

    public static int name_width(int pointer_count){
        int name_bytecount = 0;

        while(pointer_count <= full_received_packet.length && full_received_packet[pointer_count] != 0){
            if (isCompressed(full_received_packet[pointer_count])){
                name_bytecount = name_bytecount + 2;
                break;
            }
            int count = full_received_packet[pointer_count];
            pointer_count = pointer_count + count + 1;
            //as long as no pointer was used, count + 1 ( 3 w w w  = 3 + 1 )
            name_bytecount = name_bytecount + count + 1;
        }

        return name_bytecount;
    }
    public static int to_unsigned(byte signed){
        return (signed & 0xFF);
    }

    public static boolean isCompressed(byte response){
        if (((response >> 6) & 3) == 3){
            return true;
        }
        return false;
    }

    public static boolean isResponse(byte[] response){
        return (response[2] >> 7 & 1) == 1;
    }

    public static void set_RecursiveMessage(boolean isRecursive){
        if (isRecursive){
            isRecursiveSupported = "Recursive queries are supported";
        }else {
            isRecursiveSupported = "Recursive queries are not supported";
        }
    }

    public static void set_RCODE_message(int code){
        if (code == 3){
            RCODE_message = "NOTFOUND";
        } else if (code != 0) {
            RCODE_message = "ERROR";
        }
    }
}
