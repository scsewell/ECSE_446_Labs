public class Logger {
    public static void main (String [] args){

    }
    public static void query_summary(String name, String ip, String type){
        System.out.println("DnsClient sending request for " + name);
        System.out.println("Server: " + ip);
        System.out.println("Request type: " + type);
        System.out.println();
    }
    public static void performance(double ttl, int num_retries){
        System.out.println("Response received after " + Double.toString(ttl) + " seconds (" + Integer.toString(num_retries) + " retries)");
        System.out.println();
    }
    public static void answer_section(int num_answer){
        System.out.println("***Answer Section(" + Integer.toString(num_answer) + " records)***" );
    }
    public static void type_format(String type, String alias, int time, boolean auth){
        String authority = "";
        if (!auth){
            authority = "nonauth";
        } else {
            authority = "auth";
        }
        System.out.println(type + "\t" + alias + "\t" + time + "\t" + authority);
    }
    public static void MX_format(String alias, int pref, int time, boolean auth){
        String authority = "";
        if (!auth){
            authority = "nonauth";
        } else {
            authority = "auth";
        }
        System.out.println("MS \t" + alias + "\t" + pref + "\t" + time + "\t" + authority);
    }
    public static void additional_section(int num_additional){
        System.out.println();
        System.out.println("***Additional Section(" + Integer.toString(num_additional) + " records)***" );
    }
    public static void no_records(){
        System.out.println("NOTFOUND");
    }

}
