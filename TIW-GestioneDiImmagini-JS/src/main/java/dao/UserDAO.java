package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public void addUser(String email, String password, String name, String surname) throws SQLException {
		String query = "INSERT INTO User VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstatment = con.prepareStatement(query);) {
			pstatment.setString(1, email);
			pstatment.setString(2, password);
			pstatment.setString(3, name);
			pstatment.setString(4, surname);
			pstatment.executeUpdate();
		}
	}

	public User checkCredentials(String email, String password) throws SQLException {
		String query = "SELECT Email, Name, Surname FROM User  WHERE Email = ? AND Password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, email);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					User user = new User();
					user.setEmail(result.getString("email"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
}