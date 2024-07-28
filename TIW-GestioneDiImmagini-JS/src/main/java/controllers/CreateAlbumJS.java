package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
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
public class CreateAlbumJS extends HttpServlet {
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
		
		String title = null;
		List<Integer> idImage=new LinkedList<>();
		
		Map<String, String[]> allMap = request.getParameterMap();
		//System.out.print("parameters " + allMap.keySet().size());
		for (String key : allMap.keySet()) {
			String[] strArr = (String[]) allMap.get(key);
			for (String val : strArr) { 
			    if(key.equals("title")) {
			    	//System.out.println(val + " was the map " + key + " was the key");
			        title= val;
			     }else if(key.equals("id")) {
			    	 //System.out.println(val + " was the map " + key + " was the key");
			    	 try {
			        	idImage.add(Integer.parseInt(val));			        	
			    	 }catch (NumberFormatException | NullPointerException ex) {
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							response.getWriter().println("Incorrect or missing param values");
							return;
					}
			     }
			}
		}
		
		try {
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			if (title == null || title.isEmpty() || idImage.size() <= 0) {
				throw new Exception("Missing tile or ids");
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}
		
		int[] imageIds = new int[idImage.size()];
		for (int i = 0; i < idImage.size(); i++) {
			imageIds[i] = idImage.get(i);
		}
		
		User user = (User) session.getAttribute("user");
		AlbumDAO albumDAO = new AlbumDAO(connection);
		
		try {
			albumDAO.createAlbum(title, user.getEmail(), imageIds);
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
