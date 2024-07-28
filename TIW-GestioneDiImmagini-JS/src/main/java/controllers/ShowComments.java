package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.*;
import dao.*;
import utils.ConnectionHandler;

@WebServlet("/ShowComments")
public class ShowComments extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	

	public ShowComments() {
		super();
	}

	public void init() throws ServletException {
	
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		Integer imageId = null;
		try {
			imageId = Integer.parseInt(request.getParameter("imageId"));
				
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ImageDAO imagesDAO= new ImageDAO(connection);
		CommentDAO commentDAO= new CommentDAO(connection);
		Image image=null;
		List<Comment> comments=new ArrayList<>();
		try {
			image= imagesDAO.findById(imageId);
			comments=commentDAO.findByImage(imageId);
			
			if (image == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover album");
			return;
		}
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(comments);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
	
	}



	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}


