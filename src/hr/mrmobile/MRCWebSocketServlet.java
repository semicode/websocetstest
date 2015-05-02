package hr.mrmobile;

import javax.servlet.annotation.WebServlet;


import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;	

@SuppressWarnings("serial")
@WebServlet(name = "MyEcho WebSocket Servlet", urlPatterns = { "/echo" })
public class MRCWebSocketServlet extends WebSocketServlet {
	 @Override
	 public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(10000);
        
        factory.setCreator(new MRCSocketCreator());
        //factory.register(MRCSocket.class);
        
    }
}
