import java.net.InetAddress;

public class Logger 
{
	public static void logError(String message)
	{
		System.out.println("Error \t" + message);
	}
	
	public static void logQuery(String name, InetAddress ip, Qtype type)
	{
		System.out.println("DnsClient sending request for " + name);
		System.out.println("Server: " + ip.getHostAddress());
		System.out.println("Request type: " + type);
		System.out.println();
	}

	public static void logPerformance(double duration, int numRetries)
	{
		System.out.println("Response received after " + Double.toString(duration) + " seconds (" + Integer.toString(numRetries) + " retries)");
		System.out.println();
	}

	public static void logAnswerSection(int numAnswer)
	{
		System.out.println("***Answer Section (" + Integer.toString(numAnswer) + " records)***");
	}

	public static void logAnswer(Qtype type, String alias, int time, boolean auth)
	{
		String authority = auth ? "auth" : "nonauth";
		System.out.println(type + "\t" + alias + "\t" + time + "\t" + authority);
	}

	public static void logMX(String alias, int pref, int time, boolean auth)
	{
		String authority = auth ? "auth" : "nonauth";
		System.out.println("MS \t" + alias + "\t" + pref + "\t" + time + "\t" + authority);
	}

	public static void logAdditionalSection(int num_additional)
	{
		System.out.println();
		System.out.println("***Additional Section (" + Integer.toString(num_additional) + " records)***");
	}

	public static void logNoRecords()
	{
		System.out.println("NOTFOUND");
	}
}
