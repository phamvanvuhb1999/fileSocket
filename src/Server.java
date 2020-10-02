import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    public static void main(String[] args) throws Exception{

        //uploads//?
        ServerSocket ss = new ServerSocket(1234);
        Socket s;
        while(true){
            s = ss.accept();
            System.out.println("New Client request received " + s);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating a new handler for client. . .");
            ClientHandler mtch = new ClientHandler(s,"client " + i,dis, dos);
            //create a thread with object
            Thread t = new Thread(mtch);

            System.out.println("Adding this client to active client list");
            ar.add(mtch);
            t.start();
            i++;
        }
        
    }
    static Vector<ClientHandler> ar = new Vector<>();
    static int i = 0;
}
