import java.net.*;
import java.io.*;


//可以设计成如 $java Redirector 103 6667
//main接收两个参数，作为两个端口
//第一个端口重定向到第二个端口
//默认这两个端口都在本机
//相当于这既是一个Server，也是一个Client
public class Redirector extends Thread{

	private ServerSocket serverSocket;
	private InputStream inputStream;
	private OutputStream outputStream;

	private int port_in;
	private int port_out;

	public Redirector(int port_in, int port_out) throws IOException {
		this.port_in = port_in;
		this.port_out = port_out;

		serverSocket = new ServerSocket(port_in);
		//serverSocket.setTimeOut(10000);
	}


	public static void main(String[] args) throws IOException{

		int port_in = Integer.parseInt(args[0]);
		int port_out = Integer.parseInt(args[1]);

		Thread t = new Redirector(port_in, port_out);
		t.run();
	}


	@Override
	public void run(){
		while (true) {
			try{
				System.out.println("正在监听端口 " +  serverSocket.getLocalPort() + " ...");
				Socket server = serverSocket.accept(); //Waiting for connection
				System.out.println("Connection established ! The remote host address is " + server.getRemoteSocketAddress());
				this.inputStream = server.getInputStream();
				System.out.println("收到来自端口 " + serverSocket.getLocalPort() + " 的数据");

				redirect(this.inputStream);
				//server.close();

			} catch(IOException e){
				e.printStackTrace();
				break;
			} 
		}
	}

	//redirect the inputstream received from port_in to port_out
	private void redirect(InputStream is){

		String serverName = "127.0.0.1";

		try{
			System.out.println("Trying to connect to " + serverName + " , Port: " + this.port_out);
			Socket client  = new Socket(serverName, this.port_out);
			System.out.println("Connection established " + serverName + " , Port: " + this.port_out);

			byte[] b = new byte[8192];//开辟缓冲区
			is.read(b); //从inputstream里读取然后存到缓冲区

			OutputStream os = client.getOutputStream();

			os.write(b); //写入缓冲区
			os.flush();//手动发送出去

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}