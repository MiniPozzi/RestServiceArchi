package com.rest.services;


import com.rest.objects.Manager;
import com.rest.objects.User;
import com.rest.objects.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.UriInfo;

@Path("userService")
public class UserService {
    @Context
    private UriInfo context;

    public UserService() {
    }

    /**
     * Vérifie si le nom d'utilisateur et le mot de passe sont correct dans la bdd
     * @param username nom d'utilisateur
     * @param password mot de passe
     * @return renvoi une réponse en json avec response : "correct ou incorrect"
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public String authenticateUser(@FormParam("username") String username,
                                   @FormParam("password") String password) {

        try {
            boolean isCorrect  = authenticate(username, password);

            // Creation d'un JSON pour la réponse
            JSONObject object = new JSONObject();
            object.put("response", isCorrect);

            return object.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * Vérifie le nom d'utilisateur et le mot de passe dans la bdd
     * @param username (username)
     * @param password (not Hash)
     * @return vrai si username et password sont correct faux à l'inversse
     */
    private boolean authenticate(String username, String password) {
        // Hash the password in SHA-256
        String passwordClient = Utils.passwordDigest(password);
        User user = Manager.logIn(username, passwordClient);

        // Retourne false si le username ou password sont incorrect
        if (user == null)
            return false;
        return true;
    }

    /**
     * Création d'un utilisateur dans la BD
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @return vrai ou faux en format JSON
     */
    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@FormParam("username") String username,
                         @FormParam("password") String password,
                         @FormParam("fistName") String firstName,
                         @FormParam("lastName") String lastName) throws JSONException {

        User user = new User(username,Utils.passwordDigest(password),firstName,lastName);
        boolean isCorrect = Manager.createUser(user);

        // Creation d'un JSON pour la réponse
        JSONObject object = new JSONObject();
        object.put("response", isCorrect);
        return object.toString();
    }

    /**
     * Suppression d'un utilisateur avec son username
     * @param username
     * @return une réponse en Json soit vrai ou faux
     * @throws JSONException
     */
    @DELETE
    @Path("/deleteUser/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteUser(@PathParam("username") String username) throws JSONException {
        boolean isDeleted = Manager.deleteUser(username);

        // Creation d'un JSON pour la réponse
        JSONObject object = new JSONObject();
        object.put("response", isDeleted);
        return object.toString();
    }
}
