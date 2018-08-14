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
import lu.dainesch.luxadrdto.entity.HouseNumberDTO;
import lu.dainesch.luxadrdto.entity.PostalCodeDTO;
import lu.dainesch.luxadrdto.entity.StreetDTO;
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.StreetHandler;

@Path("street")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StreetResource {

    @Inject
    private ApiService as;
    @Inject
    private StreetHandler strHand;

    @GET
    @Path("{id}")
    public StreetDTO getById(@PathParam("id") Long id) {
        Street str = strHand.getById(id);
        if (str == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return str.toDTO(true);
    }

    @GET
    @Path("{id}/postcodes")
    public List<PostalCodeDTO> getPostCodes(@PathParam("id") Long id) {
        List<PostalCode> streets = strHand.getPostCodes(id);
        return streets.stream().map(s -> s.toDTO()).collect(Collectors.toList());
    }

    @GET
    @Path("{id}/numbers")
    public List<HouseNumberDTO> getNumbers(@PathParam("id") Long id) {
        List<HouseNumber> nums = strHand.getHouseNumbers(id);
        return nums.stream().map(n -> n.toDTO(false)).collect(Collectors.toList());
    }

    @POST
    @Path("search")
    public SearchResult<StreetDTO> search(SearchRequest req) {
        if (!as.validateAndFix(req, true)) {
            return new SearchResult<>(req);
        }
        List<Street> streets = strHand.search(req);
        return new SearchResult<>(
                req,
                streets.stream().map(l -> l.toDTO(true)).collect(Collectors.toList())
        );

    }

}
