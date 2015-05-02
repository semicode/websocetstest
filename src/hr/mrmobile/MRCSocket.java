package hr.mrmobile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpSession;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class MRCSocket {
	private final CountDownLatch closeLatch;
 
	private HttpSession httpSession;
    private Session wsSession;
	
	
	
	public MRCSocket(HttpSession httpSession) {
		this.httpSession = httpSession;
		closeLatch = new CountDownLatch(1);
	}
	
	public Session getWsSession() {
		return this.wsSession;
	}
	
	public HttpSession getHttpSession() {
		return this.httpSession;
	}
	
	public String getUniqueId() {
        // unique ID from this class' hash code
        return Integer.toHexString(this.hashCode());
    }
	
    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }
	
	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.printf("Connection closed");
		ClientsHub.getInstance().removeClient(this);
		this.wsSession = null;
		this.httpSession = null;
		this.closeLatch.countDown();
	}
	
	@OnWebSocketConnect
	public void OnConnect(Session session) {
		System.out.printf("Connect");
		this.wsSession = session;
		
		this.wsSession.setIdleTimeout(Long.MAX_VALUE);
		ClientsHub.getInstance().addClient(this);
		try {
			Future<Void> fut = session.getRemote().sendStringByFuture("HELLO");
			/*fut.get(2, TimeUnit.SECONDS);
			fut = session.getRemote().sendStringByFuture("Thanks for conversation.");
			fut.get(2, TimeUnit.SECONDS);
			session.close(StatusCode.NORMAL, "I'm done");*/
		} catch (Throwable t) {
			t.printStackTrace();
		
		}
	}
	
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        if (session.isOpen()) {
        	if (message.equals("close")) {
        		try {
        			
        			Future<Void> fut = session.getRemote().sendStringByFuture("Closing");
        			
					fut.get(2, TimeUnit.SECONDS);
				
					session.close(StatusCode.NORMAL, "Client requested close and release.");
					
    			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	} else if (message.equals("image")) {
        		try {
        	
	        		String path = httpSession.getServletContext().getRealPath("/");
	        		File file = new File(path + "\\Capture.png");
	        		FileInputStream stream = new FileInputStream(file);
					
	        		byte[] data = new byte[(int)stream.getChannel().size()];
	        		DataInputStream dis = new DataInputStream(stream);
	        		
					dis.readFully(data);
					dis.close();
					
	        		Base64.Encoder encoder = Base64.getEncoder();
	        		String base64data =encoder.encodeToString(data);
	        		session.getRemote().sendString("data:image/png;base64," +base64data, null);
        		} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
        	} else {
        		System.out.printf("Echoing back message [%s]%n", message);
            
	            session.getRemote().sendString("Echo: " + message, null);
	            for (String uniqueId : ClientsHub.getInstance().getClients().keySet()) {
	            	Session otherSession = ClientsHub.getInstance().getClients().get(uniqueId).getWsSession();
	            	otherSession.getRemote().sendString("Broadcase: " + message, null);
	            }
	            
        	}
        }
    }
}