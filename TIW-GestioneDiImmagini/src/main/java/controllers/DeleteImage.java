package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Album;
import beans.Image;
import beans.User;
import dao.ImageDAO;
import utils.ConnectionHandler;

@WebServlet("/DeleteImage")
public class DeleteImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	String folderPath = "";
	
	public DeleteImage() {
		super();
	}

	public void init() throws ServletException {
		folderPath = getServletContext().getInitParameter("uploadLocation");
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
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect imageId");
			return;
		}
		
		Image image = null;
		ImageDAO imageDAO = new ImageDAO(connection);
		
		try {
			image = imageDAO.findById(imageId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image not exist");
			return;
		}
		
		User user = (User) session.getAttribute("user");
		if (!image.getUser().equals(user.getEmail())) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User not allowed");
			return;
		}
		
		try {
			imageDAO.deleteImage(imageId);
		} catch (SQLException e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to delete image");
			return;
		}

		File file = new File(folderPath, image.getPath());
		
		try {
			if (!file.delete()) {
				throw new Exception("Not possible to delete image");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing image path");
			return;
		}
		Album album = (Album) session.getAttribute("album");
		response.sendRedirect(getServletContext().getContextPath() + "/GoToAlbum?albumId=" + album.getId());
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
