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
    private final int m_queryType;
    private final int m_port;
    private final InetAddress m_address;
    private final String m_domainName;

    public Socket(int timeout, int retries, int queryType, int port, InetAddress address, String domainName)
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
        byte[] sendBuf = new byte[0];
        DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, m_address, m_port);
        
        // attempt to send the DNS query and get a response 
        int tries = m_retries;
        while (tries > 0)
        {
            try
            {
                // send the query
                socket.send(sendPacket);
                
                // get the response
                byte[] responseBuffer = new byte[MAX_RESPONSE_LENGTH];
                DatagramPacket packet = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(packet);
                
                // parse the response
                String output = new String(packet.getData(), 0, packet.getLength());
                System.out.println(output);
                
                // exit the loop
                break;
            }
            catch (IOException e)
            {
                System.out.println(e.toString());
            }
        }
        
        // close the socket
        socket.close();
    }
}
