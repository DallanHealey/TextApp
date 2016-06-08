import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TextServer
{
    public static ServerSocket SERVERSOCKET;
    public static boolean isRunning = true;

    public static void main(String[] args) throws IOException, BadLocationException
    {
        SERVERSOCKET = new ServerSocket(1000);
        Socket socket;
        System.out.println("Waiting for a android user...");

        while (isRunning)
        {
            socket = SERVERSOCKET.accept();
            TextServerThread server = new TextServerThread(socket);
            Thread t = new Thread(server);
            t.start();
        }

        TextServer.isRunning = false;
        SERVERSOCKET.close();
    }
}
