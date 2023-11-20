package si.fri.rso.katalogdestinacij.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.katalogdestinacij.lib.KatalogDestinacij;
import si.fri.rso.katalogdestinacij.services.beans.KatalogDestinacijBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;



@ApplicationScoped
@Path("/katalogDestinacij")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KatalogDestinacijResource {

    private Logger log = Logger.getLogger(KatalogDestinacijResource.class.getName());

    @Inject
    private KatalogDestinacijBean katalogDestinacijBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all katalog destinacij.", summary = "Get all metadata")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of destinacij",
                    content = @Content(schema = @Schema(implementation = KatalogDestinacij.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getKatalogDestinacij() {
        log.info("Get all katalog destinacij.") ;
        List<KatalogDestinacij> imageMetadata = katalogDestinacijBean.getKatalogDestinacijFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(imageMetadata).build();
    }


    @Operation(description = "Get metadata for an destinacija.", summary = "Get metadata for an destinacija")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Katalog destinacij",
                    content = @Content(
                            schema = @Schema(implementation = KatalogDestinacij.class))
            )})
    @GET
    @Path("/{katalogDestinacijId}")
    public Response getKatalogDestinacij(@Parameter(description = "Metadata ID.", required = true)
                                     @PathParam("katalogDestinacijId") Integer imageMetadataId) {

        KatalogDestinacij katalogDestinacij = katalogDestinacijBean.getKatalogDestinacij(imageMetadataId);

        if (katalogDestinacij == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(katalogDestinacij).build();
    }

    @Operation(description = "Add image metadata.", summary = "Add metadata")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Metadata successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createKatalogDestinacij(@RequestBody(
            description = "DTO object with destinacija metadata.",
            required = true, content = @Content(
            schema = @Schema(implementation = KatalogDestinacij.class))) KatalogDestinacij katalogDestinacij) {

        if ((katalogDestinacij.getTitle() == null || katalogDestinacij.getDescription() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            katalogDestinacij = katalogDestinacijBean.createKatalogDestinacij(katalogDestinacij);
        }

        return Response.status(Response.Status.CONFLICT).entity(katalogDestinacij).build();

    }


    @Operation(description = "Update metadata for an destinacija.", summary = "Update metadata")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Metadata successfully updated."
            )
    })
    @PUT
    @Path("{katalogDestinacijId}")
    public Response putImageMetadata(@Parameter(description = "Metadata ID.", required = true)
                                     @PathParam("katalogDestinacijId") Integer imageMetadataId,
                                     @RequestBody(
                                             description = "DTO object with image metadata.",
                                             required = true, content = @Content(
                                             schema = @Schema(implementation = KatalogDestinacij.class)))
                                     KatalogDestinacij katalogDestinacij){

        katalogDestinacij = katalogDestinacijBean.putImageMetadata(imageMetadataId, katalogDestinacij);

        if (katalogDestinacij == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @Operation(description = "Delete metadata for an destinacija.", summary = "Delete metadata")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Metadata successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{katalogDestinacijId}")
    public Response deleteKatalogDestinacij(@Parameter(description = "Metadata ID.", required = true)
                                        @PathParam("katalogDestinacijId") Integer imageMetadataId){

        boolean deleted = katalogDestinacijBean.deleteImageMetadata(imageMetadataId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }





}
