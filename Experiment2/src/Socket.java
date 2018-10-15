import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Socket
{
    private static final int MAX_RESPONSE_LENGTH = 576;
    
    private final int m_timeout;
    private final int m_retries;
    private final Qtype m_queryType;
    private final int m_port;
    private final InetAddress m_address;
    private final String m_domainName;

    public Socket(int timeout, int retries, Qtype queryType, int port, InetAddress address, String domainName)
    {
        m_timeout = timeout;
        m_retries = retries;
        m_queryType = queryType;
        m_port = port;
        m_address = address;
        m_domainName = domainName;
    }
    
    public void run()
    {
        // create the socket
        DatagramSocket socket;
        try
        {
            socket = new DatagramSocket(m_port);
            socket.setSoTimeout(m_timeout * 1000);
        }
        catch (SocketException e)
        {
            System.out.println(e.toString());
            return;
        }
        
        // create the DNS query packet
        byte[] sendBuf = Packet_Formatting.createRequest(m_queryType, m_domainName);
        DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, m_address, m_port);
        
        // attempt to send the DNS query and get a response
        long startTime = System.nanoTime();
        long endTime = 0;
        int tries = m_retries;
        while (tries > 0)
        {
            try
            {
                // send the query
                socket.send(sendPacket);

                Logger.query_summary(m_domainName, m_address.getHostAddress(), m_queryType.toString());
                
                // get the response
                byte[] responseBuffer = new byte[MAX_RESPONSE_LENGTH];
                DatagramPacket packet = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(packet);

                endTime = System.nanoTime() - startTime;
                double seconds_elapsed = (double)endTime / 1000000000.0;
                Logger.performance(seconds_elapsed,tries);
                
                // parse the response
                String output = new String(packet.getData(), 0, packet.getLength());
                //System.out.println(output);
                Packet_Interpreting.interpret_results(packet.getData());

                //System.out.println(output);
                
                // exit the loop
                break;
            }
            catch (IOException e)
            {
                System.out.println(e.toString());
            }
            tries--;
        }
        // close the socket
        socket.close();
    }
}
