package hr.mrmobile;

import java.io.IOException;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="test", urlPatterns={"/test"})
public class MRCServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			  throws ServletException, IOException {
		 resp.setContentType("text/html");
		 PrintWriter pw = resp.getWriter();
		 req.getSession().setAttribute("test", "test");
		 pw.write("Dummy response");
		 pw.close();
		 
	}

}
