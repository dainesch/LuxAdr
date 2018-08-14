package lu.dainesch.luxadrservice.api;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrdto.AdrEntry;
import lu.dainesch.luxadrdto.SearchRequest;
import lu.dainesch.luxadrdto.SearchResult;
import lu.dainesch.luxadrdto.SearchResultAdrEntry;
import lu.dainesch.luxadrservice.search.AdrSearchEntry;
import lu.dainesch.luxadrservice.search.SearchException;
import lu.dainesch.luxadrservice.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("search")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SearchResource {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResource.class);

    @Inject
    private ApiService as;
    @Inject
    private SearchService search;

    @POST
    @Operation(summary = "Search for complete addresses",
            description = "Allows you to search for an address using the default notation for addresses",
            tags = {"address", "search"}
    )
    public SearchResultAdrEntry search(SearchRequest req) {
        if (!as.validateAndFix(req)) {
            return new SearchResultAdrEntry(req);
        }

        try {
            Set<AdrSearchEntry> results = search.search(req.getValue(), req.getMaxResults());
            return new SearchResultAdrEntry(
                    req,
                    results.stream().map(r -> r.toDTO()).collect(Collectors.toList())
            );

        } catch (SearchException ex) {
            LOG.error("Error while searching for " + req.getValue());
            throw new WebApplicationException("Error during search", Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

}
