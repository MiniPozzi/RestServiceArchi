package com.rest.objects;

import com.rest.objects.FileShared;
import com.rest.objects.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public abstract class Manager {
    private static Connection connection = setConnection();

    /**
     * Fonction créant la connexion à la base de données
     * @return La connexion à la base de données
     */
    private static Connection setConnection() {
        try {
            Properties properties = new Properties();
            System.out.println("je suis là");
            properties.loadFromXML(new FileInputStream("properties.xml"));

            String url = properties.get("connection.url").toString();
            String password = properties.get("connection.password").toString();
            String username = properties.get("connection.username").toString();

            return DriverManager.getConnection(url, username, password);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fonction supprimant un utilisateur de la base de données
     * @param username Le nom d'utilisateur de l'utilisateur
     * @return Un booléen confirmant la suppression
     */
    public static boolean deleteUser(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM user WHERE username = ?");
            statement.setString(1, username);
            boolean success = statement.execute();

            connection.commit();
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fonction créant un utilisateur dans la base de données
     * @param user L'utilisateur à sauvegarder dans la base
     * @return Un booléen confirmant l'insertion
     */
    public static boolean createUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO user (username, password, firstName, lastName) VALUE (?,?,?,?)");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());

            boolean success = statement.execute();
            connection.commit();

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fonction retournant un utilisateur voulant se connecter à la base de données
     * @param username Le nom d'utilisateur
     * @param password Le mot de passe hashé
     * @return L'utilisateur si il existe sinon null
     */
    public static User logIn(String username, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT firstName, lastName FROM user WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            User user = null;
            while (resultSet.next())
                user = new User(username, password, resultSet.getString(1), resultSet.getString(2));

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fonction supprimant un fichier de la base de données
     * @param accessToken Le token permettant l'accès à ce fichier
     * @return Un booléen confirmant la suppression
     */
    public static boolean deleteFile(String accessToken) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM file WHERE token = ?");
            statement.setString(1, accessToken);
            boolean success = statement.execute();

            connection.commit();
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fonction insérant un fichier dans la base de données (fichier avec le chemin distant)
     * @param file Le fichier
     * @return Un booléen confirmant l'insertion
     */
    public static boolean uploadFile(FileShared file) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO file (filename, token) VALUE (?,?)");
            statement.setString(1, file.getFilename());
            statement.setString(2, file.getAccessToken());

            boolean success = statement.execute();
            connection.commit();

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fonction récupérant un fichier de la base de données afin de le télécharger
     * @param accessToken Le token d'accès au fichier
     * @return Le fichier avec le chemin distant
     */
    public static FileShared downloadFile(String accessToken) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT filename FROM file WHERE token = ?");
            statement.setString(1, accessToken);
            ResultSet resultSet = statement.executeQuery();

            FileShared file = null;
            while (resultSet.next()) {
                file = new FileShared(resultSet.getString(1));
                file.setAccessToken(accessToken);
            }

            return file;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

