package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

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

@WebServlet("/CreateAlbum")
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		String title = null;
		int[] imageIds = null;
		try {
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			imageIds = Stream.of(request.getParameterValues("checkedImageIds")).mapToInt(Integer:: parseInt).toArray();
			if (title == null || imageIds == null || title.isEmpty() || imageIds.length <= 0) {
				throw new Exception("Missing tile or ids");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file or title");
			return;
		}
		
		User user = (User) session.getAttribute("user");
		ImageDAO imageDAO = new ImageDAO(connection);
		AlbumDAO albumDAO = new AlbumDAO(connection);
		
		//TODO
		/*Album newAlbum = null;
		try {
			albumDAO.createAlbum(title, user.getEmail());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create Album");
			return;
		}
		for (int id : imageIds) {
			try {
				imageDAO.addToAlbum(id, newAlbum.getId());
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create Album");
				return;
			}
		}*/
		
		request.getSession().setAttribute("user", user);
		response.sendRedirect(getServletContext().getContextPath() + "/GoToHome");
	}

}
