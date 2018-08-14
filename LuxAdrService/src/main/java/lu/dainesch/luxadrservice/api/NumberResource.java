package lu.dainesch.luxadrservice.api;

import io.swagger.v3.oas.annotations.Operation;
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
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class NumberResource {

    @PersistenceContext
    private EntityManager em;

    @GET
    @Path("{id}")
    @Operation(summary = "House number by id",
            description = "Returns the house number with the given id. Includes also inactive numbers.",
            tags = {"housenumber"}
    )
    public HouseNumberDTO getById(@PathParam("id") Long id) {
        HouseNumber num = em.find(HouseNumber.class, id);
        if (num == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return num.toDTO(true);
    }

}
