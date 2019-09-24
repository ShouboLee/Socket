import java.io.*;
import java.net.*;
import java.util.*;


public class VerySimpleChatServer {
    ArrayList clientOutputStreams;

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSOcket) {
            try {
                sock = clientSOcket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {

                    System.out.println("read " + message);
                    tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        new VerySimpleChatServer().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList();
        Map<String, String> user = new HashMap();
        user.put("lsb", "123");
        user.put("tom", "admin");
        user.put("pony", "123456");
        try {
            ServerSocket serverSock = new ServerSocket(5000);
            while (true) {
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);


                System.out.println(clientSocket.getInetAddress().getHostAddress() + ".....connected");
                //判断用户名是否为空
                BufferedReader bufIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String name = bufIn.readLine();
                String password = null;
                if (name == null) {
                    System.out.println("程序结束");
                    break;
                }
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                //匹配用户名实现登录
                    System.out.println(name + "尝试登录");
                    out.println(name + "，输入口令：");

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                //判断口令是否为空
                password = bufIn.readLine();
                //System.out.println(user.get(name));
                if (password == null) {
                    System.out.println(",输入口令为空，程序结束");
                    break;
                }
                boolean p = false;
                //匹配用户名实现登录
                String passWord = String.valueOf(password);
                ;
                if (passWord.equals(user.get(name))) {
                    p = true;
                }
                if (p) {
                    System.out.println(name + "口令正确");
                    out.println(name + "，验证成功");
                } else {
                    System.out.println(name + "尝试登录失败");
                    out.println(name + "，验证失败，断开连接！");
                    //serverSock.close();
                    clientSocket.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}