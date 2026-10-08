package ressources;

import entities.Option;
import metiers.OptionBusiness;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("options")
@Produces(MediaType.APPLICATION_JSON)
public class OptionRessource {
    private static final OptionBusiness business = new OptionBusiness();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response creer(Option option) {
        if (option == null || option.getCodeOption() <= 0) return Response.status(400).build();
        if (!business.addOption(option)) return Response.status(409).build();
        return Response.ok(option).build();
    }

    @GET
    public Response lister(@QueryParam("domaine") String domaine) {
        return Response.ok(domaine == null ? business.getListeOptions() :
                business.getOptionsByDomaine(domaine)).build();
    }

    @GET
    @Path("{code}")
    public Response lire(@PathParam("code") int code) {
        Option option = business.getOptionByCode(code);
        return option == null ? Response.status(404).build() : Response.ok(option).build();
    }

    @PUT
    @Path("{code}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifier(@PathParam("code") int code, Option option) {
        if (option == null) return Response.status(400).build();
        if (!business.updateOption(code, option)) return Response.status(404).build();
        return Response.ok(business.getOptionByCode(code)).build();
    }

    @DELETE
    @Path("{code}")
    public Response supprimer(@PathParam("code") int code) {
        return business.deleteOption(code) ? Response.noContent().build() : Response.status(404).build();
    }
}
