import static java.util.Arrays.copyOfRange;

public class Packet_Interpreting {

    public static boolean AA;       //1: name server is authority for a domain name in question section
    public static String error;
    public static boolean isTruncated;
    public static String isRecursiveSupported;
    public static String RCODE_message;
    public static int ANCOUNT;
    public static int ARCOUNT;

    public static void main(String[] args, byte[] received_packet) {
        //Question: Should we interpret the packet Header and packet Questions as well?
        //          or is everything already in Packet answer?

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
        ANCOUNT = response[6] >> 7 + response[7];

        //retrieve ARCOUNT
        ARCOUNT = response[11] >> 7 + response[12];
    }

    public static void parse_Answer(byte[] response){
        if (isCompressed(response)){

        } else {

        }
    }

    //TODO: double check
    public static boolean isCompressed(byte[] response){
        if ((response[0] << 7 &1) == 1 && (response[0] << 6 &1) == 1){
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
