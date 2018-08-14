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
import lu.dainesch.luxadrdto.entity.LocalityDTO;
import lu.dainesch.luxadrdto.entity.PostalCodeDTO;
import lu.dainesch.luxadrdto.entity.StreetDTO;
import lu.dainesch.luxadrservice.adr.entity.Locality;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.LocalityHandler;

@Path("locality")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocalityResource {

    @Inject
    private ApiService as;
    @Inject
    private LocalityHandler locHand;

    @GET
    @Path("{id}")
    public LocalityDTO getById(@PathParam("id") Long id) {
        Locality loc = locHand.getById(id);
        if (loc == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return loc.toDTO();
    }

    @GET
    @Path("{id}/streets")
    public List<StreetDTO> getStreets(@PathParam("id") Long id) {
        List<Street> streets = locHand.getStreets(id);
        return streets.stream().map(s -> s.toDTO(false)).collect(Collectors.toList());
    }

    @GET
    @Path("{id}/postcodes")
    public List<PostalCodeDTO> getPostCodes(@PathParam("id") Long id) {
        List<PostalCode> pcs = locHand.getPostCodes(id);
        return pcs.stream().map(p -> p.toDTO()).collect(Collectors.toList());
    }

    @POST
    @Path("search")
    public SearchResult<LocalityDTO> search(SearchRequest req) {

        if (!as.validateAndFix(req, true)) {
            return new SearchResult<>(req);
        }
        
        List<Locality> locs = locHand.search(req);
        return new SearchResult<>(
                req,
                locs.stream().map(l -> l.toDTO()).collect(Collectors.toList())
        );
    }

}
