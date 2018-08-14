package lu.dainesch.luxadrservice.api;

import lu.dainesch.luxadrdto.SearchRequest;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrdto.SearchResult;
import lu.dainesch.luxadrdto.entity.PostalCodeDTO;
import lu.dainesch.luxadrdto.entity.StreetDTO;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.PostCodeHandler;

@Path("postcode")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostCodeResource {

    @Inject
    private ApiService as;
    @Inject
    private PostCodeHandler pcHand;

    @GET
    @Path("{id}")
    public PostalCodeDTO getById(@PathParam("id") Long id) {
        PostalCode pc = pcHand.getById(id);
        if (pc == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return pc.toDTO();
    }

    @GET
    @Path("{id}/streets")
    public List<StreetDTO> getStreets(@PathParam("id") Long id) {
        List<Street> streets = pcHand.getStreets(id);
        return streets.stream().map(s -> s.toDTO(true)).collect(Collectors.toList());
    }

    @GET
    @Path("code/{code}")
    public PostalCodeDTO getByCode(@PathParam("code") String code) {

        PostalCode pc = pcHand.getByCode(code);
        if (pc == null || !pc.isActive()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return pc.toDTO();

    }

    @GET
    @Path("code/{code}/streets")
    public List<StreetDTO> getStreets(@PathParam("code") String code) {
        List<Street> streets = pcHand.getStreets(code);
        return streets.stream().map(s -> s.toDTO(true)).collect(Collectors.toList());
    }

    @POST
    @Path("search")
    public SearchResult<PostalCodeDTO> search(SearchRequest req) {
        if (!as.validateAndFix(req, true)) {
            return new SearchResult<>(req);
        }
        List<PostalCode> codes = pcHand.search(req);
        return new SearchResult<>(
                req,
                codes.stream().map(l -> l.toDTO()).collect(Collectors.toList())
        );

    }

}
