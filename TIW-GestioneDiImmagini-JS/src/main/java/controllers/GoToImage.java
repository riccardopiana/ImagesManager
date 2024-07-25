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

import dao.*;
import utils.ConnectionHandler;
import beans.*;

@WebServlet("/GoToImage")
public class GoToImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    public GoToImage() {
        super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		Integer imageId = null;
		try {
			imageId = Integer.parseInt(request.getParameter("imageId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect ImageId");
			return;
		}
		
		ImageDAO imageDAO = new ImageDAO(connection);
		CommentDAO commentDAO = new CommentDAO(connection);
		Image image = new Image();
		List<Comment> comments = new ArrayList<Comment>();
		int owner = 0;
		
		try {
			image = imageDAO.findById(imageId);
			session.setAttribute("image", image);
			if (image == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover image");
			return;
		}
		
		User user = (User) session.getAttribute("user");
		if (image.getUser().equals(user.getEmail())) {
			owner = 1;
		}
		
		try {
			session.setAttribute("image", image);
			comments = commentDAO.findByImage(imageId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover comments");
			return;
		}
		
		String path = "/WEB-INF/ImagePage.html";
		ServletContext servletContext = getServletContext();
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
