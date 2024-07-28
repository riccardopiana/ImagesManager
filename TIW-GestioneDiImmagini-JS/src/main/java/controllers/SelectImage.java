package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
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

@WebServlet("/SelectImage")
public class SelectImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	

	public SelectImage() {
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
			e.printStackTrace();
		}

		ImageDAO imagesDAO= new ImageDAO(connection);
		Image image=null;
		
		try {
			image= imagesDAO.findById(imageId);
			
			if (image == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Resource not found");
				return;
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(image);
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



