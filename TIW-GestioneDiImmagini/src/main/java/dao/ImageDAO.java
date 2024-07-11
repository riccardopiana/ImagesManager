package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Image;

public class ImageDAO {
	private Connection connection;
	
	public ImageDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Image> findByUser(String email) throws SQLException{
		List<Image> images = new ArrayList<Image>();
		String query = "SELECT * FROM Image WHERE User = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, email);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Image image = new Image();
					image.setId(result.getInt("id"));
					image.setPath(result.getString("path"));
					image.setTitle(result.getString("title"));
					image.setCreationDate(result.getDate("creation date"));
					image.setDescription(result.getString("description"));
					image.setUser(result.getString("user"));
					images.add(image);
				}
			}
		}
		return images;
	}

	public void addImage(String title, String description, String outputPath, String email) throws SQLException{
		String query = "INSERT into Image (Title, Description, Path, User) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstatment = connection.prepareStatement(query);) {
			pstatment.setString(1, title);
			pstatment.setString(2, description);
			pstatment.setString(3, outputPath);
			pstatment.setString(4, email);
			pstatment.executeUpdate();
		}
	}

	public void addToAlbum(int imageId, int albumId) throws SQLException{
		String query = "INSERT into ImageOfAlbum VALUES (?, ?)";
		try (PreparedStatement pstatment = connection.prepareStatement(query);) {
			pstatment.setInt(1, albumId);
			pstatment.setInt(2, imageId);
		}
	}

}
