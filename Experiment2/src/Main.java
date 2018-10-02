import org.apache.commons.cli.*;

public class Main
{
    public static void main(String[] args)
    {
        for (int i=0; i<args.length;i++){
            System.out.println(args[i]);
        }
        Options options = new Options();

        Option input = new Option("t", "timeout", true, "How long to wait, in seconds, before retransmitting an unanswered query. Default value is 5.");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);

        //OptionGroup asd
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");

        System.out.println(inputFilePath);
        System.out.println(outputFilePath);

        Packet_Formatting packet = new Packet_Formatting();
    }
}
