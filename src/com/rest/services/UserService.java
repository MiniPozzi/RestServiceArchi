package com.rest.services;


import com.rest.objects.Manager;
import com.rest.objects.User;
import com.rest.objects.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String authenticateUser(@FormParam("username") String username,
                                   @FormParam("password") String password) {

        try {
            User user = authenticate(username, Utils.passwordDigest(password));

            // Creation d'un JSON pour la réponse
            JSONObject object = new JSONObject();

            if (user != null) {
                object.put("response", true);
                object.put("username",user.getUsername());
                object.put("lastName", user.getLastName());
                object.put("firstName", user.getFirstName());
                object.put("password", user.getPassword());
            }
            else
                object.put("response", false);

            System.out.println(object.toString());

            return object.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * Vérifie le nom d'utilisateur et le mot de passe dans la bdd
     * @param username Le surnom de l'utilisateur
     * @param password Mot de passe haché
     * @return L'utilisateur en question
     */
    private User authenticate(String username, String password) {

        User user = Manager.logIn(username, password);

        // Retourne false si le username ou password sont incorrect

        return user;
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
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String create(@FormParam("username") String username,
                         @FormParam("password") String password,
                         @FormParam("firstName") String firstName,
                         @FormParam("lastName") String lastName,
                         @Context HttpServletResponse servletResponse) throws JSONException {

        User user = new User(username,password,firstName,lastName);
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
