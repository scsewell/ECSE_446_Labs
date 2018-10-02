import org.apache.commons.cli.*;

public class Main
{
    public static void main(String[] args)
    {
        for (int i=0; i<args.length;i++){
            System.out.println(args[i]);
        }
        Options options = new Options();
        
        Option help = new Option("h", "help", false, "Show details for the command line options.");
        Option timeout = new Option("t", "timeout", true, "How long to wait, in seconds, before retransmitting an unanswered query. Default value is 5.");        
        Option retries = new Option("r", "max-retries", true, "The maximum number of times to retransmit an unanswered query before giving up. Default value is 3.");
        Option port = new Option("p", "port", true, "The UDP port number of the DNS server. Default value is 5.");
        
        help.setRequired(false);
        timeout.setRequired(false);
        retries.setRequired(false);
        port.setRequired(false);
        
        options.addOption(help);
        options.addOption(timeout);
        options.addOption(retries);
        options.addOption(port);
        
        OptionGroup queryType = new OptionGroup();
        Option mailServer = new Option("mx", "mail-server", false, "Send a mail server query.");
        Option nameServer = new Option("ns", "name-server", false, "Send a name server query.");

        queryType.setRequired(false);
        mailServer.setRequired(false);
        nameServer.setRequired(false);
        
        queryType.addOption(nameServer);
        queryType.addOption(mailServer);
        options.addOptionGroup(queryType);
        
        CommandLine cmd = null;
        try
        {
            cmd = new DefaultParser().parse(options, args);
        }
        catch (ParseException e)
        {
            System.out.println(e.getMessage());
            printHelp(options);
        }
        
        if (cmd.hasOption("h"))
        {
            printHelp(options);
        }

        String[] mainArgs = cmd.getArgs();

        Packet_Formatting packet = new Packet_Formatting();
        if (mainArgs.length < 2)
        {
            System.out.println("Required arguments are missing!");
            printHelp(options);
        }
        
        String serverAddr = mainArgs[0].replace("@", "");
        String domainName = mainArgs[1];
    }
    
    private static void printHelp(Options options)
    {
        new HelpFormatter().printHelp("DnsClient [-t timeout][-r retries][-p port][-mx|-ns] @server name", options);
        System.out.println("server: The IPv4 address of the DNS server, in a.b.c.d format. Starts with a @ symbol.");
        System.out.println("name: The domain name to query for.");
        System.exit(1);
    }
}
