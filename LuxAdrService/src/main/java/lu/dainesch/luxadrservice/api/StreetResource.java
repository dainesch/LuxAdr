package lu.dainesch.luxadrservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import lu.dainesch.luxadrdto.SearchResultStreet;
import lu.dainesch.luxadrdto.entity.HouseNumberDTO;
import lu.dainesch.luxadrdto.entity.PostalCodeDTO;
import lu.dainesch.luxadrdto.entity.StreetDTO;
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.StreetHandler;

@Path("street")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class StreetResource {

    @Inject
    private ApiService as;
    @Inject
    private StreetHandler strHand;

    @GET
    @Path("{id}")
    @Operation(summary = "Street by id",
            description = "Returns the street with the given id. Includes also inactive streets.",
            tags = {"street"}
    )
    public StreetDTO getById(@PathParam("id") Long id) {
        Street str = strHand.getById(id);
        if (str == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return str.toDTO(true);
    }

    @GET
    @Path("{id}/postcodes")
    @Operation(summary = "Postal codes of a street",
            description = "Returns the postal codes for the street id. Includes also inactive postal codes.",
            tags = {"street", "postalcode"}
    )
    public List<PostalCodeDTO> getPostCodes(@PathParam("id") Long id) {
        List<PostalCode> streets = strHand.getPostCodes(id);
        return streets.stream().map(s -> s.toDTO()).collect(Collectors.toList());
    }

    @GET
    @Path("{id}/numbers")
    @Operation(summary = "House numbers of a street",
            description = "Returns the possible house numbers for the street is. Includes also inactive numbers",
            tags = {"street", "housenumber"}
    )
    public List<HouseNumberDTO> getNumbers(@PathParam("id") Long id) {
        List<HouseNumber> nums = strHand.getHouseNumbers(id);
        return nums.stream().map(n -> n.toDTO(false)).collect(Collectors.toList());
    }

    @POST
    @Path("search")
    @Operation(summary = "Street search",
            description = "Searches for streets fulfilling the given search request. Only includes active streets",
            tags = {"street","search"}
    )
    public SearchResultStreet search(SearchRequest req) {
        if (!as.validateAndFix(req, true)) {
            return new SearchResultStreet(req);
        }
        List<Street> streets = strHand.search(req);
        return new SearchResultStreet(
                req,
                streets.stream().map(l -> l.toDTO(true)).collect(Collectors.toList())
        );

    }

}
