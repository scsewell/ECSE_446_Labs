public class Logger {
    public static void main (String [] args){

    }
    public static void query_summary(String name, String ip, String type){
        System.out.println("DnsClient sending request for " + name);
        System.out.println("Server: " + ip);
        System.out.println("Request type: " + type);
    }
    public void performance(int ttl, int num_retries){
        System.out.println("Response received after " + Integer.toString(ttl) + "seconds (" + Integer.toString(num_retries) + ")");
    }
    public static void answer_section(int num_answer){
        System.out.println("***Answer Section(" + Integer.toString(num_answer) + " records)***" );
    }
    public static void type_format(String type, String alias, int time, boolean auth){
        System.out.println(type + "\t" + alias + "\t" + time + "\t" + auth);
    }
    public static void MX_format(String alias, int pref, int time, boolean auth){
        System.out.println("MS \t" + alias + "\t" + pref + "\t" + time + "\t" + auth);
    }
    public void additional_seciont(int num_additional){
        System.out.println("***Additional Section(" + Integer.toString(num_additional) + " records)***" );
    }
    public void no_records(){
        System.out.println("NOTFOUND");
    }

}
