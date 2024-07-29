package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

@WebServlet("/CreateAlbumJS")
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public CreateAlbum() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		
		String title = null;
		List<Image> images = new ArrayList<>();
		ImageDAO imageDAO = new ImageDAO(connection);
		User user = (User) session.getAttribute("user");
		
		Map<String, String[]> allMap = request.getParameterMap();
		
		for (String key : allMap.keySet()) {
			String[] strArr = (String[]) allMap.get(key);
			for (String val : strArr) { 
			    if(key.equals("title")) {
			    	
			     }else if(key.equals("id")) {
			    	 Integer id = null;
			    	 Image image = null;
			    	 try {
			    		 id = Integer.parseInt(val);			        	
			    	 }catch (NumberFormatException | NullPointerException ex) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Incorrect or missing param values");
						return;
					}
			    	try {
			    		image = imageDAO.findById(id);
			    		if (!user.getEmail().equals(image.getUser())) { 
			    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.getWriter().println("User not allowed");
							return;
			    		}
			    	} catch (Exception e) {
			    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Not possible to recove images");
						return;
			    	}
			    	images.add(image);
			     }
			}
		}
		
		try {
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			if (title == null || title.isEmpty() || images.size() <= 0) {
				throw new Exception("Missing tile or ids");
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}
		
		AlbumDAO albumDAO = new AlbumDAO(connection);

		try {
			albumDAO.createAlbum(title, user.getEmail(), images);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Paramters for albums are incorrect");
			return;
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
