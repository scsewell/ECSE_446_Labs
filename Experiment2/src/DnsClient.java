import java.net.InetAddress;

public class DnsClient
{
    public static void main(String[] args)
    {
        // show the help if requested
        if (hasArg(args, "-h"))
        {
            printHelp();
        }

        // get the required arguments
        InetAddress address = null;
        try
        {
            String[] serverAddr = args[args.length - 2].replace("@", "").split("\\.");
            byte[] addr = new byte[serverAddr.length];
            for (int i = 0; i < addr.length; i++)
            {
                addr[i] = (byte)Integer.parseInt(serverAddr[i]);
            }
            address = InetAddress.getByAddress(addr);
        }
        catch (Exception e)
        {
        	Logger.logError("Invalid server address provided: " + e.toString());
            printHelp();
        }
        
        String domainName = args[args.length - 1];
        
        // get the optional arguments
        int timeout = getArgValue(args, "-t", 5, 0, 1000);
        int retries = getArgValue(args, "-r", 3, 0, 1000);
        int port = getArgValue(args, "-p", 53, 0, 65535);
        
        Qtype queryType = Qtype.A;
        if (hasArg(args, "-mx"))
        {
            queryType = Qtype.MX;
        }
        else if (hasArg(args, "-ns"))
        {
            queryType = Qtype.NS;
        }
        
        // start the socket
        Socket socket = new Socket(timeout, retries, queryType, port, address, domainName);
        socket.run();
    }
    
    private static void printHelp()
    {
        System.out.println();
        System.out.println("DnsClient [-t timeout][-r retries][-p port][-mx|-ns] @server name");
        System.out.println("-h,   help:          Show this help.");
        System.out.println("-t,   timeout:       How long to wait, in seconds, before retransmitting an unanswered query. Default value is 5.");
        System.out.println("-r,   retries:       The maximum number of times to retransmit an unanswered query before giving up. Default value is 3.");
        System.out.println("-p,   port:          The UDP port number of the DNS server. Default value is 5.");
        System.out.println("-mx,  mail-server:   Send a mail server query.");
        System.out.println("-ns,  name-server:   Send a name server query.");
        System.out.println("server:              The IPv4 address of the DNS server, in a.b.c.d format. Starts with a @ symbol.");
        System.out.println("name:                The domain name to query for.");
        System.out.println();
        System.exit(1);
    }
    
    private static boolean hasArg(String[] args, String flag)
    {
    	for (String arg : args)
    	{
    		if (arg.equals(flag)) 
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    private static int getArgValue(String[] args, String flag, int defaultValue, int minValue, int maxValue)
    {
    	for (int i = 0; i < args.length; i++)
    	{
			if (args[i].equals(flag) && (i + 1) < args.length)
			{
				int value = Integer.parseInt(args[i + 1]);
				if (value < minValue || value < maxValue)
				{
					return value;
				}
		        Logger.logError("Value for flag \"" + flag + "\" is not in the range of valid inputs [" + minValue + ", " + maxValue +"]!");
		        System.exit(1);
			}
		}
    	return defaultValue;
    }
}
