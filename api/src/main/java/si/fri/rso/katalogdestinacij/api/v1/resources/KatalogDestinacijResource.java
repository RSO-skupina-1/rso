package si.fri.rso.katalogdestinacij.api.v1.resources;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.json.JSONArray;
import org.json.JSONObject;
import si.fri.rso.katalogdestinacij.lib.KatalogDestinacij;
import si.fri.rso.katalogdestinacij.services.beans.KatalogDestinacijBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    @Counted(name = "get_all_katalog_destinacij_count")
    @Operation(description = "Returns all katalog destinacij.", summary = "Katalog destinacij list")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of destinacij",
                    content = @Content(schema = @Schema(implementation = KatalogDestinacij.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getKatalogDestinacij() {
        log.info("Get all katalog destinacij.") ;
        List<KatalogDestinacij> katalogDesitnacij = katalogDestinacijBean.getKatalogDestinacijFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(katalogDesitnacij).build();
    }


    @Operation(description = "Get metadata for an destinacija.", summary = "Get metadata for an destinacija")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Katalog destinacij",
                    content = @Content(
                            schema = @Schema(implementation = KatalogDestinacij.class))
            )})

    @GET
    @Path("/nearest/{startlat}/{startlng}/{offset}/{limit}")
    public Response getClosestKatalogDestinacij(@PathParam("startlat") float startlat, @PathParam("startlng") float startlng, @PathParam("offset") int offset, @PathParam("limit") int limit) {
        log.info("Get all nearest katalog destinacij.") ;
        List<KatalogDestinacij> katalogDestinacij = katalogDestinacijBean.getNearestKatalogDestinacij(startlat, startlng, offset, limit);

        return Response.status(Response.Status.OK).entity(katalogDestinacij).build();
    }

    @GET
    @Path("/nearestByName/{name}")
    public Response getClosestKatalogDestinacij(@PathParam("name") String locationName) {

        // make a request to open street map api to get the coordinates of the name
        // then call the getNearestKatalogDestinacij function with the coordinates  and return the result


        try {
            String apiUrl = "https://nominatim.openstreetmap.org/search?q=" + locationName +"&format=json&limit=1";
            float startlat;
            float startlng;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                // Extract latitude and longitude from the JSON response here

                //System.out.println("Response: " + response.toString());
                float[] coordinates = parseCoordinates(response.toString());
                startlat = coordinates[0];
                startlng = coordinates[1];
                connection.disconnect();
                List<KatalogDestinacij> katalogDestinacij = katalogDestinacijBean.getNearestKatalogDestinacij(startlat, startlng, 0, 10);

                return Response.status(Response.Status.OK).entity(katalogDestinacij).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }



        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private float[] parseCoordinates(String jsonResponse) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);

            if (jsonArray.length() > 0) {
                JSONObject firstResult = jsonArray.getJSONObject(0);

                // Extract latitude and longitude
                float latitude = (float) firstResult.getDouble("lat");
                float longitude = (float) firstResult.getDouble("lon");

                return new float[]{latitude, longitude};
            } else {
                System.out.println("No results found in the JSON response.");
                return new float[]{};
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
    @Counted(name = "number_of_created_katalog_destinacij")
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
    @Counted(name = "number_of_updated_katalog_destinacij")
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
    @Counted(name = "number_of_deleted_katalog_destinacij")
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
