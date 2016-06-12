import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TextServer
{
    public static ServerSocket SERVERSOCKET;
    public static boolean isRunning = true;

    public static void main (String[] args) throws IOException, BadLocationException
    {
        //Accepts the server socket on port 1000
        SERVERSOCKET = new ServerSocket(1000);
        Socket socket;
        System.out.println("Waiting for a android user...");

        //Launches the GUI
        GUI.main(args);

        //Breaks for some reason if this is not a loop
        while (isRunning)
        {
            socket = SERVERSOCKET.accept();
            TextServerThread server = new TextServerThread(socket);
            Thread t = new Thread(server);
            t.start();
        }

        //Closes the server socket so a JVM_Bind error does not occur next time
        SERVERSOCKET.close();
    }
}
