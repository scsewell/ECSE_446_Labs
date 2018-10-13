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
            parse_Answer(answer_packet);
        }
        //Format Packet Authority - ignore
        //Format Packet Additional
    }

    public static void interpret_results(byte[] received_packet){
        //Check Packet Header
        full_received_packet = received_packet;
        parse_Header(received_packet);
        //Format Packet Answer
        current_answer_pointer = 31;
        for (int i=0;i<ANCOUNT; i++){
            parse_Answer(full_received_packet);
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

    public static void parse_Answer(byte[] response){
        System.out.println("parsing answer");
        StringBuilder domain_name = new StringBuilder();
        int pointer_count = current_answer_pointer;

        while(full_received_packet[pointer_count] != 0){
            if (isCompressed(full_received_packet[pointer_count])){
                byte compressed_byte_1 = full_received_packet[pointer_count];
                byte compressed_byte_2 = full_received_packet[pointer_count + 1];
                compressed_byte_1 &= ~(3 << 6);        //invert the 1s
                pointer_count = (compressed_byte_1 << 8) + compressed_byte_2;        //find the pointed number
                continue;
            }
            int count = full_received_packet[pointer_count];

            for (int i=1;i<(count+1);i++){
                domain_name.append((char)full_received_packet[pointer_count+i]);
            }
            domain_name.append(".");
            pointer_count = pointer_count + count + 1;
        }
        domain_name.deleteCharAt(domain_name.length() -1);
        domain_name.toString();
    }

    //TODO: double check
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
