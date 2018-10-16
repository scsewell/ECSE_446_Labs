import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Socket
{
    private static final int MAX_RESPONSE_LENGTH = 2048;
    
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
            socket = new DatagramSocket();
            socket.setSoTimeout(m_timeout * 1000);
        }
        catch (SocketException e)
        {
            Logger.logError(e.toString());
            return;
        }
        
        // create the DNS query packet
        DatagramPacket sendPacket;
        try
        {
            byte[] sendBuf = PacketFormatting.createRequest(m_domainName, m_queryType);
            sendPacket = new DatagramPacket(sendBuf, sendBuf.length, m_address, m_port);
        }
	    catch (Exception e)
	    {
	    	socket.close();
            Logger.logError(e.toString());
	        return;
	    }
        
        // attempt to send the DNS query and get a response
        long startTime = System.nanoTime();
        int tries = 0;
        while (tries < m_retries)
        {
            try
            {
                // send the query
                socket.send(sendPacket);

                Logger.logQuery(m_domainName, m_address, m_queryType);
                
                // get the response
                byte[] responseBuffer = new byte[MAX_RESPONSE_LENGTH];
                DatagramPacket packet = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(packet);

                Logger.logPerformance((System.nanoTime() - startTime) / 1000000000.0, tries);
                
                // parse the response
                PacketInterpreting.interpretResults(packet.getData());
                
                // exit the loop
                socket.close();
                return;
            }
            catch (Exception e)
            {
                Logger.logError(e.toString());
            }
            tries++;
        }

        Logger.logError("Maximum number of retries (" + m_retries + ") exceeded.");
        socket.close();
    }
}
