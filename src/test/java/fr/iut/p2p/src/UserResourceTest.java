package fr.iut.p2p.src;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class UserResourceTest extends JerseyTest {

	/**
	 * Il est obligatoire de redefinir cette methode qui permet de configurer le
	 * contexte de Jerse
	 */
	@Override
	protected Application configure() {
		return new ResourceConfig(User.class);
	}

	/**
	 * Test de creation d'un utilisateur (retour HTTP et envoi de l'URI de la
	 * nouvelle instance)
	 */
	@Test
	public void testCreateUser() {
		User user = new User("jsteed", "Steed", "jsteed@mi5.uk");
		// Conversion de l'instance de User au format JSON pour l'envoi
		Entity<User> userEntity = Entity.entity(user,
				MediaType.APPLICATION_JSON);

		// Envoi de la requete HTTP POST pour la creation de l'utilisateur
		Response response = target("/users").request().post(userEntity);

		// Verification du code de retour HTTP
		assertEquals(201, response.getStatus());

		// Verification que la creation renvoie bien l'URI de la nouvelle
		// instance dans le header HTTP 'Location'
		// ici : http://localhost:8080/users/jsteed
		URI uriAttendue = target("/users").path(user.getLogin()).getUri();
		assertTrue(uriAttendue.equals(response.getLocation()));
	}
}