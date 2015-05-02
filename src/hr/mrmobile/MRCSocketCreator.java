package hr.mrmobile;

import javax.servlet.http.HttpSession;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class MRCSocketCreator implements WebSocketCreator {
	
	@Override
	public Object createWebSocket(ServletUpgradeRequest req,
			ServletUpgradeResponse resp) {
		HttpSession httpSession = (HttpSession)req.getSession();
		for (String subprotocol : req.getSubProtocols()) {
			if ("text".equals(subprotocol)) {
				resp.setAcceptedSubProtocol(subprotocol);
				return new MRCSocket(httpSession);		
			}
			/*
			 * if ("binary".equals(subprotocol))
			 */
		}
		return null;
		
	}

}
