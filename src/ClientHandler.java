import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable{
    Scanner scn = new Scanner(System.in);
    private String name;
    public final DataInputStream dis;
    public final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    public static String url = "uploads//";
    public Sender sender;
    public Receiver receiver;

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos){
        this.s = s;
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.isloggedin = true;
        this.sender = new Sender(dos);
        this.receiver = new Receiver(dis);
    }

    public String getName(){
        return this.name;
    }

    @Override
    public void run() {
        String received;
        while(!this.s.isClosed()){
            try{
                received = this.dis.readUTF();
                System.out.println(received);
                if(received.equals("logout")){
                    this.isloggedin = false;
                    int index = 0;
                    for(ClientHandler x: Server.ar){
                        if(x.name.equals(this.name)){
                            Server.ar.remove(index);
                            break;
                        }
                        index++;
                    }
                    this.s.close();
                    break;
                }
                StringTokenizer  st = new StringTokenizer(received,"#");
                String headerRequest = "";
                String mainRequest = "";
                //truoc dau # la kieu request, sau dau thang la noi dung file,vv,..
                if(st.hasMoreTokens()){
                    headerRequest = st.nextToken();
                    if(st.hasMoreTokens()){
                        mainRequest = st.nextToken();
                    }
                }

                switch(headerRequest){
                    case "GET":
                        //input filename to thread sender
                        sender.setFilename(mainRequest);
                        sender.start();
                        break;
                    case "POST":
                        receiver.setFilename(mainRequest);
                        receiver.start();
                        break;
                    case "CONNECT":
                        final File folder = new File("uploads");
                        String[] listfile = folder.list();
                        for(int i = 0 ; i < listfile.length; i ++){
                            this.dos.writeUTF(listfile[i]);
                        }
                        break;
                    default:
                        break;
                }



            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}


class Sender extends Thread{
    private DataOutputStream dos;
    private String filename;
    public Sender(DataOutputStream dos){
        this.dos = dos;
        this.filename = "";
    }
    public void setFilename(String newname){
        this.filename = newname;
    }

    @Override
    public void run() {
        byte[] mybytearray = new byte[8192];
        File myFile;
        try{
            myFile = new File(ClientHandler.url + filename);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(myFile)));
        
        dos.writeUTF(filename);
        int read;
        while((read = dis.read(mybytearray)) != -1){
            this.dos.write(mybytearray, 0, read);
        }
        this.dos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

class Receiver extends Thread{
    private DataInputStream dis;
    public String filename;
    public Receiver(DataInputStream dis){
        this.dis = dis;
        this.filename = "";
    }

    public void setFilename(String filename) {
        this.filename = filename;
    } 

    @Override
    public void run() {
        int bufferSize = 8192;
        try{
            System.out.println(filename);
            int j = filename.lastIndexOf(".");
            String name = "";
            File temp;
            for(int i = 0 ; i < j; i ++){
                name += filename.toCharArray()[i];
            }
            do{
                temp = new File(ClientHandler.url + name + filename.substring(j));
                name += "x";
            }
            while(temp.exists());
            OutputStream output = new FileOutputStream(temp);
            byte[] buffer = new byte[bufferSize];
            int read;
            while((read = this.dis.read(buffer)) != -1){
                output.write(buffer, 0, read);
            }
            output.flush();
            output.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

