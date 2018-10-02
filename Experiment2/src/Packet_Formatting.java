import java.util.Random;

//Responsible for formatting the packet
public class Packet_Formatting {

    public static void main(String[] args) {
        //Format Packet Header
        System.out.println(header());
        for(byte x: header()){
            System.out.println(x);
        }
        //Format Packet Question
        //Format Packet Answer
        //Format Packet Authority
        //Format Packet Additional
    }

    public static byte[] header() {
        byte[] id = new byte[2];
        Random random_num = new Random();
        random_num.nextBytes(id);
        return id;
    }
}
