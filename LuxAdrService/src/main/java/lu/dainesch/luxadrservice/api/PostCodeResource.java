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
import lu.dainesch.luxadrdto.SearchResultPostCode;
import lu.dainesch.luxadrdto.entity.PostalCodeDTO;
import lu.dainesch.luxadrdto.entity.StreetDTO;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.PostCodeHandler;

@Path("postcode")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class PostCodeResource {

    @Inject
    private ApiService as;
    @Inject
    private PostCodeHandler pcHand;

    @GET
    @Path("{id}")
    @Operation(summary = "Postal code by id",
            description = "Returns the postal code with the given id. Includes also inactive postal codes.",
            tags = {"postalcode"}
    )
    public PostalCodeDTO getById(@PathParam("id") Long id) {
        PostalCode pc = pcHand.getById(id);
        if (pc == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return pc.toDTO();
    }

    @GET
    @Path("{id}/streets")
    @Operation(summary = "Streets of postal code",
            description = "Returns the streets of the given postal code id. Includes also inactive streets.",
            tags = {"postalcode", "street"}
    )
    public List<StreetDTO> getStreets(@PathParam("id") Long id) {
        List<Street> streets = pcHand.getStreets(id);
        return streets.stream().map(s -> s.toDTO(true)).collect(Collectors.toList());
    }

    @GET
    @Path("code/{code}")
    @Operation(summary = "Postal code by code",
            description = "Returns the postal code with the given code. Only returns active codes.",
            tags = {"postalcode"}
    )
    public PostalCodeDTO getByCode(@PathParam("code") String code) {

        PostalCode pc = pcHand.getByCode(code);
        if (pc == null || !pc.isActive()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return pc.toDTO();

    }

    @GET
    @Path("code/{code}/streets")
    @Operation(summary = "Streets of postal code",
            description = "Returns the streets for the given postal code. Only returns active streets.",
            tags = {"postalcode", "street"}
    )
    public List<StreetDTO> getStreets(@PathParam("code") String code) {
        List<Street> streets = pcHand.getStreets(code);
        return streets.stream().map(s -> s.toDTO(true)).collect(Collectors.toList());
    }

    @POST
    @Path("search")
    @Operation(summary = "Postal code search",
            description = "Searches for postal codes fulfilling the given search request. Only includes active codes",
            tags = {"postalcode", "search"}
    )
    public SearchResultPostCode search(SearchRequest req) {
        if (!as.validateAndFix(req, true)) {
            return new SearchResultPostCode(req);
        }
        List<PostalCode> codes = pcHand.search(req);
        return new SearchResultPostCode(
                req,
                codes.stream().map(l -> l.toDTO()).collect(Collectors.toList())
        );

    }

}
