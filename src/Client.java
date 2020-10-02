import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    static int ServerPort = 1234;

    public static void main(String[] args) throws Exception{
        File file = new File("downloads//panningtree.pdf");
        //System.out.println(file.exists());
        InetAddress ip = InetAddress.getByName("localhost");
        Socket s = new Socket(ip, ServerPort);

        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        dos.writeUTF("POST#" + "panningtree.pdf");
        byte[] mybytearray = new byte[8192];
        //try{
        int read;
        while((read = dis.read(mybytearray)) != -1){
            dos.write(mybytearray, 0, read);
        }
        dos.flush();
        dos.close();
        // }catch(Exception e){
        //     e.printStackTrace();
        // }
        //s.close();
        
    }
}
