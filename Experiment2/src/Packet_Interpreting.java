public class Packet_Interpreting {

    public static boolean AA;       //1: name server is authority for a domain name in question section
    public static String error;
    public static boolean isTruncated;

    public static void main(String[] args, byte[] received_packet) {
        //Question: Should we interpret the packet Header and packet Questions as well?
        //          or is everything already in Packet answer?

        //Check Packet Header
        check_Header(received_packet);
        //Format Packet Answer
        //Format Packet Authority - ignore
        //Format Packet Additional
    }

    public static void check_Header(byte[] response) {
        //check if it's an answer
        if (!isResponse(response)){
            //TODO: return some kind of error?
        }
        //retrieve AA code
        AA = (response[2] >> 1 & 1) == 1;

        //retrieve isTruncated
        isTruncated = (response[2] >> 2 & 1) == 1;
    }

    public static boolean isResponse(byte[] response){
        return (response[2] >> 7 & 1) == 1;
    }
}
