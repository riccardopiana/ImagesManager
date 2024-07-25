package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Comment;

public class CommentDAO {
	private Connection connection;

	public CommentDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Comment> findByImage(Integer imageId) throws SQLException {
		List<Comment> comments = new ArrayList<Comment>();
		String query = "SELECT * FROM Comment WHERE Image = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setLong(1, imageId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Comment comment = new Comment();
					comment.setId(result.getInt("id"));
					comment.setText(result.getString("text"));
					comment.setUser(result.getString("user"));
					comment.setImage(result.getInt("image"));
					comments.add(comment);
				}
			}
		}
		return comments;
	}

	public void addComment(String text, String email, int imageId) throws SQLException {
		String query = "INSERT into Comment (Text, User, Image) VALUES (?, ?, ?)";
		try (PreparedStatement pstatment = connection.prepareStatement(query);) {
			pstatment.setString(1, text);
			pstatment.setString(2, email);
			pstatment.setLong(3, imageId);
			pstatment.executeUpdate();
		}
	}

}
