import java.net.InetAddress;
import org.apache.commons.cli.*;

public class DnsClient
{
    public static void main(String[] args)
    {
        // specify command line options
        Options options = new Options();
        
        Option helpOption = new Option("h", "help", false, "Show details for the command line options.");
        Option timeoutOption = new Option("t", "timeout", true, "How long to wait, in seconds, before retransmitting an unanswered query. Default value is 5.");        
        Option retriesOption = new Option("r", "max-retries", true, "The maximum number of times to retransmit an unanswered query before giving up. Default value is 3.");
        Option portOption = new Option("p", "port", true, "The UDP port number of the DNS server. Default value is 5.");
        
        helpOption.setRequired(false);
        timeoutOption.setRequired(false);
        retriesOption.setRequired(false);
        portOption.setRequired(false);
        
        options.addOption(helpOption);
        options.addOption(timeoutOption);
        options.addOption(retriesOption);
        options.addOption(portOption);
        
        OptionGroup queryTypeOption = new OptionGroup();
        Option mailServerOption = new Option("mx", "mail-server", false, "Send a mail server query.");
        Option nameServerOption = new Option("ns", "name-server", false, "Send a name server query.");

        queryTypeOption.setRequired(false);
        mailServerOption.setRequired(false);
        nameServerOption.setRequired(false);
        
        queryTypeOption.addOption(nameServerOption);
        queryTypeOption.addOption(mailServerOption);
        options.addOptionGroup(queryTypeOption);
        
        // read the provided options
        CommandLine cmd = null;
        try
        {
            cmd = new DefaultParser().parse(options, args);
        }
        catch (ParseException e)
        {
        	Logger.logError(e.getMessage());
            printHelp(options);
        }
        
        // show the help if requested
        if (cmd.hasOption("h"))
        {
            printHelp(options);
        }

        // get the required arguments
        String[] mainArgs = cmd.getArgs();
        if (mainArgs.length < 2)
        {
        	Logger.logError("Required arguments are missing!");
            printHelp(options);
        }
        
        InetAddress address = null;
        try
        {
            String[] serverAddr = mainArgs[0].replace("@", "").split("\\.");
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
            printHelp(options);
        }
        
        String domainName = mainArgs[1];
        
        // get the optional arguments
        int timeout = 5;
        if (cmd.hasOption(timeoutOption.getArgName()))
        {
            timeout = Integer.parseInt(cmd.getOptionValue(timeoutOption.getArgName()));
        }

        int retries = 3;
        if (cmd.hasOption(retriesOption.getArgName()))
        {
            retries = Integer.parseInt(cmd.getOptionValue(retriesOption.getArgName()));
        }

        int port = 53;
        if (cmd.hasOption(portOption.getArgName()))
        {
            port = Integer.parseInt(cmd.getOptionValue(portOption.getArgName()));
        }
        
        Qtype queryType = Qtype.A;
        if (cmd.hasOption(mailServerOption.getArgName()))
        {
            queryType = Qtype.MX;
        }
        else if (cmd.hasOption(nameServerOption.getArgName()))
        {
            queryType = Qtype.NS;
        }
        
        // start the socket
        Socket socket = new Socket(timeout, retries, queryType, port, address, domainName);
        socket.run();
    }
    
    private static void printHelp(Options options)
    {
        new HelpFormatter().printHelp("DnsClient [-t timeout][-r retries][-p port][-mx|-ns] @server name", options);
        System.out.println("server: The IPv4 address of the DNS server, in a.b.c.d format. Starts with a @ symbol.");
        System.out.println("name: The domain name to query for.");
        System.exit(1);
    }
}
