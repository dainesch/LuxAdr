package lu.dainesch.luxadrservice.api;

import io.swagger.v3.oas.annotations.Operation;
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
import lu.dainesch.luxadrdto.SearchResultLocality;
import lu.dainesch.luxadrdto.entity.LocalityDTO;
import lu.dainesch.luxadrdto.entity.PostalCodeDTO;
import lu.dainesch.luxadrdto.entity.StreetDTO;
import lu.dainesch.luxadrservice.adr.entity.Locality;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.LocalityHandler;

@Path("locality")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class LocalityResource {

    @Inject
    private ApiService as;
    @Inject
    private LocalityHandler locHand;

    @GET
    @Path("{id}")
    @Operation(summary = "Locality by id",
            description = "Returns the locality with the given id. Includes also inactive localities.",
            tags = {"locality"}
    )
    public LocalityDTO getById(@PathParam("id") Long id) {
        Locality loc = locHand.getById(id);
        if (loc == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return loc.toDTO();
    }

    @GET
    @Path("{id}/streets")
    @Operation(summary = "Streets of locality",
            description = "Returns the streets of the given locality id. Includes also inactive streets.",
            tags = {"locality", "street"}
    )
    public List<StreetDTO> getStreets(@PathParam("id") Long id) {
        List<Street> streets = locHand.getStreets(id);
        return streets.stream().map(s -> s.toDTO(false)).collect(Collectors.toList());
    }

    @GET
    @Path("{id}/postcodes")
    @Operation(summary = "Postal codes of a locality",
            description = "Returns the postal codes for the locality id. Includes also inactive postal codes.",
            tags = {"locality", "postalcode"}
    )
    public List<PostalCodeDTO> getPostCodes(@PathParam("id") Long id) {
        List<PostalCode> pcs = locHand.getPostCodes(id);
        return pcs.stream().map(p -> p.toDTO()).collect(Collectors.toList());
    }

    @POST
    @Path("search")
    @Operation(summary = "Locality search",
            description = "Searches for localities fulfilling the given search request. Only includes active localities",
            tags = {"locality", "search"}
    )
    public SearchResultLocality search(SearchRequest req) {

        if (!as.validateAndFix(req, true)) {
            return new SearchResultLocality(req);
        }

        List<Locality> locs = locHand.search(req);
        return new SearchResultLocality(
                req,
                locs.stream().map(l -> l.toDTO()).collect(Collectors.toList())
        );
    }

}
