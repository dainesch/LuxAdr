package lu.dainesch.luxadrservice.api;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrdto.entity.HouseNumberDTO;
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;

@Path("number")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NumberResource {

    @PersistenceContext
    private EntityManager em;

    @GET
    @Path("{id}")
    public HouseNumberDTO getById(@PathParam("id") Long id) {
        HouseNumber num = em.find(HouseNumber.class, id);
        if (num == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return num.toDTO(true);
    }

}
