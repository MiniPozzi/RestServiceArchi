package com.rest.services;

import com.rest.objects.FileShared;
import com.rest.objects.Manager;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

@Path("fileService")
public class FileService {

    private static final String FILE_PATH = System.getenv("CATALINA_HOME") + "\\";

    /**
     * Permet de télécharger un fichier en donnant le token accès au fichier
     * @param token le lien d'accès
     * @return une réponse RESPONSE Not FOUND si le token est incorrect sinon télécharge le fichier
     */
    @GET
    @Path("/download/{token}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response downloadFile(@PathParam("token") String token) {
        FileShared fileShared = Manager.downloadFile(token);
        if (fileShared != null)
        {
            File file = new File(FILE_PATH+fileShared.getFilename());
            Response.ResponseBuilder response = Response.ok((Object) file);
            response.header("Content-Disposition", "attachment; filename=newfile.txt");

            return response.build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Met un fichier sur le serveur en lui ajoutant un token d'accès
     * @param fileInputStream
     * @param contentDispositionHeader
     * @return la réponse en JSON, vrai ou faux
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

        String filePath = FILE_PATH + contentDispositionHeader.getFileName();

        // Met le fichier en BD en lui ajoutant un token
        FileShared fileShared = new FileShared(contentDispositionHeader.getFileName());
        boolean isCorrect = Manager.uploadFile(fileShared);

        // Sauvegarde le fichier dans le serveur
        if (isCorrect)
            saveFile(fileInputStream, filePath);

        // Création d'un JSON pour la réponse
        JSONObject object = new JSONObject();
        try {
            object.put("response", isCorrect);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    /**
     * Sauvegarde un fichier dans le serveur
     * @param uploadedInputStream fichier à enregistrer
     * @param serverLocation chemin du répertoire du serveur
     */
    private void saveFile(InputStream uploadedInputStream,
                          String serverLocation) {
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream outputStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    /**
     * Suppression d'un fichier avec le token
     * @param token le token d'accès au fichier
     * @return une réponse en Json soit vrai ou faux
     * @throws JSONException
     */
    @DELETE
    @Path("/deleteFile/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteFile(@PathParam("token") String token) throws JSONException {
        //TODO faire la suppresion de fichier dans le serveur TOMCAT
        boolean isDeleted = Manager.deleteFile(token);

        // Creation d'un JSON pour la réponse
        JSONObject object = new JSONObject();
        object.put("response", isDeleted);
        return object.toString();
    }
}
