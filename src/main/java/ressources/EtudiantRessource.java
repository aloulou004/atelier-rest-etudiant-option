package ressources;

import entities.Etudiant;
import entities.EtudiantList;
import entities.Option;
import metiers.EtudiantBusiness;
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

@Path("etudiants")
@Produces(MediaType.APPLICATION_JSON)
public class EtudiantRessource {
    private static final EtudiantBusiness business = new EtudiantBusiness();
    private static final OptionBusiness options = new OptionBusiness();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response creer(Etudiant etudiant) {
        if (etudiant == null || etudiant.getIdentifiant() == null ||
                etudiant.getIdentifiant().trim().isEmpty() || etudiant.getOption() == null) {
            return Response.status(400).build();
        }
        if (options.getOptionByCode(etudiant.getOption().getCodeOption()) == null)
            return Response.status(404).build();
        if (!business.addEtudiant(etudiant)) return Response.status(409).build();
        return Response.ok(etudiant).build();
    }

    @GET
    public Response lister() {
        return Response.ok(business.getAllEtudiants()).build();
    }

    @GET
    @Path("option")
    @Produces(MediaType.APPLICATION_XML)
    public Response listerParOption(@QueryParam("codeOption") Integer codeOption) {
        if (codeOption == null) return Response.status(400).build();
        Option option = options.getOptionByCode(codeOption);
        if (option == null) return Response.status(404).build();
        return Response.ok(new EtudiantList(business.getEtudiantsByOption(option))).build();
    }

    @GET
    @Path("{identifiant}")
    public Response lire(@PathParam("identifiant") String identifiant) {
        Etudiant etudiant = business.getEtudiantByIdentifiant(identifiant);
        return etudiant == null ? Response.status(404).build() : Response.ok(etudiant).build();
    }

    @PUT
    @Path("{identifiant}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifier(@PathParam("identifiant") String identifiant, Etudiant etudiant) {
        if (etudiant == null || etudiant.getOption() == null) return Response.status(400).build();
        if (business.getEtudiantByIdentifiant(identifiant) == null ||
                options.getOptionByCode(etudiant.getOption().getCodeOption()) == null)
            return Response.status(404).build();
        business.updateEtudiant(identifiant, etudiant);
        return Response.ok(business.getEtudiantByIdentifiant(identifiant)).build();
    }

    @DELETE
    @Path("{identifiant}")
    public Response supprimer(@PathParam("identifiant") String identifiant) {
        return business.deleteEtudiant(identifiant) ? Response.noContent().build() : Response.status(404).build();
    }
}
