package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.*;
import dao.*;
import utils.ConnectionHandler;

@WebServlet("/ReorderAlbum")
public class ReorderAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		
		Integer albumId = null;
		Map<Integer, Image> imagesOrder = new HashMap<Integer, Image>();
		ImageDAO imageDAO = new ImageDAO(connection);

		User user = (User) session.getAttribute("user");
		
		try {
			albumId = Integer.parseInt(request.getParameter("albumId"));
		} catch (NumberFormatException | NullPointerException ex) {
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
    	}
		
		AlbumDAO albumDAO = new AlbumDAO(connection);
		List<Image> imagesOfAlbum = new ArrayList<>();
		
		try {
			albumDAO.findById(albumId);
			imagesOfAlbum = imageDAO.findByAlbum(albumId, user.getEmail());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		response.getWriter().println("Not possible to recove album");
    		return;
		}
		
		
		Map<String, String[]> allMap = request.getParameterMap();
		
		for (String key : allMap.keySet()) {
			String[] strArr = (String[]) allMap.get(key);
			for (String val : strArr) {
				Integer id = null;
		    	Image image = null;
		    	Integer position = null;
		    	
			    if(key.equals("id")) {
			    	try {
			    		id = Integer.parseInt(val.substring(0, val.indexOf("-")));
			    		position = Integer.parseInt(val.substring(val.indexOf("-") + 1, val.length()));
			    	} catch (NumberFormatException | NullPointerException ex) {
			    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Incorrect or missing param values");
						return;
			    	}
			    	try {
			    		image = imageDAO.findById(id);
			    		Boolean found = false;
			    		for (Image i : imagesOfAlbum) {
			    			if (i.getId() == image.getId()) {
			    				found = true;
			    			}
			    		}
			    		if (!found) {
			    			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				    		response.getWriter().println("Images not of this album");
				    		return;
			    		}
			    	} catch (Exception e) {
			    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			    		response.getWriter().println("Not possible to recove images");
			    		return;
			    	}
			   		imagesOrder.put(imagesOfAlbum.size() - 1 - position, image);
		    	}
			}
		}
		
		try {
			albumDAO.reorderAlbum(albumId, imagesOrder, user.getEmail());
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
