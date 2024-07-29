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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.*;
import dao.*;
import utils.ConnectionHandler;

@WebServlet("/GetAlbumInfo")
public class GetAlbumInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetAlbumInfo() {
        super();
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		
		Integer albumId = null;
		
		try {
			albumId = Integer.parseInt(request.getParameter("albumId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		}
		
		
		Album album = new Album();
		AlbumDAO albumDAO = new AlbumDAO(connection);
		
		try {
			album = albumDAO.findById(albumId);
			 if (album == null) {
				 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				 response.getWriter().println("This album is empty!");
				 return;
			 }
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover album images");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(album);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
