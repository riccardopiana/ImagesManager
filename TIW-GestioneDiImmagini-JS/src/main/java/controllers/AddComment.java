package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import beans.*;
import dao.*;
import utils.ConnectionHandler;

@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	public AddComment() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		Image image = new Image();
		boolean isBadRequest = false;
		User user =	(User) session.getAttribute("user");
		String text = null;
		Integer idImage = null;
		
		ImageDAO imageDAO=new ImageDAO(connection);
		CommentDAO commentDAO=new CommentDAO(connection);
		
		try {
			text = StringEscapeUtils.escapeJava(request.getParameter("text"));
			idImage =Integer.parseInt(request.getParameter("idImage"));
			
		}catch (NumberFormatException | NullPointerException ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}
		
		
		try {
			//If the text is blank throw an exception
			if(text==null) {
				isBadRequest=true;
			}else if( text.equals("")) {
				isBadRequest=true;
			}
			
		}catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("You cannot insert a blank comment");
			return;
		}
		
		
		try {
			//check if the id of the image exists
			image = imageDAO.findById(idImage);	
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Incorrect param values for  comment");
			return;
		}
		
		// If the text is more than 180 send an error message
					if (text.length()>180) {
						
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Text length max is 180");
						return;
						}
					try {
					commentDAO.addComment(text, user.getEmail(), idImage);
					}catch (SQLException e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.getWriter().println("Not able to create a new comment");
					}
		
		
		
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}