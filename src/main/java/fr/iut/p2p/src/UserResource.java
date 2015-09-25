package fr.iut.p2p.src;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Ressource User (accessible avec le chemin "/users")
 */
@Path("users")
public class UserResource {
	// Pour l'instant, on se contentera d'une variable statique pour conserver
	// l'etat
	private static Map<String, User> users = new HashMap<>();

	// L'annotation @Context permet de recuperer des informations sur le
	// contexte d'execution de la ressource.
	// Ici, on recupere les informations concernant l'URI de la requête HTTP, ce
	// qui nous permettra de manipuler
	// les URI de maniere generique.
	@Context
	public UriInfo uriInfo;

	/**
	 * Une ressource doit avoir un contructeur (eventuellement sans arguments)
	 */
	public UserResource() {
	}

	/**
	 * Methode de creation d'un utilisateur qui prend en charge les requetes
	 * HTTP POST La methode renvoie l'URI de la nouvelle instance en cas de
	 * succes
	 * 
	 * @param user
	 *            Instance d'utilisateur à creer
	 * @return Response le corps de la reponse est vide, le code de retour HTTP
	 *         est fixe à 201 si la creation est faite L'en-tete contient un
	 *         champs Location avec l'URI de la nouvelle ressource
	 */
	@POST
	public Response createUser(User user) {
		// Si l'utilisateur existe deja, renvoyer 409
		if (users.containsKey(user.getLogin())) {
			return Response.status(Response.Status.CONFLICT).build();
		} else {
			users.put(user.getLogin(), user);

			// On renvoie 201 et l'instance de la ressource dans le Header HTTP
			// 'Location'
			URI instanceURI = uriInfo.getAbsolutePathBuilder()
					.path(user.getLogin()).build();
			return Response.created(instanceURI).build();
		}
	}
}